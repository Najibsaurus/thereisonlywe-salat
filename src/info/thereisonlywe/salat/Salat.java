package info.thereisonlywe.salat;

import info.thereisonlywe.quran.QuranicVerse;

/**
 * Implemented by Dhikr and Prayer.
 * @author thereisonlywe
 * @since  October 28th 2011
 */
public interface Salat {
	
	public void next();
	
	public void previous();
    
    public QuranicVerse[] getAllVerses();
    
    public void rewind();
}
