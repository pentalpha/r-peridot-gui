/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.component;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JButton;
import peridot.GUI.Aesthetics;
/**
 *
 * @author Pitágoras Alves
 */
public class Button extends JButton{
    public Button(){
        super();
        //this.setBackground(Aesthetics.element);
        //this.set
        this.setForeground(Color.white);
        this.setFont(Aesthetics.defaultFont);
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder());
    }
    //@Override
    //protected void paintComponent(Graphics g){
    //    
    //}
}
