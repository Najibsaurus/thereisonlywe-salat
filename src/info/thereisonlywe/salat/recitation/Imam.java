package info.thereisonlywe.salat.recitation;

import info.thereisonlywe.core.audio.LiteAudioPlayer;
import info.thereisonlywe.core.io.NetworkIO;
import info.thereisonlywe.core.toolkit.StringToolkit;
import info.thereisonlywe.core.toolkit.ThreadToolkit;
import info.thereisonlywe.core.toolkit.TypeToolkit;
import info.thereisonlywe.quran.Quran;
import info.thereisonlywe.quran.QuranicVerse;
import info.thereisonlywe.salat.Dhikr;
import info.thereisonlywe.salat.Salat;
import info.thereisonlywe.salat.prayer.Prayer;
import info.thereisonlywe.salat.prayer.PrayerConstants;

import java.io.File;
import java.net.URL;

import javax.swing.JOptionPane;

public class Imam {
	
	public static final File TAKBEER = new File(RecitationConstants.RECITATION_PATH +File.separator +RecitationConstants.IMAM_PATH + File.separator + RecitationConstants.TAKBEER_PATH + RecitationConstants.FILE_TYPE);
	
	public static final File SALAM = new File(RecitationConstants.RECITATION_PATH +File.separator +RecitationConstants.IMAM_PATH + File.separator + RecitationConstants.SALAM_PATH + RecitationConstants.FILE_TYPE);
	
	public static final File SAMIALLAH = new File(RecitationConstants.RECITATION_PATH +File.separator +RecitationConstants.IMAM_PATH + File.separator + RecitationConstants.SAMIALLAH_PATH + RecitationConstants.FILE_TYPE);
	
	public static final File IQAMA = new File(RecitationConstants.RECITATION_PATH +File.separator +RecitationConstants.IMAM_PATH + File.separator + RecitationConstants.IQAMA_PATH + RecitationConstants.FILE_TYPE);
	
	public static final File DHIKR = new File(RecitationConstants.RECITATION_PATH +File.separator +RecitationConstants.IMAM_PATH + File.separator + RecitationConstants.DHIKR_PATH + RecitationConstants.FILE_TYPE);
	
	public static final File ATHAN_ASR = new File(RecitationConstants.RECITATION_PATH +File.separator +RecitationConstants.IMAM_PATH + File.separator + RecitationConstants.ATHAN_ASR_PATH + RecitationConstants.FILE_TYPE);
	
	public static final File ATHAN_DHUHR = new File(RecitationConstants.RECITATION_PATH +File.separator +RecitationConstants.IMAM_PATH + File.separator + RecitationConstants.ATHAN_DHUHR_PATH + RecitationConstants.FILE_TYPE);
	
	public static final File ATHAN_FAJR = new File(RecitationConstants.RECITATION_PATH +File.separator +RecitationConstants.IMAM_PATH + File.separator + RecitationConstants.ATHAN_FAJR_PATH + RecitationConstants.FILE_TYPE);
	
	public static final File ATHAN_ISHA = new File(RecitationConstants.RECITATION_PATH +File.separator +RecitationConstants.IMAM_PATH + File.separator + RecitationConstants.ATHAN_ISHA_PATH + RecitationConstants.FILE_TYPE);
	
	public static final File ATHAN_MAGHRIB = new File(RecitationConstants.RECITATION_PATH +File.separator +RecitationConstants.IMAM_PATH + File.separator + RecitationConstants.ATHAN_MAGHRIB_PATH + RecitationConstants.FILE_TYPE);
	
	public static final int DEFAULT_RECITATION_SPEED = 900;
	
	public static final int FAST_RECITATION_SPEED = 0;
	
	public static final int SLOW_RECITATION_SPEED = 1800;
	
	private static boolean loop = false;
	
	private static double volume = 0.5;
	
	private static boolean alive = false; // alive = recitation is in progress
	
    private static boolean sigTerm = false;
    
    private static boolean sigPause = false;
	
	private final LiteAudioPlayer player = new LiteAudioPlayer();
	
	private final Salat salat;
	
	private final String errorDialog;
	
	private final String prostrationDialog;
	
	private final int maxChars;
	
	private boolean openDialog = false;
	
	private File recitationFile;
	
	private Reciter reciter;
	
	private Reciter textReciter;
	
	private QuranicVerse currentVerse;
	
	private boolean readyForRecitation = false;
	
	private boolean downloadsFirst = false;
	
    public Imam()
    {
    	salat = null; 
    	errorDialog = null;
    	prostrationDialog = null;
    	maxChars = 0;
    }
    
    public Imam(File f)
    {
    	
    	this();
    	
    	if (!f.exists())
    	{
	    	if ((f == ATHAN_ASR || f == ATHAN_DHUHR || f == ATHAN_FAJR || 
	    			f == ATHAN_ISHA || f == ATHAN_MAGHRIB) && !checkAthanFiles())
	    		return;
	    	
	    	else if ((f == DHIKR || f == IQAMA) && !checkOptionalPrayerFiles())
	    		return;
	    	
	    	else if ((f == TAKBEER || f == SALAM || f == SAMIALLAH) && !checkRequiredPrayerFiles())
	    		return;
	    	
	    	else return; //file is not known and does not exist
    	}
    	
    	this.recitationFile = f;
    	sigTerm = false;
    	sigPause = false;
    	alive = true;
    	readyForRecitation = true;
    	new Thread(recite).start();
    }
    
    public Imam(Salat salat, Reciter reciter, Reciter textReciter,
    		String errorDialog, String prostrationDialog, int maxChars, boolean downloadsFirst)
    {
    	this(salat, reciter, textReciter, errorDialog, prostrationDialog, maxChars);
    	this.downloadsFirst = downloadsFirst;
    }
    
    public Imam(Salat salat, Reciter reciter, Reciter textReciter,
    		String errorDialog, String prostrationDialog, int maxChars)
    {
    	this.salat = salat;
    	//reciter and textReciter do not matter if the salat is a Prayer
    	this.reciter = reciter;
    	this.textReciter = textReciter;
    	//feed below null if not dedicated (no popups)
    	this.errorDialog = errorDialog;
    	this.prostrationDialog = prostrationDialog; 
    	this.maxChars = maxChars;
    	sigTerm = false;
    	sigPause = false;
    	alive = true;
    	
    	if (salat instanceof Prayer)
    	{
    		new Thread(startPrayer).start();
    	}
    	
    	else if (salat instanceof Dhikr)
    	{
    		new Thread(startDhikr).start();
    	}      
    	
    }
    
    public Imam(Salat salat, Reciter reciter, Reciter textReciter, boolean downloadsFirst)
    {
    	this(salat, reciter, textReciter);
    	this.downloadsFirst = downloadsFirst;
    }
    
    public Imam(Salat salat, Reciter reciter, Reciter textReciter)
    {
    	this(salat, reciter, textReciter, null, null, -1);
    }
    
    public Imam(Salat salat, Reciter reciter)
    {
    	this(salat, reciter, null);
    }
    
    public Imam(Salat salat, Reciter reciter, boolean downloadsFirst)
    {
    	this(salat, reciter, null, downloadsFirst);
    }
    
    public Imam(Prayer prayer)
    {
    	this(prayer, null, null);
    }

    public static boolean isAlive()
    {
    	return alive;
    }
    
    public static boolean isPaused()
    {
    	return sigPause;
    }
    
    public int getNumberOfVersesRemaining()
    {
    	if (salat == null) return -1;
    	else
    		return ((Dhikr)salat).getNumberOfVersesRemaining();
    }
    
    public boolean download(final QuranicVerse[] verses, final Reciter reciter)
    {
    	for (int i = 0; i < verses.length; i++)
    	{
    		if (sigTerm || !checkAudioFile(reciter, verses[i]))
    			return false;
    	}
    	return true;
    }
    
    public QuranicVerse getVerse()
    {
    	return currentVerse;
    }
    
    public Reciter getReciter()
    {
    	return reciter;
    }
    
    public boolean isDialogOpen()
    {
    	return openDialog;
    }
    
    public Reciter getTextReciter()
    {
    	return textReciter;
    }
    
    public static void setVolume(double v)
    {
    	Imam.volume = v;
    }
    
    public static void setVolume(int v)
    {
    	Imam.volume = v * 1.0 / 100.0;
    }
    
    public static void setLoop(boolean val)
    {
    	Imam.loop = val;
    }
    
    public static void terminate()
    {
    	if (sigTerm)
    		return;
    	else if (sigPause)
    	{
    		sigTerm = true;
    		sigPause = false;
    		alive = false;
    	}
    	else
    		sigPause = true;
    }
    
    public static void revive()
    {
    	if (!sigTerm)
    		sigPause = false;
    }
    
    
    private final Thread startPrayer = new Thread() {
        @Override
        public void run() {
        	//first check & download required files for recitation
        	if (!checkRequiredPrayerFiles()) return;
        	checkOptionalPrayerFiles();
        	Prayer prayer = (Prayer) salat;
        	int irm = prayer.getImamRandomizationMode();
        	if (irm == PrayerConstants.IMAM_RANDOMIZATION_NONE)
        	{
    			for (int j = 0; j < 7; j++)
    			{
    				if (sigTerm || (j != 0 && !checkAudioFile(prayer.getReciters()[0], Quran.getVerse(j))))
    					return;
    			}
        		QuranicVerse verses[] = prayer.getAllVerses();
        		for (int i = 0; i < verses.length; i++)
        		{
        			if (sigTerm || !checkAudioFile(prayer.getReciters()[0], verses[i]))
        				return;
        		}
        	}
        	else
        	{//contains errors TODO: f
        		int len = irm == PrayerConstants.IMAM_RANDOMIZATION_PER_RAKAAT ?
        				prayer.getRakaahCount() : prayer.getSalatCount();
        				
        		for (int i = 0; i < len; i++)
        		{
        			QuranicVerse verses[] = irm == PrayerConstants.IMAM_RANDOMIZATION_PER_RAKAAT ? 
        					prayer.getVersesForRakaat() : prayer.getVersesForSalat(i);
        			for (int j = 0; j < verses.length; j++)
        			{
        				if (sigTerm || !checkAudioFile(prayer.getReciters()[i], verses[j]))
        					return;
        			}
        			for (int j = 0; j < 7; j++)
        			{
        				if (sigTerm || (j != 0 && !checkAudioFile(prayer.getReciters()[i], Quran.getVerse(j))))
        					return;
        			}
        		}
        	}
        	
         	//then start salat
         	setSalat(prayer);
         	
         	player.reset();
        	alive = false;
        	sigTerm = false;
        	sigPause = false;

        }
    };
    
    private Thread startDhikr = new Thread() {
        @Override
        public void run() {
        	final int len = ((Dhikr)salat).getAllVerses().length;
        	if (downloadsFirst) //download all, return if not successful
        	{
        		if (!download(salat.getAllVerses(), reciter)) 
        		{
        			endSession();
        			return;
        		}
        	}
        	else if (len > 1) //download first three verses
        	{
        		Thread prepareRecitationFiles = new Thread(){
        			@Override
        			public void run(){
        				boolean success = false;
        				for (int i = 0; i < len && i < 3; i++)
        				{
        					success = checkAudioFile(reciter, ((Dhikr)salat).getNextNthVerse(i));
        					if (success && textReciter != null)
        						success = checkAudioFile(textReciter, ((Dhikr)salat).getNextNthVerse(i));
        					if (!success || sigTerm)
        					{
        						endSession();
        						return;
        					}
        				}
        				readyForRecitation = true;
        			}
        		};
        		if (!sigTerm)
        			new Thread(prepareRecitationFiles).start();
        	}

        	if (len > 1)
        	{
        		if (!sigTerm)
        			reciteAudhubillah();
        		currentVerse = ((Dhikr)salat).getCurrentVerse();
        		if (currentVerse.getVerseNumber() == 1 && 
        				currentVerse.getSectionNumber() != 9 &&
        				currentVerse.getSectionNumber() != 1)
        		{
        			if (!sigTerm)
        				reciteBasmala();
        		}
        		if (!sigTerm){while (!readyForRecitation){ ThreadToolkit.sleep(10);}}
        	}
			
			while (!sigTerm && ((Dhikr)salat).getNumberOfVersesRemaining() > 0)
        	{
				currentVerse = ((Dhikr)salat).getCurrentVerse();
				new Thread(downloadNextInQueue).start();
				setDhikr();
        		ThreadToolkit.sleep(DEFAULT_RECITATION_SPEED);
        		salat.next();
        	}
        	
        	player.reset();
        	
        	if (!sigTerm && loop)
        	{
        		salat.rewind();
        		new Thread(startDhikr).start();
        	}
        	
        	alive = false;
        	sigTerm = false;
        	sigPause = false;
        }
    };
    
    private final Thread recite = new Thread() {
        @Override
        public void run() {
        	recite(recitationFile);
        	alive = false;
        	sigTerm = false;
        	sigPause = false;
        	player.reset();
        }
    };
    
    private void setSalat(Prayer prayer)
    {
    	int rc = 0;
    	reciter = prayer.getReciters()[rc];
    	
    	ThreadToolkit.sleep(prayer.getPause(11));
    	if (sigTerm) return;
    	if (prayer.reciteIqama() && IQAMA.exists())
    		recite(IQAMA);
    	if (sigTerm) return;
    	recite(TAKBEER);
    	ThreadToolkit.sleep(prayer.getPause(0));
    	if (sigTerm) return;
    	
    	for (int i = 0; i < prayer.getRakaahCount(); i++)
    	{
    		QuranicVerse[] verses = prayer.getVersesForRakaat();
    		if (prayer.getImamRandomizationMode() == PrayerConstants.IMAM_RANDOMIZATION_PER_RAKAAT)
    		{
    			reciter = prayer.getReciters()[rc];
    			rc++;
    		}
    		//start with Fatiha
    		if (reciter.getFile(Quran.getVerse(0)).exists()){ //some reciters dont have basmala
    			recite(reciter.getFile(Quran.getVerse(0)));
    			ThreadToolkit.sleep(prayer.getPauseBetweenVerses());
    			if (sigTerm) return;
    		}
    		recite(reciter.getFile(Quran.getVerse(1)));
    		ThreadToolkit.sleep(prayer.getPauseBetweenVerses());
    		if (sigTerm) return;
    		recite(reciter.getFile(Quran.getVerse(2)));
    		ThreadToolkit.sleep(prayer.getPauseBetweenVerses());
    		if (sigTerm) return;
    		recite(reciter.getFile(Quran.getVerse(3)));
    		ThreadToolkit.sleep(prayer.getPauseBetweenVerses());
    		if (sigTerm) return;
    		recite(reciter.getFile(Quran.getVerse(4)));
    		ThreadToolkit.sleep(prayer.getPauseBetweenVerses());
    		if (sigTerm) return;
    		recite(reciter.getFile(Quran.getVerse(5)));
    		ThreadToolkit.sleep(prayer.getPauseBetweenVerses());
    		if (sigTerm) return;
    		recite(reciter.getFile(Quran.getVerse(6)));
    		ThreadToolkit.sleep(prayer.getPause(1)); //pause after Fatiha
    		//recite verses of this rakaat
    		for (int j = 0; j < verses.length; j++)
    		{
    			if (sigTerm) return;
    			recite(reciter.getFile(verses[j]));
    			ThreadToolkit.sleep(prayer.getPauseBetweenVerses());
    		}
    		ThreadToolkit.sleep(prayer.getPause(2));
    		if (sigTerm) return;
    		recite(TAKBEER);
    		ThreadToolkit.sleep(prayer.getPause(3)); //wait at rkh
    		recite(SAMIALLAH);
    		ThreadToolkit.sleep(prayer.getPause(4)); //wait at second kym
    		if (sigTerm) return;
    		recite(TAKBEER);
    		ThreadToolkit.sleep(prayer.getPause(5));//wait at first sjd
    		if (sigTerm) return;
    		recite(TAKBEER);
    		ThreadToolkit.sleep(prayer.getPause(6)); //sit
    		if (sigTerm) return;
    		recite(TAKBEER);
    		ThreadToolkit.sleep(prayer.getPause(7)); //wait at second sjd
    		if (sigTerm) return;
    		recite(TAKBEER);
    		if (i + 1 == prayer.getRakaahCount() || 
    				(((i % 2 == 1 && prayer.getRakaahPerSalat() == 2) || 
    				(i % 4 == 3 && prayer.getRakaahPerSalat() == 4)) && 
    				(i + 1 + prayer.getRakaahPerSalat() >= prayer.getRakaahCount())))
    		{ //end salat and start new one
    			ThreadToolkit.sleep(prayer.getPause(10)); //sit at the end
    			if (sigTerm) return;
    			recite(SALAM);
    			if (i + 1 == prayer.getRakaahCount()){ ThreadToolkit.sleep(2000); return;}
    			ThreadToolkit.sleep(prayer.getPause(11)); //salat division interval
    			if (sigTerm) return;
    			if (prayer.getImamRandomizationMode() == PrayerConstants.IMAM_RANDOMIZATION_PER_SALAT)
    			{
    				rc++;
    				reciter = prayer.getReciters()[rc];
    			}
    	    	if (prayer.reciteIqama() && IQAMA.exists())
    	    		recite(IQAMA);
    			if (sigTerm) return;
    			recite(TAKBEER);
    	    	ThreadToolkit.sleep(prayer.getPause(0));
    		}
    		
    		else if(i % 2 == 0)
    		{
    			ThreadToolkit.sleep(prayer.getPause(8)); //after sjd
    		}
    		
    		else //if i % 2 == 1
    		{
    			ThreadToolkit.sleep(prayer.getPause(9)); 
    			if (sigTerm) return;
    			recite(TAKBEER);
    			ThreadToolkit.sleep(prayer.getPause(8));
    		}
    		
    		prayer.next();
    	}
    	
    	if (sigTerm) return;
    	if (prayer.reciteDhikr() && DHIKR.exists())
    		recite(DHIKR);
    }
    
    private void setDhikr()
    {
    	
		boolean fileExists = reciter.getFile(currentVerse).exists();
		
        if (!fileExists) 
        {
        	if (currentVerse.getIndex() == 0) return; //skip basmala if N/A
        	else
        	{
        		Reciter current = reciter;
        		if (switchReciter())
        		{
        			recite(reciter.getFile(currentVerse));
        			this.reciter = current; //switch back to original reciter
        		}
        		else if (errorDialog != null) 
        			showErrorDialog();
        	}
        }
        
        else
        	recite(reciter.getFile(currentVerse));
        
        if (textReciter != null && !sigTerm)
        {
            if (textReciter.getFile(currentVerse).exists())
            {
            	ThreadToolkit.sleep(DEFAULT_RECITATION_SPEED);
            	recite(textReciter.getFile(currentVerse));
            }
            else
            {
            	if (sigTerm) return;
            	Reciter current = textReciter;
            	if (switchTextReciter())
            	{
            		ThreadToolkit.sleep(DEFAULT_RECITATION_SPEED);
            		recite(textReciter.getFile(currentVerse));
            		textReciter = current; //switch back to original text reciter
            	}
            	else if (errorDialog != null)
            	{
            		showErrorDialog();
            	}
            }
        }
        
        if (!sigTerm && prostrationDialog != null && currentVerse.isAProstrationVerse())
            showProstrationDialog();
    }
    
    private void recite(File f)
    {
    	double vol = volume;
    	player.play(f);
    	player.setVolume(volume);
		do {
            if (sigTerm)
                return;
            
            else if (sigPause)
            {
            	player.pause(f);
            	while (sigPause)
            	{
            		ThreadToolkit.sleep(10);
            		if (sigTerm) return;
            	}
            	player.resume(f);
            }
            	
            else 
            {
            	if (volume != vol){
            		vol = volume;
            		player.setVolume(volume);
            	}
            }
            
            ThreadToolkit.sleep(10);
        }
        while (player != null && 
        		!player.isCloseToCompletion(f));
    }
    
    private void endSession()
    {
    	player.reset();
    	alive = false;
    	sigTerm = false;
    	sigPause = false;
    }
    
    
    private void showErrorDialog()
    {
    	if (errorDialog != null)
    	{
	    	openDialog = true;
	        JOptionPane.showMessageDialog(null, 
	            StringToolkit.multiLine(errorDialog, maxChars), 
	            null, JOptionPane.ERROR_MESSAGE);
	        openDialog = false;
    	}
        sigTerm = true;
    }
    
    private void showProstrationDialog()
    {
    	openDialog = true;
        JOptionPane.showMessageDialog(null, 
            StringToolkit.multiLine(prostrationDialog, maxChars), 
            null, JOptionPane.PLAIN_MESSAGE);
        openDialog = false;
    }
    
    private boolean switchReciter(){
    	Reciter[] reciters = ReciterList.getReciters();
        for (int i = 0; i < reciters.length; i++){
        	if (sigTerm)
        		return false;
        	else if (reciters[i] == reciter || reciters[i].isTextReciter())
        		continue;
            if (reciters[i].getFile(currentVerse).exists())
            {
            	this.reciter = reciters[i];
            	return true;
            }
        }
        return false;
    }
    
    private boolean switchTextReciter(){
    	Reciter[] treciters = ReciterList.getReciters();
        for (int i = 0; i < treciters.length; i++){
        	if (sigTerm)
        		return false;
        	else if (!treciters[i].isTextReciter() || treciters[i] == textReciter)
                continue;
            if (treciters[i].getFile(currentVerse).exists())
            {
            	this.textReciter = treciters[i];
            	return true;
            }
        }
        return false;
    }
    
    private final Thread downloadNextInQueue = new Thread() {
        @Override
        public void run() {
        	
        	if (sigTerm) return;
        	
        	QuranicVerse v = ((Dhikr)salat).getNextNthVerse(3);
        	
        	if (sigTerm || v == null) return;
        	
        	checkAudioFile(reciter, v);
        	
        	if (sigTerm || textReciter == null) return;
        	
            checkAudioFile(textReciter, v);
            
        }
    };
    
    
    /**
     * @return Returns true if we either know or assume we have the right file.
     */
    private boolean checkAudioFile(File file, URL url)
    {
    	if (!file.exists())
    	{
    		if (url == null) return false;
    		else
    			return NetworkIO.download(file, url);
    	}
    	else
    	{
    		if (url == null) return true;
    		else
    		{
	    		long size = NetworkIO.getLength(url);
	    		if ((size != -1 || NetworkIO.exists(url)) && size != file.length()){ //update file
	    			file.delete();
	    			return NetworkIO.download(file, url);
	    		}
	    		return true;
    		}
    	}
    }
    
    private boolean checkAudioFile(Reciter reciter, QuranicVerse verse)
    {
    	if (sigTerm) return false;
    	
    	if (verse == null || reciter == null) return false;
    	
    	return checkAudioFile(reciter.getFile(verse), reciter.getAddress(verse));
    }
    
    private boolean checkRequiredPrayerFiles()
    {
    	if (sigTerm) return false;
    	if (!checkAudioFile(TAKBEER, getPrayerFileURL(RecitationConstants.TAKBEER_PATH))) 
    		return false;
    	if (!checkAudioFile(SALAM, getPrayerFileURL(RecitationConstants.SALAM_PATH))) 
    		return false;
    	if (!checkAudioFile(SAMIALLAH, getPrayerFileURL(RecitationConstants.SAMIALLAH_PATH))) 
    		return false;
    	return true;
    }
    
    private boolean checkOptionalPrayerFiles()
    {
    	if (!checkAudioFile(DHIKR, getPrayerFileURL(RecitationConstants.DHIKR_PATH)))
    		return false;
    	if (!checkAudioFile(IQAMA, getPrayerFileURL(RecitationConstants.IQAMA_PATH)))
    		return false;
    	return true;
    }
    
    private boolean checkAthanFiles()
    {
    	
    	if (!checkAudioFile(TAKBEER, getPrayerFileURL(RecitationConstants.ATHAN_FAJR_PATH))) 
    		return false;
    	if (!checkAudioFile(SALAM, getPrayerFileURL(RecitationConstants.SALAM_PATH))) 
    		return false;
    	if (!checkAudioFile(SAMIALLAH, getPrayerFileURL(RecitationConstants.SAMIALLAH_PATH))) 
    		return false;
    	return true;
    }
    
    private URL getPrayerFileURL(String file)
    {
    	return TypeToolkit.createURL(RecitationConstants.ROOT_ADDRESS + 
    			RecitationConstants.IMAM_PATH + "/" + file + RecitationConstants.FILE_TYPE);
    }
    
    private void reciteAudhubillah()
    {
    	File f = reciter.getFile(Quran.AUDHUBILLAH);
		if (!f.exists())
        {
            checkAudioFile(reciter, Quran.AUDHUBILLAH);
        }
		if (!sigTerm && f.exists())
			recite(f);
    }
    
    private void reciteBasmala()
    {
    	File f = reciter.getFile(Quran.getVerse(0));
		if (!f.exists())
        {
            checkAudioFile(reciter, Quran.getVerse(0));
        }
		if (!sigTerm && f.exists())
			recite(f);
    }
    
}
