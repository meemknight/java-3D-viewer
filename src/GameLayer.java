import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.FloatBuffer;

public class GameLayer extends GameManager
{
	float pos = 0;
	int vertexBuffer = 0;
	int indexBuffer = 0;
	Shader shader = new Shader();
	Camera camera = new Camera();
	int u_viewProjection;
	int vao;
	Texture t = new Texture();
	
	public void gameInit()
	{
		
		try
		{
			shader.loadShaderFromFile("resources//vert.vert", "resources//frag.frag");
			u_viewProjection = GL30.glGetUniformLocation(shader.id, "u_viewProjection");
			t.load("resources//dog.png");
		}
		catch(Exception e){
			System.out.println("shader error" + e);
		}
		
		shader.bind();
		
		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		
		float bufferData[] = {0.f, 1.f, -1.f, -1.f, 1.f, -1.f};

		float cubePositionsNormals[] = {
				-1.0f, +1.0f, +1.0f, // 0
				+0.0f, +1.0f, +0.0f, // Normal
				0, 0,				 //uv
				
				+1.0f, +1.0f, +1.0f, // 1
				+0.0f, +1.0f, +0.0f, // Normal
				1, 0,				 //uv
				
				+1.0f, +1.0f, -1.0f, // 2
				+0.0f, +1.0f, +0.0f, // Normal
				1, 1,				 //uv
				
				-1.0f, +1.0f, -1.0f, // 3
				+0.0f, +1.0f, +0.0f, // Normal
				0, 1,				 //uv
				
				-1.0f, +1.0f, -1.0f, // 4
				0.0f, +0.0f, -1.0f, // Normal
				0, 1,				 //uv
				
				+1.0f, +1.0f, -1.0f, // 5
				0.0f, +0.0f, -1.0f, // Normal
				1, 1,				 //uv
				
				+1.0f, -1.0f, -1.0f, // 6
				0.0f, +0.0f, -1.0f, // Normal
				1, 0,				 //uv
				
				-1.0f, -1.0f, -1.0f, // 7
				0.0f, +0.0f, -1.0f, // Normal
				0, 0,				 //uv
				
				+1.0f, +1.0f, -1.0f, // 8
				+1.0f, +0.0f, +0.0f, // Normal
				1, 0,				 //uv
				
				+1.0f, +1.0f, +1.0f, // 9
				+1.0f, +0.0f, +0.0f, // Normal
				1, 1,				 //uv
				
				+1.0f, -1.0f, +1.0f, // 10
				+1.0f, +0.0f, +0.0f, // Normal
				0, 1,				 //uv
				
				+1.0f, -1.0f, -1.0f, // 11
				+1.0f, +0.0f, +0.0f, // Normal
				0, 0,				 //uv
				
				-1.0f, +1.0f, +1.0f, // 12
				-1.0f, +0.0f, +0.0f, // Normal
				1, 1,				 //uv
				
				-1.0f, +1.0f, -1.0f, // 13
				-1.0f, +0.0f, +0.0f, // Normal
				1, 0,				 //uv
				
				-1.0f, -1.0f, -1.0f, // 14
				-1.0f, +0.0f, +0.0f, // Normal
				0, 0,				 //uv
				
				-1.0f, -1.0f, +1.0f, // 15
				-1.0f, +0.0f, +0.0f, // Normal
				0, 1,				 //uv
				
				+1.0f, +1.0f, +1.0f, // 16
				+0.0f, +0.0f, +1.0f, // Normal
				1, 1,				 //uv
				
				-1.0f, +1.0f, +1.0f, // 17
				+0.0f, +0.0f, +1.0f, // Normal
				0, 1,				 //uv
				
				-1.0f, -1.0f, +1.0f, // 18
				+0.0f, +0.0f, +1.0f, // Normal
				0, 0,				 //uv
				
				+1.0f, -1.0f, +1.0f, // 19
				+0.0f, +0.0f, +1.0f, // Normal
				1, 0,				 //uv
				
				+1.0f, -1.0f, -1.0f, // 20
				+0.0f, -1.0f, +0.0f, // Normal
				1, 0,				 //uv
				
				-1.0f, -1.0f, -1.0f, // 21
				+0.0f, -1.0f, +0.0f, // Normal
				0, 0,				 //uv
				
				-1.0f, -1.0f, +1.0f, // 22
				+0.0f, -1.0f, +0.0f, // Normal
				0, 1,				 //uv
				
				+1.0f, -1.0f, +1.0f, // 23
				+0.0f, -1.0f, +0.0f, // Normal
				1, 1,				 //uv
		};
		
		int cubeIndices[] = {
			0,   1,  2,  0,  2,  3, // Top
			4,   5,  6,  4,  6,  7, // Back
			8,   9, 10,  8, 10, 11, // Right
			12, 13, 14, 12, 14, 15, // Left
			16, 17, 18, 16, 18, 19, // Front
			20, 22, 21, 20, 23, 22, // Bottom
		};
		
		vertexBuffer = GL30.glGenBuffers();
		GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vertexBuffer);
		GL30.glBufferData(GL30.GL_ARRAY_BUFFER, cubePositionsNormals, GL30.GL_STATIC_DRAW);
		
		GL30.glEnableVertexAttribArray(0);
		GL30.glVertexAttribPointer(0, 3, GL13.GL_FLOAT, false, 4*8, 0);
		GL30.glEnableVertexAttribArray(1);
		GL30.glVertexAttribPointer(1, 3, GL13.GL_FLOAT, false, 4*8, 4*3);
		GL30.glEnableVertexAttribArray(2);
		GL30.glVertexAttribPointer(2, 2, GL13.GL_FLOAT, false, 4*8, 4*6);
		
		indexBuffer = GL30.glGenBuffers();
		GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, cubeIndices, GL30.GL_STATIC_DRAW);
		
		GL30.glBindVertexArray(0);
		
	}
	
	float rotation = 0.f;
	
	float lastMouseX = getMousePosX();
	float lastMouseY = getMousePosY();
	
	public void gameUpdate()
	{
		int w = getWindowW();
		int h = getWindowH();

		camera.updateAspectRation(w, h);
		glViewport(0, 0, w, h);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
		
		//input
		{
			Vector3f move = new Vector3f(0,0,0);
			float speed = 10 * getDeltaTime();
			
			if(isKeyHeld(GLFW_KEY_W))
			{
				move.z -= speed;
			}
			if(isKeyHeld(GLFW_KEY_S))
			{
				move.z += speed;
			}
			
			if(isKeyHeld(GLFW_KEY_A))
			{
				move.x -= speed;
			}
			if(isKeyHeld(GLFW_KEY_D))
			{
				move.x += speed;
			}
			
			if(isKeyHeld(GLFW_KEY_Q))
			{
				move.y -= speed;
			}
			if(isKeyHeld(GLFW_KEY_E))
			{
				move.y += speed;
			}
			
			if(isRightMouseButtonHeld())
			{
				Vector2f delta = new Vector2f(getMousePosX(), getMousePosY());
				delta.x -= lastMouseX;
				delta.y -= lastMouseY;
				delta.mul(getDeltaTime());
				camera.rotateCamera(delta);
			}

			lastMouseX = getMousePosX();
			lastMouseY = getMousePosY();
			
			camera.moveFPS(move);
			
		}
		
		
		GL30.glBindVertexArray(vao);
		
		shader.bind();
		try (MemoryStack stack = MemoryStack.stackPush()) {
			
			FloatBuffer fb = camera.getViewProjectionMatrix().get(stack.mallocFloat(16));
			//Vector3f rotate = new Vector3f(0.0f, 0.0f, 1.f);
			//FloatBuffer fb = new Matrix4f().rotate(rotation * (float)Math.PI, rotate).get(stack.mallocFloat(16));
			
			rotation += getDeltaTime();
			
			GL30.glUniformMatrix4fv(u_viewProjection, false,
					fb);
		}
		
		GL30.glActiveTexture(GL30.GL_TEXTURE0);
		GL30.glBindTexture(GL_TEXTURE_2D, t.id);
		
		GL30.glDrawElements(GL30.GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0);
		
		GL30.glBindVertexArray(0);
		
		
	}
	
	public void gameClose()
	{
	
	
	}
	
	
}
