/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.component;

import peridot.GUI.Aesthetics;

import javax.swing.*;
import java.awt.*;
/**
 *
 * @author Pitágoras Alves
 */
public class CheckBox extends JCheckBox{
    public CheckBox(){
        super();
        setForeground(Color.white);
        setFont(Aesthetics.defaultFont);
    }
    public CheckBox(String name){
        super(name);
        setForeground(Color.white);
        setFont(Aesthetics.defaultFont);
    }
}
