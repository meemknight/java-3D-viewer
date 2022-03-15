import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Shader
{
	
	int id = 0;
	
	public void bind()
	{
		GL30.glUseProgram(id);
	}
	
	String readEntireFile(String file) throws IOException
	{
		return new String(Files.readAllBytes(Paths.get(file)));
	}
	
	private int loadShaderComponent(int type, String text)
	{
		int id = GL30.glCreateShader(type);
		GL30.glShaderSource(id, text);
		GL30.glCompileShader(id);
		
		if(GL30.glGetShaderi(id, GL30.GL_COMPILE_STATUS) == 0)
		{
			int messageSize[] = new int[1];
			messageSize[0] = GL30.glGetShaderi(id, GL30.GL_INFO_LOG_LENGTH);
			System.out.println(GL30.glGetShaderInfoLog(id, messageSize[0]));
			return 0;
		}
		
		return id;
	}
	
	public void loadShaderFromFile(String vertexShader, String fragmentShader) throws IOException
	{
		loadShaderFromMemory(readEntireFile(vertexShader), readEntireFile(fragmentShader));
	}
	
	public void loadShaderFromMemory(String vertexShader, String fragmentShader)
	{
		int vs = loadShaderComponent(GL30.GL_VERTEX_SHADER, vertexShader);
		int fs = loadShaderComponent(GL30.GL_FRAGMENT_SHADER, fragmentShader);
		
		id = GL30.glCreateProgram();
		
		GL30.glAttachShader(id, vs);
		GL30.glAttachShader(id, fs);
		
		GL30.glLinkProgram(id);
		
		if(GL30.glGetProgrami(id, GL30.GL_LINK_STATUS) == 0)
		{
			int messageSize[] = new int[1];
			messageSize[0] = GL30.glGetProgrami(id, GL30.GL_INFO_LOG_LENGTH);
			System.out.println(GL30.glGetProgramInfoLog(id, messageSize[0]));
			GL30.glDeleteProgram(id);
			GL30.glDeleteShader(vs);
			GL30.glDeleteShader(fs);
			id = 0;
			return;
		}
		
		GL30.glValidateProgram(id);
		
		GL30.glDeleteShader(vs);
		GL30.glDeleteShader(fs);
		
	}
	
}
