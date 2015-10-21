package zitatemanager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Diese Klasse beinhaltet alle Methoden zur Umrechnung der Dabendorf Orthodoxen Zeit in den gregorianischen Kalender und andersherum.
 * Diese Klasse ist eine Musterklasse und entstammt dem Projekt 'Dabendorfer Zeitrechnung'.
 * 
 * @author Lukas Schramm
 * @version 1.0
 *
 */
public class DabendorferZeit {
	
	private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	private Calendar gregKalender = new GregorianCalendar();
	private long unix0 = new GregorianCalendar(1970,0,1,1,0,0).getTimeInMillis();
    private long unixNap = new GregorianCalendar(1821,4,6,3,14,15).getTimeInMillis();
    private long dorZeit;
    private boolean meldung = false;
    
    /**
     * Diese Methode nimmt ein gregorianisches Datum entgegen und rechnet die Dabendorfer Zeit dazu aus.
     * @param einlesedatum Nimmt die gregorianische Datumsangabe entgegen.
     */
    public void gregZuDOR(String einlesedatum) {
    	try {
			gregKalender.setTime(dateFormat.parse(einlesedatum));
			if(gregKalender.get(Calendar.YEAR)<1583 && !meldung) {
				meldung = true;
			} else if(!(gregKalender.get(Calendar.YEAR)<1583)) {
				meldung = false;
				long gregLong = gregKalender.getTimeInMillis()/1000;
				long zeitabstand;
				if(gregKalender.getTimeInMillis()>0) {
					zeitabstand = unix0/1000 - unixNap/1000 + gregLong;
				} else {
					zeitabstand = gregLong - unixNap/1000;
				}
				dorZeit = zeitabstand;
			}
        } catch (ParseException e) {}
    }
    
    /**
     * Diese Methode nimmt eine Dabendorfer Zeitangabe entgegen und rechnet das Datum des gregorianischen Kalenders aus.
     * @param dorLong Nimmt die Dabendorfer Zeit entgegen.
     */
    public void dorZuGreg(long dorLong) {
        long zeitabstand = unixNap + dorLong*1000;
        long oldMillis = gregKalender.getTimeInMillis();
        if(zeitabstand != oldMillis) {
            gregKalender.setTimeInMillis(zeitabstand);
        }
    }

	public DateFormat getDateFormat() {
		return dateFormat;
	}

	public Calendar getGregKalender() {
		return gregKalender;
	}

	public long getDorZeit() {
		return dorZeit;
	}
}