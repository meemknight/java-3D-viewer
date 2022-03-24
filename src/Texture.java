
import org.lwjgl.opengl.*;

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
	
	Texture load(String name)
	{
		id = TextureLoader.load(name);
		return  this;
	}

}
