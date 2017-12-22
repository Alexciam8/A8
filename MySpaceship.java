// adc226
package student;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import models.Edge;
import models.Node;
import models.NodeStatus;
import models.RescueStage;
import models.ReturnStage;
import models.Spaceship;

/** An instance implements the methods needed to complete the mission */
public class MySpaceship extends Spaceship {

	/**
	 * Explore the galaxy, trying to find the missing spaceship that has crashed
	 * on Planet X in as little time as possible. Once you find the missing
	 * spaceship, you must return from the function in order to symbolize that
	 * you've rescued it. If you continue to move after finding the spaceship
	 * rather than returning, it will not count. If you return from this
	 * function while not on Planet X, it will count as a failure.
	 * 
	 * At every step, you only know your current planet's ID and the ID of all
	 * neighboring planets, as well as the ping from the missing spaceship.
	 * 
	 * In order to get information about the current state, use functions
	 * currentLocation(), neighbors(), and getPing() in RescueStage. You know
	 * you are standing on Planet X when foundSpaceship() is true.
	 * 
	 * Use function moveTo(long id) in RescueStage to move to a neighboring
	 * planet by its ID. Doing this will change state to reflect your new
	 * position.
	 */
	
	private boolean found = false;
	@Override
	public void rescue(RescueStage state) {
		// TODO : Find the missing spaceship
		HashMap<Long, Boolean> visited= new HashMap<Long, Boolean>();

		dfs(state.currentLocation(), null, state, visited);

	}

	public void dfs(Long current, Long previous, RescueStage state, HashMap<Long, Boolean> visited) { 
		Collection<NodeStatus> neighbors = state.neighbors();
		if(state.foundSpaceship() || found){ //base case
			found = true;
			return;
		}
	
		visited.put(current, true);
		LinkedList sortedNeighbors = new LinkedList();
		for(NodeStatus e: neighbors){
			if(!(visited.containsKey(e.getId()))){
				//adds neighbors to a listed sorted by ping
				sortedNeighbors = sortedList(sortedNeighbors, e); 
			}
		}

		for(Object e: sortedNeighbors){
			if(!found)
				//moves to the neighbor with the highest ping
				state.moveTo(((NodeStatus)e).getId());
			dfs(((NodeStatus)e).getId(), current, state, visited); 
		}
						
		if(state.foundSpaceship() || found){
			found = true;
			return;
		}
		if(previous == null)
			return;
		state.moveTo(previous);
		
	} 
	
	/**
	 * Adds a NodeStatus to a LinkedList in sorted order based
	 * on ping form high to low
	 */
	public LinkedList sortedList(LinkedList pings, NodeStatus d){
		LinkedList sortedPings = pings;
		if(pings == null){
			sortedPings = new LinkedList();
			sortedPings.add(d);
			return sortedPings;
		}	

		int place = 0;
		for(Object d1: sortedPings){
			if(d.getPingToTarget() > ((NodeStatus)d1).getPingToTarget()){
				sortedPings.add(place, d);
				return sortedPings;
			}
			place++;	
		}
		
		sortedPings.add(d);
		return sortedPings;
	}
	
	

	/**
	 * Get back to Earth, avoiding hostile troops and searching for speed
	 * upgrades on the way. Traveling through 3 or more planets that are hostile
	 * will prevent you from ever returning to Earth.
	 *
	 * You now have access to the entire underlying graph, which can be accessed
	 * through ScramState. currentNode() and getEarth() will return Node objects
	 * of interest, and getNodes() will return a collection of all nodes in the
	 * graph.
	 *
	 * You may use state.grabSpeedUpgrade() to get a speed upgrade if there is
	 * one, and can check whether a planet is hostile using the isHostile
	 * function in the Node class.
	 *
	 * You must return from this function while on Earth. Returning from the
	 * wrong location will be considered a failed run.
	 *
	 * You will always be able to return to Earth without passing through three
	 * hostile planets. However, returning to Earth faster will result in a
	 * better score, so you should look for ways to optimize your return.
	 */
	@Override
	public void returnToEarth(ReturnStage state) {
		// TODO: Return to Earth
		List<Node> shortestPath = shortestPath(state.currentNode(), state.getEarth());
		
		System.out.println(shortestPath);
		for(Node n: shortestPath){
			if(n != state.currentNode())
				state.moveTo(n);
			if(n.hasSpeedUpgrade())
				state.grabSpeedUpgrade();
			
			
		}
	}
	
	/** Return the shortest path from start to end, or the empty list if a path
     * does not exist.
     * Note: The empty list is NOT "null"; it is a list with 0 elements. */
    public static List<Node> shortestPath(Node start, Node end) {
        /* TODO Read note A7 FAQs on the course piazza for ALL details. */
        Heap<Node> F= new Heap<Node>(); // As in lecture slides
        int hp = 0;
        // map contains an entry for each node in S or F. Thus,
        // |map| = |S| + |F|.
        // For each such key-node, the value part contains the shortest known
        // distance to the node and the node's backpointer on that shortest path.
        HashMap<Node, SFdata> map= new HashMap<Node, SFdata>();

        
        F.add(start, 0);
        map.put(start, new SFdata(0, null, 0, 1));
        // invariant: as in lecture slides, together with def of F and map
        while (F.size() != 0) {

            Node f= F.poll();
            if(map.get(f).hp < 3){
	            if (f == end){ 
	            return constructPath(end, map);}
	            
	         int fDist= map.get(f).distance;	         
	         for (Edge e : f.getExits()) {// for each neighbor w of f
		                Node w= e.getOther(f);
		                //distance with respect to speed
		                int newWdist= fDist + (int)(((double)e.length)/map.get(f).speed);
		               
		                SFdata wData= map.get(w);
		                
		                if (wData == null) { //if w not in S or F
			                if(f.isHostile()) //creates new SFData based on hostility 
			                	if(f.hasSpeedUpgrade() && map.get(f).speed == 1)
			                		map.put(w, new SFdata(newWdist, f,
			                				map.get(f).hp + 1, map.get(f).speed + .2));
			                	else if(f.hasSpeedUpgrade() && map.get(f).speed > 1)
			                		map.put(w, new SFdata(newWdist, f,
			                				map.get(f).hp + 1, map.get(f).speed));
			                	else if(!f.hasSpeedUpgrade() && map.get(f).speed > 1)
			                		map.put(w, new SFdata(newWdist, f,
			                				map.get(f).hp + 1, map.get(f).speed - .2));
			                	else
			                		map.put(w, new SFdata(newWdist, f,
			                				map.get(f).hp + 1, map.get(f).speed));

			                else
			                	if(f.hasSpeedUpgrade())
			                		map.put(w, new SFdata(newWdist,
			                				f, map.get(f).hp, map.get(f).speed + .2));
			                	else
			                		map.put(w, new SFdata(newWdist,
			                				f, map.get(f).hp, map.get(f).speed));
	
		                    F.add(w, newWdist);
		                } else if (newWdist < wData.distance) {
		                	 if(f.isHostile()) //modifies SFData based on hostility   	
		                		 if(f.hasSpeedUpgrade() && map.get(f).speed == 1){
			                		wData.hp = map.get(f).hp + 1;
	                	 			wData.speed = map.get(f).speed +.2;
			                	}
			                	else if(f.hasSpeedUpgrade() && map.get(f).speed > 1)
			                		wData.hp = map.get(f).hp + 1;
			                	else if(!f.hasSpeedUpgrade() && map.get(f).speed > 1){
			                		wData.hp = map.get(f).hp + 1;
	                	 			wData.speed = map.get(f).speed - .2;
			                	}
			                	else
			                		wData.hp = map.get(f).hp + 1;
             	
		                    wData.distance= newWdist;
		                    wData.backPointer= f;
		                    
		                   //if(F.valPos.containsKey(w));
		                    //F.updatePriority(w, newWdist);
		                }
		            }
	            
            }
	        }
            
        // no path from start to end
        return new LinkedList<Node>();
    }


    /** Return the path from the start node to node end.
     *  Precondition: nData contains all the necessary information about
     *  the path. */
    public static List<Node> constructPath(Node end, HashMap<Node, SFdata> nData) {
        LinkedList<Node> path= new LinkedList<Node>();
        Node p= end;
        // invariant: All the nodes from p's successor to the end are in
        //            path, in reverse order.
        while (p != null) {
            path.addFirst(p);
            p= nData.get(p).backPointer;
        }
        return path;
    }

    /** Return the sum of the weights of the edges on path path. */
    public static int pathDistance(List<Node> path) {
        if (path.size() == 0) return 0;
        synchronized(path) {
            Iterator<Node> iter= path.iterator();
            Node p= iter.next();  // First node on path
            int s= 0;
            // invariant: s = sum of weights of edges from start to p
            while (iter.hasNext()) {
                Node q= iter.next();
                s= s + p.getConnect(q).length;
                p= q;
            }
            return s;
        }
    }

    /** An instance contains information about a node: the previous node
     *  on a shortest path from the start node to this node and the distance
     *  of this node from the start node. */
    private static class SFdata {
        private Node backPointer; // backpointer on path from start node to this one
        private int distance; // distance from start node to this one
        private int hp; //number of hostile planets visited
        private double speed; //speed of rocket
        /** Constructor: an instance with distance d from the start node and
         *  backpointer p.*/
        private SFdata(int d, Node p, int h, double s) {
            distance= d;     // Distance from start node to this one.
            backPointer= p;  // Backpointer on the path (null if start node)
            hp = h; // number of hostile planets visited
            speed = s; //speed of rocket
            
        }

        /** return a representation of this instance. */
        public String toString() {
            return "dist " + distance + ", bckptr " + backPointer + ", hp " + hp + ", speed " + speed;
        }
    }
	
}