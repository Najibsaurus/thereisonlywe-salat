package info.thereisonlywe.salat.recitation;

import info.thereisonlywe.core.io.NetworkIO;
import info.thereisonlywe.quran.QuranicVerse;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Reciter{
	
	private final String name;
    private final String path;
    private final boolean isTextReciter;
    
    @SuppressWarnings("unused")
	private Reciter(){this.name = null; this.path = null; this.isTextReciter = false;}
	
	protected Reciter(String name, String path)
	{
		this.name = name;
		this.path = path;
		this.isTextReciter = false;
	}
	
	protected Reciter(String name, String path, boolean isTextReciter)
	{
		this.name = name;
		this.path = path;
		this.isTextReciter = isTextReciter;
	}
	
	public boolean isTextReciter()
	{
		return isTextReciter;
	}
	
	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}
	
	public URL getAddress(QuranicVerse v) {
		if (!NetworkIO.gotInternetConnection()) return null;
		else
		{
			URL[] urls = getAddresses(v);
			if (urls == null) return null;
			for (int i = 0; i < urls.length; i++)
			{
				if (NetworkIO.exists(urls[i]))
					return urls[i];
			}
		}
		return null;
	}
	
	public URL[] getAddresses(QuranicVerse v) {
		
        ArrayList<String> codes = new ArrayList<>();
        
        URL res[] = null;
        
        try {
        	codes.add(this.getPath());
        
        if (codes.isEmpty()) return null;
        res = new URL[codes.size()];
        for (int i = 0; i < codes.size(); i++)
        {
        	res[i] = new URL(RecitationConstants.ROOT_ADDRESS + codes.get(i) +"/" + 
        			v.toString() + RecitationConstants.FILE_TYPE);
        }
        } catch (MalformedURLException ex) {}
        return res;
	}
	
	public File getFile(QuranicVerse v) {
		return new File(RecitationConstants.RECITATION_PATH + File.separator + path + File.separator + v.toString() + RecitationConstants.FILE_TYPE);
	}
	
	@Override
	public String toString()
	{
		return name;
	}

}
