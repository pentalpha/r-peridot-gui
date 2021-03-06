package peridot.GUI.javaFXPanels;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.*;
import peridot.GUI.GUIUtils;
import peridot.GUI.MainGUI;
import peridot.script.r.Interpreter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by pentalpha on 20/03/2018.
 */
public class InterpreterManagerJX implements Initializable{
    static private InterpreterManagerJX _instance = null;

    HashMap<TitledPane, Integer> interpreterOfPane;
    TitledPane selectedPane = null;

    @FXML
    Accordion accordion;
    @FXML
    Label recommendation;
    @FXML
    Button rmEnvButton;
    @FXML
    Button installButton;
    @FXML
    Button addEnvButton;
    @FXML
    Button okButton;
    @FXML
    AnchorPane anchorPane;

    @FXML
    public void installPackages(ActionEvent event){
        if(selectedPane == null){
            return;
        }
        InstallationBatchJX ibJX = new InstallationBatchJX();
        Stage stage = new Stage();
        //Scene scene = new Scene(ibJX, 396, 400);
        Scene scene = new Scene(ibJX);
        //stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("Installation Queue");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        java.awt.Image awtIcon = MainGUI.getDefaultIcon();
        BufferedImage buffImage = GUIUtils.toBufferedImage(awtIcon);
        Image fxIcon = SwingFXUtils.toFXImage(buffImage, null);
        stage.getIcons().add(fxIcon);
        //stage.setResizable(false);
        ibJX.installPackagesIn(Interpreter.interpreters.get(interpreterOfPane.get(selectedPane)));
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                ibJX.askToStopNow();
                startAccordion();
            }
        });
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
            expanded.setText(Interpreter.interpreters.get(interpreterOfPane.get(expanded)).titleString());
        }else{
            peridot.Log.logger.warning("Invalid environment chosen, not using it.");
        }
    }

    @FXML
    public void okButton(ActionEvent event){
        InterpreterManagerSwingDialog.closeWindow();
    }

    @FXML
    public void addInterpreter(ActionEvent event){
        Node source = (Node) event.getSource();
        Window theStage = source.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(theStage);
        if(file != null){
            if(file.exists() && file.isFile()){
                boolean valid = Interpreter.addInterpreter(file.getAbsolutePath());
                if(valid){
                    startAccordion();
                    return;
                }
            }
        }

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid R Environment");
        alert.setHeaderText(null);
        alert.setContentText("Please choose a R executable next time.");

        alert.showAndWait();
    }

    @FXML
    public void removeInterpreter(ActionEvent event){
        if(selectedPane != null){
            int activeInterpreter = interpreterOfPane.get(selectedPane);
            boolean removed = Interpreter.removeInterpreter(activeInterpreter);
            if(removed){
                startAccordion();
            }
        }
    }

    public void updateInterpreterAccordion(){
        this.startAccordion();
    }

    public void startAccordion(){
        interpreterOfPane = new HashMap<>();
        accordion.getPanes().remove(0, accordion.getPanes().size());
        int i = 0;
        selectedPane = null;
        for(Interpreter interpreter : Interpreter.interpreters){
            TitledPane pane = getTitledPaneFromEnvironment(interpreter);
            accordion.getPanes().add(pane);
            interpreterOfPane.put(pane, i);
            i++;
        }

    }

    public void updateButtonsEnabled(){
        rmEnvButton.setDisable(true);
        installButton.setDisable(true);
        if(selectedPane != null){
            rmEnvButton.setDisable(false);
            try {
                if (Interpreter.interpreters.get(interpreterOfPane.get(selectedPane)).getPackagesToInstall().size() > 0) {
                    installButton.setDisable(false);
                }
            }catch (NullPointerException ex){
                //do nothing
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        _instance = this;
        peridot.Log.logger.info("Initializing R Environment Manager");
        startAccordion();
        okButton.setGraphic(new ImageView(
                new Image(getClass().getResourceAsStream("/icons/Clear-Green-Button-icon24.png")
                )));
        addEnvButton.setGraphic(new ImageView(
                new Image(getClass().getResourceAsStream("/icons/add-green-button-icon-24.png")
                )));
        rmEnvButton.setGraphic(new ImageView(
                new Image(getClass().getResourceAsStream("/icons/Delete-icon-24.png")
                )));
        installButton.setGraphic(new ImageView(
                new Image(getClass().getResourceAsStream("/icons/download-icon-24.png")
                )));

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
                pane.setText(interpreter.titleString());
                selectedPane = pane;
                pane.setExpanded(true);
            }
        }

        pane.expandedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue){
                    //peridot.Log.logger.info(pane.getText() + " pane expanded!");
                    if(pane != selectedPane){
                        pane.setFont(Font.font(pane.getFont().getFamily(), FontWeight.BOLD, pane.getFont().getSize()));
                        pane.setText(interpreter.titleString());
                        chooseInterpreter(pane);
                        for(TitledPane p : accordion.getPanes()){
                            if(p != pane){
                                p.setFont(Font.font(p.getFont().getFamily(), FontWeight.NORMAL, p.getFont().getSize()));
                                pane.setText(interpreter.titleString());
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
                updateButtonsEnabled();
            }
        });
        return pane;
    }

    public static void askToUpdateListOfInterpreters(){
        if(_instance != null){
            Platform.runLater(() -> {
                _instance.startAccordion();
            });
        }
    }
}
