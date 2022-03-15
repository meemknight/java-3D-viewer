
import org.lwjgl.opengl.*;
import org.newdawn.slick.opengl.TextureLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public class Texture
{
	
	public int id;
	
	//https://www.youtube.com/watch?v=SPt-aogu72A&list=PLRIWtICgwaX0u7Rf9zkZhLoLuZVfUksDP&index=6
	void load(String name)// throws IOException
	{
		//org.newdawn.slick.opengl.Texture t = TextureLoader.getTexture("PNG", new FileInputStream(name));
		//id = t.getTextureID();
		
		int[] pixels = null;
		int width = 0;
		int height = 0;
		
		try {
			BufferedImage image = ImageIO.read(new FileInputStream(name));
			width = image.getWidth();
			height = image.getHeight();
			pixels = new int[width * height];
			image.getRGB(0, 0, width, height, pixels, 0, width);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int[] data = new int[width * height];
		for (int i = 0; i < width * height; i++) {
			int a = (pixels[i] & 0xff000000) >> 24;
			int r = (pixels[i] & 0xff0000) >> 16;
			int g = (pixels[i] & 0xff00) >> 8;
			int b = (pixels[i] & 0xff);
			
			data[i] = a << 24 | b << 16 | g << 8 | r;
		}
		
		int result = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, result);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		
		IntBuffer buffer = ByteBuffer.allocateDirect(data.length << 2)
				.order(ByteOrder.nativeOrder()).asIntBuffer();
		buffer.put(data).flip();
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA,
				GL_UNSIGNED_BYTE, buffer);
		glBindTexture(GL_TEXTURE_2D, 0);
		
		id = result;
	}

}
