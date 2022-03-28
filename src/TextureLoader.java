import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import org.lwjgl.system.MemoryStack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL30.*;

public class TextureLoader
{
	private TextureLoader()
	{
	}
	
	;
	
	public static Shader cubeMapConvoluteShader;
	public static Shader cubeMapSpecualrIBLShader;
	
	private static int u_viewProjectionDiffuse;
	private static int u_viewProjectionSpecular;
	private static int u_roughness;
	private static int vertexArray;
	private static int vertexBuffer;
	
	//todo refactor
	private static float skyboxVertices[] = {
			// positions
			-1.0f, 1.0f, -1.0f,
			-1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, -1.0f,
			1.0f, 1.0f, -1.0f,
			-1.0f, 1.0f, -1.0f,
			
			-1.0f, -1.0f, 1.0f,
			-1.0f, -1.0f, -1.0f,
			-1.0f, 1.0f, -1.0f,
			-1.0f, 1.0f, -1.0f,
			-1.0f, 1.0f, 1.0f,
			-1.0f, -1.0f, 1.0f,
			
			1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, 1.0f,
			1.0f, 1.0f, 1.0f,
			1.0f, 1.0f, 1.0f,
			1.0f, 1.0f, -1.0f,
			1.0f, -1.0f, -1.0f,
			
			-1.0f, -1.0f, 1.0f,
			-1.0f, 1.0f, 1.0f,
			1.0f, 1.0f, 1.0f,
			1.0f, 1.0f, 1.0f,
			1.0f, -1.0f, 1.0f,
			-1.0f, -1.0f, 1.0f,
			
			-1.0f, 1.0f, -1.0f,
			1.0f, 1.0f, -1.0f,
			1.0f, 1.0f, 1.0f,
			1.0f, 1.0f, 1.0f,
			-1.0f, 1.0f, 1.0f,
			-1.0f, 1.0f, -1.0f,
			
			-1.0f, -1.0f, -1.0f,
			-1.0f, -1.0f, 1.0f,
			1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, -1.0f,
			-1.0f, -1.0f, 1.0f,
			1.0f, -1.0f, 1.0f
	};
	
	public static void init()
	{
		cubeMapConvoluteShader = new Shader();
		cubeMapSpecualrIBLShader = new Shader();
		
		try
		{
			cubeMapConvoluteShader.loadShaderFromFile("resources/cubeMapBasicShader.vert",
					"resources/convolute.frag");
			
			cubeMapSpecualrIBLShader.loadShaderFromFile("resources/cubeMapBasicShader.vert",
					"resources/preFilterSpecular.frag");
			
		} catch(Exception e)
		{
			System.out.println("Error loading shader in thxture loader" + e);
		}
		
		u_viewProjectionDiffuse = cubeMapConvoluteShader.getUniformLocation("u_viewProjection");
		u_viewProjectionSpecular = cubeMapSpecualrIBLShader.getUniformLocation("u_viewProjection");
		u_roughness = cubeMapSpecualrIBLShader.getUniformLocation("u_roughness");
		
		vertexArray = glGenVertexArrays();
		glBindVertexArray(vertexArray);
		
		vertexBuffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
		
		glBufferData(GL_ARRAY_BUFFER, skyboxVertices, GL_STATIC_DRAW);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		
		glBindVertexArray(0);
	}
	
	//https://www.youtube.com/watch?v=SPt-aogu72A&list=PLRIWtICgwaX0u7Rf9zkZhLoLuZVfUksDP&index=6
	public static IntBuffer loadTexturePixelData(String name, Vector2i dimensions)
	{
		int[] pixels = null;
		
		try
		{
			BufferedImage image = ImageIO.read(new FileInputStream(name));
			dimensions.x = image.getWidth();
			dimensions.y = image.getHeight();
			pixels = new int[dimensions.x * dimensions.y];
			image.getRGB(0, 0, dimensions.x, dimensions.y, pixels, 0, dimensions.x);
		} catch(IOException e)
		{
			e.printStackTrace();
		}
		
		int[] data = new int[dimensions.x * dimensions.y];
		for(int i = 0; i < dimensions.x * dimensions.y; i++)
		{
			int a = (pixels[i] & 0xff000000) >> 24;
			int r = (pixels[i] & 0xff0000) >> 16;
			int g = (pixels[i] & 0xff00) >> 8;
			int b = (pixels[i] & 0xff);
			
			data[i] = a << 24 | b << 16 | g << 8 | r;
		}
		
		int result = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, result);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
		
		IntBuffer buffer = ByteBuffer.allocateDirect(data.length << 2)
				.order(ByteOrder.nativeOrder()).asIntBuffer();
		buffer.put(data).flip();
		
		return buffer;
	}
	
	public static int load(String name)
	{
		Vector2i dimensions = new Vector2i();
		IntBuffer buffer = loadTexturePixelData(name, dimensions);
		
		int result = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, result);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, dimensions.x, dimensions.y, 0, GL_RGBA,
				GL_UNSIGNED_BYTE, buffer);
		glGenerateMipmap(GL_TEXTURE_2D);
		
		glBindTexture(GL_TEXTURE_2D, 0);
		return result;
	}
	
	//refactor
	public static int loadSkyBox(String names[])
	{
		//todo check if names is of size 6.
		
		int id = GL30.glGenTextures();
		GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, id);
		
		for(int i = 0; i < 6; i++)
		{
			Vector2i dimensions = new Vector2i();
			IntBuffer buffer = loadTexturePixelData(names[i], dimensions);
			
			GL30.glTexImage2D(
					GL30.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
					0, GL30.GL_SRGB, dimensions.x, dimensions.y, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		}
		
		GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
		GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
		GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
		GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
		GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_R, GL30.GL_CLAMP_TO_EDGE);
		
		return id;
	}
	
	private final static Matrix4f captureProjection = new Matrix4f().perspective(GameMath.toRadians(90.0f),
			1.0f, 0.1f, 10.0f);
	
	private static Matrix4f captureViews[] =
			{
					new Matrix4f().lookAt(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f), new Vector3f(0.0f, -1.0f, 0.0f)),
					new Matrix4f().lookAt(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(-1.0f, 0.0f, 0.0f), new Vector3f(0.0f, -1.0f, 0.0f)),
					new Matrix4f().lookAt(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, 0.0f, 1.0f)),
					new Matrix4f().lookAt(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, -1.0f, 0.0f), new Vector3f(0.0f, 0.0f, -1.0f)),
					new Matrix4f().lookAt(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 1.0f), new Vector3f(0.0f, -1.0f, 0.0f)),
					new Matrix4f().lookAt(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, -1.0f), new Vector3f(0.0f, -1.0f, 0.0f))
			};
	
	
	public static void generateSkyBoxConvoluteTextures(SkyBox skyBox)
	{
		final int quality = 32;
		
		int captureFBO = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, captureFBO);
		
		glBindTexture(GL_TEXTURE_CUBE_MAP, skyBox.texture);
		glGenerateMipmap(GL_TEXTURE_CUBE_MAP);
		
		GL30.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glEnable(GL43.GL_TEXTURE_CUBE_MAP_SEAMLESS);
		
		int viewPort[] = new int[4];
		glGetIntegerv(GL_VIEWPORT, viewPort);
		
		skyBox.diffuseIrradianceMap = glGenTextures();
		glBindTexture(GL_TEXTURE_CUBE_MAP, skyBox.diffuseIrradianceMap);
		
		for(int i = 0; i < 6; i++)
		{
			GL43.glTexImage2D(GL30.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL30.GL_RGB16F, quality, quality, 0,
					GL30.GL_RGB, GL30.GL_FLOAT, 0);
		}
		
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		
		cubeMapConvoluteShader.bind();
		glBindVertexArray(vertexArray);
		
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_CUBE_MAP, skyBox.texture);
		glViewport(0, 0, quality, quality);
		
		for(int i = 0; i < 6; i++)
		{
			try(MemoryStack stack = MemoryStack.stackPush())
			{
				
				Matrix4f mat = new Matrix4f(captureProjection);
				
				FloatBuffer fb = (mat.mul(captureViews[i])).get(stack.mallocFloat(16));
				
				GL30.glUniformMatrix4fv(u_viewProjectionDiffuse, false,
						fb);
			}
			
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
					GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, skyBox.diffuseIrradianceMap, 0);
			
			glClear(GL_COLOR_BUFFER_BIT);
			
			glDrawArrays(GL_TRIANGLES, 0, 6 * 6); // renders a 1x1 cube //todo refactor to draw only a face
		}
		
		
		final int maxMipMap = 5;
		skyBox.speculatIrradianceMap = glGenTextures();
		glBindTexture(GL_TEXTURE_CUBE_MAP, skyBox.speculatIrradianceMap);
		for(int i = 0; i < 6; i++)
		{
			glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB16F,
					128,128, 0, GL_RGB, GL_FLOAT, 0);
			
		}
		
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAX_LEVEL, maxMipMap);
		glGenerateMipmap(GL_TEXTURE_CUBE_MAP);
		
		
		cubeMapSpecualrIBLShader.bind();
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_CUBE_MAP, skyBox.texture);
		
		for(int mip=0; mip<maxMipMap; mip++)
		{
			int mipWidth = (int)(128 * Math.pow(0.5, mip));
			int mipHeight = (int)(128 * Math.pow(0.5, mip));
			
			glViewport(0, 0, mipWidth, mipHeight);
			
			float roughness = (float)mip / (float)(maxMipMap - 1);
			roughness *= roughness;
			
			glUniform1f(u_roughness, roughness);
			
			for (int i = 0; i < 6; i++)
			{
				try(MemoryStack stack = MemoryStack.stackPush())
				{
					
					Matrix4f mat = new Matrix4f(captureProjection);
					
					FloatBuffer fb = (mat.mul(captureViews[i])).get(stack.mallocFloat(16));
					
					GL30.glUniformMatrix4fv(u_viewProjectionDiffuse, false,
							fb);
				}
				
				glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
						GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, skyBox.speculatIrradianceMap, mip);
				glClear(GL_COLOR_BUFFER_BIT);
				
				glDrawArrays(GL_TRIANGLES, 0, 6 * 6); // renders a 1x1 cube
			}
		}
		
		glBindTexture(GL_TEXTURE_CUBE_MAP, skyBox.texture);
		GL30.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		
		glViewport(viewPort[0], viewPort[1], viewPort[2], viewPort[3]);
		glBindVertexArray(0);
		glDisable(GL43.GL_TEXTURE_CUBE_MAP_SEAMLESS);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glDeleteFramebuffers(captureFBO);
	}
	
	
}
