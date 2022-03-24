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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameLayer extends GameManager
{
	float pos = 0;
	int vertexBuffer = 0;
	int indexBuffer = 0;
	MainShader shader = new MainShader();
	Camera camera = new Camera();
	GyzmosRenderer gyzmosRenderer = new GyzmosRenderer();
	int vao;
	Texture t = new Texture();
	Texture lightBulb = new Texture();
	Texture spotLight = new Texture();
	
	ArrayList<PointLight> pointLightArray = new ArrayList<PointLight>();
	ArrayList<DirectionalLight> directionalLightArray = new ArrayList<DirectionalLight>();
	ArrayList<SpotLight> spotLightArray = new ArrayList<SpotLight>();
	LightManager lightManager = new LightManager();
	
	Material metalMaterial = new Material();
	
	public void gameInit()
	{
		//GL43.glEnable(GL_CULL_FACE);
		
		lightManager.init();
		shader.init();
		gyzmosRenderer.init();
		
		try
		{
			t.load("resources//dog.png");
			lightBulb.load("resources//light.png");
			spotLight.load("resources//spotLight.png");
			
			metalMaterial.albedoTexture = new Texture().load("resources//rusted_iron//albedo.png");
			metalMaterial.normalTexture = new Texture().load("resources//rusted_iron//normal.png");
			metalMaterial.aoTexture = new Texture().load("resources//rusted_iron//ao.png");
			metalMaterial.metallicTexture = new Texture().load("resources//rusted_iron//metallic.png");
			metalMaterial.roughnessTexture = new Texture().load("resources//rusted_iron//roughness.png");
			
		}
		catch(Exception e){
			System.out.println("texture loading error" + e);
		}
		
		pointLightArray.add(new PointLight(5, 1, 0, 1, 0, 0));
		pointLightArray.add(new PointLight(-4, 4, 1, 0, 0, 1));
		
		//directionalLightArray.add(new DirectionalLight(-1, -1, 0, 0.2f, 0.2f, 0.2f));
		
		spotLightArray.add(new SpotLight(-1,-1,0, 3,3,0, 1,1,1,
				GameMath.toRadians(15.f)));
		
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
		
		GL30.glUniform3f(shader.u_eye, camera.position.x, camera.position.y,camera.position.z);
		
		lightManager.sendDataToGpu(pointLightArray, directionalLightArray, spotLightArray,
				shader.u_pointLightsCount, shader.u_directionalLightsCount, shader.u_spotLightsCount);
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			
			FloatBuffer fb = camera.getViewProjectionMatrix().get(stack.mallocFloat(16));
			GL30.glUniformMatrix4fv(shader.u_viewProjection, false,
					fb);
			
			//this will be model matrix
			fb = new Matrix4f().identity().get(stack.mallocFloat(16));
			GL30.glUniformMatrix4fv(shader.u_model, false,
					fb);

		}
		
		GL30.glActiveTexture(GL30.GL_TEXTURE0);
		GL30.glBindTexture(GL_TEXTURE_2D, metalMaterial.albedoTexture.id);
		GL30.glActiveTexture(GL30.GL_TEXTURE1);
		GL30.glBindTexture(GL_TEXTURE_2D, metalMaterial.normalTexture.id);
		GL30.glActiveTexture(GL30.GL_TEXTURE2);
		GL30.glBindTexture(GL_TEXTURE_2D, metalMaterial.aoTexture.id);
		GL30.glActiveTexture(GL30.GL_TEXTURE3);
		GL30.glBindTexture(GL_TEXTURE_2D, metalMaterial.metallicTexture.id);
		GL30.glActiveTexture(GL30.GL_TEXTURE4);
		GL30.glBindTexture(GL_TEXTURE_2D, metalMaterial.roughnessTexture.id);
		
		
		GL30.glDrawElements(GL30.GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0);
		
		GL30.glBindVertexArray(0);
		
		for(var l : pointLightArray)
		{
			float c = (float)Math.cos(3.1415926f * 0.5f * getDeltaTime());
			float s = (float)Math.sin(3.1415926f * 0.5f * getDeltaTime());
			
			float newX = l.positionX * c - l.positionZ * s;
			float newZ = l.positionX * s + l.positionZ * c;
			
			l.positionX = newX;
			l.positionZ = newZ;
		}
		
		for(var i : pointLightArray)
		{
			gyzmosRenderer.render(i.positionX,i.positionY,i.positionZ, lightBulb,
					i.colorR, i.colorG, i.colorB);
		}
		
		for(var i : spotLightArray)
		{
			gyzmosRenderer.render(i.positionX,i.positionY,i.positionZ, spotLight,
					i.colorR, i.colorG, i.colorB);
		}
		
		gyzmosRenderer.flush(camera);
		
	}
	
	public void gameClose()
	{
	
	
	}
	
	
}
