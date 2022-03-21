
public class DirectionalLight extends Light
{
	public DirectionalLight()
	{
		super();
	}
	
	public DirectionalLight(float directionX, float directionY, float directionZ)
	{
		this.directionX = directionX;
		this.directionY = directionY;
		this.directionZ = directionZ;
	}
	
	public float directionX = 0;
	public float directionY = 0;
	public float directionZ = 0;
	
}
