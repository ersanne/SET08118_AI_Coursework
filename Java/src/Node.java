import java.util.*;

public class Node {
    private final int id;
    private final float x;
    private final float y;
    private float g; //Distance from start
    private float h; //Heuristic of goal
    private float f; //f = g + h
    HashMap<Node, Float> destCosts = new HashMap<>(); //Map containing all possible destination / neighbours
    Node parent = null;

    public Node(int id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.g = Float.MAX_VALUE;
    }

    public int getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getG() {
        return g;
    }

    public void setG(float g) {
        this.g = g;
    }

    public void setH(Node goal) {
        this.h = calcCost(goal);
    }

    public Float getH(){
        return h;
    }

    public float getF() {
        return f;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Map<Node, Float> getDestMap(){
        return destCosts;
    }

    public void calcF(){
        this.f = g + h;
    }

    //Add a destination to the node
    public void addDestination(Node dest){
        if(destCosts == null){
            throw new NullPointerException("Destination map must exist");
        }

        destCosts.put(dest,this.calcCost(dest));
    }

    //Calculate cost between this node and another node using pythagoras
    public float calcCost(Node dest){

        float x = (float) Math.pow(dest.getX() - this.getX(), 2.0);
        float y = (float) Math.pow(dest.getY() - this.getY(), 2.0);
        double xy = (double) x+y;
        return (float) Math.sqrt(xy);
    }
}