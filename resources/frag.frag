#version 430

layout (location = 0) out vec4 color;
in vec3 v_normal;
in vec2 v_uv;
in vec3 v_worldSpacePosition;

layout(binding = 0) uniform sampler2D u_texture;

struct PointLights
{
    vec4 position;
    vec4 color;
};

struct DirectionalLights
{
    vec4 direction;
    vec4 color;
};

struct SpotLights
{
    vec4 position;

    vec3 direction;
    float angleCos;

    vec4 color;
};

readonly restrict layout(std140) buffer u_pointLights
{
    PointLights pointLights[];
};
uniform int u_pointLightsCount;

readonly restrict layout(std140) buffer u_directionalLights
{
    DirectionalLights directionalLights[];
};
uniform int u_directionalLightsCount;

readonly restrict layout(std430) buffer u_spotLights
{
    SpotLights spotLights[];
};
uniform int u_spotLightsCount;


float exposure = 1.2;

const float gamma = 2.2;

vec4 toGammaSpace(in vec4 color)
{
    color.rgb = pow(color.rgb, vec3(1.0/gamma));
    return color;
}

vec4 toLinearSpace(in vec4 color)
{
    color.rgb = pow(color.rgb, vec3(gamma));
    return color;
}


//https://github.com/TheRealMJP/BakingLab/blob/master/BakingLab/ACES.hlsl
/*
=================================================================================================

  Baking Lab
  by MJP and David Neubelt
  http://mynameismjp.wordpress.com/

  All code licensed under the MIT license

=================================================================================================
 The code in this file was originally written by Stephen Hill (@self_shadow), who deserves all
 credit for coming up with this fit and implementing it. Buy him a beer next time you see him. :)
*/
// sRGB => XYZ => D65_2_D60 => AP1 => RRT_SAT
mat3x3 ACESInputMat = mat3x3
(
0.59719, 0.35458, 0.04823,
0.07600, 0.90834, 0.01566,
0.02840, 0.13383, 0.83777
);
// ODT_SAT => XYZ => D60_2_D65 => sRGB
mat3x3 ACESOutputMat = mat3x3
(
1.60475, -0.53108, -0.07367,
-0.10208,  1.10813, -0.00605,
-0.00327, -0.07276,  1.07602
);
vec3 RRTAndODTFit(vec3 v)
{
    vec3 a = v * (v + 0.0245786f) - 0.000090537f;
    vec3 b = v * (0.983729f * v + 0.4329510f) + 0.238081f;
    return a / b;
}
vec3 ACESFitted(vec3 color)
{
    color = transpose(ACESInputMat) * color;
    // Apply RRT and ODT
    color = RRTAndODTFit(color);
    color = transpose(ACESOutputMat) * color;
    color = clamp(color, 0, 1);
    return color;
}

vec3 phongLightModel(vec3 lightDirection, vec3 normal, vec3 lightColor)
{
    vec3 light = max(dot(v_normal, -lightDirection), 0.f) * lightColor;
    return light;
}


void main()
{
    color = toLinearSpace(texture2D(u_texture, v_uv).rgba);

    vec3 light = vec3(0);

    light += vec3(0.1); //ambient light

    for(int i=0; i< u_pointLightsCount; i++)
    {
        vec3 lightPosition = pointLights[i].position.xyz;
        vec3 lightColor = pointLights[i].color.rgb;
        vec3 lightDirection = normalize(v_worldSpacePosition - lightPosition);

        light += phongLightModel(lightDirection, v_normal, lightColor);
    }

    for(int i=0;i <u_directionalLightsCount; i++)
    {
        vec3 lightColor = directionalLights[i].color.rgb;
        vec3 lightDirection = directionalLights[i].direction.xyz;

        light += phongLightModel(lightDirection, v_normal, lightColor);
    }

    for(int i=0; i< u_spotLightsCount; i++)
    {
        vec3 lightPosition = spotLights[i].position.xyz;
        vec3 lightColor = spotLights[i].color.rgb;
        vec3 lightRayDirection = normalize(v_worldSpacePosition - lightPosition);
        vec3 lightOrientation = spotLights[i].direction.xyz;

        float cosAngleCalculated = dot(lightRayDirection, lightOrientation);
        float desiredAngle = spotLights[i].angleCos;

        //todo add penumbra
        if(desiredAngle < cosAngleCalculated)
        {
            light += phongLightModel(lightRayDirection, v_normal, lightColor);
        }else
        {

        }

    }



    color.rgb *= light;

    color.rgb = ACESFitted(color.rgb * exposure);
    color = toGammaSpace(color);
}