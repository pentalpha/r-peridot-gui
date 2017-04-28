/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.panel;

import peridot.GUI.component.BiggerLabel;
import peridot.GUI.component.Panel;
import peridot.GUI.component.TabbedPane;
import peridot.GUI.component.BigButton;
import peridot.Archiver.Spreadsheet;
import peridot.Archiver.Manager;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import peridot.script.RScript;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import org.apache.commons.lang3.SystemUtils;
import peridot.GUI.JTableUtils;
import peridot.Global;
import peridot.Log;
/**
 *
 * @author pithagoras
 */
public class ViewResultsPanel extends Panel {
    HashMap<String, JPanel> resultViewers;
    /**
     * Creates new form PackageResultsPanel
     */
    public ViewResultsPanel(String scriptName, File resultsDir, boolean headerAlwaysOnFirstLineOfTable) {
        super();
        initComponents();
        resultViewers = new HashMap<>();
        Set<String> results = RScript.availableScripts.get(scriptName).results;
        for(String result : results)
        {
            JComponent content = null;
            File file = new File(resultsDir.getAbsolutePath() + File.separator + result);
            if(file.exists())
            {
                 if(Manager.isImageFile(result)){
                    content = peridot.GUI.component.Label.getImageLabel(file);
                }else if (Spreadsheet.isTableFile(result)){
                    if(headerAlwaysOnFirstLineOfTable){
                        content = JTableUtils.getTable(file, true);
                    }else{
                        content = JTableUtils.getTable(file, false);
                    }
                }else{
                    content = ViewResultsPanel.getUnknownFormatMessage(file);
                }
                JScrollPane scroller = new JScrollPane(content);
                scroller.getViewport().setBackground(Color.white);
                //scroller.setLayout(new java.awt.CardLayout());
                JPanel panel = new Panel();
                panel.setLayout(new java.awt.CardLayout());
                panel.add(scroller);
                resultViewers.put(result, panel);
                tabsPanel.add(result, panel);
            }
        }
    }
    
    private static JPanel getUnknownFormatMessage(File file){
        JPanel panel = new Panel();
        panel.setMaximumSize(new java.awt.Dimension(300, 3000));
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        JLabel message = new BiggerLabel();
        message.setText(file.getName() + " is in a format unknow to SGS.");
        //JLabel message2 = new Label();
        //message2.setText("Please use other program to view this result.");
        BigButton openButton = new BigButton();
        openButton.setText("Open With External Program");
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Global.openFileWithSysApp(file);
            }
        });
        openButton.setSize(new Dimension(120, 70));
        openButton.setIcon(new ImageIcon(ViewResultsPanel.class.getClass().getResource("/peridot/GUI/icons/Write-Document-icon16.png")));
        panel.add(message);
        //panel.add(message2);
        panel.add(openButton);
        
        return panel;
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

        setLayout(new java.awt.CardLayout());
        add(tabsPanel, "card2");
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane tabsPanel;
    // End of variables declaration//GEN-END:variables
}
