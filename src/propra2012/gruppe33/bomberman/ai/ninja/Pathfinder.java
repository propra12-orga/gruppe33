package propra2012.gruppe33.bomberman.ai.ninja;

import java.awt.Point;

public class Pathfinder {
	private Point predecessor;
	private int distance;
	private boolean visited;

	public Point getPredecessor() {
		return predecessor;
	}

	public void setPredecessor(Point predecessor) {
		this.predecessor = predecessor;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

}
