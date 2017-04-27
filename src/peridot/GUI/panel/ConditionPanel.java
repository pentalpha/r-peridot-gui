/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.panel;

import peridot.GUI.component.Label;
import peridot.GUI.component.Panel;
import peridot.GUI.component.Button;
import java.awt.Dimension;
//import javafx.scene.layout.Border;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import peridot.GUI.dialog.NewExpressionDialog;
import peridot.IndexedString;
import peridot.GUI.dragAndDrop.ListTransferHandler;
/**
 *
 * @author pentalpha
 */
public class ConditionPanel extends Panel {
    //private String conditionName;
    public JList contents;
    private javax.swing.JLabel conditionNameLabel;
    private javax.swing.JButton editNameButton;
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
        setPreferredSize(new java.awt.Dimension(188, 135));
        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        
        conditionNameLabel.setText("condition-XXX");
        add(conditionNameLabel);

        editNameButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/peridot/GUI/icons/Write-Document-icon16.png"))); // NOI18N
        editNameButton.setToolTipText("Edit name");
        editNameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editNameButtonActionPerformed(evt);
            }
        });
        add(editNameButton);
        
        //conditionNameLabel.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        //conditionNameLabel.setText("condition-XXX");
        //add(conditionNameLabel);
        
        conditionNameLabel.setText(conditionName);
        this.editNameButton.setEnabled(nameEditable);
        if(conditionName.equals("not-use")){
            editNameButton.setEnabled(false);
        }
        //setMaximumSize(new java.awt.Dimension(190, 2000));
        
        //((TitledBorder)this.getBorder()).setTitle(conditionName);
        Dimension contentsSize = new Dimension(this.getPreferredSize().width - 20, this.getPreferredSize().height-30);
        contents = new JList();
        contents.setMinimumSize(new java.awt.Dimension(contentsSize.width-23-5, 30));
        contents.setDragEnabled(true);
        contents.setTransferHandler(new ListTransferHandler());
        this.setTransferHandler(new ListTransferHandler());
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < indexedNames.length; i++) {
            model.addElement(new IndexedString(indexedNames[i].getNumber(), indexedNames[i].getText()));
        }
        contents.setModel(model);
        
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setFocusCycleRoot(false);
        scrollPane.setFocusTraversalPolicyProvider(true);
        scrollPane.setFocusable(false);
        scrollPane.setPreferredSize(contentsSize);
        JPanel internalPanel = new Panel();
        //internalPanel.setBorder(null);
        internalPanel.setFocusable(false);
        internalPanel.setMaximumSize(new java.awt.Dimension(contentsSize.width-23, 2000));
        //internalPanel.setPreferredSize(new java.awt.Dimension(contentsSize.width-23, contentsSize.height-15));
        internalPanel.setMinimumSize(new java.awt.Dimension(contentsSize.width-23, contentsSize.height-15));
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setMaximumSize(new java.awt.Dimension(0, 0));
        setMinimumSize(new java.awt.Dimension(0, 0));
        setPreferredSize(new java.awt.Dimension(188, 135));
        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
    }// </editor-fold>//GEN-END:initComponents

    
    
    //private javax.swing.JLabel conditionNameLabel;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
