package ant;
public class Path{
	private Node node;
	private Edge edge;

	public Path(Node node, Edge edge){
		this.node = node;
		this.edge = edge;
	}

	public Node getNode() {
		return node;
	}
	public void setNode(Node node) {
		this.node = node;
	}
	public Edge getEdge() {
		return edge;
	}
	public void setEdge(Edge edge) {
		this.edge = edge;
	}
}
