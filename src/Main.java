

public class Main
{
	
	public static void main(String[] args)
	{
		GameLayer gm = new GameLayer();
		
		try
		{
			gm.init();
			gm.loop();
		} catch(Exception e)
		{
			System.out.println("Err: " + e);
		}
		
		gm.close();
		//gm.freeResources(); //not necessary for this app
	}
	
}
