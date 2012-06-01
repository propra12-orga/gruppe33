package propra2012.gruppe33.bomberman.ai;

/**
 * 
 * Dieses Interface muss von der KI implementiert werden.
 * 
 * @version 0.1 Alpha
 * 
 * @author (IMPLEMENTED BY) Christopher Probst
 * @author (USED BY) Malte Schmidt
 */
public interface AIProcessor {

	/**
	 * Diese Methode wird in gewissen Abstaenden aufgerufen. (Die Zeit ist nicht
	 * von der KI beeinflussbar sondern wird vorab im Programm eingestellt.)
	 * Hier sollten alle Berechnungen der KI rein.
	 * <p>
	 * WICHTIG: Diese Methode wird vermutlich in einem eigenen Thread
	 * aufgerufen, da KI Berechnungen evt. relativ viel Zeit in Anspruch nehmen
	 * koennen. Da es nicht wuenschenswert waere, wenn die Grafik durch komplexe
	 * KI Berechnungen verlangsamt wuerde, ist ein Thread vermutlich
	 * unabdingbar.
	 * 
	 * @param timeSinceLastFrame
	 * @param aiControl
	 */
	void process(float timeSinceLastFrame, AIControl aiControl);
}
