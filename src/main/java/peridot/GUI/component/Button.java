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
public class Button extends JButton{
    public Button(){
        super();

        this.setForeground(Color.white);
        this.setFont(Aesthetics.defaultFont);
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder());
    }

}
