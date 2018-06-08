/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.component;

import peridot.GUI.Aesthetics;

import javax.swing.*;
/**
 *
 * @author Pitágoras Alves
 */
public class BigButton extends JButton{
    public BigButton(){
        super();

        this.setFont(Aesthetics.bigFont);
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder());
    }

}