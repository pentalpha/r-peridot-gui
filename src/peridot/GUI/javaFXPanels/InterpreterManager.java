package peridot.GUI.javaFXPanels;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import peridot.Operations;
import peridot.script.r.Interpreter;
import sun.rmi.runtime.Log;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by pentalpha on 20/03/2018.
 */
public class InterpreterManager implements Initializable{

    HashMap<TitledPane, Integer> interpreterOfPane;
    TitledPane selectedPane = null;

    @FXML
    Accordion accordion;
    @FXML
    Label recommendation;

    @FXML
    public void installPackages(ActionEvent event){

    }

    public void chooseInterpreter(TitledPane expanded){
        selectedPane = expanded;
        int i = interpreterOfPane.get(expanded);
        if(Interpreter.setDefault(i)){
            if(Interpreter.defaultInterpreter.getPackagesToInstall().size() > 0){
                recommendation.setVisible(true);
            }else{
                recommendation.setVisible(false);
            }
            expanded.setFont(Font.font(expanded.getFont().getFamily(), FontWeight.BOLD, expanded.getFont().getSize()));
        }else{
            peridot.Log.logger.warning("Invalid environment chosen, not using it.");
        }
    }

    @FXML
    public void addInterpreter(ActionEvent event){

    }

    @FXML
    public void removeInterpreter(ActionEvent event){

    }

    public void updateInterpreterAccordion(){
        this.startAccordion();
    }

    public void startAccordion(){
        interpreterOfPane = new HashMap<>();
        accordion.getPanes().remove(0, accordion.getPanes().size());
        int i = 0;
        for(Interpreter interpreter : Interpreter.interpreters){
            TitledPane pane = getTitledPaneFromEnvironment(interpreter);
            accordion.getPanes().add(pane);
            interpreterOfPane.put(pane, i);
            i++;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        peridot.Log.logger.info("Initializing R Environment Manager");
        startAccordion();
        //accordion.
    }

    public void paneExpanded(TitledPane pane, boolean expanded){

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

        pane.expandedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue){
                    //peridot.Log.logger.info(pane.getText() + " pane expanded!");
                    if(pane != selectedPane){
                        pane.setFont(Font.font(pane.getFont().getFamily(), FontWeight.BOLD, pane.getFont().getSize()));
                        chooseInterpreter(pane);
                        for(TitledPane p : accordion.getPanes()){
                            if(p != pane){
                                p.setFont(Font.font(p.getFont().getFamily(), FontWeight.NORMAL, p.getFont().getSize()));
                            }
                        }
                    }
                }else{
                    //boolean makeNormal = true;
                    //peridot.Log.logger.info(pane.getText() + " pane closed!");
                    //if(Interpreter.isDefaultInterpreterDefined()){
                    //    if(Interpreter.interpreters.get(interpreterOfPane.get(pane)).exe.equals(Interpreter.defaultInterpreter)){
                    //        makeNormal = pane == selectedPane;
                    //    }
                    //}
                    //if(pane != selectedPane){
                    //    pane.setFont(Font.font(pane.getFont().getFamily(), FontWeight.NORMAL, pane.getFont().getSize()));
                    //}
                }
            }
        });
        return pane;
    }
}
