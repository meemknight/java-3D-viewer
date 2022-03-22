import org.lwjgl.opengl.GL43;


import java.util.List;

//saves light data on the gpu
public class LightManager
{
	
	public int pointLightBlockBuffer = 0;
	public void init()
	{
		pointLightBlockBuffer = GL43.glGenBuffers();
		
	};

	public void sendDataToGpu(List<PointLight> pointLights, int u_pointLightsCount)
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
		
		GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, pointLightBlockBuffer);
		GL43.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, pointLightsRawData, GL43.GL_STREAM_DRAW);
		GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, StorageBLockBindings.pointLight, pointLightBlockBuffer);
		
		GL43.glUniform1i(u_pointLightsCount, pointLights.size());
		
	}
	
}
