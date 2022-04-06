#version 330

vec2 positions[] =
    {vec2(1,1), vec2(-1,1), vec2(-1,-1),
     vec2(1,-1)
    };

vec2 uvs[] =
    {vec2(1,1), vec2(0,1), vec2(0,0),
    vec2(1,0)
    };

uniform mat4 u_modelViewProjection;
out vec2 v_uv;

void main()
{
    v_uv = uvs[gl_VertexID];
    v_uv.y = 1-v_uv.y;
    v_uv *= 0.99;

    gl_Position = vec4(positions[gl_VertexID] * 0.5, 0, 1);
    gl_Position = u_modelViewProjection * gl_Position;

}

