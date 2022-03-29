import org.joml.Matrix4f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

//renders 2D textures used to mark where light sources are for debugging purposes but not only.
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
	
	//List<float> data = new ArrayList<>();
	FloatVector data = new FloatVector();
	IntVector textureData = new IntVector();
	
	//rendering means adding the object in a que in this case.
	public void render(float positionX, float positionY, float positionZ, Texture t, float colorR, float colorG, float colorB)
	{
		textureData.pushBack(t.id);
		
		data.pushBack(positionX);
		data.pushBack(positionY);
		data.pushBack(positionZ);
		data.pushBack(colorR);
		data.pushBack(colorG);
		data.pushBack(colorB);
		
	}
	
	//the flush is the actual operation that renders the object. Rendering multiple objects separately can be highly inefficient.
	public void flush(Camera camera)
	{
		GL43.glBindVertexArray(vao);
		gyzmosShader.bind();
		
		GL43.glActiveTexture(GL43.GL_TEXTURE0);
		
		for(int i=0; i<textureData.size; i++)
		{
			
			GL43.glBindTexture(GL11.GL_TEXTURE_2D, textureData.data[i]);
			
			try(MemoryStack stack = MemoryStack.stackPush())
			{
				Matrix4f projection = camera.getProjectionMatrix();
				Matrix4f view = camera.getViewMatrix();
				Matrix4f model = new Matrix4f().translate
						(new Vector3f(
								data.get(i*6 + 0),
								data.get(i*6 + 1),
								data.get(i*6 + 2)));
				
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
			
			GL43.glUniform3f(u_color,
					data.get(i*6 + 3),
					data.get(i*6 + 4),
					data.get(i*6 + 5));
			
			GL43.glDrawArrays(GL11.GL_TRIANGLE_FAN, 0, 4);
			
		}
		
		GL43.glBindVertexArray(0);
		
		data.clear();
		textureData.clear();
		
	}
	
	
}
