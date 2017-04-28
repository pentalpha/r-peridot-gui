/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.component;
import java.awt.Graphics;
import javax.swing.JButton;
import peridot.GUI.Aesthetics;
/**
 *
 * @author Pitágoras Alves
 */
public class BigButton extends JButton{
    public BigButton(){
        super();
        //this.setBackground(Aesthetics.element);
        //this.set
        //this.setForeground(Aesthetics.letter);
        this.setFont(Aesthetics.bigFont);
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder());
    }
    //@Override
    //protected void paintComponent(Graphics g){
    //    
    //}
}