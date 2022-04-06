package Logic;

public class Texture
{
	
	public int id;
	
	public Texture load(String name)
	{
		id = TextureLoader.load(name);
		return  this;
	}

}
