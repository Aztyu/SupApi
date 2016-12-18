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
    
    public List<List<StationList>> getList(){		//Fonction récursive qui déroule l'arbre en tableau à 2 dimensions de trajets utilisables par le site
    	List<List<StationList>> list = new ArrayList<List<StationList>>();
    	
    	if(children.isEmpty()){	//Si il n'y a pas d'enfant on retourne la StationList
    		StationList sl = new StationList();
    		sl.setStart(parent.getValue());
    		sl.setStop(value);
    		
    		list.add(new ArrayList<StationList>());
    		list.get(0).add(sl);
    	}else{
	    	for(Node n : children){		//Si il y a des enfants on les parse
	    		List<List<StationList>> sub_list = n.getList();
	    		
	    		StationList sl = new StationList();
	    		sl.setStart(value);
	    		sl.setStop(n.getValue());
	    		//todo fill up
	    		
	    		for(List<StationList> lsl : sub_list){
	    			lsl.add(0, sl);
	    			list.add(lsl);
	    		}
	    	}
    	}
    	return list;
    }
}
