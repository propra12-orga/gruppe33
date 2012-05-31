package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid;

/**
 * 
 * Here we store the grid constants.
 * 
 * @author Christopher Probst
 * @author Malte Schmidt
 * 
 */
public interface GridConstants {

	int EXPLOSION_ORDER = 100000;
	
	Object FREE_TAG = new Object();
	Object EXPLOSION_TAG = new Object();

	char UP_BARRIER = 'u';
	char DOWN_BARRIER = 'd';
	char LEFT_BARRIER = 'l';
	char RIGHT_BARRIER = 'r';

	char UP_LEFT_CORNER = '(';
	char UP_RIGHT_CORNER = ')';

	char DOWN_LEFT_CORNER = '[';
	char DOWN_RIGHT_CORNER = ']';

	char START = 's';

	/**
	 * Free fields use the defined default speed. To configure field with a
	 * custom speed you can use the numbers 0-9.
	 * <p>
	 * ' ' = 100% of default speed.
	 * <p>
	 * <b>Range '0' to '4'</b>
	 * <p>
	 * <ul>
	 * <li>'0' = 0% of default speed.</li>
	 * <li>'1' = 20% of default speed.</li>
	 * <li>'2' = 40% of default speed.</li>
	 * <li>'3' = 60% of default speed.</li>
	 * <li>'4' = 80% of default speed.</li>
	 * </ul>
	 * 
	 * <p>
	 * <b>Range '5' to '9'</b>
	 * <p>
	 * <ul>
	 * <li>'5' = 120% of default speed.</li>
	 * <li>'6' = 140% of default speed.</li>
	 * <li>'7' = 160% of default speed.</li>
	 * <li>'8' = 180% of default speed.</li>
	 * <li>'9' = 200% of default speed.</li>
	 * </ul>
	 */
	char FREE = ' ';

	char SOLID = '#';

	/**
	 * This value specifies the offset of a breakable object. To setup a
	 * breakable field on which you can go with 60% of the default speed just
	 * <p>
	 * <code>char field = '3' + BREAKABLE_OFFSET;</code>
	 */
	char BREAKABLE_OFFSET = 10000;
}
