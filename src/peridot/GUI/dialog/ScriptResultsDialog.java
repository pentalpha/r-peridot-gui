/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.dialog;

import peridot.GUI.component.Dialog;
import peridot.GUI.panel.ViewResultsPanel;
import peridot.script.RModule;

import java.io.File;

/**
 *
 * @author Pitágoras Alves
 */
public class ScriptResultsDialog extends Dialog {
    ViewResultsPanel results;
    /**
     * Creates new form ScriptResultsViewerDialog
     */
    public ScriptResultsDialog(java.awt.Frame parent, boolean modal, String scriptName, File resultsDir) {
        super(parent, modal);
        initComponents();
        setTitle(scriptName + " results");
        results = new ViewResultsPanel(scriptName, resultsDir, RModule.getAvailablePackages().contains(scriptName));
        this.getContentPane().add(results);
        pack();
    }

    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.CardLayout());

        pack();
    }
}
