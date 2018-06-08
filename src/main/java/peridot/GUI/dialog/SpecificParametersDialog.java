/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.dialog;

import peridot.AnalysisParameters;
import peridot.GUI.GUIUtils;
import peridot.GUI.WrapLayout;
import peridot.GUI.component.Button;
import peridot.GUI.component.Dialog;
import peridot.GUI.panel.ParametersPanel;

import java.awt.*;

/**
 *
 * @author pentalpha
 */
public class SpecificParametersDialog extends Dialog {
    public ParametersPanel parametersPanel;
    private Dimension dialogSize = new java.awt.Dimension(1020, 120);
    /**
     * Creates new form SpecificParametersPanel
     */
    public SpecificParametersDialog(java.awt.Frame parent, boolean modal, 
            AnalysisParameters initialValues, String title) {
        super(parent, modal);
        this.setTitle(title);
        initComponents(initialValues);

        
        pack();
    }
    private void initComponents(AnalysisParameters initialValues) {

        jButton1 = new Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(dialogSize);
        //getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        setLayout(new WrapLayout(FlowLayout.CENTER, 5, 5));
        Dimension loc = GUIUtils.getCenterLocation(dialogSize.width, dialogSize.height);
        setLocation(loc.width, loc.height);

        parametersPanel = new ParametersPanel(initialValues, true);
        add(parametersPanel/*, new org.netbeans.lib.awtextra.AbsoluteConstraints(
                0, 0, this.getMinimumSize().width, this.getMinimumSize().height)*/);

        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jButton1.setPreferredSize(new Dimension(100,20));
        getContentPane().add(jButton1/*, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 90, 90, -1)*/);

        pack();
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        this.setVisible(false);
    }
    private javax.swing.JButton jButton1;
}
