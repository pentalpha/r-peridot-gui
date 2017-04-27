/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.component;
import java.awt.Color;
import javax.swing.JRadioButton;
import peridot.GUI.Aesthetics;

/**
 *
 * @author pentalpha
 */
public class RadioButton extends JRadioButton{
    public RadioButton(){
        super();
        setForeground(Color.white);
        setFont(Aesthetics.defaultFont);
    }
    public RadioButton(String name){
        super(name);
        setForeground(Color.white);
        setFont(Aesthetics.defaultFont);
    }
}
