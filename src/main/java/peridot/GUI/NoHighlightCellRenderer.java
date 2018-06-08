/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 *
 * @author pitagoras
 */
public class NoHighlightCellRenderer extends DefaultTableCellRenderer {
    
    //@Override
    public Component getTableCellEditorComponent(JTable table, Object value,boolean isSelected, int row, int column) {  
            //this.getColorModel().
        if( isSelected )  // User clicked on this cell.
            setBackground(Color.black);

        return this;
    }
}
