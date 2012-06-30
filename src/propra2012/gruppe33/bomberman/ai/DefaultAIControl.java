package propra2012.gruppe33.bomberman.ai;

import java.awt.Point;

import propra2012.gruppe33.bomberman.GameRoutines;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Grid;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.PositionPath;

public class DefaultAIControl implements AIControl {

	private final GraphicsEntity gridEntity;
	private final Entity pathAnimator = new Entity();
	private final Grid grid;
	private final int[][][] aiField;
	private final GraphicsEntity player;

	public DefaultAIControl(GraphicsEntity player) {
		gridEntity = (GraphicsEntity) player.parent().parent();
		this.player = player;
		grid = gridEntity.typeProp(Grid.class);
		aiField = gridEntity.typeProp(int[][][].class);
		player.attach(pathAnimator);
	}

	@Override
	public boolean placeDefaultBomb() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean placeNukeBomb() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean placeFastBomb() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean placePalisade() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean useShield() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float edgeWeight(Point a, Point b) {
		return GameRoutines.edgeWeight(gridEntity, a, b);
	}

	@Override
	public Point activePosition() {
		return ((GraphicsEntity) player.parent()).position().round().point();
	}

	@Override
	public int[] activeField() {
		Point loc = activePosition();
		return aiField[loc.y][loc.x];
	}

	@Override
	public boolean isResting() {
		return !isMoving();
	}

	@Override
	public boolean isMoving() {
		return !pathAnimator.taskQueue().tasks().isEmpty();
	}

	@Override
	public void setPath(Point... points) {

		if (points == null) {
			pathAnimator.taskQueue().cancel();
		} else {

			Point[] tmp = points;
			points = new Point[tmp.length + 1];
			points[0] = activePosition();
			System.arraycopy(tmp, 0, points, 1, tmp.length);
			for (int i = 0; i < points.length - 1; i++) {

				PositionPath pp = new PositionPath(player, new Vector2f(
						points[i]).add(new Vector2f(points[i + 1])), 3f) {
					@Override
					protected boolean updateTask(float tpf) {
						boolean res = super.updateTask(tpf);
						GameRoutines.rearrangeGridNode((GraphicsEntity) player
								.parent());
						return res;
					}

				};
				pathAnimator.taskQueue().tasks().offer(pp);
			}
		}
	}

	@Override
	public Point[] getPath() {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public boolean hasFieldChanged() {
		return true;
	}

	@Override
	public int[][][] fields() {
		return aiField;
	}

}
