/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.dragAndDrop;

import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.TransferHandler;
import peridot.IndexedString;
import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import peridot.GUI.panel.ColumnPanel;

/**
 *
 * @author pentalpha
 */
public class ColumnTransferHandler extends TransferHandler{
@Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        return (support.getComponent() instanceof JPanel) 
                && support.isDataFlavorSupported(ColumnPanelTransferable.COLUMN_PANEL_DATA_FLAVOR);
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        boolean accept = false;
        //Log.info("checking if suport can import...");
        if (canImport(support)) {
            //Log.info("...can import the transfer");
            try {
                Transferable t = support.getTransferable();
                Object value = t.getTransferData(ColumnPanelTransferable.COLUMN_PANEL_DATA_FLAVOR);
                //Log.info("value is...");
                if (value instanceof ColumnPanel) {
                    //Log.info("a ColumnPanel...");
                    Component component = support.getComponent();
                    //Log.info("component is...");
                    /*if (component instanceof JLabel) {
                        Log.info("a JLabel");
                        ((JLabel)component).setText(((IndexedString)value).getText());
                        accept = true;
                    }else if(component instanceof JList){
                        Log.info("a JList");
                        DefaultListModel model = (DefaultListModel)((JList)component).getModel();
                        model.addElement((IndexedString)value);
                        ((JList)component).setModel(model);
                    }
                    else */if(value instanceof JPanel)
                    {
                        //Log.info("a JPanel");
                        ((JPanel)component).add((ColumnPanel)value);
                    }
                    else{
                        //Log.info("something not defined.");
                    }
                }else{
                    //Log.info("something not defined.");
                }
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        }else{
            //Log.info("...can not import the transfer");
        }
        return accept;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return DnDConstants.ACTION_MOVE;
    }

    /*@Override
    protected Transferable createTransferable(JComponent c) {
        Transferable t = null;
        if (c instanceof JList) {
            JList list = (JList) c;
            Object value = list.getSelectedValue();
            if (value instanceof IndexedString) {
                IndexedString li = (IndexedString) value;
                t = new IndexedStringTransferable(li);
            }
        }
        if(c instanceof JPanel){
            JPanel panel = (JPanel) c;
            Object value = 
        }
        return t;
    }*/

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        String text = null;
        //Log.info("ExportDone, trying to remove: ");
        try {
            Object value = data.getTransferData(IndexedStringTransferable.LIST_ITEM_DATA_FLAVOR);
            if (value instanceof IndexedString) {
                text = ((IndexedString)value).getText();
                //Log.info(text);
                if(source instanceof JList){
                    DefaultListModel model = (DefaultListModel)((JList) source).getModel();
                    model.removeElement(value);
                }
            }else{
                throw new Exception("value is not a ListItem");
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }


        // Here you need to decide how to handle the completion of the transfer,
        // should you remove the item from the list or not...
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author pentalpha
 
public class ListTransferHandler extends TransferHandler {

        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            return (support.getComponent() instanceof JLabel || support.getComponent() instanceof JList) 
                    && support.isDataFlavorSupported(ListItemTransferable.LIST_ITEM_DATA_FLAVOR);
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            boolean accept = false;
            Log.info("checking if suport can import...");
            if (canImport(support)) {
                Log.info("...can import the transfer");
                try {
                    Transferable t = support.getTransferable();
                    Object value = t.getTransferData(ListItemTransferable.LIST_ITEM_DATA_FLAVOR);
                    Log.info("value is...");
                    
                    if (value instanceof ListItem) {
                        Log.info("a ListItem...");
                        Component component = support.getComponent();
                        Log.info("component is...");
                        if (component instanceof JLabel) {
                            Log.info("a JLabel");
                            ((JLabel)component).setText(((ListItem)value).getText());
                            accept = true;
                        }else if(component instanceof JList){
                            Log.info("a JList");
                            DefaultListModel model = (DefaultListModel)((JList)component).getModel();
                            model.addElement((ListItem)value);
                            ((JList)component).setModel(model);
                        }else{
                            Log.info("something not defined.");
                        }
                    }else{
                        Log.info("something not defined.");
                    }
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }else{
                Log.info("...can not import the transfer");
            }
            return accept;
        }

        @Override
        public int getSourceActions(JComponent c) {
            return DnDConstants.ACTION_COPY_OR_MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            Transferable t = null;
            if (c instanceof JList) {
                JList list = (JList) c;
                Object value = list.getSelectedValue();
                if (value instanceof ListItem) {
                    ListItem li = (ListItem) value;
                    t = new ListItemTransferable(li);
                }
            }
            return t;
        }

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            String text = null;
            Log.info("ExportDone, trying to remove: ");
            try {
                Object value = data.getTransferData(ListItemTransferable.LIST_ITEM_DATA_FLAVOR);
                if (value instanceof ListItem) {
                    text = ((ListItem)value).getText();
                    Log.info(text);
                    if(source instanceof JList){
                        DefaultListModel model = (DefaultListModel)((JList) source).getModel();
                        model.removeElement(value);
                    }
                }else{
                    throw new Exception("value is not a ListItem");
                }
            } catch (Exception exp) {
                exp.printStackTrace();
            }
            
            
            // Here you need to decide how to handle the completion of the transfer,
            // should you remove the item from the list or not...
        }
    }*/