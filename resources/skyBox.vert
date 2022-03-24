#version 330

layout (location = 0) in vec3 aPos;

out vec3 v_texCoords;

uniform mat4 u_viewProjection;

void main()
{
    v_texCoords = aPos;
    vec4 pos = u_viewProjection * vec4(aPos, 1.0);
    gl_Position = pos.xyww; //z is w so when moving to normalized device coordonates it will become 1 aka as far as possible.
    //gl_Position.z *= 0.9;
    //gl_Position = u_viewProjection * vec4(aPos, 1.0);
}