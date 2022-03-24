#version 430

layout (location = 0) out vec4 color;
in vec3 v_normal;
in vec2 v_uv;
in vec3 v_worldSpacePosition;

layout(binding = 0) uniform sampler2D u_texture;
layout(binding = 1) uniform sampler2D u_normal;
layout(binding = 2) uniform sampler2D u_ao;
layout(binding = 3) uniform sampler2D u_metallic;
layout(binding = 4) uniform sampler2D u_roughness;

uniform vec3 u_eye;

const float PI = 3.14159265359;

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


float exposure = 1.7;

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

vec3 phongLightModel(vec3 lightDirection, vec3 normal, vec3 lightColor, vec3 viewDir)
{
    float lightIntensity = max(dot(v_normal, -lightDirection), 0.f);

    if(lightIntensity <= 0){return vec3(0);}

    vec3 light = lightIntensity * lightColor;

    //specular
    vec3 halfwayDir = normalize(-lightDirection + viewDir);
    float spec = pow(max(dot(normal, halfwayDir), 0.0), 64);
    vec3 specular = lightColor * spec * 2;
    //return specular;
    return light += specular;
}

//https://gamedev.stackexchange.com/questions/22204/from-normal-to-rotation-matrix#:~:text=Therefore%2C%20if%20you%20want%20to,the%20first%20and%20second%20columns.
mat3x3 NormalToRotation(in vec3 normal)
{
    // Find a vector in the plane
    vec3 tangent0 = cross(normal, vec3(1, 0, 0));
    if (dot(tangent0, tangent0) < 0.001)
    tangent0 = cross(normal, vec3(0, 1, 0));
    tangent0 = normalize(tangent0);
    // Find another vector in the plane
    vec3 tangent1 = normalize(cross(normal, tangent0));
    // Construct a 3x3 matrix by storing three vectors in the columns of the matrix

    return mat3x3(tangent0,tangent1,normal);
}

//n normal
//h halfway vector
//a roughness	(1 rough, 0 glossy)
//this gets the amount of specular light reflected
float DistributionGGX(vec3 N, vec3 H, float roughness)
{
    //GGX/Trowbridge-Reitz
    //			 a^2
    // ------------------------
    // PI ((N*H)^2 (a^2-1)+1)^2

    float a      = roughness*roughness;
    float a2     = a*a;
    float NdotH  = max(dot(N, H), 0.0);
    float NdotH2 = NdotH*NdotH;

    float denom = (NdotH2 * (a2 - 1.0) + 1.0);
    denom = PI * denom * denom;

    return  a2 / max(denom, 0.0000001);
}

float GeometrySchlickGGX(float NdotV, float roughness)
{
    //float r = (roughness + 1.0);
    //float k = (r*r) / 8.0;			//disney

    float k = roughness*roughness / 2;

    float num   = NdotV;
    float denom = NdotV * (1.0 - k) + k;

    return num / max(denom, 0.0000001);
}

//oclude light that is hidded begind small geometry roughnesses
float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness)
{
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    float ggx2  = GeometrySchlickGGX(NdotV, roughness);
    float ggx1  = GeometrySchlickGGX(NdotL, roughness);

    return ggx1 * ggx2;
}


//cosTheta is the dot between the normal and halfway
//ratio between specular and diffuse reflection
vec3 fresnelSchlick(float cosTheta, vec3 F0)
{
    return F0 + (1.0 - F0) * pow(max(1.0 - cosTheta, 0.0), 5.0);
}
vec3 fresnelSchlickRoughness(float cosTheta, vec3 F0, float roughness)
{
    return F0 + (max(vec3(1.0 - roughness), F0) - F0) * pow(max(1.0 - cosTheta, 0.0), 5.0);
}

vec3 fSpecular(vec3 normal, vec3 halfwayVec, vec3 viewDir,
vec3 lightDirection, float dotNVclamped, float roughness, vec3 F)
{
    //fCook-Torrance
    float NDF = DistributionGGX(normal, halfwayVec, roughness);
    float G   = GeometrySmith(normal, viewDir, lightDirection, roughness);
    float denominator = 4.0 * dotNVclamped
    * max(dot(normal, lightDirection), 0.0);
    vec3 specular     = (NDF * G * F) / max(denominator, 0.001);

    return specular;
}

vec3 fDiffuse(vec3 color)
{
    //fLambert
    return color.rgb / PI;
}

vec3 fDiffuseOrenNayar(vec3 color, float roughness, vec3 L, vec3 V, vec3 N)
{
    float a = roughness;
    float a2 = a*a;

    float cosi = max(dot(L, N), 0);
    float cosr = max(dot(V, N), 0);
    float sini = sqrt(1-cosi*cosi);
    float sinr = sqrt(1-cosr*cosr);
    float tani = sini/cosi;
    float tanr = sinr/cosr;

    float A = 1 - 0.5 * a2/(a2 + 0.33);
    float B = 0.45*a2/(a2+0.09);

    float sinAlpha = max(sini, sinr);
    float tanBeta = min(tani, tanr);

    return color.rgb * (A + (B* max(0, dot(L,reflect(V,N))) * sinAlpha * tanBeta  )) / PI;
}

//https://mimosa-pudica.net/improved-oren-nayar.html

vec3 fDiffuseOrenNayar2(vec3 color, float roughness, vec3 L, vec3 V, vec3 N)
{
    float a = roughness;
    float a2 = a*a;
    //vec3 A = 1.f/PI * (1 - 0.5 * a2/(a2 + 0.33) + 0.17*color*a2/(a2+0.13));
    //float B = 0.45*a2/(a2+0.09);

    float A = 1.0/(PI+(PI/2.0-2/3.0)*a);
    float B = PI/(PI+(PI/2.0-2/3.0)*a);

    float s = dot(L,N) - dot(N,L)*dot(N,V);

    float t;
    if(s <= 0)
    t = 1;
    else
    t = max(dot(N,L), dot(N,V));

    return color * (A + B * s/t);
}

vec3 renderingEquation(vec3 lightDirection, float metallic, float roughness, in vec3 lightColor, in vec3 worldPosition,
in vec3 viewDir, in vec3 color, in vec3 normal)
{
    lightDirection = -lightDirection;
    vec3 F0 = vec3(0.04);
    F0 = mix(F0, color.rgb, vec3(metallic));

    float dotNVclamped = clamp(dot(normal, viewDir), 0.0, 0.99);

    vec3 halfwayVec = normalize(lightDirection + viewDir);

    vec3 radiance = lightColor; //here the first component is the light color

    vec3 F  = fresnelSchlick(max(dot(halfwayVec, viewDir), 0.0), F0);

    vec3 specular = fSpecular(normal, halfwayVec, viewDir, lightDirection, dotNVclamped, roughness, F);

    vec3 kS = F; //this is the specular contribution
    vec3 kD = vec3(1.0) - kS; //the difuse is the remaining specular
    kD *= 1.0 - metallic;	//metallic surfaces are darker

    vec3 diffuse = fDiffuse(color.rgb);
    //vec3 diffuse = fDiffuseOrenNayar(color.rgb, roughness, lightDirection, viewDir, normal);
    //vec3 diffuse = fDiffuseOrenNayar2(color.rgb, roughness, lightDirection, viewDir, normal);

    float NdotL = max(dot(normal, lightDirection), 0.0);
    return (kD * diffuse + specular) * radiance * NdotL;

}

void main()
{

    vec3 sampeledNormal = texture2D(u_normal, v_uv).xyz;
    sampeledNormal = normalize(2*sampeledNormal - 1.f);

    vec3 normalMappedNormal = normalize(NormalToRotation(v_normal) * sampeledNormal);
    vec3 viewDir = normalize(u_eye - v_worldSpacePosition);

    float metallic = texture2D(u_metallic, v_uv).r;
    float ao = texture2D(u_ao, v_uv).r;
    float roughness = texture2D(u_roughness, v_uv).r;

    color = toLinearSpace(texture2D(u_texture, v_uv).rgba);

    vec3 light = vec3(0);

    light += vec3(0.1) * ao; //ambient light

    for(int i=0; i< u_pointLightsCount; i++)
    {
        vec3 lightPosition = pointLights[i].position.xyz;
        vec3 lightColor = pointLights[i].color.rgb;
        vec3 lightDirection = normalize(v_worldSpacePosition - lightPosition);

        //light += phongLightModel(lightDirection, normalMappedNormal, lightColor, viewDir);
        light += renderingEquation(lightDirection,
        metallic, roughness, lightColor, v_worldSpacePosition, viewDir, color.rgb, normalMappedNormal);
    }

    for(int i=0;i <u_directionalLightsCount; i++)
    {
        vec3 lightColor = directionalLights[i].color.rgb;
        vec3 lightDirection = directionalLights[i].direction.xyz;

        //light += phongLightModel(lightDirection, normalMappedNormal, lightColor, viewDir);
        light += renderingEquation(lightDirection, metallic, roughness, lightColor, v_worldSpacePosition, viewDir, color.rgb, normalMappedNormal);
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
            //light += phongLightModel(lightRayDirection, normalMappedNormal, lightColor, viewDir);
            light += renderingEquation(lightRayDirection, metallic, roughness, lightColor, v_worldSpacePosition, viewDir, color.rgb, normalMappedNormal);
        }else
        {

        }

    }

    color.rgb *= light;

    color.rgb = ACESFitted(color.rgb * exposure);
    color = toGammaSpace(color);

}