package propra2012.gruppe33.bomberman.ai;

import java.awt.Point;
import java.io.Serializable;

/**
 * DIESES INTERFACE IST EIN ERSTER ENTWURF UND SOMIT NOCH NICHT FUER DEN
 * PRAKTISCHEN EINSATZ GEEIGNET.
 * <p>
 * Dieses Interface dient als Schnittstelle zwischen der KI und dem Spiel. Die
 * KI kann die Methoden, die dieses Interface definiert nutzen um eigene
 * Berechnungen anzustellen oder um der KI-Figur Befehle zu erteilen.
 * <p>
 * Dieses Interface wird mit aeußerster Hinsicht auf Performance implementiert,
 * das bedeutet die KI muss sich keine Gedanken darum machen, wie oft Sie z.B.
 * die int[][][] fields() Methode aufruft.
 * <p>
 * Die KI muss sich selbst um Dinge wie Pathfinding etc. kuemmern. Dieses
 * Interface stellt nur die notwendigsten Funktionen zur Verfuegung, damit die
 * KI Pathfinding etc. ueberhaupt betreiben kann.
 * <p>
 * Die KI kann ihrer Spielfigur nicht direkt Movementbefehle geben, sondern
 * stattdessen lediglich "Pfade" setzen. Die interne Impl. wird dann fuer die
 * Animation der Spielfigur sorgen. Mit anderen Worten: Die KI kann nur sagen
 * "Gehe mal zu diesem Punkt und benutze dafuer diese Felder". Die interne Impl.
 * wird gewisse Standardchecks durchfuehren um ein Fehlverhalten der KI zu
 * verhindern. Dennoch sollte sich der Programmierer der KI SELBST Gedanken
 * ueber die Richtigkeit seiner Impl. machen.
 * 
 * VORABENTSCHEIDUNG: Dieses Interface ist THREAD-SAFE.
 * 
 * @version 0.1 Alpha
 * 
 * @author (IMPLEMENTED BY) Christopher Probst
 * @author (USED BY) Malte Schmidt
 */
public interface AIControl extends Serializable {

	/**
	 * Repräsentiert einen absolut soliden Block (Unzerstörbar).
	 */
	int SOLID = 0;

	/**
	 * Repräsentiert einen zerstörbaren Block.
	 */
	int BREAKABLE = 1;

	/**
	 * Repräsentiert eine gelegte Bombe.
	 */
	int BOMB = 2;

	/**
	 * Repräsentiert eine (gleich stattfindende) Explosion.
	 */
	int EXPLOSION = 3;

	/**
	 * Repräsentiert einen Spieler.
	 */
	int PLAYER = 4;

	/**
	 * Repräsentiert ein gutes, aufnehmbares Item.
	 */
	int GOOD_ITEM = 5;

	/**
	 * Repräsentiert ein schlechtes, aufnehmbares Item.
	 */
	int BAD_ITEM = 6;

	boolean placeDefaultBomb();

	boolean placeNukeBomb();

	boolean placeFastBomb();

	boolean placePalisade();

	boolean useShield();

	/**
	 * Diese Methode gibt das Kantengewicht (Wichtig fuer Pathfinding) zurueck.
	 * <p>
	 * Wichtig: Feld A und Feld B muessen logischerweise aneinander liegen. Dies
	 * wird ueberprueft. Falls ein Fehler hierbei auftritt wird eine Exception
	 * intern geworfen.
	 * <p>
	 * 
	 * 
	 * @param a
	 *            Feld A (Von...)
	 * @param b
	 *            Feld B (Nach...)
	 * @return Liefert das Kantengewicht zurueck.
	 */
	float edgeWeight(Point a, Point b);

	/**
	 * @return Liefert die akuelle Position der ueberwachten Spielfigur zurueck.
	 *         Die KI muss nicht ueberpruefen ob die Position innerhalb des
	 *         Feldes ist, dies wird intern gecheckt und sollte somit immer
	 *         korrekt sein.
	 */
	Point activePosition();

	/**
	 * @return Liefert ein Int-Array des AKTUELLEN FELDES (Also wo der Spieler
	 *         gerade steht) zurueck. Dieses Int-Array beinhaltet alle dort
	 *         befindlichen Gegenstaender zurueck. Welcher Int-Wert zu welchem
	 *         Gegenstand gehoert sollte den FINALEN Werten am Anfang dieses
	 *         Interfaces entnommen werden.
	 */
	int[] activeField();

	/**
	 * @return Liefert true zurueck wenn der gesetzte Pfad "vollendet" wurde.
	 *         Mit anderen Worten wenn der Spieler den gesamten gesetzen Pfad
	 *         abgelaufen hat. Wenn der Pfad vorzeitig geloescht wird (also z.b.
	 *         = null gesetzt wird) gibt diese Methode auch true zurueck. MIT
	 *         ANDEREN WORTEN: Diese Methode gibt immer dann true zurueck wenn
	 *         sich die Figur gerade nicht bewegt.
	 */
	boolean isResting();

	/**
	 * Gegenteil von
	 * 
	 * @see AIControl#isResting()
	 */
	boolean isMoving();

	/**
	 * Diese Methode setzt einen Pfad. Das bedeutet es wird ein Array von Points
	 * verlangt und ab diesem Punkt wird die interne Impl. die Figur ueber diese
	 * Punkte animieren (das bedeutet bewegen).
	 * <p>
	 * Diese Methode macht intern einige Tests um zu gewaehrleisten, dass der KI
	 * Programmierer keine Fehler gemacht hat!!! Das beinhaltet z.B. ob keiner
	 * der uebergebenen Punkt null ist und ob alle Punkte jeweils aneinander
	 * liegen.
	 * 
	 * @param points
	 *            Ein Array mit allen Punkten ueber die der Spieler hinweg
	 *            animiert wird.
	 */
	void setPath(Point... points);

	/**
	 * @see AIControl#setPath(Point...)
	 * @return Liefert den akutell gesetzten Pfad zurueck.
	 */
	Point[] getPath();

	/**
	 * @return Liefert true zurueck falls sich das Feld in IRGENDEINERWEISE
	 *         geaendert hat. Das betrifft jede Kleinigkeit wie z.B. das Setzen
	 *         einer Bombe, das Aufnehmen eines Items, das Verschwinden einer
	 *         SpawnExplosion etc.
	 */
	boolean hasFieldChanged();

	/**
	 * 
	 * @return Liefert das gesamte Feld zurueck. Warum 3d-Array ?? Nun, die
	 *         ersten 2 Dimensionen spiegeln das 2D-Feld wieder. WICHTIG: die 1.
	 *         Dimension ist y und 2. Dimension ist x. Also umgekehrt zum
	 *         kartesischen Koordinatensystem. Die 3. Dimension liefert das
	 *         Array an Objekten auf diesem Feld zurueck.
	 */
	int[][][] fields();
}
