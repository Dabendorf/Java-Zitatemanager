package zitatemanager;

/**
 * Diese Klasse generiert Zitat-Objekte, die den Autor, den Text und das Datum enthalten.
 * 
 * @author Lukas Schramm
 * @version 1.0
 *
 */
public class Zitat {
	
	private int datumInt;
	private String autor;
	private String zitat;
	
	public Zitat(int datumInt, String autor, String zitat) {
		this.datumInt = datumInt;
		this.autor = autor;
		this.zitat = zitat;
	}

	public String getAutor() {
		return autor;
	}

	public String getZitat() {
		return zitat;
	}

	public int getDatumInt() {
		return datumInt;
	}
}