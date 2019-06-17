/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.dialog;

import peridot.GUI.component.Dialog;
import peridot.script.RModule;
import peridot.Global;

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
    String output, scriptName, outputFilePath;
    public AtomicBoolean stopFlag;
    public AtomicBoolean newTextFlag;



    /**
     * Creates new form ScriptOutputDialog
     */
    public ScriptOutputDialog(java.awt.Frame parent, boolean modal, String scriptName) {
        super(parent, modal);
        this.scriptName = scriptName;
        outputFilePath = RModule.availableModules.get(scriptName).resultsFolder.getAbsolutePath()
         + File.separator + "output.txt";
        setTitle(scriptName);
        builder();
    }

    public ScriptOutputDialog(java.awt.Frame parent, boolean modal, String scriptName, String outputFilePath) {
        super(parent, modal);
        this.scriptName = scriptName;
        this.outputFilePath = outputFilePath;
        setTitle(scriptName);
        builder();
    }

    private void builder(){
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

    public void stop(){
        stopFlag.set(true);
    }

    public void updateText(){
        String newContent = Global.readFileUsingSystem(outputFilePath);
        if(newContent.length() > 0){
            output = newContent;
            SwingUtilities.invokeLater(() -> {
                textArea.setText(output);
                //scrollPanel.revalidate();
                //scrollPanel.updateUI();
                //pack();
            });
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
