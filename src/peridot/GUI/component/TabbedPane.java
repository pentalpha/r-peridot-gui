/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.component;
import javax.swing.JTabbedPane;
import peridot.GUI.Aesthetics;
/**
 *
 * @author Pitágoras Alves
 */
public class TabbedPane extends JTabbedPane{
    public TabbedPane(){
        super();
        this.setFont(Aesthetics.bigFont);
    }
}
