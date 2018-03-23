package peridot.GUI.javaFXPanels;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import peridot.GUI.dialog.ScriptOutputDialog;
import peridot.Log;
import peridot.script.r.InstallationBatch;
import peridot.script.r.Interpreter;
import peridot.script.r.PackageInstaller;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by pentalpha on 23/03/2018.
 */
public class InstallationBatchJX implements Initializable {

    public List<PackageInstaller> installers = null;
    protected Map<PackageInstaller, Label> statusIndicator = null;
    protected Map<PackageInstaller, Button> outputButtons = null;
    protected Map<PackageInstaller, ScriptOutputDialog> outputDialogs = null;

    public Interpreter interpreter = null;
    public InstallationBatch batch = null;

    private AtomicBoolean stopFlag = new AtomicBoolean(false);

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
        createWatchRows();
        startInstallations();
    }

    private void createWatchRows(){
        /*for(PackageInstaller installer : installers){
            createWatchRow(installer);
        }*/
        for(int i = 0; i < 20; i++){
            createWatchRow(installers.get(0));
        }
    }

    private void createWatchRow(PackageInstaller installer){
        HBox box = new HBox();
        //TODO
    }

    private void startInstallations(){

    }

    private void watcherThread(){

    }

    public void askToStopNow(){
        stopFlag.set(true);
    }

}
