/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.dragAndDrop;

import peridot.IndexedString;
import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.TransferHandler;
import peridot.GUI.panel.ConditionPanel;
import peridot.GUI.dialog.NewExpressionDialog;
import peridot.Log;

/**
 *
 * @author pentalpha
 */
public class ListTransferHandler extends TransferHandler {

        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            return (support.getComponent() instanceof JList || support.getComponent() instanceof ConditionPanel) 
                    && support.isDataFlavorSupported(IndexedStringListTransferable.LIST_ITEMS_DATA_FLAVOR);
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            boolean accept = false;
            //System.out.println("checking if suport can import...");
            if (canImport(support)) {
                //System.out.println("...can import the transfer");
                try {
                    Transferable t = support.getTransferable();
                    Object value = t.getTransferData(IndexedStringListTransferable.LIST_ITEMS_DATA_FLAVOR);
                    //System.out.println("value is...");
                    if(value instanceof List){
                        //System.out.println("a ListItem...");
                        Component component = support.getComponent();
                        //System.out.println("component is...");
                        if (component instanceof ConditionPanel) {
                            //System.out.println("a ConditionPanel");
                            DefaultListModel model = (DefaultListModel) ((ConditionPanel) component).contents.getModel();
                            List<IndexedString> list = (List<IndexedString>) value;
                            for(IndexedString string : list){
                                model.addElement(string);
                            }
                            ((ConditionPanel) component).contents.setModel(model);
                            accept = true;
                            //System.out.println("accepted and added the list.");
                        } else {
                            //System.out.println("something not defined.");

                        }
                    }else{
                        //System.out.println("something not defined.");
                    }
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }else{
                //System.out.println("...can not import the transfer");
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
                Object value = list.getSelectedValuesList();
                if (value instanceof List) {
                    List<IndexedString> li = (List<IndexedString>) value;
                    t = new IndexedStringListTransferable(li);
                }else{
                    Log.logger.severe("What you are trying to export is no List");
                }
            }
            return t;
        }

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            String text = null;
            if(action == DnDConstants.ACTION_NONE){
                return;
            }
            //System.out.println("ExportDone, trying to remove: ");
            
            try {
                Object value = data.getTransferData(IndexedStringListTransferable.LIST_ITEMS_DATA_FLAVOR);
                if (value instanceof List) {
                    List<IndexedString> list = (List<IndexedString>)value;
                    //System.out.println(text);
                    if(source instanceof JList){
                        DefaultListModel model = (DefaultListModel)((JList) source).getModel();
                        for(IndexedString element : list){
                            model.removeElement(element);
                        }
                        NewExpressionDialog.setChangedConditions(true);
                    }
                }else{
                    throw new Exception("value is not a ListItem");
                }
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        }
    }