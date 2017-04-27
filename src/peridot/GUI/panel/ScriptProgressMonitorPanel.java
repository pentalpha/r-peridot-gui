/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.panel;

import peridot.GUI.component.Label;
import peridot.GUI.component.BigLabel;
import peridot.GUI.component.Panel;
import peridot.GUI.component.Button;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import peridot.script.ScriptExec;
import java.util.concurrent.atomic.*;
import javax.swing.SwingUtilities;
import peridot.GUI.dialog.ScriptOutputDialog;
/**
 *
 * @author pentalpha
 */
public class ScriptProgressMonitorPanel extends Panel {
    private enum IconState{
        NONE,
        WAITING,
        STOP,
        SUCCESS,
        FAIL
    };
    
    private static ImageIcon failIcon = new ImageIcon(
            ScriptProgressMonitorPanel.class.getResource("/peridot/GUI/icons/Delete-icon-24.png"));
    private static ImageIcon clearIcon = new ImageIcon(
            ScriptProgressMonitorPanel.class.getResource("/peridot/GUI/icons/Clear-Green-Button-icon24.png"));
    private static ImageIcon stopIcon = new ImageIcon(
            ScriptProgressMonitorPanel.class.getResource("/peridot/GUI/icons/Stop-icon24.png"));
    private static ImageIcon consoleIcon = new ImageIcon(
            ScriptProgressMonitorPanel.class.getResource("/peridot/GUI/icons/Terminal-icon-32.png"));
    private static ImageIcon waitingIcon = new ImageIcon(
            ScriptProgressMonitorPanel.class.getResource("/peridot/GUI/icons/waiting32.gif"));
    
    private JLabel nameLabel, successLabel, waitingLabel, failLabel;
    public JButton stopButton, outputButton;
    private AtomicBoolean stoppable;
    private ScriptExec scriptExec;
    private IconState iconState;
    private ScriptOutputDialog outputDialog;
    /**
     * Creates new form ScriptProgressMonitorPanel
     */
    public ScriptProgressMonitorPanel(ScriptExec scriptExec, ScriptOutputDialog outputDialog) {
        this.outputDialog = outputDialog;
        iconState = IconState.NONE;
        this.scriptExec = scriptExec;
        this.stoppable = new AtomicBoolean();
        this.stoppable.set(false);
        setPreferredSize(new java.awt.Dimension(228, 50));
        
        nameLabel = new BigLabel();
        nameLabel.setText(scriptExec.getName());
        
        stopButton = new Button();
        stopButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            requireToAbort();
        });
        stopButton.setIcon(stopIcon);
        stopButton.setText("");
        failLabel = new Label();
        failLabel.setText("");
        failLabel.setIcon(failIcon);
        waitingLabel = new Label();
        waitingLabel.setText("");
        waitingLabel.setIcon(waitingIcon);
        successLabel = new Label();
        successLabel.setText("");
        successLabel.setIcon(clearIcon);
        
        outputButton = new Button();
        outputButton.setIcon(consoleIcon);
        outputButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            showScriptOutputDialog();
        });
        
        setStoppable(false);
        add(nameLabel);
        add(waitingLabel);
        add(outputButton);
    }
    
    //public void stopMonitoring(){
    //    stopMonitoringFlag.set(true);
    //}
    
    //public void startMonitoring(){
    //    stopMonitoringFlag.set(false);
    //    monitor.start();
    //}
    
    public void switchToWaitingIcon(){
        if(this.iconState == IconState.WAITING){
            return;
        }
        iconState = IconState.WAITING;
        setStoppable(false);
        SwingUtilities.invokeLater(() -> {
            remove(stopButton);
            remove(failLabel);
            remove(successLabel);
            remove(this.outputButton);
            add(waitingLabel);
            add(outputButton);
            revalidate();
            repaint();
        });
    }
    
    public void switchToStopIcon(){
        if(this.iconState == IconState.STOP){
            return;
        }
        iconState = IconState.STOP;
        setStoppable(true);
        SwingUtilities.invokeLater(() -> {
            remove(successLabel);
            remove(failLabel);
            remove(waitingLabel);
            remove(outputButton);
            stopButton.setEnabled(true);
            this.stoppable.set(true);
            add(stopButton);
            add(outputButton);
            //stopButton.setEnabled(false);
            revalidate();
            repaint();
        });
        
    }
    
    public void switchToSuccessIcon(){
        if(this.iconState == IconState.SUCCESS){
            return;
        }
        iconState = IconState.SUCCESS;
        setStoppable(false);
        SwingUtilities.invokeLater(() -> {
            remove(stopButton);
            remove(failLabel);
            remove(waitingLabel);
            remove(this.outputButton);
            add(successLabel);
            add(outputButton);
            revalidate();
            repaint();
        });
    }
    
    public void switchToFailIcon(){
        if(this.iconState == IconState.FAIL){
            return;
        }
        iconState = IconState.FAIL;
        setStoppable(false);
        SwingUtilities.invokeLater(() -> {
            remove(stopButton);
            remove(successLabel);
            remove(waitingLabel);
            remove(outputButton);
            add(failLabel);
            add(outputButton);
            revalidate();
            repaint();
        });
    }
    
    private void setStoppable(boolean stoppable){
        SwingUtilities.invokeLater(() -> {
            stopButton.setEnabled(stoppable);
        });
        this.stoppable.set(stoppable);
    }
    
    public void requireToAbort(){
        if(stoppable.get()){
            scriptExec.abort();
        }else{
            this.scriptExec.output.appendLine("Cannot abort");
        }
    }
    
    private void showScriptOutputDialog(){
        SwingUtilities.invokeLater(() -> {
            outputDialog.setVisible(true);
        });
    }
    
}
