import org.lwjgl.opengl.GL43;

public class MainShader extends Shader
{
	
	public int u_viewProjection;
	public int u_pointLightsCount;
	public int u_directionalLightsCount;
	public int u_spotLightsCount;
	public int u_pointLights;
	public int u_directionalLights;
	public int u_spotLights;
	public int u_model;
	public int u_eye;
	
	public void init()
	{
		try
		{
			super.loadShaderFromFile("resources//vert.vert", "resources//frag.frag");
			u_viewProjection = super.getUniformLocation("u_viewProjection");
			u_pointLightsCount = super.getUniformLocation("u_pointLightsCount");
			u_model = super.getUniformLocation("u_model");
			u_directionalLightsCount = super.getUniformLocation("u_directionalLightsCount");
			u_spotLightsCount = super.getUniformLocation("u_spotLightsCount");
			u_eye = super.getUniformLocation("u_eye");
			
			//set the storage block binding for lights
			u_pointLights = super.getStorageBLockIndex("u_pointLights");
			GL43.glShaderStorageBlockBinding(id, u_pointLights, StorageBLockBindings.pointLight);
			
			u_directionalLights = super.getStorageBLockIndex("u_directionalLights");
			GL43.glShaderStorageBlockBinding(id, u_directionalLights, StorageBLockBindings.directionalLight);
			
			u_spotLights = super.getStorageBLockIndex("u_spotLights");
			GL43.glShaderStorageBlockBinding(id, u_spotLights, StorageBLockBindings.spotLight);
			
		}
		catch(Exception e)
		{
			System.out.println("shader error" + e);
		}
		
	}


}
