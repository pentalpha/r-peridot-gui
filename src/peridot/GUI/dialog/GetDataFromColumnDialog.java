/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.dialog;

import peridot.GUI.component.Label;
import peridot.GUI.component.Table;
import peridot.GUI.component.BigLabel;
import peridot.GUI.component.BigButton;
import peridot.GUI.component.CheckBox;
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
public class GetDataFromColumnDialog extends JDialog{
    public LinkedList<String> columnData;
    public String dataName;
    public File file;
    public GetDataFromColumnDialog(java.awt.Frame parent, boolean modal, File file, String dataName){
        super(parent, modal);
        this.dataName = dataName;
        columnData = null;
        this.file = file;
        initComponents();
    }
    
    public void initComponents(){
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(650, 300));
        setMinimumSize(this.getPreferredSize());
        getContentPane().setLayout(new WrapLayout());
        this.setResizable(false);
        this.setTitle("Select " + dataName);
        upperLabel = new BigLabel("What column contains the " + dataName + "?");
        //upperLabel.setPreferredSize(new Dimension(getPreferredSize().width-150, 40));
        dataTable = JTableUtils.getTableWithoutHeader(file, false);
        scroller = new JScrollPane(dataTable);
        scroller.getViewport().setBackground(Color.white);
        scroller.setPreferredSize(new Dimension(getPreferredSize().width-50, 200));
        
        String[] headers = Spreadsheet.getDefaultHeader(dataTable.getColumnCount());
        columnOptions = new JComboBox();
        for(String x : headers){
            columnOptions.addItem(x);
        }
        
        hasHeader = new CheckBox();
        hasHeader.setText("The selected column contains a header.");
        
        yesButton = new BigButton();
        yesButton.setText("Get data");
        yesButton.addActionListener((java.awt.event.ActionEvent evt) -> 
            {
                int columnIndex = columnOptions.getSelectedIndex();
                LinkedList<String> data = new LinkedList<>();
                int startRow = 0;
                if(hasHeader.isSelected()){
                    startRow = 1;
                }
                for(int row = startRow; row < dataTable.getModel().getRowCount(); row++){
                    data.add(dataTable.getModel().getValueAt(row, columnIndex).toString());
                }
                columnData = data;
                
                setVisible(false);
            }
        );
        
        add(scroller);
        add(upperLabel);
        add(columnOptions);
        add(hasHeader);
        add(yesButton);
    }
    private Label upperLabel;
    private Table dataTable;
    private JScrollPane scroller;
    private JComboBox columnOptions;
    private CheckBox hasHeader;
    private BigButton yesButton;
}
