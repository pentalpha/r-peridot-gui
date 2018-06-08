/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.dialog;

import peridot.GUI.component.Dialog;
import peridot.GUI.component.TabbedPane;
import peridot.GUI.panel.ViewResultsPanel;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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

    private void initComponents() {

        tabsPanel = new TabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.CardLayout());
        getContentPane().add(tabsPanel, "card2");

        pack();
    }

    private javax.swing.JTabbedPane tabsPanel;
}
