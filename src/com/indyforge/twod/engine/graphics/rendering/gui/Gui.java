package com.indyforge.twod.engine.graphics.rendering.gui;

import java.awt.event.KeyEvent;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.RenderedImage;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.animation.RenderedAnimation;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;
import com.indyforge.twod.engine.graphics.sprite.Animation;
import com.indyforge.twod.engine.resources.assets.AssetManager;

public class Gui extends Scene {

	private RenderedImage backGround, cursor;

	private Vector2f[] pointList;
	private Animation itemChooseAnimation;
	private Map<Vector2f, List<Runnable>> buttons = new LinkedHashMap<Vector2f, List<Runnable>>();

	private int menuPos, menuEntries;

	public Gui(AssetManager assetManager, int width, int height,
			Animation itemChooseAnimation, String bgAssetPath,
			String cursorAssetPath, Vector2f... points) throws Exception {
		super(assetManager, width, height);

		this.itemChooseAnimation = itemChooseAnimation;
		backGround = new RenderedImage(
				assetManager.loadImage(bgAssetPath, true)).centered(true);
		backGround.position().set(0.5f, 0.5f);
		cursor = new RenderedImage(
				assetManager.loadImage(cursorAssetPath, true)).centered(true);

		
		
		scale(sizeAsVector());
		attach(backGround);
		attach(cursor);

		cursor.scale().set(0.08f, 0.08f);

		pointList = points;
		for (Vector2f p : points) {
			buttons.put(p, new LinkedList<Runnable>());
		}
		menuPos = 0;
		menuEntries = points.length;

		updateCursor();
	}

	boolean pressedUP = false, pressedDOWN = false, pressedENTER = false;

	public List<Runnable> actions(int index) {
		return buttons.get(pointList[index]);
	}

	@Override
	protected void onUpdate(float tpf) {
		super.onUpdate(tpf);

		if (!pressedUP && isPressed(KeyEvent.VK_UP)) {

			updatePos(false);
			updateCursor();

			pressedUP = true;

		}
		if (pressedUP && !isPressed(KeyEvent.VK_UP)) {
			pressedUP = false;
		}

		if (!pressedDOWN && isPressed(KeyEvent.VK_DOWN)) {

			updatePos(true);
			updateCursor();

			pressedDOWN = true;

		}
		if (pressedDOWN && !isPressed(KeyEvent.VK_DOWN)) {
			pressedDOWN = false;
		}

		if (!pressedENTER && isPressed(KeyEvent.VK_ENTER)) {
			// Create an entity using the animation
			RenderedAnimation renderedAnimation = new RenderedAnimation() {
				protected void onAnimationFinished(
						RenderedAnimation renderedAnimation, Animation animation) {

					// WICHTIG ... Wenn die bombe tot is... (Das letzte
					// Frame erreicht wurde... detach!)

					renderedAnimation.detach();

					for (Runnable task : actions(menuPos)) {
						task.run();
					}
				}
			};
			renderedAnimation.index(10); // Damit es auf jeden fall über
											// dem gui
											// is---
			renderedAnimation.animationBundle().add(itemChooseAnimation)
					.reset();
			renderedAnimation.animationName("explosion");

			renderedAnimation.position().set(getMenuVector());
			attach(renderedAnimation);
			pressedENTER = true;
		}

		if (pressedENTER && !isPressed(KeyEvent.VK_ENTER)) {
			pressedENTER = false;
		}

	}

	// Navigate up and down in a Menu
	// if value is false you navigate up
	// if value is true you navigate down
	// Only for vertical menu
	public void updatePos(boolean value) {
		if (value) {
			if (menuPos == menuEntries - 1) {
				menuPos = 0;
			} else {
				menuPos++;
			}

		} else {
			if (menuPos == 0) {
				menuPos = menuEntries - 1;
			} else {
				menuPos--;
			}
		}

	}

	public void updateCursor() {
		cursor.position().set(this.getMenuVector());
	}
	

	// Retun a 2D vector of the actual menu Position
	public Vector2f getMenuVector() {
		Vector2f menuVector = pointList[menuPos];

		return menuVector;

	}
}
