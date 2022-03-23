import org.lwjgl.opengl.GL43;


import java.util.List;

//saves light data on the gpu
public class LightManager
{
	
	private int pointLightBlockBuffer = 0;
	private int directionalLightBlockBuffer = 0;

	public void init()
	{
		pointLightBlockBuffer = GL43.glGenBuffers();
		directionalLightBlockBuffer = GL43.glGenBuffers();
		
	};

	public void sendDataToGpu(List<PointLight> pointLights,
							  List<DirectionalLight> directionalLights,
							  int u_pointLightsCount,
							  int u_directionalLightsCount
							)
	{
		float pointLightsRawData[] = new float[pointLights.size() * 8];
		for(int i=0; i<pointLights.size(); i++)
		{
			pointLightsRawData[8 * i + 0] = pointLights.get(i).positionX;
			pointLightsRawData[8 * i + 1] = pointLights.get(i).positionY;
			pointLightsRawData[8 * i + 2] = pointLights.get(i).positionZ;
			pointLightsRawData[8 * i + 3] = 0.f;
			
			pointLightsRawData[8 * i + 4] = pointLights.get(i).colorR;
			pointLightsRawData[8 * i + 5] = pointLights.get(i).colorG;
			pointLightsRawData[8 * i + 6] = pointLights.get(i).colorB;
			pointLightsRawData[8 * i + 7] = 0.f;
		}
		
		float directionalLightsRawData[] = new float[directionalLights.size() * 8];
		for(int i=0; i<directionalLights.size(); i++)
		{
			directionalLights.get(i).normalizeData();

			directionalLightsRawData[8 * i + 0] = directionalLights.get(i).directionX;
			directionalLightsRawData[8 * i + 1] = directionalLights.get(i).directionY;
			directionalLightsRawData[8 * i + 2] = directionalLights.get(i).directionZ;
			directionalLightsRawData[8 * i + 3] = 0.f; //not used
			
			directionalLightsRawData[8 * i + 4] = directionalLights.get(i).colorR;
			directionalLightsRawData[8 * i + 5] = directionalLights.get(i).colorG;
			directionalLightsRawData[8 * i + 6] = directionalLights.get(i).colorB;
			directionalLightsRawData[8 * i + 7] = 0.f; //not used
		}
		
		GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, pointLightBlockBuffer);
		GL43.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, pointLightsRawData, GL43.GL_STREAM_DRAW);
		GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, StorageBLockBindings.pointLight, pointLightBlockBuffer);
		GL43.glUniform1i(u_pointLightsCount, pointLights.size());
		
		GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, directionalLightBlockBuffer);
		GL43.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, directionalLightsRawData, GL43.GL_STREAM_DRAW);
		GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, StorageBLockBindings.directionalLight, directionalLightBlockBuffer);
		GL43.glUniform1i(u_directionalLightsCount, directionalLights.size());
		
		
		
	}
	
}
