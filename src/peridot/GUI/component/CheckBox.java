/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.component;
import java.awt.Color;
import javax.swing.JCheckBox;
import peridot.GUI.Aesthetics;
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
