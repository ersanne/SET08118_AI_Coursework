import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

class FilePanel implements ActionListener {

    Window window;
    Data data = Data.getInstance();

    FilePanel(Window window){
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
        try{
            data.readCaves(datafile);
        }catch(Exception excep){
            JOptionPane.showMessageDialog(window.panel, "Could not parse file! Make sure it is a .cav file with less than 20 caves.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Reset algo in case there was already a file read previously and some info may still be set
        window.resetAlgo(false);
        //Enable buttons for using the algo as it has been initialized
        window.enableAlgoBtn();
    }
}