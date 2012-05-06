package propra2012.gruppe33.map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Diese Klasse lädt aus einer Textdatei Zeilenweise die Karte aus.
 * Kann IOExceptions werfen.
 * @author Malte
 *
 */
public class MapLoader {

	/**
	 * Berechnet die Map aus der Textdatei und gibt ein Array mit dem Inhalt der
	 * Map zurück. Wirft IOException, wenn die Karte nicht geladen werden kann,
	 * z.B. wenn der File nicht geladen werden kann.
	 * 
	 * @param input
	 *            Die zu ladene Map.
	 * @return Gibt ein Charrarray zurück mit den geladenen Werten der Map.
	 * @throws IOException
	 *             Wenn zu ladene Karte leer oder nicht kompatibel (z.B. Wenn
	 *             die Strings unterschiedlich lang sind.
	 */
	public static char[][] load(InputStream input) throws IOException {
		String line; // Eine Zeile der Karte.
		char[][] map;

		// Laden des files
		BufferedReader br = new BufferedReader(new InputStreamReader(input));

		// Liste in der Zwischengespeichert wird
		List<String> cache = new LinkedList<String>();

		// Initialisieren des Arrays fehlt.

		while ((line = br.readLine()) != null) {
			cache.add(line);
		}
		
		br.close();
		map = new char[line.length()][cache.size() - 1];

		if (!ctrllength(cache)) {
			throw new IOException("wrong input");
		}

		int i=0;
		for(String row : cache){
			line=row;
			for (int j = 0; i < line.length() - 1; i++) {
				map[j][i] = line.charAt(j);
			}
			i++;
		}
		return map;
	}

	/**
	 * Kontrolliert ob alle Strings gleich viele Zeichen haben.
	 * 
	 * @param cache
	 *            Die Liste der Strings aus dem Inputstream.
	 * @return Gibt wahr zurück falls alle Strings gleich viele Zeichen haben.
	 */
	private static boolean ctrllength(List<String> cache) {
		String a;
		String b;
		a = cache.get(0);
		if (cache.isEmpty()) {
			return false;
		}
		
		for(String row : cache){
			b = a;
			a = row;
			if (a.length() != b.length()) {
				return false;
			}
		}
		return true;
	}
}
