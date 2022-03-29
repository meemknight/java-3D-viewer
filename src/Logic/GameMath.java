package Logic;

import java.lang.Math;

public class GameMath
{
	static public float toRadians(float degrees)
	{
		final float PI = (float)Math.PI;
		return (degrees * PI) / 180.f;
	}
	
	//https://stackoverflow.com/questions/11513344/how-to-implement-the-fast-inverse-square-root-in-java
	static public float inverseSqrt(float x)
	{
		float xhalf = 0.5f * x;
		int i = Float.floatToIntBits(x);
		i = 0x5f3759df - (i >> 1);
		x = Float.intBitsToFloat(i);
		x *= (1.5f - xhalf * x * x);
		return x;
	}
	
}
