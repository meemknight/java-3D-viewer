#version 430

layout (location = 0) out vec4 color;

layout(binding = 0) uniform sampler2D u_texture;

in vec2 v_uv;
uniform vec3 u_color;

void main()
{
    vec4 t = texture2D(u_texture, v_uv).rgba;

    if(t.a <= 0.01)
        discard;

    vec4 alphaGather = textureGather(u_texture, v_uv, 3).rgba;
    float avgAlpha = alphaGather.r + alphaGather.g + alphaGather.b + alphaGather.a;
    avgAlpha /= 4.f;

    t.rgb *= u_color.rgb * avgAlpha;

    color = t;

}