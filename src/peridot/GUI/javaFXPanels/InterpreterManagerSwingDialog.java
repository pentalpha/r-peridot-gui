package peridot.GUI.javaFXPanels;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import peridot.GUI.MainGUI;
import peridot.Log;
import peridot.script.r.Interpreter;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created by pentalpha on 24/02/2018.
 */
public class InterpreterManagerSwingDialog extends JDialog {
    public static java.awt.Dimension defaultSize = new java.awt.Dimension(600, 400);
    private static int spacing = 0;
    //public static java.awt.Dimension rightButtonsSize = new java.awt.Dimension(160, defaultSize.height -spacing*2);
    //public static java.awt.Dimension leftListSize = new java.awt.Dimension(defaultSize.width-rightButtonsSize.width-spacing*3,
    //        rightButtonsSize.height);

    public static InterpreterManagerSwingDialog _instance =  null;
    public static final String titleString = "R Environment Manager";
    private final JFXPanel jfxPanel = new JFXPanel();

    public InterpreterManagerSwingDialog(java.awt.Frame parent){
        super(parent, true);
        this.setModal(true);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int)(screenSize.width - defaultSize.getWidth()) / 2;
        int y = (int)(screenSize.height - defaultSize.getHeight()) / 2;
        this.setLocation(x, y);
        setTitle(titleString);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                closeEvent();
            }
        });
        setResizable(false);
        this.setIconImage(MainGUI.getDefaultIcon(this));
        this.setLayout(new FlowLayout(FlowLayout.LEFT ,spacing, spacing));
        this.setMinimumSize(defaultSize);
        this.setPreferredSize(defaultSize);
        this.add(jfxPanel);

        _instance = this;

        Platform.runLater(() -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("InterpreterManager.fxml"));
                jfxPanel.setScene(new Scene(root, defaultSize.width-7, defaultSize.height-28));
            }catch (IOException ex){
                Log.logger.severe("Could not load InterpreterManagerJX scene.");
                ex.printStackTrace();
            }
        });
        this.setVisible(true);
    }

    private void closeEvent(){
        if(Interpreter.isDefaultInterpreterDefined() == false){
            int reply = JOptionPane.showConfirmDialog(_instance, "Since no R environment was chosen,"
                            + " R-Peridot will have to close.",
                    "Exiting Environment Manager without choosing an environment!"
                    , JOptionPane.OK_CANCEL_OPTION);
            if(reply == JOptionPane.CANCEL_OPTION){
                return;
            }
        }
        this.setVisible(false);
        this.dispose();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible) {
            ((DummyFrame)getParent()).dispose();
        }
    }

    public static class DummyFrame extends JFrame {
        public DummyFrame(String title) {
            super(title);
            setUndecorated(true);
            setVisible(true);
            setLocationRelativeTo(null);
            this.setIconImage(MainGUI.getDefaultIcon(this));
        }
    }
}
