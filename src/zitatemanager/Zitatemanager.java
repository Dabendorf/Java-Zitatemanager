package zitatemanager;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * Dies ist die Hauptklasse des Zitatemanagers. Sie liest alle falschen Zitate aus der xml-Datei ein, speichert sie ab und fragt den Nutzer entsprechend, welches Zitat er aufrufen moechte.
 * 
 * @author Lukas Schramm
 * @version 1.0
 *
 */
public class Zitatemanager {

	/**ArrayList aller Zitate*/
	private ArrayList<Zitat> zitateliste = new ArrayList<Zitat>();
	/**Element des Zeitrechners*/
	private Zeitrechner zr = new Zeitrechner("Datumseingabe",Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"."+(Calendar.getInstance().get(Calendar.MONTH)+1)+"."+Calendar.getInstance().get(Calendar.YEAR)+" 09:00:00");
	/**Pfad zur Zitatedatei*/
	private String dateiname = "files/quotations.xml";
	/**Anzahl zu ladener Zitate*/
	private int anzahlTage = 365;
	/**Die geladene Speicherdatei*/
	private File file;
	/**Zu speichernde Propertieselemente*/
	private Properties prop = new Properties();;
	/**Internes Label zur Berechnung der Textbreite*/
	private JLabel cacheLabel;
	
	public Zitatemanager() {
		file = new File(dateiname);
		zitateLaden();
		nutzerFrage();
	}
	
	/**
	 * Diese Methode wird bei Programmstart aufgerufen. Sie liest die Zitate alle in eine ArrayList ein.
	 */
	private void zitateLaden() {
		try {
			FileInputStream fileInput = new FileInputStream(file);
			prop.loadFromXML(fileInput);
			fileInput.close();
			for(int i=0;i<anzahlTage;i++) {
				List<String> zitatStrArr = aufrufen(String.valueOf(i));
				zitatStrArr.set(1,zitatStrArr.get(1).replaceAll("\t",""));
				zitateliste.add(new Zitat(i,zitatStrArr.get(0),zitatStrArr.get(1)));
			}
		} catch(IOException e) {
			try {
				file.createNewFile();
			} catch (IOException e1) {
				fileDamage(dateiname);
			}
		} catch(NullPointerException e) {
			JOptionPane.showMessageDialog(null, "Es tut uns leid Genosse, aber in der Zitatliste fehlen einige Zitate."+System.getProperty("line.separator")+"Bitte stelle sicher, dass die Datei vollständig ist.", "Datei defekt", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
	
	/**
	 * Dies ist das Hauptmenue des Zitatemanagers. Es fragt den Nutzer, ob er das heutige Zitat oder ein anderes Zitat aufrufen moechte.
	 */
	private void nutzerFrage() {
		Object[] options = {"Heute", "Datum wählen", "Schließen"};
		int selected = JOptionPane.showOptionDialog(null, "Welches Zitat möchtest Du aufrufen?", "Zitatemanager", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		switch(selected) {
		case -1: System.exit(0);
		case 0: zitatAusgeben(true);break;
		case 1: datumAuswaehlen();break;
		case 2: System.exit(0);
		default: System.exit(0);
		}
	}
	
	/**
	 * Diese Methode ruft das Zitat auf. Es liest aus dem heutigen oder einem anderen Kalender das Datum aus und zeigt das passende Zitat an.
	 * Bei einem Schaltjahr wird eine entsprechende Meldung angezeigt.
	 * @param heute Prueft, ob es sich um das heutige Datum handelt oder ein anderes geladen werden muss.
	 */
	private void zitatAusgeben(boolean heute) {
		GregorianCalendar cal;
		if(heute) {
			cal = (GregorianCalendar) GregorianCalendar.getInstance();
		} else {
			cal = (GregorianCalendar) zr.dorZeit.getGregKalender();
		}
		int tagNum = cal.get(Calendar.DAY_OF_YEAR) - 1;
		boolean schaltjahr = false;
		if(cal.isLeapYear(cal.get(GregorianCalendar.YEAR)) && tagNum>58) {
			if(tagNum==59) {
				schaltjahr = true;
				JOptionPane.showMessageDialog(null, "Es tut uns leid Genosse, aber Schaltjahre führen wir nicht."+System.getProperty("line.separator")+"Bitte komme morgen noch einmal wieder.", "Schaltjahre", JOptionPane.WARNING_MESSAGE);
			} else {
				tagNum--;
			}
		}
		if(tagNum<365 && !schaltjahr) {
			try {
				String zitatStr = zitateliste.get(tagNum).getZitat();
				cacheLabel = new JLabel(zitatStr);
		        if(cacheLabel.getPreferredSize().width > 400) {
		        	zitatStr = "<html><body><p style='width: 400px;'>" + zitatStr + "</p></body></html>";
		        }
		        String autorStr = "- "+zitateliste.get(tagNum).getAutor();
		        cacheLabel = new JLabel(autorStr);
		        if(cacheLabel.getPreferredSize().width > 400) {
		        	autorStr = "<html><body><p style='width: 400px;'>" + autorStr + "</p></body></html>";
		        }
				JOptionPane.showMessageDialog(null, zitatStr+System.getProperty("line.separator")+autorStr, "Zitat", JOptionPane.PLAIN_MESSAGE);
				if(heute) {
					nutzerFrage();
				}
			} catch(IndexOutOfBoundsException e1) {
				JOptionPane.showMessageDialog(null, "Es tut uns leid Genosse, aber in der Zitatliste fehlt das heutige Datum."+System.getProperty("line.separator")+"Bitte stelle sicher, dass die Datei vollständig ist.", "Datei defekt", JOptionPane.ERROR_MESSAGE);
				nutzerFrage();
			}
		}
	}
	
	/**
	 * Diese Methode ruft eine Dabendorfer Zeitrechnung auf, in welcher man das gewuenschte Datum eingeben und das Zitat aufrufen kann.
	 */
	private void datumAuswaehlen() {
		JFrame frame1 = new JFrame();
		JButton zitatAnzeige = new JButton("Zitat anzeigen");
		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame1.setPreferredSize(new Dimension(260,130));
		frame1.setMinimumSize(new Dimension(260,130));
		frame1.setMaximumSize(new Dimension(390,195));
		frame1.setResizable(true);
		
		Container cp = frame1.getContentPane();
		cp.setLayout(new BorderLayout());
		
		zitatAnzeige.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				zitatAusgeben(false);
			}
		});
		
		cp.add(zr,BorderLayout.NORTH);
		cp.add(zitatAnzeige,BorderLayout.CENTER);
		
		frame1.pack();
		frame1.setLocationRelativeTo(null);
		frame1.setVisible(true);
		zr.umrechnen();
	}
	
	/**
	 * Gibt ein Zitat anhand seines Keys aus.
	 * @param key Hier gibt man den Key ein.
	 * @return Gibt eine Liste der Zitat-Attribute zurueck.
	 */
	private List<String> aufrufen(String key) {
		try {
			String temp = prop.getProperty(key);
			String[] temp2 = temp.split("###");
			List<String> temp3 = Arrays.asList(temp2);
			return temp3;
		} catch (NullPointerException np) { 
			np.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Diese Methode gibt eine Meldung ueber eine fehlerhaft angelegte oder nicht vorhandene Speicherdatei aus.
	 * @param filename Pfad der fehlerhaften Datei.
	 */
	public void fileDamage(String filename) {
		String linebreak = System.getProperty("line.separator");
		JOptionPane.showMessageDialog(null, "Die Speicherdatei /"+filename+" ist nicht vorhanden oder beschädigt."+linebreak+"Die Spielfunktion ist nur eingeschränkt möglich."+linebreak+"Stelle die Speicherdatei wieder her und versuche es erneut.", "Fehlerhafte Datei", JOptionPane.ERROR_MESSAGE);
	}
	
	public static void main(String[] args) {
		new Zitatemanager();
	}
}