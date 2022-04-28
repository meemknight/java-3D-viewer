package Logic;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import platform.GameManager;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;

import java.awt.*;
import java.util.List;
import java.util.stream.Stream;

public class GameLayer extends GameManager
{
	
	Entity entity = new Entity();
	
	Camera camera = new Camera();
	GyzmosRenderer gyzmosRenderer = new GyzmosRenderer();
	Renderer renderer = new Renderer();
	
	Texture lightBulb = new Texture();
	Texture spotLight = new Texture();
	SkyBox skyBox = new SkyBox();
	
	Material metalMaterial = new Material();
	
	public void gameInit()
	{
		List<PointLight> pl = Serializer.load("resources/pointLights.csv", PointLight.class);
		Serializer.save(pl,"resources/pointLight2.csv", PointLight.class);
		
		GL30.glEnable(GL_CULL_FACE);
		
		TextureLoader.init();
		
		gyzmosRenderer.init();
		renderer.init();
		
		try
		{
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
		
		String names[] =
			{	"resources/skyBoxes/ocean/right.jpg",
					"resources/skyBoxes/ocean/left.jpg",
					"resources/skyBoxes/ocean/top.jpg",
					"resources/skyBoxes/ocean/bottom.jpg",
					"resources/skyBoxes/ocean/front.jpg",
					"resources/skyBoxes/ocean/back.jpg" };
		
		skyBox.texture = TextureLoader.loadSkyBox(names);
		TextureLoader.generateSkyBoxConvoluteTextures(skyBox);
		
		
		renderer.lightManager.addLight(new PointLight(5, 1, 0, 1, 0, 0));
		renderer.lightManager.addLight(new PointLight(-4, 4, 1, 0, 0, 1));
		
		//directionalLightArray.add(new Logic.DirectionalLight(-1, -1, 0, 0.2f, 0.2f, 0.2f));
		
		renderer.lightManager.addLight(new SpotLight(-1,-1,0, 3,3,0, 1,1,1,
				GameMath.toRadians(15.f)));
		

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
		
		
		//Model model = new Model();
		//model.loadFromComputedData(cubePositionsNormals, cubeIndices);
		//model.material = metalMaterial;
		//entity.models.add(model);
		
		entity = ModelLoader.loadEntity("resources/helmet/helmet.obj");
		//entity = ModelLoader.loadEntity("resources/barrel/Barrel_01.obj");
		//entity.models.get(0).material = metalMaterial;
		
		entity.transform.rotation = entity.transform.rotation.rotateAxis(3.141592f * 0.5f , new Vector3f(1,0,0));
		
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
		
		
		renderer.renderEntity(entity, camera, skyBox);
		
		//rotate lights around center
		renderer.lightManager.pointLights.stream().limit(2).forEach(l ->
		{
			float c = (float)Math.cos(3.1415926f * 0.5f * getDeltaTime());
			float s = (float)Math.sin(3.1415926f * 0.5f * getDeltaTime());
			
			float newX = l.positionX * c - l.positionZ * s;
			float newZ = l.positionX * s + l.positionZ * c;
			
			l.positionX = newX;
			l.positionZ = newZ;
		});
	
		
		for(var i : renderer.lightManager.pointLights)
		{
			gyzmosRenderer.render(i.positionX,i.positionY,i.positionZ, lightBulb,
					i.colorR, i.colorG, i.colorB);
		}
		
		for(var i : renderer.lightManager.spotLights)
		{
			gyzmosRenderer.render(i.positionX,i.positionY,i.positionZ, spotLight,
					i.colorR, i.colorG, i.colorB);
		}
		
		gyzmosRenderer.flush(camera);
		
		if(isKeyReleased(GLFW_KEY_P)){toggle = !toggle;}
		
		if(toggle)
		{
			renderer.renderSkyBox(camera, skyBox);
		}else
		{
			box2.texture = skyBox.speculatIrradianceMap;
			renderer.renderSkyBox(camera, box2);
		}
		
	}

	static boolean toggle = true;
	static SkyBox box2 = new SkyBox();
	
	public void gameClose()
	{
	
	
	}
	
	
}
