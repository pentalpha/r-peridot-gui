/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.dialog.modulesManager;
import java.awt.Dimension;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;
import peridot.Archiver.Places;
import peridot.GUI.component.*;
import peridot.script.RModule;
/**
 *
 * @author pentalpha
 */
public class GetFileFromTreeDialog extends Dialog{
    public TreeNode[] selected = null;
    
    public GetFileFromTreeDialog(java.awt.Frame parent, boolean modal){
        super(parent, modal);
        this.setTitle("Select a file:");
        Dimension dialogSize = new Dimension(250, 500);
        Dimension scrollerSize = new Dimension(230, 480);
        this.setMinimumSize(dialogSize);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
        
        JTree tree = getTreeOfFiles();
        JScrollPane treeView = new JScrollPane(tree);
        treeView.setPreferredSize(scrollerSize);
        add(treeView);
        
        //setResizable(false);
    }
    
    public static String getAResult(java.awt.Frame parent, boolean modal){
        GetFileFromTreeDialog dialog = new GetFileFromTreeDialog(parent, modal);
        dialog.setVisible(true);
        TreeNode[] nodes = dialog.selected;
        
        if(nodes == null){
            return null;
        }
        
        if(nodes.length == 2){
            return nodes[1].toString();
        }
        else if(nodes.length >= 3){
            String result = nodes[1].toString() + ": ";
            for(int i = 2; i < nodes.length; i++){
                if(i > 2){
                    result += "/";
                }
                result += nodes[i].toString();
            }
            return result;
        }else{
            return null;
        }
    }
    
    private JTree getTreeOfFiles(){
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("results/");
        top.add(new DefaultMutableTreeNode(Places.countReadsInputFile.getName()));
        top.add(new DefaultMutableTreeNode(Places.conditionInputFile.getName()));
        for(String modName : RModule.getAvailablePackages()){
            top.add(moduleToTreeNode(modName));
        }
        for(String modName : RModule.getAvailablePostAnalysisScripts()){
            top.add(moduleToTreeNode(modName));
        }
        
        JTree tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener((TreeSelectionEvent e)->{
            if(tree.getSelectionCount() > 0){
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                       tree.getLastSelectedPathComponent();
                if(node.isLeaf()){
                    selected = node.getPath();
                    this.setVisible(false);
                }
            }
        });
        return tree;
    }
    
    private DefaultMutableTreeNode moduleToTreeNode(String module){
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(module);
        RModule script = RModule.availableScripts.get(module);
        
        DefaultMutableTreeNode child = null;
        for(String result : script.results){
            child = new DefaultMutableTreeNode(result);
            top.add(child);
        }
        
        return top;
    }
    
}
