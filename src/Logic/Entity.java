package Logic;

import java.util.ArrayList;

//an entity has multiple 3D models (that can have different materials) and a position in 3D space.
public class Entity
{
	public Transform transform = new Transform();
	public ArrayList<Model> models = new ArrayList<Model>();
}
