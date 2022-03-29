
import static org.lwjgl.opengl.GL30.*;

//a sky box is an omnidirectional texture that represents the sky and or environment.
public class SkyBox
{
	//the sky texture
	//https://en.wikipedia.org/wiki/Skybox_(video_games)
	public int texture;
	
	//used for IBL
	// https://learnopengl.com/PBR/IBL/Diffuse-irradiance
	public int diffuseIrradianceMap;

	//used for IBL
	//https://learnopengl.com/PBR/IBL/Specular-IBL
	public int speculatIrradianceMap;
	
}
