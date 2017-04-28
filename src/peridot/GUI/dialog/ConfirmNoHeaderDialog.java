/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.dialog;

import peridot.GUI.component.Table;
import peridot.GUI.component.BigLabel;
import peridot.GUI.component.BigButton;
import peridot.Archiver.Spreadsheet;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.util.LinkedList;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import peridot.GUI.JTableUtils;
import peridot.GUI.WrapLayout;
/**
 *
 * @author pitagoras
 */
public class ConfirmNoHeaderDialog extends JDialog{
    
    public File file;
    public ConfirmNoHeaderDialog(java.awt.Frame parent, File file){
        super(parent, true);
        this.file = file;
        initComponents();
    }
    
    public void initComponents(){
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(650, 290));
        setMinimumSize(this.getPreferredSize());
        getContentPane().setLayout(new WrapLayout());
        this.setResizable(false);
        String question = "Does the table below have a header on the first line?";
        this.setTitle(question);
        //upperLabel.setPreferredSize(new Dimension(getPreferredSize().width-150, 40));
        dataTable = JTableUtils.getTableWithoutHeader(file, false);
        scroller = new JScrollPane(dataTable);
        scroller.getViewport().setBackground(Color.white);
        scroller.setPreferredSize(new Dimension(getPreferredSize().width-50, 200));
        
        //String[] headers = Spreadsheet.getDefaultHeader(dataTable.getColumnCount());
        
        hasHeaderLabel = new peridot.GUI.component.BigLabel(question);
        //hasHeaderLabel.setPreferredSize(new Dimension(getPreferredSize().width-150, 40));
        
        yesButton = new BigButton();
        yesButton.setText("Yes");
        yesButton.addActionListener((java.awt.event.ActionEvent evt) -> 
            {
                this.isHeaderOnFirstLine = true;
                setVisible(false);
            }
        );
        noButton = new BigButton();
        noButton.setText("No");
        noButton.addActionListener((java.awt.event.ActionEvent evt) -> 
            {
                this.isHeaderOnFirstLine = false;
                setVisible(false);
            }
        );
        
        add(hasHeaderLabel);
        add(scroller);
        add(yesButton);
        add(noButton);
    }
    
    public boolean isHeaderOnFirstLine;
    
    private Table dataTable;
    private JScrollPane scroller;
    private BigLabel hasHeaderLabel;
    private BigButton yesButton;
    private BigButton noButton;
}
