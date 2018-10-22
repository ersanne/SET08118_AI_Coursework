package UI;

import AStar.AStar;
import AStar.Node;
import DAL.AStarDAL;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Map;

class GraphPanel extends JPanel {

    private AStar aStar;
    private AStarDAL aStarDAL = AStarDAL.getInstance();
    private int padding = 50;
    int height = this.getHeight() - padding;
    int width = this.getWidth() - padding;
    int xAdjust = 0;
    int yAdjust = 0;
    private JPanel panel;

    public GraphPanel(JFrame newFrame, AStar aStar){
        this.aStar = aStar;
    }

    //Draw the graph
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setStroke(new BasicStroke(2));

        //Dynamic sizing
        padding = (this.getWidth() < this.getHeight()) ? this.getWidth()/10 : this.getHeight()/10;
        width = this.getWidth() - padding;
        height = this.getHeight() - padding;

        //Scaling for current Window size
        int scale = ((width / aStarDAL.getMaxX()) < (height / aStarDAL.getMaxY())) ? Math.round(width / aStarDAL.getMaxX()) : Math.round(height / aStarDAL.getMaxY());

        //Get required AStarDAL
        ArrayList<Node> nodeList = aStarDAL.getNodeList();
        ArrayList<Node> path = aStarDAL.getCurrentPath();
        ArrayList<Node> closedList = aStarDAL.getClosedList();

        Float diam = 16f;

        System.out.println(padding);
        System.out.println(scale);
        System.out.println(aStarDAL.getMinY());

        for (Node node : nodeList) {

            Map<Node, Float> destMap = node.getDestMap();

            for (Map.Entry<Node, Float> neighborEntry : destMap.entrySet()) {

                Node dest = neighborEntry.getKey();
                if (path.contains(dest) && dest.getParent() == node) {
                    continue;
                }

                g2d.setPaint(Color.blue);
                drawLine(g2d, node.getX(), node .getY(), dest.getX(), dest.getY(), scale);
            }

            if (path.contains(node) && node.getParent() != null) {
                g2d.setPaint(Color.yellow);
                drawLine(g2d, node.getX(), node .getY(), node.getParent().getX(), node.getParent().getY(), scale);

            }
        }

        //Draw nodes last so they are on top (for nodes that are different colors than lines)
        for(Node node : nodeList){
            if (nodeList.indexOf(node) == 0) {
                g2d.setPaint(Color.green);
                drawNote(g2d,node,scale,diam);
            } else if (nodeList.indexOf(node) == nodeList.size() - 1) {
                g2d.setPaint(Color.red);
                drawNote(g2d,node,scale,diam);
            } else if (path.contains(node)) {
                g2d.setPaint(Color.yellow);
                drawNote(g2d,node,scale,diam);
            } else {
                closedList(g2d, scale, diam, closedList, node);
            }
        }

    }

    private void drawNote(Graphics2D g2d, Node node, int scale, float diam){
        g2d.fill(new Ellipse2D.Float((node.getX() * scale) - 7, (height - node.getY() * scale) - 7, diam, diam));
    }

    private void drawLine(Graphics2D g2d, float x1, float y1, float x2, float y2, int scale){
        g2d.drawLine(Math.round(x1 * scale), height - Math.round(y1 * scale), Math.round(x2 * scale), height - Math.round(y2 * scale));
    }

    private void drawLineWithArrow(Graphics2D g2d, int x1, int y1, int x2, int y2, int scale){

        int x[] = {0,10,0};
        int y[] = {-10,0,10};

        g2d.drawLine(Math.round(x1), Math.round(y1), Math.round(x2), Math.round(y2));

//        g2d.translate(x2,y2);

        double angle = getAngle(x1, y1, x2, y2);

//        g2d.rotate(angle);
//
//        g2d.translate(-x2,-y2);
//        g2d.rotate(-angle);

        g2d.fillPolygon(new Polygon(x ,y ,3));

    }

    private double getAngle(float x1, float y1, float x2, float y2){

        if((x2-x1)==0)
        {

            return Math.PI/2;
        }

        return Math.atan((y2-y1)/(x2-x1));

    }

    //Method for painting the closed list nodes in cyan color
    private void closedList(Graphics2D g2d, int scale, Float diam, ArrayList<Node> closedList, Node node){
        if(closedList != null) {
            if(closedList.contains(node)){
                g2d.setPaint(Color.cyan);
                drawNote(g2d,node,scale,diam);
            }else {
                g2d.setPaint(Color.blue);
                drawNote(g2d,node,scale,diam);
            }
        }else{
            g2d.setPaint(Color.blue);
            drawNote(g2d,node,scale,diam);
        }
    }
}