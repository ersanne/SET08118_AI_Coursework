package UI;

import AStar.AStar;
import AStar.Node;
import DAL.AStarDAL;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public class WindowClass {
    private JFrame frame;
    private JButton quit_btn;
    private JButton selectFile_btn;
    private JButton reset_btn;
    private JButton autoRun_btn;
    private JTextArea info_textArea;
    private JButton nextStep_btn;
    private JPanel graphPanel;
    private JPanel mainPanel;
    private JPanel innerPanel;

    private AStar aStar;
    private AStarDAL aStarDAL;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    WindowClass window = new WindowClass();
                    window.frame.setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public WindowClass() {
        initialize();
    }

    private void initialize() {

        aStarDAL = AStarDAL.getInstance();

        frame = new JFrame();
        frame.setSize(1000, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);

        quit_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        selectFile_btn.addActionListener(new FilePanel(this));

        reset_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetAlgo(true);
            }
        });

        autoRun_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                aStar.autoRun();
            }
        });

        nextStep_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aStar.nextStep();
                redrawGraph();
            }
        });

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (graphPanel.getComponentCount() > 0) {
                    Component comp = graphPanel.getComponents()[0];
                    comp.setBounds(graphPanel.getX(), graphPanel.getY(), graphPanel.getWidth(), graphPanel.getHeight());
                    comp.repaint();
                    comp.setVisible(true);
                }
            }
        });

    }

    private void resetAlgo(boolean reReadFile) {
        aStarDAL.resetData();
        initializeGraph();
        info_textArea.setText(null);
        nextStep_btn.setEnabled(true);
        autoRun_btn.setEnabled(true);
    }

    private void enableAlgoBtns(){
        nextStep_btn.setEnabled(true);
        autoRun_btn.setEnabled(true);
        reset_btn.setEnabled(true);
    }

    void initializeGraph() {
        JPanel tempPanel = new GraphPanel(frame, aStar);
        tempPanel.setBounds(graphPanel.getX(), graphPanel.getY(), graphPanel.getWidth(), graphPanel.getHeight());
        tempPanel.setName("panel");
        graphPanel.add(tempPanel);
        graphPanel.repaint();
        graphPanel.setVisible(true);
        aStar = new AStar(this);
        enableAlgoBtns();
    }

    public void redrawGraph(){
        graphPanel.repaint();
    }

    JPanel getMainPanel() {
        return mainPanel;
    }

}
