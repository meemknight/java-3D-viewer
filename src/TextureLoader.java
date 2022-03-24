import org.joml.Vector2i;
import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class TextureLoader
{
	
	//https://www.youtube.com/watch?v=SPt-aogu72A&list=PLRIWtICgwaX0u7Rf9zkZhLoLuZVfUksDP&index=6
	public static IntBuffer loadTexturePixelData(String name, Vector2i dimensions)
	{
		int[] pixels = null;
		
		try {
			BufferedImage image = ImageIO.read(new FileInputStream(name));
			dimensions.x = image.getWidth();
			dimensions.y = image.getHeight();
			pixels = new int[dimensions.x * dimensions.y];
			image.getRGB(0, 0, dimensions.x, dimensions.y, pixels, 0, dimensions.x);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int[] data = new int[dimensions.x * dimensions.y];
		for (int i = 0; i < dimensions.x * dimensions.y; i++) {
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

	public static int loadSkyBox(String names[])
	{
		
		//todo check if names is of size 6.
		
		int id = GL30.glGenTextures();
		GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, id);
		
		for(int i=0; i<6; i++)
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
	

}
