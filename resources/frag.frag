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

readonly restrict layout(std140) buffer u_pointLights
{
    PointLights pointLights[];
};

uniform int u_pointLightsCount;

void main()
{
    color = texture2D(u_texture, v_uv).rgba;

    vec3 light = vec3(0);

    light += vec3(0.1); //ambient light

    for(int i=0; i< u_pointLightsCount; i++)
    {
        vec3 lightPosition = pointLights[i].position.xyz;
        vec3 lightColor = pointLights[i].color.rgb;
        vec3 lightDirection = normalize(v_worldSpacePosition - lightPosition);

        light += max(dot(v_normal, -lightDirection), 0.f) * lightColor;

    }

    light = clamp(light, vec3(0), vec3(1));

    color.rgb *= light;

}