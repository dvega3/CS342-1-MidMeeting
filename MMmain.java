 /* ------------------------------------------------ 
  * Midpoint Meeting:
  * Input 3 files (hardcoded). One for city names, one for distances and one for participants
  * Create adjacency list for all cities and their distances. Represented as a graph for processing
  * Run modified prim's algorithm to calculate the shortest distance from one city
  * run algorithm to calculate shortest distance and city for all participants to meet.
  *
  * Class: CS 342, Fall 2016  
  * System: OS X, Eclipse IDE
  * Author Code Number: 3452R  
  *
  * ToDo: some of my city distances are off by 30miles exactly, it is only a couple of them and cannot find out the reason. 
  * however, doesn't affect the performance of the project and the result is the desired one
  * -------------------------------------------------
  */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

class Neighbor {
    public int vertexNum;
    public int distance;
    public Neighbor next;
    public Neighbor(int vnum, int distance, Neighbor nbr) {
            this.vertexNum = vnum;
            this.distance = distance;
            next = nbr;
    }
}

class Vertex {
    String name;
    int cityNumber;
    Neighbor adjList;
    Vertex(String name, int cityNumber, Neighbor neighbors) {
            this.name = name;
            this.cityNumber = cityNumber;
            this.adjList = neighbors;
    }
    public Neighbor getHead(){
    	return this.adjList;
    }
}


public class MMmain {

    static Vertex[] adjLists;

    public MMmain(String file) throws FileNotFoundException {

        Scanner sc = new Scanner(new File(file));

        String firstLine = sc.nextLine();
        int numCities= Integer.parseInt(firstLine);
        System.out.println("Generating space for " +numCities+ " cities.");
        adjLists = new Vertex[numCities+1];//allocate space for n number of cities
        //System.out.println(adjLists.length); testing length
        // read vertices
        for (int v=1; v <= adjLists.length-1; v++) {
        	//System.out.println("adding city number "+v);
            adjLists[v] = new Vertex(sc.nextLine(), (v), null);
        }

        //print adjacency list until now for testing purposes
        //for (int v=1; v <= adjLists.length-1; v++) {
           // System.out.println(v+": "+adjLists[v].name);
        //}

        // read edges from different file
        String file1 = "CityDistances.txt";
        Scanner sc1 = new Scanner(new File(file1));
        String firstLine1 = sc1.nextLine();
        int numDistances= Integer.parseInt(firstLine1);
        System.out.println("Loading the "+numDistances+ " distances from CityDistances.txt file...");

        int city1=0;
        int city2=0;
        int distance=0;
        for (int v=1; v <= numDistances-1; v++) {
            // read vertex names and translate to vertex numbers
            city1 = Integer.parseInt(sc1.next());
            city2 = Integer.parseInt(sc1.next());
            distance = Integer.parseInt(sc1.next());
            //System.out.println("From city "+city1+" to city "+city2+" there are "+distance+" miles.");
            // add v2 to front of v1's adjacency list and add v1 to front of v2's adjacency list
            adjLists[city1].adjList = new Neighbor(city2, distance, adjLists[city1].adjList);
            adjLists[city2].adjList = new Neighbor(city1, distance, adjLists[city2].adjList);
        }
    }

    public void print() {
    	System.out.println("The adjacency list is:");
        for (int v=1; v <= adjLists.length-1; v++) {
        	//System.out.println("print2repeat");
        	//System.out.println("\n");
        	System.out.print(adjLists[v].name+"\t");
            for (Neighbor nbr=adjLists[v].adjList; nbr != null;nbr=nbr.next) {
                System.out.print("->[" + adjLists[nbr.vertexNum].name+"]");
            }
            System.out.println("");
        }
    }

    public int[] primCreateMinSpanningTree(int start){
    	Neighbor pTemp;//temporary graph
    	int MAX_VERTICES= adjLists.length;
    	boolean[] isInTree= new boolean[MAX_VERTICES];
    	int[] distance= new int[MAX_VERTICES];
    	int[] parent= new int[MAX_VERTICES];
    	int currentVertex;
    	int adjacentVertex;
    	int weight;
    	int shortestNewNodeDistance;
        // Initialize all vertices as not being in the tree, having max distance and no parent.
    	for(int i=1; i<=MAX_VERTICES-1;i++){
    		isInTree[i]=false;
    		distance[i]=Integer.MAX_VALUE;
    		parent[i]=-1;
    	}
    	// Set values for starting node
        distance[start] = 0;
        currentVertex = start;
     // main loop, continued until all vertices are handled
        while (isInTree[ currentVertex] == false) {
            isInTree[ currentVertex] = true;	// Include current vertex into tree

            // Examine in turn each edge incident to the current vertex
            pTemp= adjLists[currentVertex].getHead();
            while(pTemp!=null){
            	adjacentVertex = pTemp.vertexNum;//or pTemp.next.vertexNum;
            	weight = pTemp.distance;

            	if(distance[adjacentVertex]>(distance[currentVertex]+weight)){
            		distance[ adjacentVertex] = distance[currentVertex] + weight;
                    parent[ adjacentVertex] = currentVertex;
            	}
            	pTemp=pTemp.next;
            }
            currentVertex=1;
            shortestNewNodeDistance=Integer.MAX_VALUE;

            // Examine each vertex in graph
            for (int i=1; i<=MAX_VERTICES-1; i++) {
            	if ((isInTree[i] == false) && (shortestNewNodeDistance > distance[i])) {
            		// This ith vertex is not yet in tree and is closest so far
            		shortestNewNodeDistance = distance[i];  // set new shortest distance
            		currentVertex = i;                      // set new closest vertex
            	}
            }//end for( int i...

        }//end while( isInTre...

        return distance;
    }

    public void findMidpoint() throws FileNotFoundException{
    	String file2 = "Participants.txt";
        Scanner sc2 = new Scanner(new File(file2));
        String firstLine2 = sc2.nextLine();
        int numParticipants= Integer.parseInt(firstLine2);
        System.out.println("Loading the "+numParticipants+ " participants from Participants.txt file...");

        String name;
        int cityNumber=0;
        int [] participants=new int[numParticipants];
        for (int i=0; i < numParticipants; i++) {
          // read participant names and their cities
          name = sc2.next();
          cityNumber = Integer.parseInt(sc2.next());
          participants[i] = cityNumber;
        }
        float lowestAvgDistance = Float.MAX_VALUE;
        int lowestAvgCity = 0;

        for (int i=1; i<=adjLists.length - 1; i++) {
          int totalDistance = 0;
          int [] distances = primCreateMinSpanningTree(i); // shortest distance from i to all other cities
          for (int j=0;j<numParticipants;j++) {
            int participantCity = participants[j]; // [58, 73, 23]
            totalDistance += distances[participantCity]; // shortest distance from i -> 58
          }

          float avgDistance = (float)totalDistance / numParticipants;
          if(avgDistance<lowestAvgDistance){
            lowestAvgDistance = avgDistance;
            lowestAvgCity = i;
          }
        }

        String cityName = adjLists[lowestAvgCity].name;
        System.out.println("The closest city for all participants is: "+cityName+" with an average distance of "+lowestAvgDistance+ " miles.");
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    throws IOException {
    	System.out.println("Author Code Number: 229V");
    	System.out.println("Class: CS 342, Fall 2016");
    	System.out.println("Program: #1, Mid Meeting\n");
        // TODO Auto-generated method stub
        System.out.println("Opening CityNames.txt file...");
        String file = "CityNames.txt";
        MMmain graph = new MMmain(file);
        //uncomment to visualize the adjacency list
        //graph.print();
        System.out.println("Done creating the Adjacency list! Uncomment code to visualize it. "
        		+ "\nLet's calculate the distance from Chicago [58] to all other cities:");
        int startCity=58;
        //int [] parent= new int[adjLists.length];
        int[] distances = graph.primCreateMinSpanningTree(startCity);
        /*print to see the distance from Chicago to each city
        for(int v=1; v <= adjLists.length-1; v++){
        	System.out.println("Distance from "+startCity+" to "+v+" is: "+distances[v]+" miles.");
        }
        */
        System.out.println("Done! Uncomment code to see results.");
        System.out.println("Now lets find the city where all participants should meet.");
        graph.findMidpoint();
    }
}
