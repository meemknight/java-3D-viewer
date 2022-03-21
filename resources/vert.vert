#version 330

layout (location = 0) in vec3 pos;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 uv;

uniform mat4 u_viewProjection;
uniform mat4 u_model;

out vec3 v_normal;
out vec2 v_uv;
out vec3 v_worldSpacePosition;

void main()
{
    v_normal = normal;
    v_uv = uv;
    v_worldSpacePosition = pos;//vec3(u_model * vec4(pos, 1));
    gl_Position = u_viewProjection * vec4(pos, 1);
}