/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.component;

import peridot.GUI.NoHighlightCellRenderer;

import javax.swing.*;

/**
 *
 * @author pitagoras
 */
public class Table extends JTable{
    public Table(Object[][] rowData, Object[] columnNames){
        super(rowData, columnNames);
        this.setDefaultRenderer(Object.class, new NoHighlightCellRenderer());
    }
}
