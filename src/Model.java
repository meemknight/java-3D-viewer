import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

//this holds all the data needed for rendering a 3D object with one material.
public class Model
{
	//material of the object
	public Material material = null;

	//this buffer holds the 3D model data
	public int vertexBuffer = 0;
	
	//this buffer tells the graphics card in what order to draw the data
	public int indexBuffer = 0;
	
	//this tells the graphics card how to read the data (3 floats values for positions, 3 for normals then 2 floats for texture)
	public int vao = 0;
	
	//how many vertices to draw
	public int vertexCount = 0;
	
	void deleteData()
	{
		GL30.glDeleteBuffers(vertexBuffer);
		GL30.glDeleteBuffers(indexBuffer);
		GL30.glDeleteVertexArrays(vao);
		vertexCount = 0;
	}
	
	//data represent positions, normals and uv's in this order
	void loadFromComputedData(float data[], int indices[])
	{
		assert(data.length % 8 == 0);
		
		deleteData();
		
		//the vertex array object holds the configuration of our 3D object. We can refer to it and draw the object.
		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		
		vertexCount = indices.length;
		
		//load the data
		indexBuffer = GL30.glGenBuffers();
		GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indices, GL30.GL_STATIC_DRAW);
		
		vertexBuffer = GL30.glGenBuffers();
		GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vertexBuffer);
		GL30.glBufferData(GL30.GL_ARRAY_BUFFER, data, GL30.GL_STATIC_DRAW);
		
		
		//tell opengl how to read the data
		GL30.glEnableVertexAttribArray(0);
		GL30.glVertexAttribPointer(0, 3, GL13.GL_FLOAT, false, 4*8, 0);
		GL30.glEnableVertexAttribArray(1);
		GL30.glVertexAttribPointer(1, 3, GL13.GL_FLOAT, false, 4*8, 4*3);
		GL30.glEnableVertexAttribArray(2);
		GL30.glVertexAttribPointer(2, 2, GL13.GL_FLOAT, false, 4*8, 4*6);
		
		GL30.glBindVertexArray(0);
	}
	
}
