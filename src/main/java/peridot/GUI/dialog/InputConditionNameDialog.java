/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.dialog;

import peridot.GUI.component.Button;
import peridot.GUI.component.Dialog;
import peridot.GUI.component.Label;

import javax.swing.*;

/**
 *
 * @author pentalpha
 */
public class InputConditionNameDialog extends Dialog {
    private String input = null;
    /**
     * Creates new form InputConditionNameDialog
     */
    public InputConditionNameDialog(java.awt.Frame parent, String initialName) {
        super(parent, true);
        initComponents();
        this.jTextField1.setText(initialName);
    }

    public String getInput(){
        return input;
    }
    
    private boolean textIsValid(){
        String text = jTextField1.getText();
        for(int i = 0; i < text.length(); i++){
            if(Character.isLetterOrDigit(text.charAt(i)) == false){
                return false;
            }
        }
        return true;
    }

    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new Label();
        jButton1 = new Button();
        jButton2 = new Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Write a new name for the condition");
        getContentPane().setLayout(new java.awt.FlowLayout());

        jTextField1.setText("conditionName");
        jTextField1.setMinimumSize(new java.awt.Dimension(100, 10));
        jTextField1.setName(""); // NOI18N
        jTextField1.setPreferredSize(new java.awt.Dimension(200, 25));
        getContentPane().add(jTextField1);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Use only: a-z, A-Z and 1-9");
        jLabel1.setPreferredSize(new java.awt.Dimension(180, 15));
        getContentPane().add(jLabel1);

        jButton1.setText("OK");
        jButton1.setPreferredSize(new java.awt.Dimension(60, 25));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);

        jButton2.setText("Cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);

        pack();
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        this.setVisible(false);
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        if(textIsValid()){
            this.input = this.jTextField1.getText();
            this.setVisible(false);
        }else{
            JOptionPane.showMessageDialog(rootPane, "Not a valid name!", "INPUT ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField jTextField1;

}
