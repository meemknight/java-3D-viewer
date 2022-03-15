#version 430

layout (location = 0) out vec4 color;
in vec3 v_normal;
in vec2 v_uv;

layout(binding = 0) uniform sampler2D u_texture;

void main()
{
    color = texture2D(u_texture, v_uv).rgba;
    //color = (gl_FragCoord.x<25.0) ? vec4(1.0, 0.0, 0.0, 1.0) : vec4(0.0, 1.0, 0.0, 1.0);
    //color = vec4(gl_FragCoord.xy/1000.f,0.9,1);

    float light = 0.1;
    light += dot(v_normal, normalize(vec3(0.2,1,0.1)));

    color *= light;

}