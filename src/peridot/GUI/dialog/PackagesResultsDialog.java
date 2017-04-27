/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.dialog;

import peridot.GUI.component.Dialog;
import peridot.GUI.component.TabbedPane;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import peridot.GUI.panel.ViewResultsPanel;

/**
 *
 * @author pithagoras
 */
public class PackagesResultsDialog extends Dialog {

    /**
     * Creates new form PackagesResultsDialogs
     */
    public PackagesResultsDialog(java.awt.Frame parent, boolean modal, HashMap<String, File> packages) {
        super(parent, modal);
        initComponents();
        setTitle("Analysis Results");
        for(Map.Entry<String, File> pair : packages.entrySet()){
            tabsPanel.add(pair.getKey(), new ViewResultsPanel(pair.getKey(), pair.getValue(), true));
        }
        this.setSize(new java.awt.Dimension(600, 500));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabsPanel = new TabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.CardLayout());
        getContentPane().add(tabsPanel, "card2");

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane tabsPanel;
    // End of variables declaration//GEN-END:variables
}
