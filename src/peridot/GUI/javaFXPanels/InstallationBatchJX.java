package peridot.GUI.javaFXPanels;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import peridot.GUI.GUIUtils;
import peridot.GUI.dialog.ScriptOutputDialog;
import peridot.Output;
import peridot.script.r.InstallationBatch;
import peridot.script.r.Interpreter;
import peridot.script.r.PackageInstaller;

import javax.swing.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by pentalpha on 23/03/2018.
 */
public class InstallationBatchJX implements Initializable {

    public List<PackageInstaller> installers = null;
    protected Map<PackageInstaller, Label> statusIndicators = null;
    protected Map<PackageInstaller, Button> outputButtons = null;
    protected Map<PackageInstaller, ScriptOutputDialog> outputDialogs = null;

    public Interpreter interpreter = null;
    public InstallationBatch batch = null;

    private AtomicBoolean stopFlag = new AtomicBoolean(false);
    private Timeline timeLine;

    @FXML private CheckBox autoCloseCheckBox;
    @FXML private FlowPane flowPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        peridot.Log.logger.info("Initializing InstallationBatch GUI");
    }

    public void installPackagesIn(Interpreter interpreter) {
        this.interpreter = interpreter;
        //Log.logger.info("Env is set as " + interpreter.exe);
        batch = new InstallationBatch(interpreter.getPackagesToInstall(), interpreter);
        installers = new LinkedList<>();
        installers.addAll(batch.installationQueue);

        statusIndicators = new HashMap<>();
        outputButtons = new HashMap<>();
        outputDialogs = new HashMap<>();

        createWatchRows();
        startInstallations();
    }

    private void createWatchRows(){
        for(PackageInstaller installer : installers){
            createWatchRow(installer);
        }
    }

    private void createWatchRow(PackageInstaller installer){
        HBox box = new HBox();
        box.setPrefWidth(flowPane.getPrefWidth()-5);
        box.setAlignment(Pos.CENTER);
        box.setSpacing(22);

        Label nameLabel = new Label(installer.getPackageName());
        nameLabel.setFont(autoCloseCheckBox.getFont());

        Label statusLabel = new Label(installer.status.name().replace('_', ' '));
        statusLabel.setFont(autoCloseCheckBox.getFont());
        statusIndicators.put(installer, statusLabel);

        Button outputButton = new Button();
        Image img = new Image(getClass().getResourceAsStream("/peridot/GUI/icons/Terminal-icon-32.png"));
        outputButton.setGraphic(new ImageView(img));
        outputButton.setPrefSize(30,25);
        outputButton.setDisable(true);
        outputButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                ScriptOutputDialog dialog = outputDialogs.get(installer);
                if(dialog != null){
                    SwingUtilities.invokeLater(() -> {
                        dialog.setVisible(true);
                    });
                }else{
                    Output output = installer.script.output;
                    if(output != null){
                        SwingUtilities.invokeLater(() -> {
                            ScriptOutputDialog newDialog = new ScriptOutputDialog(null, false,
                                    installer.getPackageName() + " Installation",
                                    output);
                            outputDialogs.put(installer, newDialog);
                            newDialog.setVisible(true);
                        });
                    }

                }
            }
        });

        outputButtons.put(installer, outputButton);

        box.getChildren().add(nameLabel);
        box.getChildren().add(statusLabel);
        box.getChildren().add(outputButton);
        flowPane.getChildren().add(box);
        //TODO
    }

    private void startInstallations(){
        batch.startInstallations();

        KeyFrame kf = new KeyFrame(Duration.millis(500), (actionEvent) -> {
            for (PackageInstaller installer : installers) {
                statusIndicators.get(installer).setText(installer.status.name().replace('_', ' '));

                if(outputButtons.get(installer).isDisable()
                        && installer.status != PackageInstaller.Status.NOT_STARTED){
                    outputButtons.get(installer).setDisable(false);
                }
            }

            if(!batch.isRunning()){
                askToStopNow();
            }
        });
        timeLine = new Timeline(kf);
        timeLine.setCycleCount(Animation.INDEFINITE);
        timeLine.play();
    }

    /*private void watcherThread(){
        boolean finished = true;

        while(batch.isRunning()) {
            for (PackageInstaller installer : installers) {
                statusIndicators.get(installer).setText(installer.status.name().replace('_', ' '));

                if(outputButtons.get(installer).isDisable()
                        && installer.status != PackageInstaller.Status.NOT_STARTED){
                    outputButtons.get(installer).setDisable(false);
                }
            }
        }
    }*/

    public void askToStopNow(){
        if(batch.isRunning()){
            batch.stop();
        }
        timeLine.stop();
        if(autoCloseCheckBox.isSelected()){
            Stage stage = (Stage) autoCloseCheckBox.getScene().getWindow();
            stage.close();
        }
        InterpreterManagerJX.askToUpdateListOfInterpreters();
    }

}
