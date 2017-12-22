
package student;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.Board;
import models.Edge;
import models.Node;

/** This class contains Dijkstra's shortest-path algorithm and some other methods. */
public class Paths {

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

        System.out.println(start + " " + end);
        F.add(start, 0);
        map.put(start, new SFdata(0, null, 0));
        // invariant: as in lecture slides, together with def of F and map
        while (F.size() != 0) {
            Node f= F.poll();
            if(map.get(f).hp < 3){
	            if (f == end) return constructPath(end, map);
	            int fDist= map.get(f).distance;
	            hp++;
	            
	            if(hp != 3)
		            for (Edge e : f.getExits()) {// for each neighbor w of f
		                Node w= e.getOther(f);
		                int newWdist= fDist + e.length;
		                SFdata wData= map.get(w);
		                
		                if (wData == null) { //if w not in S or F
			                if(f.isHostile())
			                	map.put(w, new SFdata(newWdist, f, map.get(f).hp + 1));
			                else
			                	map.put(w, new SFdata(newWdist, f, map.get(f).hp));
	
		                    F.add(w, newWdist);
		                } else if (newWdist < wData.distance) {
		                    wData.distance= newWdist;
		                    wData.backPointer= f;
		                    F.updatePriority(w, newWdist);
		                }
		            }
	            else
	            	hp--;
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
        
        /** Constructor: an instance with distance d from the start node and
         *  backpointer p.*/
        private SFdata(int d, Node p, int h) {
            distance= d;     // Distance from start node to this one.
            backPointer= p;  // Backpointer on the path (null if start node)
            hp = h; // number of hostile planets visited
        }

        /** return a representation of this instance. */
        public String toString() {
            return "dist " + distance + ", bckptr " + backPointer + ", hp " + hp;
        }
    }
}
