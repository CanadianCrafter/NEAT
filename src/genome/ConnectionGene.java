package genome;

import java.util.*;

import neat.Neat;

/*
 * These are the Genes for the connections between nodes
 */
public class ConnectionGene extends Gene{
	
	//specifies which nodes it connects
	private NodeGene from;
	private NodeGene to;
	
	private double weight; //every connection has a weight
	private boolean enabled = true; //connections can be enabled or not
	
	//if we replace this connection by inserting a new node in between it, forming two new connections
	//the node in the middle should have this index in the innovation number
	private int replaceIndex; 
		
	
	public ConnectionGene(NodeGene from, NodeGene to) {
		this.from = from;
		this.to = to;
	}


	public NodeGene getFrom() {
		return from;
	}


	public void setFrom(NodeGene from) {
		this.from = from;
	}


	public NodeGene getTo() {
		return to;
	}


	public void setTo(NodeGene to) {
		this.to = to;
	}


	public double getWeight() {
		return weight;
	}


	public void setWeight(double weight) {
		this.weight = weight;
	}


	public boolean isEnabled() {
		return enabled;
	}


	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public int getReplaceIndex() {
		return replaceIndex;
	}

	public void setReplaceIndex(int replaceIndex) {
		this.replaceIndex = replaceIndex;
	}

	//The equals function only checks if the to and from nodes are the same
	//It does not check whether the innovationNumbers are the same since by our definition they are the same
	public boolean equals(Object object) {
		if(!(object instanceof ConnectionGene)) return false;
		ConnectionGene connectionGene2 = (ConnectionGene) object;
		return (from.equals(connectionGene2.from) && to.equals(connectionGene2.to));
	}
	
	//Assuming we don't go over out maximum number of nodes, 
	//this always provides a unique hash for each from and to node
	public int hashCode() {
		return from.getInnovationNumber() * Neat.MAX_NODES + to.getInnovationNumber();
	}

}
