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
                if (e.getClickCount() >= 2) {
                    int row = getSelectedRow();
                    //int column = getSelectedColumn();
                    String str = getValueAt(row, 0).toString();
                    //Log.logger.info(row + " " + column);
                    if(str != null){
                        while(str.charAt(0) == ' '){
                            str = str.substring(1, str.length());
                        }
                        while(str.charAt(str.length()-1) == ' '){
                            str = str.substring(0, str.length()-1);
                        }
                        //Log.logger.info(str);
                        File file = ScriptResultsDialog.getCountPlotFile(str);
                        if(file != null){
                            success = GUIUtils.showImageDialog(file);
                        }
                    }

                    if(!success){
                        JOptionPane.showMessageDialog(MainGUI._instance,
                                "This entry is not in the differential expression consensus.",
                                "Counts plot not found!",
                                JOptionPane.OK_OPTION);
                    }
                }


            }
        });
    }

    @Override
    public boolean isCellEditable(int row, int col){
        return false;
    }
}
