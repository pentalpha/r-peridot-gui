package peridot.GUI.javaFXPanels;

import javafx.fxml.FXML;

import javafx.event.ActionEvent;
/**
 * Created by pentalpha on 20/03/2018.
 */
public class InterpreterManager {
    @FXML
    public void finish(ActionEvent event){
        InterpreterManagerGUI.end();
    }
}
