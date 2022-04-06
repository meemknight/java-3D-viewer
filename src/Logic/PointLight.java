package Logic;


public class PointLight extends Light
{
	public PointLight()
	{
		super();
	}
	
	public PointLight(float posX, float posY, float posZ, float colorR, float colorG, float colorB)
	{
		super(colorR, colorG, colorB);
		this.positionX = posX;
		this.positionY = posY;
		this.positionZ = posZ;
	}
	
	public float positionX = 0.f;
	public float positionY = 0.f;
	public float positionZ = 0.f;
	
}
