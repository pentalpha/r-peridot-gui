/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.component;

import peridot.GUI.MainGUI;

import javax.swing.*;
/**
 *
 * @author pentalpha
 */
public class Dialog extends JDialog{
    protected java.awt.Frame publicParent;
    public Dialog(java.awt.Frame parent, boolean modal){
        super(parent, modal);
        publicParent = parent;
        this.setIconImage(MainGUI.getInstance().getDefaultIcon());
    }
}
