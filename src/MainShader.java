import org.lwjgl.opengl.GL43;

public class MainShader extends Shader
{
	
	int u_viewProjection;
	int u_pointLightsCount;
	int u_directionalLightsCount;
	int u_pointLights;
	int u_directionalLights;
	int u_model;
	
	public void init()
	{
		try
		{
			super.loadShaderFromFile("resources//vert.vert", "resources//frag.frag");
			u_viewProjection = super.getUniformLocation("u_viewProjection");
			u_pointLightsCount = super.getUniformLocation("u_pointLightsCount");
			u_model = super.getUniformLocation("u_model");
			u_directionalLightsCount = super.getUniformLocation("u_directionalLightsCount");
			
			//set the storage block binding for lights
			u_pointLights = super.getStorageBLockIndex("u_pointLights");
			GL43.glShaderStorageBlockBinding(super.id, u_pointLights, StorageBLockBindings.pointLight);
			
			u_directionalLights = super.getStorageBLockIndex("u_directionalLights");
			GL43.glShaderStorageBlockBinding(super.id, u_directionalLights, StorageBLockBindings.directionalLight);
			
		}
		catch(Exception e)
		{
			System.out.println("shader error" + e);
		}
		
	}


}
