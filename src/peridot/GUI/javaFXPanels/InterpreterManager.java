package peridot.GUI.javaFXPanels;

import javafx.fxml.FXML;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import peridot.script.r.Interpreter;
import sun.rmi.runtime.Log;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by pentalpha on 20/03/2018.
 */
public class InterpreterManager implements Initializable{
    @FXML
    Accordion accordion;

    @FXML
    public void finish(ActionEvent event){
        InterpreterManagerGUI.end();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        peridot.Log.logger.info("Initializing R Environment Manager");
        //for(TitledPane pane : accordion.getPanes()){
        accordion.getPanes().remove(0, accordion.getPanes().size());
        //}
        for(Interpreter interpreter : Interpreter.interpreters){
            TitledPane pane = getTitledPaneFromEnvironment(interpreter);
            accordion.getPanes().add(pane);
        }
    }

    public TitledPane getTitledPaneFromEnvironment(Interpreter interpreter){
        AnchorPane newPanelContent = new AnchorPane();
        String toInstallString = interpreter.packagesToInstallString();
        if(toInstallString.length()  <= 1){
            toInstallString = "No packages to install.";
        }
        newPanelContent.getChildren().add(new Label(toInstallString));
        TitledPane pane = new TitledPane(interpreter.titleString(), newPanelContent);
        if(Interpreter.isDefaultInterpreterDefined()){
            if(Interpreter.defaultInterpreter.exe.equals(interpreter.exe)){
                pane.setFont(Font.font(pane.getFont().getFamily(), FontWeight.BOLD, pane.getFont().getSize()));
                pane.setText("Active: " + interpreter.titleString());
            }
        }
        //pane.set
        return pane;
    }
}
