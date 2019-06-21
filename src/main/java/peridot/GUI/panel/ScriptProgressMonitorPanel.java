/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.panel;

import peridot.GUI.component.BigLabel;
import peridot.GUI.component.Button;
import peridot.GUI.component.Label;
import peridot.GUI.component.Panel;
import peridot.GUI.dialog.ScriptOutputDialog;
import peridot.tree.PipelineGraph;
import peridot.GUI.GUIUtils;
import peridot.GUI.Resources;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;
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

    private static ImageIcon failIcon = Resources.getImageIcon("Delete-icon-24.png");
    private static ImageIcon clearIcon = Resources.getImageIcon("Clear-Green-Button-icon24.png");
    private static ImageIcon stopIcon = Resources.getImageIcon("Stop-icon24.png");
    private static ImageIcon consoleIcon = Resources.getImageIcon("Terminal-icon-32.png");
    private static ImageIcon waitingIcon = Resources.getImageIcon("waiting32.gif");

    private JLabel nameLabel, successLabel, waitingLabel, failLabel;
    public JButton stopButton, outputButton;
    private AtomicBoolean stoppable;
    private PipelineGraph graph;
    private IconState iconState;
    private ScriptOutputDialog outputDialog;
    private String moduleName;
    /**
     * Creates new form ScriptProgressMonitorPanel
     */
    public ScriptProgressMonitorPanel(String moduleName, PipelineGraph graph, ScriptOutputDialog outputDialog) {
        this.moduleName = moduleName;
        this.graph = graph;
        this.outputDialog = outputDialog;
        iconState = IconState.NONE;
        this.stoppable = new AtomicBoolean();
        this.stoppable.set(false);
        setPreferredSize(new java.awt.Dimension(228, 50));

        nameLabel = new BigLabel();
        nameLabel.setText(moduleName);
        GUIUtils.setToIdealTextSize(nameLabel);

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
            graph.abort(moduleName);
        }
    }

    private void showScriptOutputDialog(){
        SwingUtilities.invokeLater(() -> {
            outputDialog.setVisible(true);
        });
    }

}
