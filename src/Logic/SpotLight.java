package Logic;

public class SpotLight extends Light
{
	
	public SpotLight()
	{
		super();
	}
	
	public SpotLight(float directionX, float directionY, float directionZ,
					 float positionX, float positionY, float positionZ,
					 float colorR, float colorG, float colorB,
					 float angleRadians)
	{
		super(colorR, colorG, colorB);
		this.directionX = directionX;
		this.directionY = directionY;
		this.directionZ = directionZ;
		
		this.positionX = positionX;
		this.positionY = positionY;
		this.positionZ = positionZ;
		
		this.angleCos = (float)Math.cos(angleRadians);
	}
	
	public float directionX = 0;
	public float directionY = 0;
	public float directionZ = 0;
	
	public float positionX = 0;
	public float positionY = 0;
	public float positionZ = 0;
	
	public float angleCos = 0;
	
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
			float inverseSrt = GameMath.getInstance().inverseSqrt(magnitude);
			directionX *= inverseSrt;
			directionY *= inverseSrt;
			directionZ *= inverseSrt;
		}
	}
	
	
}
