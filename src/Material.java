


public class Material
{
	
	public Material()
	{
	}
	
	public Material(float colorR, float colorG, float colorB, Texture albedoTexture, Texture normalTexture)
	{
		this.colorR = colorR;
		this.colorG = colorG;
		this.colorB = colorB;
		this.albedoTexture = albedoTexture;
		this.normalTexture = normalTexture;
	}
	
	public float colorR=1, colorG=1, colorB=1;
	
	public Texture albedoTexture;
	public Texture normalTexture;

}
