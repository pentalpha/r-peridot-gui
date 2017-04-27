/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.util.HashMap;
import java.util.TreeSet;
import peridot.GUI.font.FontStreamer;
import peridot.Log;

/**
 *
 * @author Pitágoras Alves
 */
public final class Aesthetics {
    public static Color background = new Color(255,255,255);
    public static Color element = new Color(100,100,255);
    public static Color letter = new Color(0,0,0);
    public static Color letterBig = new Color(20,20,100);
    public static Font defaultFont = new Font("Ubuntu", Font.PLAIN, 14 );
    public static Font bigFont = new Font("Ubuntu", Font.BOLD, 16);
    public static Font biggerFont = new Font("Ubuntu", Font.BOLD, 18);
    private Aesthetics(){
        throw new AssertionError();
    }
    public static void loadFonts(){
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        
        TreeSet<String> fontFiles = new TreeSet<>();
        fontFiles.add("Ubuntu-B.ttf");
        fontFiles.add("Ubuntu-BI.ttf");
        fontFiles.add("Ubuntu-R.ttf");
        fontFiles.add("Ubuntu-RI.ttf");
        fontFiles.add("Ubuntu-C.ttf");
        fontFiles.add("Ubuntu-LI.ttf");
        fontFiles.add("Ubuntu-M.ttf");
        fontFiles.add("Ubuntu-MI.ttf");
        fontFiles.add("UbuntuMono-B.ttf");
        fontFiles.add("UbuntuMono-BI.ttf");
        fontFiles.add("UbuntuMono-R.ttf");
        fontFiles.add("UbuntuMono-RI.ttf");
        
        for(String resource : fontFiles){
            try{
                InputStream is = FontStreamer.getFontStream(resource);
                Font font = Font.createFont(Font.TRUETYPE_FONT, is);
                //Log.logger.info(font.getFontName()+ " loaded.");
                //if(ge.)
                //if(Aesthetics.fontLoaded(font.getFontName()) == false){
                    boolean success = ge.registerFont(font);
                 //   if(success == false){
                 //       throw new Exception();
                 //   }
                //}
            }catch(Exception ex){
                Log.logger.info("not possible to load the font: " + resource);
                ex.printStackTrace();
                //break;
            }
        }
    }
    
    public static boolean fontLoaded(String name){
        GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = g.getAvailableFontFamilyNames();
        for (int i = 0; i < fonts.length; i++) {
            if(fonts[i].equals(name))
            {
                return true;
            }
        }
        return false;
    }
}
