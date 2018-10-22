package UI;

import AStar.Node;
import DAL.AStarDAL;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

class FilePanel implements ActionListener {

    WindowClass window;
    AStarDAL aStarDAL = AStarDAL.getInstance();

    FilePanel(WindowClass window){
        this.window = window;
    }

    public void actionPerformed(ActionEvent e) {
        JFrame fileFrame = new JFrame();
        JPanel filePanel = new JPanel();
        JFileChooser fileChooser = new JFileChooser();
        fileFrame.getContentPane().add(filePanel);
        filePanel.add(fileChooser);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(filePanel);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        //Set selected file
        File datafile = fileChooser.getSelectedFile();

        //Read selected file
        ArrayList<Node> nodeList;
        try{
            nodeList = aStarDAL.readCaves(datafile);
        }catch(Exception excep){
            JOptionPane.showMessageDialog(window.getMainPanel(), "Could not parse file! Make sure it is a .cav file with less than 20 caves.", "Error", JOptionPane.ERROR_MESSAGE);
            excep.printStackTrace();
            return;
        }

        window.initializeGraph();
    }
}