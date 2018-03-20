package peridot.GUI.javaFXPanels;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import peridot.GUI.MainGUI;
import peridot.GUI.component.*;
import peridot.Log;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static javafx.concurrent.Worker.State.FAILED;

/**
 * Created by pentalpha on 24/02/2018.
 */
public class InterpreterManagerGUI extends JDialog {
    public static java.awt.Dimension defaultSize = new java.awt.Dimension(600, 400);
    private static int spacing = 0;
    //public static java.awt.Dimension rightButtonsSize = new java.awt.Dimension(160, defaultSize.height -spacing*2);
    //public static java.awt.Dimension leftListSize = new java.awt.Dimension(defaultSize.width-rightButtonsSize.width-spacing*3,
    //        rightButtonsSize.height);

    public static InterpreterManagerGUI _instance =  null;
    private final JFXPanel jfxPanel = new JFXPanel();

    public InterpreterManagerGUI(){
        this.setModal(true);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int)(screenSize.width - defaultSize.getWidth()) / 2;
        int y = (int)(screenSize.height - defaultSize.getHeight()) / 2;
        this.setLocation(x, y);
        setTitle("R Environment Manager");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
                jfxPanel.setScene(new Scene(root, defaultSize.width-20, defaultSize.height-30));
            }catch (IOException ex){
                Log.logger.severe("Could not load InterpreterManager scene.");
                ex.printStackTrace();
            }
        });
        this.setVisible(true);
    }

    public void finish(){
        this.setVisible(false);
        this.dispose();
    }

    public static void end(){
        SwingUtilities.invokeLater(() -> {
            _instance.finish();
        });
    }
}
