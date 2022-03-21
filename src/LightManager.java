import org.lwjgl.opengl.GL43;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
			pointLightsRawData[i + 0] = pointLights.get(i).positionX;
			pointLightsRawData[i + 1] = pointLights.get(i).positionY;
			pointLightsRawData[i + 2] = pointLights.get(i).positionZ;
			pointLightsRawData[i + 3] = 0.f;
			
			pointLightsRawData[i + 4] = pointLights.get(i).colorR;
			pointLightsRawData[i + 5] = pointLights.get(i).colorG;
			pointLightsRawData[i + 6] = pointLights.get(i).colorB;
			pointLightsRawData[i + 7] = 0.f;
		}
		
		GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, pointLightBlockBuffer);
		GL43.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, pointLightsRawData, GL43.GL_STREAM_DRAW);
		GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, StorageBLockBindings.pointLight, pointLightBlockBuffer);
		
		GL43.glUniform1i(u_pointLightsCount, pointLights.size());
		
	}
	
}
