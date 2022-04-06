package Logic;

import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

//a shader is a small program on the gpu that tells the pipeline how to draw the triangles.
//they are loaded from resources/*.vert, resources/*.frag .
public class Shader
{
	
	public int id = 0;
	
	public void bind()
	{
		GL30.glUseProgram(id);
	}
	
	private String readEntireFile(String file) throws IOException
	{
		return new String(Files.readAllBytes(Paths.get(file)));
	}
	
	public int getUniformLocation(String uniform)
	{
		int rez = GL30.glGetUniformLocation(id, uniform);
		if(rez == -1)
		{
			System.out.println("Invalid uniform name: " + uniform);
		}
		return rez;
	}
	
	public int getStorageBLockIndex(String block)
	{
		int rez = GL43.glGetProgramResourceIndex(id, GL43.GL_SHADER_STORAGE_BLOCK, block);
		if(rez == GL43.GL_INVALID_INDEX)
		{
			System.out.println("Invalid storage block name: " + block);
		}
		return rez;
	}
	
	private int loadShaderComponent(int type, String text)
	{
		//create shader compile it and check for compile errors
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
		//shaders are made from one or more files(in my case 2 files) that have to be loaded, compiled and eventually linked
		int vs = loadShaderComponent(GL30.GL_VERTEX_SHADER, vertexShader);
		int fs = loadShaderComponent(GL30.GL_FRAGMENT_SHADER, fragmentShader);
		
		id = GL30.glCreateProgram();
		
		//add the 2 compiled shaders
		GL30.glAttachShader(id, vs);
		GL30.glAttachShader(id, fs);
		
		//link the shader program and then check for linking errors
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
