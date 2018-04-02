/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.component;

import peridot.GUI.GUIUtils;
import peridot.GUI.MainGUI;
import peridot.GUI.NoHighlightCellRenderer;
import peridot.GUI.dialog.ScriptResultsDialog;
import peridot.Log;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 *
 * @author pitagoras
 */
public class Table extends JTable{

    public Table(Object[][] rowData, Object[] columnNames){
        super(rowData, columnNames);
        this.setDefaultRenderer(Object.class, new NoHighlightCellRenderer());
        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                boolean success = false;
                if (e.getClickCount() >= 1) {
                    int row = getSelectedRow();
                    int column = getSelectedColumn();
                    String str = getValueAt(row, column).toString();
                    if(str.charAt(0) == ' '){
                        str = str.substring(1, str.length());
                    }
                    Log.logger.info(str);
                    File file = ScriptResultsDialog.getCountPlotFile(str);
                    if(file != null){
                        success = GUIUtils.showImageDialog(file);
                    }
                }

                if(!success){
                    JOptionPane.showMessageDialog(MainGUI._instance,
                            "R-Peridot could not find a counts plot for this entry.",
                            "Could not open image!",
                            JOptionPane.OK_OPTION);
                }
            }
        });
    }
}
