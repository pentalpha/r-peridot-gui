/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.dialog;

import peridot.Archiver.Places;
import peridot.GUI.component.Dialog;
import peridot.Log;
import peridot.script.RModule;
import peridot.Global;
import peridot.script.r.Script;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 *
 * @author pentalpha
 */
public class ScriptOutputDialog extends Dialog {
    JScrollPane scrollPanel;
    JPanel textPanel;
    JTextArea textArea;
    //Output buffer;
    Thread updater;
    String output, scriptName, outputFilePath, outputFilePath_f;
    public AtomicBoolean stopFlag;
    public AtomicBoolean newTextFlag;
    protected peridot.script.r.Script script;



    /**
     * Creates new form ScriptOutputDialog
     */
    public ScriptOutputDialog(java.awt.Frame parent, boolean modal, String scriptName) {
        super(parent, modal);
        script = null;
        this.scriptName = scriptName;
        outputFilePath = getOutputFile(scriptName);
        outputFilePath_f = getFinalOutputFile(scriptName);
        Log.logger.info("Final output for " + scriptName + " is " + outputFilePath_f);
        setTitle(scriptName);
        builder();
    }

    public ScriptOutputDialog(java.awt.Frame parent, boolean modal, String scriptName, String outputFilePath) {
        super(parent, modal);
        script = null;
        this.scriptName = scriptName;
        this.outputFilePath = outputFilePath;
        setTitle(scriptName);
        builder();
    }
    public ScriptOutputDialog(java.awt.Frame parent, boolean modal, String scriptName, Script script) {
        super(parent, modal);
        this.script = script;
        this.scriptName = scriptName;
        this.outputFilePath = null;
        setTitle(scriptName);
        builder();
    }

    private void builder(){
        output = "";
        setFocusable(false);
        setPreferredSize(new java.awt.Dimension(550, 550));
        setResizable(false);
        getContentPane().setLayout(new java.awt.BorderLayout());

        //textPanel = new Panel();
        //textPanel.setMaximumSize(new java.awt.Dimension(500, 100000));
        //textPanel.setMinimumSize(new java.awt.Dimension(500, 380));
        //textPanel.setLayout(new BorderLayout);
        //this.buffer = buffer;
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new java.awt.Font("Ubuntu Mono", 0, 12)); // NOI18N
        textArea.setLineWrap(true);
        textArea.setText("");
        //textPanel.add(textArea);

        scrollPanel = new JScrollPane(textArea);
        //scrollPanel.setViewportView(textPanel);

        getContentPane().add(scrollPanel, BorderLayout.CENTER);
        pack();
        stopFlag = new AtomicBoolean(false);
        newTextFlag = new AtomicBoolean(false);
        /*updater = new Thread( () -> {
            int oldLength = buffer.getText().length();
            while(!stopFlag.get()){
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    break;
                }
                if(oldLength < buffer.getText().length()){
                    updateText();
                }
                oldLength = buffer.getText().length();
            }
        });
        updater.start();*/
        output = "";
    }

    static String getOutputFile(String module_name){
        return RModule.availableModules.get(module_name).resultsFolder + File.separator + "output.txt";
    }

    static String getFinalOutputFile(String module_name){
        return Places.finalResultsDir + File.separator
                + RModule.availableModules.get(module_name).workingDirectory.getName()
                + File.separator + "output.txt";
    }

    public void stop(){
        stopFlag.set(true);
    }

    public void updateText(){
        if(script != null){
            Log.logger.finest("Updating from script instance");
            output = script.getOutputString();
            SwingUtilities.invokeLater(() -> {
                textArea.setText(output);
            });
        }else if (outputFilePath != null){
            Log.logger.finest("Updating from output file " + outputFilePath);
            String newContent = Global.readFileUsingSystem(outputFilePath);
            String newContent_f = Global.readFileUsingSystem(outputFilePath_f);
            if(newContent.length() < newContent_f.length()){
                newContent = newContent_f;
                Log.logger.finest("Reading from final output file " + outputFilePath_f);
            }
            Log.logger.finest("Updating " + this.scriptName + " from "
                    + this.output.length() + " to " + newContent.length());
            if(newContent.length() > 0){
                output = newContent;
                SwingUtilities.invokeLater(() -> {
                    textArea.setText(output);
                    //scrollPanel.revalidate();
                    //scrollPanel.updateUI();
                    //pack();
                });
            }
        }else{
            Log.logger.severe("Neither Script instance or outputFilePath to read output from, "
                    + "not updating output dialog.");
        }

    }

    /*public void appendLine(String text){
        buffer.appendLine(text);
        updateText();
        ///*SwingUtilities.invokeLater(() -> {
        //    textArea.setText(textArea.getText() + text + "\n");
        //    //scrollPanel.revalidate();
        //    //scrollPanel.updateUI();
        //    //pack();
        //});
    }

    public void appendChar(char text){
        buffer.appendChar(text);
        SwingUtilities.invokeLater(() -> {
            textArea.append("" + text);
            //scrollPanel.revalidate();
            //scrollPanel.updateUI();
            //pack();
        });
    }

    public void setText(String text){
        buffer.setText(text);
        updateText();
    }*/

    public String getText(){
        return output;
    }

}
