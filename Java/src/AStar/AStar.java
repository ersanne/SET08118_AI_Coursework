package AStar;

import DAL.AStarDAL;
import UI.WindowClass;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class AStar {

    private ArrayList<Node> path; //ArrayList to save the path
    private ArrayList<Node> nodeList; //ArrayList containing all nodes/caves
    private ArrayList<Node> openList; //OpenList contains all unchecked nodes
    private ArrayList<Node> closedList; //Closed list contains all checked nodes
    private ArrayList<Node> currentPath;
    private boolean shortestPathFound;
    private AStarDAL aStarDAL;
    private WindowClass window;

    public AStar(WindowClass window){
//        if(nodeList.isEmpty()){
//            throw new Exception("Node list can't be empty!");
//        }
        this.window = window;
        aStarDAL = AStarDAL.getInstance();
        this.nodeList = aStarDAL.getNodeList();
        path = new ArrayList<>();
        openList = new ArrayList<>();
        openList.addAll(nodeList);
        closedList = new ArrayList<>();
        currentPath = new ArrayList<>();
        shortestPathFound = false;
    }

    public ArrayList<Node> autoRun(){
        ArrayList<Node> currentPath = null;
        long startTime = System.nanoTime(); //setting star time to measure amount of time taken
        while(!shortestPathFound){
            nextStep();
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        return currentPath;
    }


    public void nextStep(){

        Node start = nodeList.get(0);
        Node goal = nodeList.get(nodeList.size() - 1);

        start.setG(0); //Set initial G (Distance from start node to this node)
        start.calcF(); //Calculate initial F (F = G + H) | H is the heuristic function

        Node current = start; //Set current node to start node

        if(openList.isEmpty() && !shortestPathFound){
            return;
        }

            //Set the current node to the lowest F in the openList
            //If the node isn't connected to the explored path it won't have an F Score
            current = getLowestF(openList);

            //Check if the goal was reached
            if (current == goal) {
                shortestPathFound = true;
                aStarDAL.setClosedList(closedList);
                aStarDAL.setCurrentPath(pathBuilder(current));
                window.redrawGraph();
                return;
            }

            openList.remove(current);
            closedList.add(current);

            //Get Map of all neigbours to the current node and the cost of travelling to the neighbour
            Map<Node, Float> currNeighbors = current.getDestMap();

            //Loop through map
            for (Map.Entry<Node, Float> neighborEntry : currNeighbors.entrySet()) {
                Node neighbor = neighborEntry.getKey();

                //If the node has already been explored skip this iteration
                if (closedList.contains(neighbor)) {
                    continue;
                }

                //get the distance to the neighbour node
                float neighborDistance = neighborEntry.getValue();
                //Calculate the new G
                float newG = neighborDistance + current.getG();

                //If the new G is lower than the old G, set the new G as the nodes G (In this a faster path has been found
                if (newG < neighbor.getG()) {
                    neighbor.setG(newG);
                    neighbor.calcF();
                }

                //Set neighbours parent to current node (which is important in order to build the final path later on
                neighbor.setParent(current);

                if (!openList.contains(neighbor)) {
                    openList.add(neighbor);
                }

            }

            aStarDAL.setClosedList(closedList);
            aStarDAL.setCurrentPath(pathBuilder(current));
            window.redrawGraph();
    }

    //Build path from current node to start node
    public ArrayList<Node> pathBuilder(Node node){
        ArrayList<Node> list = new ArrayList<>();
        list.add(node);
        while(node.parent != null){
            node = node.parent;
            list.add(node);
        }
        return list;
    }

    //Get the cost of the path from node to start node
    public Float getPathCost(Node node){

        float cost = 0;

        while(node.parent != null){
            cost += node.calcCost(node.parent);
            node = node.parent;
        }

        return cost;
    }

    //Get the lowest F in the openList
    public Node getLowestF(ArrayList<Node> openList){

        float lowest = Float.MAX_VALUE;
        Node lowestNode = null;

        for(int i = 0; i < openList.size(); i++){
            Node node = openList.get(i);
            if(node.getF() != 0){
                if(node.getF() < lowest){
                    lowest = node.getF();
                    lowestNode = node;
                }
            }
        }

        return lowestNode;
    }


}
