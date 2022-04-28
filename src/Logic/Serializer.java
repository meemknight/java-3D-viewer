package Logic;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Serializer
{
	
	private Serializer()
	{
	}
	
	public static <T> List<T> load(String file, Class<T> clazz)
	{
		try(BufferedReader fileBuffer = new BufferedReader(new FileReader(file)))
		{
			fileBuffer.readLine();
			List retVal = new ArrayList<PointLight>();
			if(clazz == PointLight.class)
			{
				String line;
				while((line = fileBuffer.readLine()) != null)
				{
					String[] data = line.split(",");
					PointLight l = new PointLight();
					l.positionX = Float.parseFloat(data[0]);
					l.positionY = Float.parseFloat(data[1]);
					l.positionZ = Float.parseFloat(data[2]);
					l.colorR = Float.parseFloat(data[3]);
					l.colorG = Float.parseFloat(data[4]);
					l.colorB = Float.parseFloat(data[5]);
					retVal.add(l);
				}
				return retVal;
			}
			else if(clazz == SpotLight.class)
			{
				String line;
				while((line = fileBuffer.readLine()) != null)
				{
					String[] data = line.split(",");
					SpotLight l = new SpotLight();
					l.directionX = Float.parseFloat(data[0]);
					l.directionY = Float.parseFloat(data[1]);
					l.directionZ = Float.parseFloat(data[2]);
					l.positionX = Float.parseFloat(data[3]);
					l.positionY = Float.parseFloat(data[4]);
					l.positionZ = Float.parseFloat(data[5]);
					l.angleCos = Float.parseFloat(data[6]);
					l.colorR = Float.parseFloat(data[7]);
					l.colorG = Float.parseFloat(data[8]);
					l.colorB = Float.parseFloat(data[9]);
					retVal.add(l);
				}
				return retVal;
			}
			else if(clazz == DirectionalLight.class)
			{
				String line;
				while((line = fileBuffer.readLine()) != null)
				{
					String[] data = line.split(",");
					DirectionalLight l = new DirectionalLight();
					l.directionX = Float.parseFloat(data[0]);
					l.directionY = Float.parseFloat(data[1]);
					l.directionZ = Float.parseFloat(data[2]);
					l.colorR = Float.parseFloat(data[3]);
					l.colorG = Float.parseFloat(data[4]);
					l.colorB = Float.parseFloat(data[5]);
					retVal.add(l);
				}
				return retVal;
				
			}
			else if(clazz == Sampler.class)
			{
				String line;
				while((line = fileBuffer.readLine()) != null)
				{
					String[] data = line.split(",");
					Sampler l = new Sampler();
					l.textureName = data[0].trim();
					l.minSampler = Integer.parseInt(data[1]);
					l.maxSampler = Integer.parseInt(data[2]);
					retVal.add(l);
				}
				return retVal;
			}
			else
			{
				throw new Exception("Unhandled type");
			}
			
		} catch(IOException e)
		{
			System.out.println("Couldn't open file " + file);
			return new ArrayList<T>();
		} catch(Exception e)
		{
			System.out.println("Couldn't parse file " + file);
			return new ArrayList<T>();
		}
		
	}
	
	;
	
	public static <T> void save(List<T> list, String file, Class<T> clazz)
	{
		try
		{
			new File(file).createNewFile();
		} catch(Exception e)
		{
			System.out.println("Couldn't create file: " + file);
		}
		
		try(FileWriter f = new FileWriter(file))
		{
			if(clazz == PointLight.class)
			{
				f.write("positionX,positionY,positionZ,colorX,colorY,colorZ\n");
				
				for(var i : list)
				{
					PointLight e = (PointLight) i;
					f.write(e.positionX + "," + e.positionY + "," + e.positionZ + ","
							+ e.colorR + "," + e.colorG + "," + e.colorB + "\n");
				}
				
			}
			else if(clazz == SpotLight.class)
			{
				for(var i : list)
				{
					
					SpotLight e = (SpotLight) i;
					f.write(
							e.directionX + "," +
									e.directionY + "," +
									e.directionZ + "," +
									e.positionX + "," +
									e.positionY + "," +
									e.positionZ + "," +
									e.angleCos + "," +
									e.colorR + "," +
									e.colorG + "," +
									e.colorB + "\n"
					);
				}
				
			}
			else if(clazz == DirectionalLight.class)
			{
				for(var i : list)
				{
					DirectionalLight e = (DirectionalLight) i;
					f.write(
							e.directionX + "," +
								e.directionY + "," +
								e.directionZ + "," +
								e.colorR + "," +
								e.colorG + "," +
								e.colorB + "\n"
					);
				}
				
			}
			else if(clazz == Sampler.class)
			{
				for(var i : list)
				{
					Sampler e = (Sampler) i;
					f.write(
							e.textureName + "," +
								e.minSampler + "," +
								e.maxSampler + "\n"
					);
				}
				
			}else
			{
				throw new Exception("Unhandled type");
			}
			
			
		} catch(Exception e)
		{
			System.out.println("Couldn't write file: " + file);
		}
		
	}
	
}
