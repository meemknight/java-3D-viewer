package Logic;

public class IntVector
{
	
	public int[] data = null;
	public int size = 0;
	public int capacity = 0;
	
	void pushBack(int element)
	{
		if(size + 1 > capacity)
		{
			if(capacity <= 0)
			{
				reserve(10);
			}else
			{
				reserve(capacity * 2);
			}
		}
		
		data[size] = element;
		size ++;
	}
	
	int popBack()
	{
		if(size <= 0)
		{
			throw new ArrayIndexOutOfBoundsException("pop back on empty container.");
		}
		
		int el = data[size-1];
		size--;
		return el;
	}
	
	int get(int i)
	{
		if(i >= size)
		{
			throw new ArrayIndexOutOfBoundsException("get invalid index: " + i);
		}
		
		return data[i];
	}
	
	void reserve(int newSize)
	{
		int[] newData = new int[newSize];
		
		for(int i=0; i<size; i++)
		{
			newData[i] = data[i];
		}
		
		data = newData;
		capacity = newSize;
	}
	
	void clear()
	{
		size = 0;
	}
	
}
