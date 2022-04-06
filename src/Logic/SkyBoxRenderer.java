package Logic;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL44;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

//holds the shader and data for rendering a skybox.
public class SkyBoxRenderer
{
	//a unit cube shape used to draw the sky
	private static final float skyboxVertices[] = {
			// positions
			-1.0f,  1.0f, -1.0f,
			-1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, -1.0f,
			1.0f,  1.0f, -1.0f,
			-1.0f,  1.0f, -1.0f,
			
			-1.0f, -1.0f,  1.0f,
			-1.0f, -1.0f, -1.0f,
			-1.0f,  1.0f, -1.0f,
			-1.0f,  1.0f, -1.0f,
			-1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f,  1.0f,
			
			1.0f, -1.0f, -1.0f,
			1.0f, -1.0f,  1.0f,
			1.0f,  1.0f,  1.0f,
			1.0f,  1.0f,  1.0f,
			1.0f,  1.0f, -1.0f,
			1.0f, -1.0f, -1.0f,
			
			-1.0f, -1.0f,  1.0f,
			-1.0f,  1.0f,  1.0f,
			1.0f,  1.0f,  1.0f,
			1.0f,  1.0f,  1.0f,
			1.0f, -1.0f,  1.0f,
			-1.0f, -1.0f,  1.0f,
			
			-1.0f,  1.0f, -1.0f,
			1.0f,  1.0f, -1.0f,
			1.0f,  1.0f,  1.0f,
			1.0f,  1.0f,  1.0f,
			-1.0f,  1.0f,  1.0f,
			-1.0f,  1.0f, -1.0f,
			
			-1.0f, -1.0f, -1.0f,
			-1.0f, -1.0f,  1.0f,
			1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, -1.0f,
			-1.0f, -1.0f,  1.0f,
			1.0f, -1.0f,  1.0f
	};
	
	private int vertexData = 0;
	private int vao = 0;
	
	private Shader shader;
	private int u_viewProjection;
	
	//load the data necessary for rendering the sky box into the gpu
	public void init()
	{
		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		
		vertexData = GL30.glGenBuffers();
		GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vertexData);
		//GL30.glBufferData(GL30.GL_ARRAY_BUFFER, skyboxVertices, GL30.GL_STATIC_DRAW);
		GL44.glBufferStorage(GL30.GL_ARRAY_BUFFER, skyboxVertices, 0);
		
		GL30.glEnableVertexAttribArray(0);
		GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 0, 0);
		
		GL30.glBindVertexArray(0);
		
		shader = new Shader();
		
		try
		{
			shader.loadShaderFromFile("resources//skyBox.vert", "resources//skyBox.frag");
		}catch(Exception e)
		{
			System.out.println("Sky box init shader error");
		}
		
		u_viewProjection = shader.getUniformLocation("u_viewProjection");
		
	}
	
	public void render(Camera camera, SkyBox skyBox)
	{
		shader.bind();
		
		//use the sky box data
		GL30.glBindVertexArray(vao);
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			
			//keep only the rotation of the camera not the movement to keep the sky box in the middle of the world
			Matrix4f viewMat = camera.getViewMatrix();
			viewMat.m03(0);
			viewMat.m13(0);
			viewMat.m23(0);
			viewMat.m30(0);
			viewMat.m31(0);
			viewMat.m32(0);
			viewMat.m33(1);
			
			Matrix4f viewProjection = camera.getProjectionMatrix().mul(viewMat);

			FloatBuffer fb = viewProjection.get(stack.mallocFloat(16));
			GL30.glUniformMatrix4fv(u_viewProjection, false,
					fb);
		}
		
		//draw the sky box
		GL30.glDepthFunc(GL30.GL_LEQUAL);
		GL30.glActiveTexture(GL30.GL_TEXTURE0);
		GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, skyBox.texture);
		GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, 36);
		
		GL30.glDepthFunc(GL30.GL_LESS);
		GL30.glBindVertexArray(0);
	}

}
