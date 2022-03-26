#version 420

layout (location = 0) out vec4 a_outColor;

in vec3 v_texCoords;

layout(binding = 0) uniform samplerCube u_skybox;
//uniform float u_exposure;

void main()
{

    a_outColor = texture(u_skybox, v_texCoords);

    //todo
    //hdr
    //float exposure = u_exposure;
    //a_outColor.rgb = vec3(1.0) - exp(-a_outColor.rgb  * exposure);

    //gama
    a_outColor.rgb = pow(a_outColor.rgb, vec3(1.0/2.2));
    a_outColor.a = 1;

    //a_outColor.rgba = vec4(1);
}