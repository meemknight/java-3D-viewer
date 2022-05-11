package platform;

import Logic.DirectionalLight;
import Logic.PointLight;
import Logic.Sampler;
import Logic.SpotLight;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log
{
	public String fileName;
	
	public Log(String fileName)
	{
		this.fileName = fileName;
	}
	
	public void writeLog(String text)
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		
		try
		{
			new File(fileName).createNewFile();
		} catch(Exception e)
		{
			System.out.println("Couldn't create file: " + fileName);
		}
		
		try(FileWriter f = new FileWriter(fileName,true))
		{
			
			f.append(dtf.format(now) + ": " + text + "\n");
			
		} catch(Exception e)
		{
			System.out.println("Couldn't write file: " + fileName);
		}
		
		
	}
	
}
