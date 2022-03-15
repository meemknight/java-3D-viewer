
import org.lwjgl.ovr.OVRMatrix4f;

import java.io.BufferedReader;
import java.lang.Math;
import java.util.Vector;

import org.joml.*;

public class Camera
{
	
	public Vector3f position = new Vector3f(0,0,5.f);
	public Vector3f up = new Vector3f(0,1,0);
	
	public float closePlane = 0.01f;
	public float farPlane  = 100.f;
	
	public float aspectRatio = 1.f;
	public float fovRadians = GameMath.toRadians(60.f);
	
	float viewAngleX = 0.f;
	float viewAngleY = 0.f;
	//public Vector3f viewDirection = new Vector3f(0,0,-1.f);
	public Vector3f getViewDirection()
	{
		Vector3f viewDirection = new Vector3f(0,0,-1);
		
		new Matrix4f().rotate(viewAngleX, up).transformPosition(viewDirection);
		
		Vector3f vectorToTheRight = new Vector3f(viewDirection).cross(up);
		
		//now we rotate by x vector
		new Matrix4f().rotate(viewAngleY, vectorToTheRight).transformPosition(viewDirection);
		
		viewDirection.normalize();
		
		return viewDirection;
	}
	
	public void updateAspectRation(float w, float h)
	{
		aspectRatio = w / h;
	}
	
	public Matrix4f getProjectionMatrix()
	{
		return new Matrix4f()
				.perspective(fovRadians, aspectRatio, closePlane, farPlane);
	}
	
	public Matrix4f getViewMatrix()
	{
		Vector3f newVector = new Vector3f();
		position.add(getViewDirection(), newVector);
		return new Matrix4f().lookAt(position, newVector, up);
	}
	
	public Matrix4f getViewProjectionMatrix()
	{
		//Matrix4f m = new Matrix4f()
		//		.perspective((float) GameMath.toRadians(45.0f), 1.0f, 0.01f, 100.0f)
		//		.lookAt(0.0f, 0.0f, 5.0f,
		//				0.0f, 0.0f, 0.0f,
		//				0.0f, 1.0f, 0.0f);
		//return m;
		//Matrix4f ret = new Matrix4f();
		return getProjectionMatrix().mul(getViewMatrix());
		//return ret;
	}
	
	public void moveFPS(Vector3f dir)
	{
		//forward
		float forward = -dir.z;
		float leftRight = dir.x;
		float upDown = dir.y;
		
		Vector3f move = new Vector3f(0,0,0);
		
		move.add(new Vector3f(up).mul(upDown));
		move.add(new Vector3f(getViewDirection()).cross(up).normalize().mul(leftRight));
		move.add(new Vector3f(getViewDirection()).mul(forward));

		this.position.add(move);
	}
	
	public void rotateCamera(Vector2f delta)
	{
		if(delta.x == 0.f && delta.y == 0.f)
		{
			return;
		}
		
		delta.x *= -1;
		delta.y *= -1;
		
		float speed = 4.f;
		
		viewAngleX += delta.x * speed;
		viewAngleY += delta.y * speed;
		
		viewAngleY = Math.max(viewAngleY, GameMath.toRadians(-89.f));
		viewAngleY = Math.min(viewAngleY, GameMath.toRadians(89.f));
	}
	
	
}
