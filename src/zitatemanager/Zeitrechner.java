package zitatemanager;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;

/**
 * Dies ist die Grafikklasse des Dabendorfer Zeitrechners. Er rechnet eine Dabendorfer Zeit in eine gregorianische Zeit um und andersherum.
 * Diese Klasse ist eine abgewandelte Musterklasse und entstammt dem Projekt 'Dabendorfer Zeitrechnung'.
 * 
 * @author Lukas Schramm
 * @version 1.0
 *
 */
public class Zeitrechner extends JPanel {

	private NumberFormat format1 = NumberFormat.getInstance();
	private NumberFormatter formatter1 = new NumberFormatter(format1);
	private JLabel name = new JLabel();
	private JFormattedTextField gregKalenderTF;
	private JFormattedTextField dorZeitTF = new JFormattedTextField(formatter1);
	private JRadioButton gregRadio = new JRadioButton("Greg.-Zeit");
    private JRadioButton dorRadio = new JRadioButton("DOR-Zeit");
    private ButtonGroup bg = new ButtonGroup();
    private ArrayList<JFormattedTextField> textfelder = new ArrayList<JFormattedTextField>();
    public DabendorferZeit dorZeit;
    
    public Zeitrechner(String bezeichnung,String zeit) {
    	dorZeit = new DabendorferZeit();
		gregKalenderTF = new JFormattedTextField(dorZeit.getDateFormat());
		this.setLayout(new GridLayout(3,1));
		
		JPanel panelGreg = new JPanel();
		panelGreg.setLayout(new BorderLayout());
		panelGreg.add(gregRadio,BorderLayout.LINE_START);
		panelGreg.add(gregKalenderTF,BorderLayout.CENTER);
		JPanel panelDOR = new JPanel();
		panelDOR.setLayout(new BorderLayout());
		panelDOR.add(dorRadio,BorderLayout.LINE_START);
		panelDOR.add(dorZeitTF,BorderLayout.CENTER);
		this.add(name);
		this.add(panelGreg);
		this.add(panelDOR);
		textfelder.add(gregKalenderTF);
		textfelder.add(dorZeitTF);
		
		bg.add(gregRadio);
	    bg.add(dorRadio);
		gregRadio.setSelected(true);
		gregKalenderTF.setText(zeit);
		name.setHorizontalAlignment(SwingConstants.CENTER);
		name.setText(bezeichnung);
		dorZeitTF.setText("0");
		for(JFormattedTextField jftf:textfelder) {
			jftf.getDocument().addDocumentListener(new DocumentListener() {
	            @Override
	            public void changedUpdate(DocumentEvent e) {
	            }
	            @Override
	            public void insertUpdate(DocumentEvent e) {
	                SwingUtilities.invokeLater(new Runnable() {
	                    @Override
	                    public void run() {
	                    	umrechnen();
	                    }
	                });
	            }
	            @Override
	            public void removeUpdate(DocumentEvent e) {
	            }
	        });
		}
		gregKalenderTF.setHorizontalAlignment(SwingConstants.RIGHT);
		dorZeitTF.setHorizontalAlignment(SwingConstants.RIGHT);
		format1.setGroupingUsed(false);
	    formatter1.setAllowsInvalid(false);
    }
    
    /**
	 * Diese Methode prueft, welcher Radiobutton als Ursprungszeit ausgewaehlt wurde.
	 * Anschliessend liest sie den Wert ein, rechnet die Zeit um und fuegt den errechneten Wert in das Textfeld ein.
	 */
    public void umrechnen() {
        if(gregRadio.isSelected()) {
        	dorZeit.gregZuDOR(gregKalenderTF.getText());
        	long neu = dorZeit.getDorZeit();
        	if(String.valueOf(neu)!=dorZeitTF.getText()) {
        		dorZeitTF.setText(String.valueOf(neu));
        	}
        } else {
        	try {
	        	dorZeit.dorZuGreg(Long.valueOf(dorZeitTF.getText()));
	        	if(dorZeit.getGregKalender().getTime()!=dorZeit.getDateFormat().parse(gregKalenderTF.getText())) {
	        		gregKalenderTF.setValue(dorZeit.getGregKalender().getTime());
	        	}
        	} catch (ParseException e) {
        		JOptionPane.showMessageDialog(null, "Du hast falsche Werte eingetragen."+System.getProperty("line.separator")+"Wenn Du dies nicht korrigierst"+System.getProperty("line.separator")+"bekommst Du kein Ergebnis!", "Falscheingabe", JOptionPane.WARNING_MESSAGE);
        	}
        }
    }
}