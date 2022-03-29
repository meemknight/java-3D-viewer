import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;

//this holds the necessary configurations to render an object
public class Renderer
{
	MainShader shader = new MainShader();
	SkyBoxRenderer skyBoxRenderer = new SkyBoxRenderer();
	Texture brdfTexture = new Texture();
	LightManager lightManager = new LightManager();
	
	public void init()
	{
		lightManager.init();
		shader.init();
		skyBoxRenderer.init();
		
		try
		{
			brdfTexture.load("resources//BRDFintegrationMap.png");
			
		}catch(Exception e)
		{
			System.out.println("Error, couldn't find brdf texture  \n" + e);
		}
	
	}
	
	public void renderEntity(Entity e, Camera camera, SkyBox skyBox,
							ArrayList<PointLight> pointLightArray,
							ArrayList<DirectionalLight> directionalLightArray,
							ArrayList<SpotLight> spotLightArray
							)
	{
		
		shader.bind();
		
		GL30.glUniform3f(shader.u_eye, camera.position.x, camera.position.y,camera.position.z);
		
		lightManager.sendDataToGpu(pointLightArray, directionalLightArray, spotLightArray,
				shader.u_pointLightsCount, shader.u_directionalLightsCount, shader.u_spotLightsCount);
		
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fb = camera.getViewProjectionMatrix().get(stack.mallocFloat(16));
			GL30.glUniformMatrix4fv(shader.u_viewProjection, false,
					fb);
			
			//this will be model matrix
			fb = e.transform.getModelMatrix().get(stack.mallocFloat(16));
			GL30.glUniformMatrix4fv(shader.u_model, false,
					fb);
		}
		
		GL30.glActiveTexture(GL30.GL_TEXTURE5);
		GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, skyBox.diffuseIrradianceMap);
		GL30.glActiveTexture(GL30.GL_TEXTURE6);
		GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, skyBox.speculatIrradianceMap);
		GL30.glActiveTexture(GL30.GL_TEXTURE7);
		GL30.glBindTexture(GL30.GL_TEXTURE_2D, brdfTexture.id);
		
		for(var model: e.models)
		{
			GL30.glBindVertexArray(model.vao);
			
			GL30.glActiveTexture(GL30.GL_TEXTURE0);
			GL30.glBindTexture(GL_TEXTURE_2D, model.material.albedoTexture.id);
			GL30.glActiveTexture(GL30.GL_TEXTURE1);
			GL30.glBindTexture(GL_TEXTURE_2D, model.material.normalTexture.id);
			GL30.glActiveTexture(GL30.GL_TEXTURE2);
			GL30.glBindTexture(GL_TEXTURE_2D, model.material.aoTexture.id);
			GL30.glActiveTexture(GL30.GL_TEXTURE3);
			GL30.glBindTexture(GL_TEXTURE_2D, model.material.metallicTexture.id);
			GL30.glActiveTexture(GL30.GL_TEXTURE4);
			GL30.glBindTexture(GL_TEXTURE_2D, model.material.roughnessTexture.id);
			
			GL30.glDrawElements(GL30.GL_TRIANGLES, model.vertexCount, GL_UNSIGNED_INT, 0);
			
		}
		
		
		GL30.glBindVertexArray(0);
	}
	
	//todo implement flush operation.
	
	public void renderSkyBox(Camera camera, SkyBox skyBox)
	{
		skyBoxRenderer.render(camera, skyBox);
	}
	
	
	
}
