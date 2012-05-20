package propra2012.gruppe33;

import java.util.Iterator;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.iterators.RecursiveChildIterator;

public class EntityTestApp {

	public static void main(String[] args) {

		Entity root = new Entity();

		for (int i = 0; i < 10; i++) {
			Entity child = new Entity();
			child.setName(i + "");

			for (int j = 0; j < 3; j++) {
				Entity sub = new Entity();
				sub.setName(i + " -> " + j);
				child.attach(sub);
			}

			root.attach(child);
		}

		Iterator<Entity> itr = new RecursiveChildIterator(root, true);
		while (itr.hasNext()) {

			System.out.println(itr.next());
		}

	}
}
