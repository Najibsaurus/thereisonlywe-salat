package info.thereisonlywe.salat.prayer;

import info.thereisonlywe.core.toolkit.PrimitiveToolkit;
import info.thereisonlywe.quran.Quran;
import info.thereisonlywe.quran.QuranicVerse;
import info.thereisonlywe.salat.Salat;
import info.thereisonlywe.salat.recitation.Imam;
import info.thereisonlywe.salat.recitation.Reciter;
import info.thereisonlywe.salat.recitation.ReciterList;

import java.util.ArrayList;

/**
 *
 * @author thereisonlywe
 * @since  October 28th 2011
 * @version May 16th 2012
 */
public class Prayer implements Salat {
	
    ArrayList<QuranicVerse[]> verseList = new ArrayList<QuranicVerse[]>();
    
    private int rakaatPointer = 0;
    
    private boolean reciteIqama = false;
    
    private boolean reciteDhikr = false;
    
    private final int imamRandomizationMode;
    
    private final int salatRandomizationMode;
    
    private final int verseRange;
    
    private final int rakaahPerSalat;
    
    private final int numberOfRakaah;
    
    private final Reciter reciter;
    
    private final int recitationSpeed;
    
    private final int pauseRandomizationInterval;
    
    private final int[] pauseIntervals;
    
    private final Reciter[] reciters;
    
    public Prayer(final int verseRange, final int rakaahPerSalat, 
			final int numberOfRakaah, final Reciter reciter, final int recitationSpeed, 
			final int salatRandomizationMode, final int imamRandomizationMode, 
			final int pauseRandomizationInterval, final int[] pauseIntervals, 
			final boolean reciteIqama, final boolean reciteDhikr)
    {
    	this.verseRange = verseRange;
    	this.rakaahPerSalat = rakaahPerSalat;
    	this.numberOfRakaah = numberOfRakaah;
    	this.reciter = reciter;
    	this.recitationSpeed = recitationSpeed;
    	this.salatRandomizationMode = salatRandomizationMode;
    	
    	this.imamRandomizationMode = imamRandomizationMode;
    	if (imamRandomizationMode == PrayerConstants.IMAM_RANDOMIZATION_NONE)
    		this.reciters = new Reciter[1];
    	else if (imamRandomizationMode == PrayerConstants.IMAM_RANDOMIZATION_PER_RAKAAT)
    		this.reciters = new Reciter[numberOfRakaah];
    	else // (imamRandomizationMode == PrayerConstants.IMAM_RANDOMIZATION_PER_SALAT)
    		this.reciters = new Reciter[(numberOfRakaah / rakaahPerSalat)];
    	
    	this.pauseRandomizationInterval = pauseRandomizationInterval;
    	this.pauseIntervals = pauseIntervals;
    	this.reciteIqama = reciteIqama;
    	this.reciteDhikr = reciteDhikr;
    	
    	initReciters();
    	initVerseList();
    }
    
    private void initReciters()
    {
    	if (imamRandomizationMode != PrayerConstants.IMAM_RANDOMIZATION_NONE)
    	{
    		Reciter[] list = ReciterList.getReciters();
    		for (int i = 1; i < reciters.length; i++)
    		{
    			reciters[i] = list[PrimitiveToolkit.newRandom(
    					(ReciterList.getReciterCount() - 1))];
    		}
    	}
    	reciters[0] = reciter;
    }
    
    private void initVerseList()
    {
        for (int i = 0; i < numberOfRakaah; i++)
        {
        	int val = 0;
        	if (i == 0)
        	{
        		while (val < ((1 + verseRange) * (1 + verseRange)))
        			val = (Math.max(1, PrimitiveToolkit.newRandom(7))) * (1 + verseRange); 
        	}
        	else
        	{
        		int counter = 0;
        		while (val == 0 && counter < 3)
        		{
        			//we want verse count to decrease along with rakaat number
        			val = verseList.get(i - 1).length - Math.max(1, PrimitiveToolkit.newRandom(3)); 
        			counter++;
        		}
        		val = (val < 1) ? 1 : val;
        	}
        	QuranicVerse[] curV = new QuranicVerse[val];
            for (int j = 0; j < curV.length; j++)
            {
                switch (salatRandomizationMode)
                {
                    case (PrayerConstants.SALAT_RANDOMIZATION_FULL):
                    {
                        curV[j] = Quran.getRandomVerse(); 
                        while (curV[j].getSectionNumber() == 1)
                        	curV[j] = Quran.getRandomVerse();
                        break;
                    }
                    case (PrayerConstants.SALAT_RANDOMIZATION_SINGLE):
                        if (j != 0)
                            curV[j] = curV[j - 1].getNextVerse();
                        else{
                            curV[j] = Quran.getRandomVerse();
                            while (curV[j].getSectionNumber() == 1)
                            	curV[j] = Quran.getRandomVerse();
                        }
                        break;
                    case (PrayerConstants.SALAT_RANDOMIZATION_SECTION): //every rakaat has verses from one section only
                        if (j != 0)
                        {
                            curV[j] = curV[j - 1].getNextVerse();
                        }
                        else
                        {
                            curV[j] = Quran.getRandomVerse();
                            while ((Quran.getInSectionVerseCount(curV[j]) - curV[j].getVerseNumber()
                                    + 1 < curV.length) || curV[j].getSectionNumber() == 1)
                                curV[j] = Quran.getRandomVerse();
                        }
                        break;
                }
            }
            this.verseList.add(curV);
        }
    }
    
    public int getPauseBetweenVerses()
    {
    	if (recitationSpeed == PrayerConstants.RECITER_SPEED_SLOW)
    		return Imam.SLOW_RECITATION_SPEED;
    	else if (recitationSpeed == PrayerConstants.RECITER_SPEED_NORMAL)
    		return Imam.DEFAULT_RECITATION_SPEED;
    	else
    		return Imam.FAST_RECITATION_SPEED;
    }
    
    public Reciter[] getReciters()
    {
    	return reciters;
    }
    
    public int getImamRandomizationMode()
    {
    	return imamRandomizationMode;
    }
    
    public boolean reciteIqama()
    {
    	return reciteIqama;
    }
    
    public boolean reciteDhikr()
    {
    	return reciteDhikr;
    }
    
    public int getRakaahPerSalat()
    {
    	return rakaahPerSalat;
    }
    
    public int getSalatCount()
    {
    	return numberOfRakaah / rakaahPerSalat;
    }
    
    @Override
    public void next()
    {
    	rakaatPointer++;
    }
    
    @Override
    public void previous()
    {
    	rakaatPointer--;
    }
    
	@Override
	public void rewind() {
		rakaatPointer = 0;
	}
	
	@Override
    public QuranicVerse[] getAllVerses() {
		ArrayList<QuranicVerse> verses = new ArrayList<>();
		for (int i = 0; i < verseList.size(); i++)
		{
			QuranicVerse[] cur = verseList.get(i);
			for (int j = 0; j < cur.length; j++)
			{
				verses.add(cur[j]);
			}
		}
		QuranicVerse[] result = new QuranicVerse[verses.size()];
        return verses.toArray(result);
    }

    public int getPause(int pause)
    {
    	int p = PrimitiveToolkit.newRandom(pauseRandomizationInterval);
    	return Math.max(0, PrimitiveToolkit.newRandom(1) == 0 ? 
    			pauseIntervals[pause] - p : pauseIntervals[pause] + p);
    }

	public int getRakaahCount() {
		return verseList.size();
	}
	
	public int getVerseCountForRakaat() {
		return verseList.get(rakaatPointer).length;
	}
	
	public QuranicVerse[] getVersesForRakaat() {
		return verseList.get(rakaatPointer);
	}
	
	public QuranicVerse[] getVersesForSalat(int salatIndex) {
		ArrayList<QuranicVerse> result = new ArrayList<QuranicVerse>();
		int index = 0;
		for (int i = 0; i < getSalatCount(); i++)
		{
			if (salatIndex == i)
			{
				int temp = index + rakaahPerSalat;
				if (temp + rakaahPerSalat < numberOfRakaah) //add remaining rakaah if number of rakaah is not even
					temp = numberOfRakaah;
				for (int j = index; j < temp; j++)
				{
					for (int k = 0; k < verseList.get(j).length; k++)
					{
						result.add(verseList.get(j)[k]);
					}
				}
				break;
			}
			
			else
				index += rakaahPerSalat;
		}
		QuranicVerse[] r = new QuranicVerse[result.size()];
		return result.toArray(r);
	}
	
}
