import java.awt.*;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Map;

/**
 * TODO: Add timer
 * TODO: Improve graphics
 * TODO: Code structure / Formatting / Comments
 * TODO: Improve given information
 **/

public class Window {

    JFrame frame;
    JPanel panel;
    JTextArea textArea;
    JButton resetButton;
    JButton nextStepButton;
    JButton autoRunButton;
    boolean autoRun = false;
    boolean nextStep = false;
    Thread aStarThread = new Thread();
    private ArrayList<Node> closedList = new ArrayList<>();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Window window = new Window();
                    window.frame.setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public Window() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     * Everything has a fixed size due to making it more simple. (This was my first attempt at Java graphics)
     */
    private void initialize() {
        //Create frame
        frame = new JFrame();
        frame.setBounds(100, 100, 615, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));
        //Create main panel
        panel = new JPanel();
        frame.getContentPane().add(panel);
        panel.setLayout(null);

        //Next step button
        nextStepButton = new JButton("Next step");
        nextStepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autoRun = false; //Set auto run to false (there is only 1 AStar method which can do both autoRun or step wise)

                //Check if the algorithm is already running i.e. it's not the first step
                if(!aStarThread.isAlive()){
                    //Start thread
                    aStarThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runAStar();
                        }
                    });

                    aStarThread.start();
                }else{
                    //If is already running tell it to do the next step
                    nextStep = true;
                }
            }
        });
        nextStepButton.setBounds(501, 227, 89, 23);
        panel.add(nextStepButton);

        //File selector, uses the FilePanel Class to generate a filepanel and read the caverns file
        JButton selectFileButton = new JButton("Select File");
        selectFileButton.addActionListener(new FilePanel(this));
        selectFileButton.setBounds(109, 227, 120, 23);
        panel.add(selectFileButton);

        //Reset button which resets all the lists as well as the displayed graph
        resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetAlgo(true);
            }
        });
        resetButton.setBounds(303, 227, 89, 23);
        panel.add(resetButton);

        //AutoRun button runs the AStar algorithm in autoRun mode
        autoRunButton = new JButton("Auto Run");
        autoRunButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                autoRun = true;

                if(aStarThread.isAlive()){
                    nextStep = true;
                }else{
                    runAStar();
                }

            }
        });
        autoRunButton.setBounds(402, 227, 89, 23);
        panel.add(autoRunButton);

        //Quit button to quit the program
        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        quitButton.setBounds(10, 227, 89, 23);
        panel.add(quitButton);

        //Initializing the graph panel (GraphPanel class)
        //It is later updated via the updateGraph method
        JPanel graphPanel = new GraphPanel(frame);
        graphPanel.setBounds(10, 11, 410, 205);
        panel.add(graphPanel);

        //Text area for displaying relevant information
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(430, 11, 160, 205);
        panel.add(scrollPane);

        //Prevent resizing which is important due to the fixed sized
        frame.setResizable(false);

        //Disable buttons for using the algorithm before the file is loaded
        disableAlgoBtn();

    }

    /**
     * Method to reinitialize all the algorithm data so it can be run again
     * @param reReadFile
     */
    public void resetAlgo(boolean reReadFile){
        Data data = Data.getInstance();
        data.setPath(null);
        data.setClosedList(null);
        if(reReadFile){
            data.reInitilizeNodes();
        }
        updateGraph();

        if(aStarThread.isAlive()){
            aStarThread.interrupt();
        }

        textArea.setText(null);
        nextStepButton.setEnabled(true);
        autoRunButton.setEnabled(true);
    }


    /**
     * Method to disable algorithmic buttons
     */
    public void disableAlgoBtn(){
        resetButton.setEnabled(false);
        autoRunButton.setEnabled(false);
        nextStepButton.setEnabled(false);
    }

    /**
     * Method to enable algorithmic buttons
     */
    public void enableAlgoBtn(){
        resetButton.setEnabled(true);
        autoRunButton.setEnabled(true);
        nextStepButton.setEnabled(true);
    }

    /**
     * Method to update the graphic panel
     * mostly used to update the graph from other classes
     */
    public void updateGraph() {
        panel.repaint();
        panel.setVisible(true);
    }

    /**
     * Method to run the AStar algorithm
     * Can return the path but is only used to check if no path was found in this case
     * @return
     */
    public ArrayList<Node> runAStar(){

            Data data = Data.getInstance(); //Get access to the data class which holds all data required in multiple classes (such as the cave data)
            ArrayList<Node> path = new ArrayList<>(); //Arraylist to save the path
            ArrayList<Node> nodeList = data.getNodeList(); //ArrayList containing all nodes/caves
            ArrayList<Node> openList = new ArrayList<>(); //OpenList contains all unchecked nodes
            closedList = new ArrayList<>(); //Closed list contains all checked nodes
            data.setClosedList(closedList); //reseting closed list as it sometimes wasnt reset properly
            long startTime = System.nanoTime(); //setting star time to measure amount of time taken

            openList.addAll(nodeList); //Add all nodes to openlist for initial state

            Node start = nodeList.get(0); //Get start node
            System.out.println(start.getId());
            Node goal = nodeList.get(nodeList.size() - 1); //Set goal node

            start.setG(0); //Set initial G (Distance from start node to this node)
            start.calcF(); //Calculate initial F (F = G + H) | H is the heuristic function

            Node current = start; //Set current node to start node

            //Start algorithm
            while (!openList.isEmpty()) {

                //Set the current node to the lowest F in the openList
                //If the node isn't connected to the explored path it won't have an F Score
                current = getLowestF(openList);

                //Check that auto run isn't on as this information should only be printed if stepping
                if(!autoRun){

                    //Print information on current state
                    textArea.append("Current Cave: " + current.getId() + "\n");
                    textArea.append("G = " + current.getG() + "\n");
                    textArea.append("H = " + current.getH() + "\n");
                    textArea.append("F = " + current.getF() + "\n");
                    textArea.append("Open List contains: " + "\n");
                    for(int i = 0; i < openList.size(); i++){
                        textArea.append(Integer.toString(openList.get(i).getId()) + ",");
                    }

                    textArea.append("\n");

                    if(closedList.size() > 0){
                        textArea.append("Closed List contains: " + "\n");
                        for(int i = 0; i < closedList.size(); i++){
                            textArea.append(Integer.toString(closedList.get(i).getId()) + ",");
                        }
                        textArea.append("\n");
                    }
                }

                //Check if the goal was reached
                if (current == goal) {

                    path = pathBuilder(goal);
                    data.setPath(path);
                    long endTime = System.nanoTime();
                    long duration = (endTime - startTime);

                    String timeformat;
                    if((duration/1000) > 1000){
                        duration = duration / 1000000;
                        timeformat = "milliseconds";
                    }else{
                        duration = duration / 1000;
                        timeformat = "microseconds";
                    }

                    textArea.append("Goal reached!");
                    textArea.append("\n" + "Distance = " + getPathCost(goal));
                    textArea.append("\n" + "Time taken = " + duration + "microseconds");
                    textArea.append("\n" + "Path: ");
                    for(int i = path.size()-1; i >= 0; i--){
                        Node node = path.get(i);
                        textArea.append(Integer.toString(node.getId()) + ",");
                    }

                    updateGraph();
                    nextStepButton.setEnabled(false);
                    autoRunButton.setEnabled(false);
                    return path;
                }

                //Remove current node from openList
                openList.remove(current);
                //Add current node to closed list
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

                if(!autoRun){
                    textArea.append("-------------------" + "\n");
                }

                //Update the closed list in the data class so it's up to date for all classes
                data.setClosedList(closedList);
                //Build path to current node if auto run is turned off
                if(!autoRun){
                    data.setPath(pathBuilder(current));
                }
                updateGraph();

                //Wait for next step if not in auto run mode
                nextStep = false;
                if (!autoRun) {
                    while(!nextStep){
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return null;
                        }
                    }
                }
            }

        //Return null if no path was found
        textArea.append("\n" + "No path found!");
        return null;

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