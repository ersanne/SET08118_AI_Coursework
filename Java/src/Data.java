import javax.swing.*;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Data {

    private ArrayList<Node> nodeList;
    private ArrayList<Node> path;
    private float maxX = 0;
    private float minX = Float.MAX_VALUE;
    private float maxY = 0;
    private float minY = Float.MAX_VALUE;
    private int noOfCaves;
    private Point2D.Float[] caves;
    private boolean initialized;
    private File datafile;
    private ArrayList<Node> closedList;

    private static Data instance;

    private Data(){}

    public static Data getInstance(){

        if(instance == null){
            instance = new Data();
        }
        return instance;
    }

    public void readCaves(File datafile) throws IOException {

        this.datafile = datafile;
        caves = null;
        nodeList = new ArrayList<>();
        maxX = 0;
        minX = Float.MAX_VALUE;
        maxY = 0;
        minY = Float.MAX_VALUE;

        // Open input.cav
        BufferedReader br = new BufferedReader(new FileReader(datafile));

        //Read the line of comma separated text from the file
        String buffer = br.readLine();

        br.close();

        //Convert the data to an array
        String[] data = buffer.split(",");

        //Now extract data from the array - note that we need to convert from String to int as we go
        noOfCaves = Integer.parseInt(data[0]);

        if(noOfCaves > 20){
            throw new IOException();
        }


        //Get coordinates
        for (int count = 1, j = 0; count < ((noOfCaves*2)+1); count=count+2, j++){

            //Get x and y
            float x = Float.parseFloat(data[count]);
            float y = Float.parseFloat(data[count+1]);

            //Set or update maxX/minX maxY/minY
            if(x > maxX){
                maxX = x;
            }

            if(x < minX){
                minX = x;
            }

            if(y > maxY){
                maxY = y;
            }

            if(y < minY){
                minY = y;
            }

            //Create new node
            Node node = new Node(j, x, y);
            nodeList.add(node);
        }

        //Build connectivity matrix

        //Declare the array
        boolean[][] connected = new boolean[noOfCaves][];

        for (int row= 0; row < noOfCaves; row++){
            connected[row] = new boolean[noOfCaves];
        }
        //Now read in the data - the starting point in the array is after the coordinates
        int col = 0;
        int row = 0;

        Node goal = nodeList.get(noOfCaves-1);

        //Add destinations to nodes
        for (int point = (noOfCaves*2)+1 ; point < data.length; point++){
            //Work through the array

            if (data[point].equals("1")) {

                Node rowNode = nodeList.get(row);
                Node colNode = nodeList.get(col);

                rowNode.addDestination(colNode);

                rowNode.setH(goal);
            }
            row++;
            if (row == noOfCaves){
                row=0;
                col++;
            }
        }

        //Put all caves into the Point2D array for plotting
        caves = new Point2D.Float[noOfCaves];

        for(int i = 0; i < caves.length; i++){
            Node node = nodeList.get(i);
            float x = node.getX();
            float y = node.getY();

            caves[i] = new Point2D.Float(x, y);
        }

        //Let other classes know that data is initialized
        initialized = true;
    }

    //Method to access data (and reset the data)
    public ArrayList<Node> getNodeList(){
        return nodeList;
    }

    public Point2D.Float[] getCaves(){
        return caves;
    }

    public float getMaxX() {
        return maxX;
    }

    public float getMinX() {
        return minX;
    }

    public float getMaxY() {
        return maxY;
    }

    public float getMinY() {
        return minY;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setPath(ArrayList<Node> nodeList) {
        path = nodeList;
    }

    public ArrayList<Node> getPath(){
        return path;
    }

    public void resetPath(){
        path = null;
    }

    public void reInitilizeNodes(){

        Window window = new Window();

        try{
            readCaves(datafile);
        }catch (IOException e){
            JOptionPane.showMessageDialog(window.panel, "Could not parse file!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setClosedList(ArrayList<Node> closedList){
        this.closedList = closedList;
    }

    public ArrayList<Node> getClosedList(){
        return closedList;
    }
}
