package zitatemanager;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Dies ist die Hauptklasse des Zitatemanagers. Sie liest alle falschen Zitate aus der Textdatei ein, speichert sie ab und fragt den Nutzer entsprechend, welches Zitat er aufrufen moechte.
 * 
 * @author Lukas Schramm
 * @version 1.0
 *
 */
public class Zitatemanager {

	private ArrayList<Zitat> zitateliste = new ArrayList<Zitat>();
	private Zeitrechner zr = new Zeitrechner("Datumseingabe","07.08.2004 09:00:00");
	private String dateiname = new String("./files/quotations.txt");
	private int anzahlTage = 365;
	private FileReader fr;
	private Properties prop;
	
	public Zitatemanager() {
		zitateLaden();
		nutzerFrage();
	}
	
	/**
	 * Diese Methode wird bei Programmstart aufgerufen. Sie liest die Zitate alle in eine ArrayList ein.
	 */
	private void zitateLaden() {
		try {
			ladeDatei();
			for(int i=0;i<anzahlTage;i++) {
				List<String> zitatStrArr = aufrufen(String.valueOf(i));
				zitatStrArr.set(1,zitatStrArr.get(1).replaceAll("\t",""));
				zitateliste.add(new Zitat(i,zitatStrArr.get(0),zitatStrArr.get(1)));
			}
			fr.close();
		} catch (IOException e) { }
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
			String zitatStr = zitateliste.get(tagNum).getZitat();
			zitatStr = "<html><body><p style='width: 400px;'>" + zitatStr + "</p></body></html>";
			JOptionPane.showMessageDialog(null, zitatStr+System.getProperty("line.separator")+"- "+zitateliste.get(tagNum).getAutor(), "Zitat", JOptionPane.PLAIN_MESSAGE);
			if(heute) {
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
	 * Diese Methode laedt die Datei, aus welcher zu lesen ist.
	 */
	private void ladeDatei() throws IOException {
		fr = new FileReader(dateiname);
		prop = new Properties();
		prop.load(fr);
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
	
	public static void main(String[] args) {
		new Zitatemanager();
	}
}