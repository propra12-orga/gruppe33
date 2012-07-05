package propra2012.gruppe33.bomberman.ai.oldschool;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TestDijkstraAlgorithm {
	private static List<Vertex> nodes;
	private static List<Edge> edges;

	private static void addLane(String laneId, int sourceLocNo, int destLocNo,
			int duration) {
		Edge lane = new Edge(laneId, nodes.get(sourceLocNo),
				nodes.get(destLocNo), duration);
		edges.add(lane);
	}

	public static void main(String[] args) {

		nodes = new ArrayList<Vertex>();
		edges = new ArrayList<Edge>();
		for (int i = 0; i < 11; i++) {
			Vertex location = new Vertex("Node_" + i, "Node_" + i);
			nodes.add(location);
		}

		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {

			}
		}

		Graph graph = new Graph(nodes, edges);

		DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);

		dijkstra.execute(nodes.get(0));

		LinkedList<Vertex> path = dijkstra.getPath(nodes.get(10));
		for (Vertex vertex : path) {
			System.out.println(vertex);
		}
	}
}
