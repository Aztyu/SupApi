package com.supinfo.supapi.entity;

import java.util.ArrayList;
import java.util.List;

public class Node
{
	private Node parent;
    private List<Node> children = null;
    private Station value;

    public Node(Station value, Node parent){
    	this.parent = parent;
        this.children = new ArrayList<Node>();
        this.value = value;
    }

    public void addChild(Node child){
        children.add(child);
    }
    
    public List<Node> getChild(){
    	return children;
    }

    public Node getParent(){
    	return parent;
    }
    
    public Station getValue(){
    	return value;
    }
}
