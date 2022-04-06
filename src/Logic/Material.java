package Logic;

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
	
	//color of the object
	public float colorR=1, colorG=1, colorB=1;
	
	//the color texture of the object
	public Texture albedoTexture;
	
	//this texture is used to add detail to geometry
	public Texture normalTexture;
	
	//this texture adds small shadows created by small features in the geometry
	public Texture aoTexture;
	
	//this textures tells how metallic the surface is
	public Texture metallicTexture;
	
	//this texture tells how rough the surface is
	public Texture roughnessTexture;
	
}
