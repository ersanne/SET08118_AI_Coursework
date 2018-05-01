import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Map;

class GraphPanel extends JPanel {

    private Data data = Data.getInstance();
    private int padding = 25;
    private JPanel panel;
    private JFrame frame;

    public GraphPanel(JFrame newFrame){
        frame = newFrame;
        panel = new JPanel(new FlowLayout());
    }

    //Draw the graph
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setStroke(new BasicStroke(2));

        //Check that graph data has been read
        if(data.isInitialized()){
            Float maxX = data.getMaxX();
            Float maxY = data.getMaxY();

            int x = Math.round(maxX);
            int y = Math.round(maxY);

            int scale = 1;

            //Calculate scaling for the graph so it fits any coordinates onto the fixed size panel
            if(maxX > maxY){
                scale = 300 / (x);
            }else{
                scale = 105 / (y);
            }

            //Get rquired data
            ArrayList<Node> nodeList = data.getNodeList();
            ArrayList<Node> path = data.getPath();
            ArrayList<Node> closedList = data.getClosedList();
            Float diam = 16f;

            //Paint all nodes and paths, unfortunately it currently does not paint arrowhead
            //I'm sure this could be written a lot cleaner and probably with a lot less code
            for (int i = 0; i < nodeList.size(); i++) {

                Node cave = nodeList.get(i);

                //Check if path should be painted
                if(path == null){

                    Map<Node, Float> destMap = cave.getDestMap();
                    for(Map.Entry<Node, Float> neighborEntry : destMap.entrySet()) {
                        Node dest = neighborEntry.getKey();

                        g2d.setPaint(Color.blue);
                        g2d.drawLine(Math.round(10 + cave.getX()*scale), Math.round(187 - cave.getY()*scale), Math.round(10 + dest.getX()*scale), Math.round(187 - dest.getY()*scale));

                    }

                    //Check for start and end nodes
                    if(i == 0){
                        g2d.setPaint(Color.green);
                        g2d.fill(new Ellipse2D.Float((cave.getX()*scale), 180 - (cave.getY()*scale), diam, diam));
                    }else if(i == nodeList.size()-1){
                        g2d.setPaint(Color.red);
                        g2d.fill(new Ellipse2D.Float((cave.getX()*scale), 180 - (cave.getY()*scale), diam, diam));
                    }else{
                        closedList(g2d, scale, diam, closedList, cave);
                    }


                }else{

                    Map<Node, Float> destMap = cave.getDestMap();

                    for(Map.Entry<Node, Float> neighborEntry : destMap.entrySet()) {
                        Node dest = neighborEntry.getKey();

                        if(path.contains(dest) && dest.parent == cave){
                            continue;
                        }

                        g2d.setPaint(Color.blue);
                        g2d.drawLine(Math.round(10 + cave.getX()*scale), Math.round(187 - cave.getY()*scale), Math.round(10 + dest.getX()*scale), Math.round(187 - dest.getY()*scale));

                    }

                    //Paint the path over the blue lines
                    if(path.contains(cave) && cave.parent != null){
                        g2d.setPaint(Color.yellow);
                        g2d.drawLine(Math.round(10 + cave.getX()*scale), Math.round(187 - cave.getY()*scale), Math.round(10 + cave.parent.getX()*scale), Math.round(187 - cave.parent.getY()*scale));
                    }

                    //Check for start and end nodes
                    if(i == 0){
                        g2d.setPaint(Color.green);
                        g2d.fill(new Ellipse2D.Float((cave.getX()*scale), 180 - (cave.getY()*scale), diam, diam));
                    }else if(i == nodeList.size()-1){
                        g2d.setPaint(Color.red);
                        g2d.fill(new Ellipse2D.Float((cave.getX()*scale), 180 - (cave.getY()*scale), diam, diam));
                    }else{
                        if(path.contains(cave)){
                            g2d.setPaint(Color.yellow);
                            g2d.fill(new Ellipse2D.Float((cave.getX()*scale), 180 - (cave.getY()*scale), diam, diam));
                        }else {
                            closedList(g2d, scale, diam, closedList, cave);
                        }
                    }
                }
            }
        }


    }

    //Method for painting the closed list nodes in cyan color
    private void closedList(Graphics2D g2d, int scale, Float diam, ArrayList<Node> closedList, Node cave){
        if(closedList != null) {
            if(closedList.contains(cave)){
                g2d.setPaint(Color.cyan);
                g2d.fill(new Ellipse2D.Float((cave.getX()*scale), 180 - (cave.getY()*scale), diam, diam));
            }else {
                g2d.setPaint(Color.blue);
                g2d.fill(new Ellipse2D.Float((cave.getX()*scale), 180 - (cave.getY()*scale), diam, diam));
            }
        }else{
            g2d.setPaint(Color.blue);
            g2d.fill(new Ellipse2D.Float((cave.getX()*scale), 180 - (cave.getY()*scale), diam, diam));
        }
    }
}