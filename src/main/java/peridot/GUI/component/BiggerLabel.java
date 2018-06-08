/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.component;

import peridot.GUI.Aesthetics;
/**
 *
 * @author Pitágoras Alves
 */
public class BiggerLabel extends Label {
    public BiggerLabel(){
        super();
        setFont(Aesthetics.biggerFont);
    }
    public BiggerLabel(String string){
        super(string);
        this.setFont(Aesthetics.biggerFont);
    }
}