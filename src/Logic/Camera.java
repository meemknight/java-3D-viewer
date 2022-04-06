package Logic;

import java.lang.Math;

import org.joml.*;

//used to move in 3D space
public class Camera
{
	
	public Vector3f position = new Vector3f(0,0,5.f);
	
	//up direction of the camera
	public Vector3f up = new Vector3f(0,1,0);
	
	//the closest point that you can see
	public float closePlane = 0.01f;
	
	//view distance
	public float farPlane  = 100.f;
	
	//window's aspect ratio
	public float aspectRatio = 1.f;
	
	//field of view expressed in radians
	public float fovRadians = GameMath.toRadians(60.f);
	
	//the rotation of the camera
	float viewAngleX = 0.f;
	float viewAngleY = 0.f;
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
		return getProjectionMatrix().mul(getViewMatrix());
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
