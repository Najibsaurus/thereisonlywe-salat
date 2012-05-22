package info.thereisonlywe.salat.recitation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class ReciterList {
	
	public static final Reciter AFASY_RECITER; 
	public static final Reciter SHURAIM_RECITER; 
    public static final Reciter GHAMDI_RECITER; 
    public static final Reciter RIFAI_RECITER; 
    public static final Reciter BASFAR_RECITER; 
    public static final Reciter HUSARY_RECITER; 
    public static final Reciter SHATREE_RECITER; 
	public static final Reciter WALK_RECITER; 
	
	static
	{
		AFASY_RECITER = 
				new Reciter("Mishary Al-Afasy", "afasy");  
		SHURAIM_RECITER = 
	    		new Reciter("Saud Al-Shuraim", "shuraim");
	    GHAMDI_RECITER = 
	    		new Reciter("Saad Al-Ghamdi", "ghamdi");
	    RIFAI_RECITER = 
	    		new Reciter("Hani Rifai", "rifai");
	    BASFAR_RECITER = 
	    		new Reciter("Abdullah Basfar", "basfar");
	    HUSARY_RECITER = 
	    		new Reciter("Husary", "husary");
	    SHATREE_RECITER = 
	    		new Reciter("Abubakr Ash-Shaatree", "shatree");
		WALK_RECITER = 
	    		new Reciter("Ibrahim Walk", "walk", true);

	}
    public static Reciter getReciterByName(String name) {
		Reciter[] reciters = getReciters();
		for (int i = 0; i < reciters.length; i++)
		{
			if (name.equals(reciters[i].getName()))
			{
				return reciters[i];
			}
		}
		return null;
	}
	
	public static Reciter getReciterByPath(String path) {
		Reciter[] reciters = getReciters();
		for (int i = 0; i < reciters.length; i++)
		{
			if (path.equals(reciters[i].getPath()))
			{
				return reciters[i];
			}
		}
		return null;
	}
	
	public static String[] getQuranReciterNames()
	{
		ArrayList<String> rawResult = new ArrayList<>();
		Reciter[] reciters = getReciters();
		for (int i = 0; i < reciters.length; i++)
		{
			if (!reciters[i].isTextReciter())
			{
				rawResult.add(reciters[i].getName());
			}
		}
		String[] result = new String[rawResult.size()];
		return rawResult.toArray(result);
	}
	
	public static String[] getTextReciterNames()
	{
		ArrayList<String> rawResult = new ArrayList<>();
		Reciter[] reciters = getReciters();
		for (int i = 0; i < reciters.length; i++)
		{
			if (reciters[i].isTextReciter())
			{
				rawResult.add(reciters[i].getName());
			}
		}
		String[] result = new String[rawResult.size()];
		return rawResult.toArray(result);
	}
	
	public static Reciter[] getReciters() {
		Reciter[] reciters = new Reciter[]{AFASY_RECITER, BASFAR_RECITER, 
				GHAMDI_RECITER, HUSARY_RECITER, RIFAI_RECITER, 
				SHATREE_RECITER, SHURAIM_RECITER, WALK_RECITER};
		Arrays.sort(reciters, new Comparator<Reciter>()
                {
            public int compare(Reciter r1, Reciter r2)
            {
                return r1.toString().compareTo(r2.toString());
            }        
        });
		return reciters; 
	}
	
	public static String[] getReciterPaths() {
		Reciter[] reciters = getReciters();
		String[] paths = new String[reciters.length];
		for (int i = 0; i < reciters.length; i++)
		{
			paths[i] = reciters[i].getPath();
		}
		return paths;
	}
	
	public static int getReciterCount() {
		return getReciters().length;
	}
	
}
