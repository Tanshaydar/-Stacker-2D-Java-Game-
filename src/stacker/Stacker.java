/**
 * This is a basic Stacker game written with Java & OpenGL using
 * LWJGL and Slick libraries.
 * Please set up your Netbeans project to run it as is. For other IDE
 * or command line options, please refer to LWJGL documents
 */
package stacker;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

/**
 *
 * @author Tansel Altınel
 */
public class Stacker {

    DisplayMode mode = new DisplayMode(250, 700);
    Texture background;
    Texture texture;
//    ByteBuffer buffer[] = new ByteBuffer[]
    
    private boolean grid[][] = new boolean[5][14];
    private int level = 13;
    private int quantity[] = new int[14];
    
    private float x = 0;
    private float waiterR = 0;
    private float waiterL = 0;
    long lastFrame = 0;
    private int speed = 20;
    private boolean direction = true;
    private boolean keyPressed = false;
    
    public Stacker(){}
    
    public void start() {
        initGL();
        init();
        run();
    }
    
    public void init() {
        try {
            background = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/background.png"));
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/box.png"));
        } catch (IOException ex) {
            Logger.getLogger(Stacker.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for( int i = 0; i < quantity.length; i++)
            quantity[i] = 0;
        for( int i = 0; i < grid.length; i++)
            for( int j = 0; j < grid[i].length; j++)
                grid[i][j] = false;
        quantity[13] = 3;
    }
    
    public void initGL() {
        try {
            Display.setDisplayMode(mode);
            Display.create();
            Display.setTitle("Stacker ~Tanshaydar");
        } catch (LWJGLException ex) {
            Logger.getLogger(Stacker.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        GL11.glDisable( GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);               

        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);          

        // enable alpha blending
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glViewport( 0, 0, 250, 700);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho( 0, 250, 700, 0, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }
    
    public void render() {
        
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        /**
         * This renders background
         */
        background.bind();
        GL11.glBegin( GL11.GL_QUADS);
            GL11.glTexCoord2f( 0, 0);
            GL11.glVertex2f( 0, 0);
            GL11.glTexCoord2f( 1, 0);
            GL11.glVertex2f( background.getTextureWidth(), 0);
            GL11.glTexCoord2f( 1, 1);
            GL11.glVertex2f( background.getTextureWidth(), background.getTextureHeight());
            GL11.glTexCoord2f( 0, 1);
            GL11.glVertex2f( 0, background.getTextureHeight());
        GL11.glEnd();
        ////////////////////////////////////////////////////////////////////////
        
        /**
         * This renders the boxes
         */
        texture.bind(); // or GL11.glBind(texture.getTextureID());
        
        for( int i = 0; i < grid.length; i++)
            for( int j = 0; j < grid[i].length; j++) {
                if( grid[i][j]) {
                    GL11.glBegin(GL11.GL_QUADS);
                        GL11.glTexCoord2f( 0, 0);
                        GL11.glVertex2f( i*50, j*50);
                        GL11.glTexCoord2f( 1, 0);
                        GL11.glVertex2f( texture.getTextureWidth() + i*50, j*50);
                        GL11.glTexCoord2f( 1, 1);
                        GL11.glVertex2f( texture.getTextureHeight() + i*50, j*50 + texture.getTextureWidth());
                        GL11.glTexCoord2f( 0, 1);
                        GL11.glVertex2f( i*50, j*50 + texture.getTextureWidth());
                    GL11.glEnd();
                }
            }
    }
    
    public void move( float delta) {
        
        if( (x/50) < 0) {
            waiterR += delta/speed;
            if( waiterR/50 > 1) {
                direction = true;
                waiterR = 0;
                x += delta/speed;
            }
        }
        else if( (x/50)+quantity[level] >= 4) {
            
//            System.out.println("X: " + x);
//            System.out.println("X/50: " + (x)/50);
//            System.out.println("Quantity: " + quantity[level]);
            
            waiterL += delta/speed;
            if( waiterL/50 > 1) {
                direction = false;
                waiterL = 0;
                x -= delta/speed;
            }
//            x = (4 - quantity[level])*50;
//            System.out.println("b: " + x);
        }
        else {
            if( direction)
                x += delta/speed;
            else
                x -= delta/speed;
        }
    }
    
    public void doLogic() {
        
        if( keyPressed) {
            if( level < 13)
                for( int i = 0; i < grid.length; i++)
                    if( grid[i][level]) {
                        if( !grid[i][level + 1]) {
                            grid[i][level] = false;
                            quantity[level]--;
                            if( quantity[level] == 0)
                                gameOver(2);
                        }
                    }
            
            x = 0;
            waiterR = 0;
            waiterL = 0;
            direction = true;
            keyPressed = false;
            level--;
            if( level < 0) {
               gameOver(1);
            }
            quantity[level] = quantity[level+1];
            if( level > 7)
                speed-=2;
            else
                speed--;
            
        } else {
            for( int i = 0; i < grid.length; i++) {
                if( i <= x/50)
                    grid[i][level] = false;
                else if( i > ( (x/50) + quantity[level]))
                    grid[i][level] = false;
                else
                    grid[i][level] = true;
            }
        }
    }
    
    public void keyHandler() {
        int events = Keyboard.getNumKeyboardEvents();

        for( int i = 0; i < events; i++) {
            if( !Keyboard.next())
                break;
  
        int key = Keyboard.getEventKey();

        //true if was pressed, false if was released
        boolean pressed = Keyboard.getEventKeyState();
         
        if( !pressed && key == Keyboard.KEY_SPACE)
            keyPressed = true;
        else
            keyPressed = false;
       }
    }
    
    public float getDelta() {
        long time = ( (Sys.getTime() * 1000)/Sys.getTimerResolution() );
//        System.out.println( time +"   "+lastFrame);
        float delta = (float) ( time - lastFrame);
        lastFrame = time;
        
        return delta;
    }
    
    public void run() {
        getDelta();
        while( true) {
//            debug();
            render();
            keyHandler();
            doLogic();
            move( getDelta());
            keyPressed = false;
            Display.update();
            Display.sync(60);
            
            if( Display.isCloseRequested()) {
                Display.destroy();
                System.exit(0);
            }
        }
    }
    
    public void gameOver(int state){
        switch( state) {
            //Win
            case 1:
                JOptionPane.showMessageDialog(null, "You have won the prize!", "Congratulations!", JOptionPane.PLAIN_MESSAGE);
                System.exit(0);
            break;
            //Lose because bricks are gone
            case 2:
                JOptionPane.showMessageDialog(null, "You have failed to stack the bricks!", "Game Over!", JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            break;
//            case 3:
//            break;
        }
    }
    
    public void debug() {
        System.out.println("---------------------");
        for( int j = 0; j < 14; j++) {
            for( int i = 0; i < 5; i++) {
                if( grid[i][j])
                    System.out.print("1");
                else
                    System.out.print("0");
            }
            System.out.println();
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Stacker stacker = new Stacker();
        stacker.start();
    }
}
