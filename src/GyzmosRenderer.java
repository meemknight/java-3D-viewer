import org.joml.Matrix4f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.List;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;


public class GyzmosRenderer
{

	public Shader gyzmosShader;
	public int vao;
	public int u_modelViewProjection;
	public int u_color;
	
	public void init()
	{
		vao = GL43.glGenVertexArrays();
		
		gyzmosShader = new Shader();
		try
		{
			gyzmosShader.loadShaderFromFile("resources//gyzmos.vert", "resources//gyzmos.frag");
		} catch(Exception e)
		{
			System.out.println("Shader loading err in Gyzmos renderer: " + e);
		}
		
		u_modelViewProjection = gyzmosShader.getUniformLocation("u_modelViewProjection");
		u_color = gyzmosShader.getUniformLocation("u_color");
	}
	
	public void render(Camera camera, Vector3f position, Texture t, float colorR, float colorG, float colorB)
	{
		GL43.glBindVertexArray(vao);
		gyzmosShader.bind();
		
		GL43.glActiveTexture(GL43.GL_TEXTURE0);
		GL43.glBindTexture(GL11.GL_TEXTURE_2D, t.id);
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			
			Matrix4f model = new Matrix4f().translate(position);
			Matrix4f projection = camera.getProjectionMatrix();
			Matrix4f view = camera.getViewMatrix();
			
			//negate rotation. set the model rotation part to the inverse(transpose in this case) of the rotatio of view matrix
			model.m00(view.m00());
			model.m11(view.m11());
			model.m22(view.m22());
			
			model.m01(view.m10());
			model.m02(view.m20());
			model.m12(view.m21());
			
			model.m10(view.m01());
			model.m20(view.m02());
			model.m21(view.m12());
			
			FloatBuffer fb = (projection.mul(view).mul(model)).get(stack.mallocFloat(16));
			
			GL30.glUniformMatrix4fv(u_modelViewProjection, false,
					fb);
		}
		
		GL43.glUniform3f(u_color, colorR, colorG, colorB);
		
		GL43.glDrawArrays(GL11.GL_TRIANGLE_FAN, 0, 4);
		GL43.glBindVertexArray(0);
		
	}
	

}
