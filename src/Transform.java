import org.joml.*;

//this reprezents the position in 3D space of the object
public class Transform
{
	public Quaternionf rotation = new Quaternionf(0,0,0,1);
	public Vector3f position = new Vector3f(0,0,0);
	public Vector3f scale = new Vector3f(1,1,1);

	//returns the full transform in matrix form
	public Matrix4f getModelMatrix()
	{
		return new Matrix4f().translate(position).rotate(rotation).scale(scale);
	}

}
