package Map;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/*   
 *Diese Klasse lädt aus einer Textdatei Zeilenweise die Karte aus.
 *Kann IOExceptions werfen. 
 */
public class MapLoader {

	/*
	 * Berechnet die Map aus der Textdatei und gibt ein Array mit dem Inhalt der
	 * Map zurück. Wirft IOException, wenn die Karte nicht geladen werden kann,
	 * z.B. wenn der File nicht geladen werden kann.
	 */
	public char[][] load() throws IOException {
		String line; // Eine Zeile der Karte.

		// Laden des files
		BufferedReader br = new BufferedReader(new FileReader("test.txt"));
		
		//Initialisieren des Arrays fehlt.
		
		line = br.readLine();
		while (line != null) {
			for (int i = 0; i < line.length()-1; i++) {
				//line.charAt(i)= load
			}
		}
		br.close();
		return load();
	}
}
