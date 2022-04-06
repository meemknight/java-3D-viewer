package Logic;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class ModelLoader
{
	
	private static Texture getTextureFromMaterial(AIMaterial mat, int textureType, String objPath)
	{
		AIString path = AIString.calloc();

		Assimp.aiGetMaterialTexture(mat, textureType, 0, path,
				(IntBuffer)null,  null, null, null, null, null);
		
		String p = path.dataString();
		
		String rootPath = new File(objPath).getParent();
		String computedPath = rootPath + "/" + new File(p);
		
		//+ new File(p);
		
		
		Texture t = new Texture().load(computedPath);
		
		return t;
	}
	
	public static Entity loadEntity(String path)
	{
		Entity e = new Entity();
		
		AIScene scene = Assimp.aiImportFile(path, Assimp.aiProcess_Triangulate |
				Assimp.aiProcess_FlipUVs|
				Assimp.aiProcess_ImproveCacheLocality |
				Assimp.aiProcess_JoinIdenticalVertices
		);
		
		PointerBuffer buffer = scene.mMeshes();
		
		if(buffer.limit() == 0){return null;}
		
		for(int i=0; i<buffer.limit(); i++)
		{
			AIMesh mesh = AIMesh.create(buffer.get(i));
			
			AIVector3D.Buffer vertices = mesh.mVertices();
			AIVector3D.Buffer normals = mesh.mNormals();
			AIVector3D.Buffer uvs = mesh.mTextureCoords(0);
			
			FloatVector computedData = new FloatVector();
			computedData.reserve(vertices.limit() * 8);
			
			for(int j=0;j<vertices.limit(); j++)
			{
				AIVector3D vertice = vertices.get(j);
				AIVector3D normal = normals.get(j);
				AIVector3D uv = uvs.get(j);
				
				computedData.pushBack(vertice.x());
				computedData.pushBack(vertice.y());
				computedData.pushBack(vertice.z());
				computedData.pushBack(normal.x());
				computedData.pushBack(normal.y());
				computedData.pushBack(normal.z());
				computedData.pushBack(uv.x());
				computedData.pushBack(uv.y());
				
			}
			
			//todo reserve
			IntVector computedIndices = new IntVector();
			
			for(int j = 0; j < mesh.mNumFaces(); j++)
			{
				AIFace face = mesh.mFaces().get(j);
				for(int k = 0; k < face.mNumIndices(); k++)
				{
					computedIndices.pushBack(face.mIndices().get(k));
				}
			}
			
			Material material = new Material();
			
			AIMaterial loadedMaterial = AIMaterial.create(scene.mMaterials().get(mesh.mMaterialIndex()));
			
			material.albedoTexture = getTextureFromMaterial(loadedMaterial, Assimp.aiTextureType_DIFFUSE, path);
			material.normalTexture = getTextureFromMaterial(loadedMaterial, Assimp.aiTextureType_NORMALS, path);
			material.metallicTexture = getTextureFromMaterial(loadedMaterial, Assimp.aiTextureType_METALNESS, path);
			material.roughnessTexture = getTextureFromMaterial(loadedMaterial, Assimp.aiTextureType_DIFFUSE_ROUGHNESS, path);
			material.aoTexture = getTextureFromMaterial(loadedMaterial, Assimp.aiTextureType_AMBIENT, path);
			
			//mesh.
			Model model = new Model();
			model.loadFromComputedData(computedData.data, computedIndices.data);
			model.material = material;
			
			e.models.add(model);
			
		}
		
		return e;
	}
	
	
}
