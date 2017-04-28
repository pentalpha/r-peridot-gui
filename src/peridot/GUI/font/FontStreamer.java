/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.font;
import java.io.InputStream;

/**
 *
 * @author Pitágoras Alves
 */
public final class FontStreamer {
    private FontStreamer(){
        throw new AssertionError();
    }
    public static InputStream getFontStream(String name){
        return FontStreamer.class.getResourceAsStream(name);
    }
}
