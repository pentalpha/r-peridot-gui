/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.component;
import peridot.GUI.Aesthetics;
import javax.swing.JLabel;

public class BigLabel extends Label {
    public BigLabel(){
        super();
        this.setFont(Aesthetics.bigFont);
    }
    public BigLabel(String string){
        super(string);
        this.setFont(Aesthetics.defaultFont);
    }
}