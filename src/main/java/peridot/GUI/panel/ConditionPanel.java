/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.panel;

import peridot.GUI.component.Button;
import peridot.GUI.component.Label;
import peridot.GUI.component.Panel;
import peridot.GUI.dialog.NewExpressionDialog;
import peridot.GUI.dragAndDrop.ListTransferHandler;
import peridot.IndexedString;
import peridot.GUI.Resources;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author pentalpha
 */
public class ConditionPanel extends Panel {
    //private String conditionName;
    public JList contents;
    private javax.swing.JLabel conditionNameLabel;
    private javax.swing.JButton editNameButton;

    private java.awt.Dimension defaultSize = new java.awt.Dimension(
            ((int)(NewExpressionDialog.conditionsPaneSize.width/4))+6,
            300);
    Dimension contentsSize = new Dimension(defaultSize.width - 6, defaultSize.height-30);
    Dimension contentsMinSize = new java.awt.Dimension(contentsSize.width-23-5, 30);
    Dimension internalPanelMax = new java.awt.Dimension(contentsSize.width-23, 2000);
    Dimension internalPanelMin = new java.awt.Dimension(internalPanelMax.width, contentsSize.height-15);
    /**
     * Creates new form ConditionPanel
     */
    public ConditionPanel(IndexedString[] indexedNames, String conditionName, boolean nameEditable) {
        super();
        //this.conditionName = conditionName;
        
        conditionNameLabel = new Label();
        editNameButton = new Button();

        setMaximumSize(new java.awt.Dimension(0, 0));
        setMinimumSize(new java.awt.Dimension(0, 0));
        setPreferredSize(defaultSize);
        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        
        conditionNameLabel.setText("condition-XXX");
        add(conditionNameLabel);

        editNameButton.setIcon(Resources.getImageIcon(getClass(),"Write-Document-icon16.png"));
        editNameButton.setToolTipText("Edit name");
        editNameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editNameButtonActionPerformed(evt);
            }
        });
        add(editNameButton);
        
        conditionNameLabel.setText(conditionName);
        this.editNameButton.setEnabled(nameEditable);
        if(conditionName.equals("not-use")){
            editNameButton.setEnabled(false);
        }
        //setMaximumSize(new java.awt.Dimension(190, 2000));
        
        //((TitledBorder)this.getBorder()).setTitle(conditionName);
        contents = new JList();
        contents.setMinimumSize(contentsMinSize);
        contents.setDragEnabled(true);
        contents.setTransferHandler(new ListTransferHandler());
        this.setTransferHandler(new ListTransferHandler());
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < indexedNames.length; i++) {
            model.addElement(new IndexedString(indexedNames[i].getNumber(), indexedNames[i].getText()));
        }
        contents.setModel(model);
        contents.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setFocusCycleRoot(false);
        scrollPane.setFocusTraversalPolicyProvider(true);
        scrollPane.setFocusable(false);
        scrollPane.setPreferredSize(contentsSize);
        JPanel internalPanel = new Panel();
        //internalPanel.setBorder(null);
        internalPanel.setFocusable(false);
        internalPanel.setMaximumSize(internalPanelMax);
        //internalPanel.setPreferredSize(new java.awt.Dimension(contentsSize.width-23, contentsSize.height-15));
        internalPanel.setMinimumSize(internalPanelMin);
        internalPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        internalPanel.add(contents);
        //contents.setMaximumSize(contentsSize);
        //contents.setMinimumSize(contentsSize);
        //contents.setPreferredSize(contentsSize);
        scrollPane.setViewportView(internalPanel);
        add(scrollPane);
        //setPreferredSize(getPreferredSize());
        //setMinimumSize(getPreferredSize());
        //setMaximumSize(getPreferredSize());
        //add(new JScrollPane(contents));
    }
    
    public String getConditionName(){
        String text = this.conditionNameLabel.getText();
        return text;
    }
    
    private void editNameButtonActionPerformed(java.awt.event.ActionEvent evt) {                                               
        // TODO add your handling code here:
        String oldName = this.getConditionName();
        String newName = NewExpressionDialog.inputConditionName(oldName);
        if(newName != null){
            this.conditionNameLabel.setText(newName);
            NewExpressionDialog.updateConditionName(oldName, newName);
        }
    }
}
