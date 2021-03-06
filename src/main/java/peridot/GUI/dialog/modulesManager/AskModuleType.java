/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.dialog.modulesManager;

import peridot.GUI.component.BigButton;
import peridot.GUI.component.Dialog;
import peridot.Log;
import peridot.script.AnalysisModule;
import peridot.script.PostAnalysisModule;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
/**
 *
 * @author pentalpha
 */
public class AskModuleType extends Dialog{
    Class type = null;
    Dimension dialogSize, buttonSize;
    public AskModuleType(Frame parent)
    {
        super(parent, true);
        this.setTitle("Which type of module?");
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        dialogSize = new Dimension(400, 120);
        buttonSize = new Dimension((dialogSize.width-50)/2, dialogSize.height-50);
        this.setPreferredSize(dialogSize);
        this.setMinimumSize(dialogSize);
        this.setResizable(false);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.add(getOptionButton("Analysis Module", AnalysisModule.class));
        this.add(getOptionButton("Post Analysis Module", PostAnalysisModule.class));
    }
    
    private JButton getOptionButton(String text, Class modtype){
        BigButton button = new BigButton();
        button.setText(text);
        button.addActionListener((ActionEvent ev) -> {
            type = modtype;
            Log.logger.info("The user chose to create a " + text);
            setVisible(false);
        });
        button.setMinimumSize(buttonSize);
        button.setPreferredSize(buttonSize);
        
        return button;
    }

}
