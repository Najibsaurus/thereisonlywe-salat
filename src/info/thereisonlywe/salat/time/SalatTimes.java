package info.thereisonlywe.salat.time;

import info.thereisonlywe.core.io.PackageIO;
import info.thereisonlywe.core.toolkit.DateToolkit;
import info.thereisonlywe.core.toolkit.PrimitiveToolkit;
import info.thereisonlywe.core.toolkit.StringToolkit;
import info.thereisonlywe.core.toolkit.ThreadToolkit;
import info.thereisonlywe.core.toolkit.TimeToolkit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author thereisonlywe
 * @since November 3rd 2011
 * @Version March 6th 2012
 */
public final class SalatTimes {
    
    public static final int FAJR_SALAT_TIME = 0;
    public static final int SUNRISE_SALAT_TIME = 1;
    public static final int DHUHR_SALAT_TIME = 2;
    public static final int ASR_SALAT_TIME = 3;
    public static final int MAGHRIB_SALAT_TIME = 4;
    public static final int ISHA_SALAT_TIME = 5;

    public static void main (String[] args)
    {
    	SalatTimes st = new SalatTimes(41.01, 28.94, DateToolkit.GMTOffset("Europe/Istanbul"), "tr");
    	System.out.println(st.getIshaTime());
    }
    
    private int currentSalatTime;
    private double lat;
    private double lon;
    private double timezone;
    
    private int[] offsets;

    private Calendar calendar;
    
    private int processID = -1;
    private String fajr;
    private String sunrise;
    private String dhuhr;
    private String asr;
    private String sunset;
    private String maghrib;
    
    private String isha;
    private String fajrName;
    private String sunriseName;
    private String dhuhrName;
    private String asrName;    
    private String sunsetName;
    private String maghribName;
    private String ishaName;

    private final Thread clockRefreshTask = new Thread() {
        @Override
        public void run() 
        {
            int id = processID;
            while (processID == id)
            {
                ThreadToolkit.sleep(3600000); //an hour
                Calendar cal = Calendar.getInstance();
                if (calendar.get(Calendar.DATE) != cal.get(Calendar.DATE))
                    setSalatClock();
                cal = null;
            }
        }
    };

    @SuppressWarnings("unused")
	private SalatTimes(){}

    public SalatTimes(double lat, double lon, double timezone, String langCode)
    {
        this.lat = lat;
        this.lon = lon;
        this.timezone = timezone;
        setSalatClock();
        setLanguage(langCode);
    }

    public SalatTimes(double lat, double lon, double timezone, String langCode,
            int[] offsets)
    {
        this.offsets = offsets;
        this.lat = lat;
        this.lon = lon;
        this.timezone = timezone;
        setSalatClock();
        setLanguage(langCode);
    }
    public String getAsrName() {
        return asrName;
    }
    public String getAsrTime()
    {
        return asr;
    }
    
    public String getCurrentSalatTimeName()
    {
    	return timeIndexToTimeName(currentSalatTime);
    }
    
    public String getCurrentSalatTimeStart()
    {
        return timeIndexToTimeStart(currentSalatTime);
    }
    
    public String getDhuhrName() {
        return dhuhrName;
    }
    
    public String getDhuhrTime()
    {
        return dhuhr;
    }
    
    public String getFajrName() {
        return fajrName;
    }
    
    public String getFajrTime()
    {
        return fajr;
    }
    
    public String getIshaName() {
        return ishaName;
    }
    
    public String getIshaTime()
    {
        return isha;
    }
    
    public String getMaghribName() {
        return maghribName;
    }
	
	public String getMaghribTime()
    {
        return maghrib;
    }
    
    private int getNextSalatTime()
    {
    	if (currentSalatTime == FAJR_SALAT_TIME)
    		return SUNRISE_SALAT_TIME;
    	else if (currentSalatTime == SUNRISE_SALAT_TIME)
    		return  DHUHR_SALAT_TIME;
    	else if (currentSalatTime == DHUHR_SALAT_TIME)
    		return ASR_SALAT_TIME;
    	else if (currentSalatTime == ASR_SALAT_TIME)
    		return MAGHRIB_SALAT_TIME;
    	else if (currentSalatTime == MAGHRIB_SALAT_TIME)
    		return ISHA_SALAT_TIME;
    	else if (currentSalatTime == ISHA_SALAT_TIME)
    		return FAJR_SALAT_TIME;
    	else
    		return -1;
    }
    
    public int getPercentElapsed(String currentTime)
	{
		String timeBefore = getCurrentSalatTimeStart();
		String timeAfter = currentTime != null ? 
				currentTime : TimeToolkit.getCurrentTime();
		double elapsed = TimeToolkit.getTimeDifferenceInMinutes(
				timeBefore, timeAfter);
		double total = TimeToolkit.getTimeDifferenceInMinutes(
				timeBefore, timeIndexToTimeStart(getNextSalatTime()));
		double value = elapsed / total * 100.0;
		return (int) value;
	}
    
    public String getSunriseName() {
        return sunriseName;
    }
    
    
    public String getSunriseTime()
    {
        return sunrise;
    }
    
    public String getSunsetName() {
        return sunsetName;
    }
    
    public String getSunsetTime()
    {
        return sunset;
    }
    
    public void refreshCurrentSalatTime()
    {
    	int radix = calendar.get(Calendar.HOUR_OF_DAY);
    	int radix2 = calendar.get(Calendar.MINUTE);
    	int[] values = new int[]{TimeToolkit.getHour(fajr), 
    			TimeToolkit.getHour(sunrise), TimeToolkit.getHour(dhuhr),
    			TimeToolkit.getHour(asr), TimeToolkit.getHour(maghrib),
    			TimeToolkit.getHour(isha)};
    	int index = PrimitiveToolkit.getClosestToRadix(values, radix);
    	switch (index)
    	{
    	case (FAJR_SALAT_TIME):
    		if (radix < values[index])
    			currentSalatTime = ISHA_SALAT_TIME;
    		else if (radix == values[index])
    		{
    			if (TimeToolkit.getMinute(fajr) > radix2)
    				currentSalatTime = ISHA_SALAT_TIME;
    			else
    				currentSalatTime = FAJR_SALAT_TIME;
    		}
    		else if (radix >= values[index])
    		{
    			currentSalatTime = FAJR_SALAT_TIME;
    		}
    	case (SUNRISE_SALAT_TIME):
    		if (radix < values[index])
    			currentSalatTime = FAJR_SALAT_TIME;
    		else if (radix == values[index])
    		{
    			if (TimeToolkit.getMinute(sunrise) > radix2)
    				currentSalatTime = FAJR_SALAT_TIME;
    			else
    				currentSalatTime = SUNRISE_SALAT_TIME;
    		}
    		else if (radix >= values[index])
    		{
    			currentSalatTime = SUNRISE_SALAT_TIME;
    		}
    	case (DHUHR_SALAT_TIME):
    		if (radix < values[index])
    			currentSalatTime = SUNRISE_SALAT_TIME;
    		else if (radix == values[index])
    		{
    			if (TimeToolkit.getMinute(dhuhr) > radix2)
    				currentSalatTime = SUNRISE_SALAT_TIME;
    			else
    				currentSalatTime = DHUHR_SALAT_TIME;
    		}
    		else if (radix >= values[index])
    		{
    			currentSalatTime = DHUHR_SALAT_TIME;
    		}
    	case (ASR_SALAT_TIME):
    		if (radix < values[index])
    			currentSalatTime = DHUHR_SALAT_TIME;
    		else if (radix == values[index])
    		{
    			if (TimeToolkit.getMinute(asr) > radix2)
    				currentSalatTime = DHUHR_SALAT_TIME;
    			else
    				currentSalatTime = ASR_SALAT_TIME;
    		}
    		else if (radix >= values[index])
    		{
    			currentSalatTime = ASR_SALAT_TIME;
    		}
    	case (MAGHRIB_SALAT_TIME):
    		if (radix < values[index])
    			currentSalatTime = ASR_SALAT_TIME;
    		else if (radix == values[index])
    		{
    			if (TimeToolkit.getMinute(maghrib) > radix2)
    				currentSalatTime = ASR_SALAT_TIME;
    			else
    				currentSalatTime = MAGHRIB_SALAT_TIME;
    		}
    		else if (radix >= values[index])
    		{
    			currentSalatTime = MAGHRIB_SALAT_TIME;
    		}
    	case (ISHA_SALAT_TIME):
    		if (radix < values[index])
    			currentSalatTime = MAGHRIB_SALAT_TIME;
    		else if (radix == values[index])
    		{
    			if (TimeToolkit.getMinute(isha) > radix2)
    				currentSalatTime = MAGHRIB_SALAT_TIME;
    			else
    				currentSalatTime = ISHA_SALAT_TIME;
    		}
    		else if (radix >= values[index] || radix < 12)
    		{
    			currentSalatTime = ISHA_SALAT_TIME;
    		}
    	}
    }
    
    public void setLanguage(String langCode)
    {
        String path = "/" + "info" +"/" + "thereisonlywe" + "/" + 
        		"salat" + "/" + "time" + "/" + "TimeNames"  + "_";
        if (SalatTimes.class.getResourceAsStream(path+langCode+".txt") == null)
            langCode = "en";
        String[] names = StringToolkit.splitLines
                (PackageIO.read(SalatTimes.class, path+langCode+".txt"));
        fajrName = names[0]; sunriseName = names[1]; dhuhrName = names[2];
        asrName = names[3]; sunsetName = names[4]; maghribName = names[5];
        ishaName = names[6];
    }
    
    private void setSalatClock()
    {
        SalatTimesCalculator prayers = new SalatTimesCalculator();
        prayers.setTimeFormat(prayers.Time24);
        prayers.setCalcMethod(prayers.Custom);
        prayers.setAsrJuristic(prayers.Shafii);
        prayers.setAdjustHighLats(prayers.AngleBased);
        if (offsets == null)
            offsets = new int[]{0, 0, 0, 0, 0, 0, 0};
        prayers.tune(offsets);
        Date date = new Date();
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        ArrayList<String> prayerTimes = prayers.getPrayerTimes(calendar, 
                lat, lon, timezone);
        fajr = prayerTimes.get(0);
        sunrise = prayerTimes.get(1);
        dhuhr = prayerTimes.get(2);
        asr = prayerTimes.get(3);
        sunset = prayerTimes.get(4);
        maghrib = prayerTimes.get(5);
        isha = prayerTimes.get(6);
        int id = PrimitiveToolkit.newRandom(10000);
        if (id != processID)
            processID = id;
        new Thread(clockRefreshTask).start();
        prayers = null;
    }
    
    public String timeIndexToTimeName(int time)
    {
    	switch (time)
    	{
    		case (FAJR_SALAT_TIME): 
    			return  fajrName;
    		case (SUNRISE_SALAT_TIME): 
    			return  sunriseName;
    		case (DHUHR_SALAT_TIME): 
    			return  dhuhrName;
    		case (ASR_SALAT_TIME): 
    			return  asrName;
    		case (MAGHRIB_SALAT_TIME): 
    			return  maghribName;
    		case (ISHA_SALAT_TIME): 
    			return  ishaName;
    		default: return null;
    	}
    }
    
    public String timeIndexToTimeStart(int index)
    {
        if (index == FAJR_SALAT_TIME)
            return fajr;
        else if (index == SUNRISE_SALAT_TIME)
            return sunrise;
        else if (index == DHUHR_SALAT_TIME)
            return dhuhr;
        else if (index == ASR_SALAT_TIME)
            return asr;
        else if (index == MAGHRIB_SALAT_TIME)
            return maghrib;
        else if (index == ISHA_SALAT_TIME)
            return isha;
        return null;
    }
    
    public int timeStartToTimeIndex(String timeStart)
    {
        if (fajr.equals(timeStart))
            return FAJR_SALAT_TIME;
        else if (sunrise.equals(timeStart))
            return SUNRISE_SALAT_TIME;
        else if (dhuhr.equals(timeStart))
            return DHUHR_SALAT_TIME;
        else if (asr.equals(timeStart))
            return ASR_SALAT_TIME;
        else if (maghrib.equals(timeStart))
            return MAGHRIB_SALAT_TIME;
        else if (isha.equals(timeStart))
            return ISHA_SALAT_TIME;
        return -1;
    }
            
}
