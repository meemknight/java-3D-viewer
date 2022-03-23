public class DirectionalLight extends Light
{
	public DirectionalLight()
	{
		super();
	}
	
	public DirectionalLight(float directionX, float directionY, float directionZ,  float colorR, float colorG, float colorB)
	{
		super(colorR, colorG, colorB);
		this.directionX = directionX;
		this.directionY = directionY;
		this.directionZ = directionZ;
	}
	
	public float directionX = 0;
	public float directionY = 0;
	public float directionZ = 0;
	
	void normalizeData()
	{
		float magnitude = directionX * directionX + directionY * directionY + directionZ * directionZ;
		if(magnitude == 0)
		{
			directionX = 0;
			directionY = -1;
			directionZ = 0;
		}else
		{
			float inverseSrt = GameMath.inverseSqrt(magnitude);
			directionX *= inverseSrt;
			directionY *= inverseSrt;
			directionZ *= inverseSrt;
		}
	}
}
