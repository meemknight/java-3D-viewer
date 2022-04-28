package Logic;
import org.lwjgl.opengl.GL30;

public class Sampler
{
	String textureName;								//the texture name on the disk
	int minSampler = GL30.GL_LINEAR_MIPMAP_NEAREST; //how to sample the texture when minimizing
	int maxSampler = GL30.GL_LINEAR;				//hot to sample the texture when maximizing
}
