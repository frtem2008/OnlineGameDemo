package Control;//работа с мышью

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Mouse implements MouseListener, MouseMotionListener {
    //позиция + кол-во прокрученных тиков
    public static boolean mouseClicked;
    public static int x, y, scroll;

    @Override
    public void mouseClicked(MouseEvent e) {
        //
        //System.out.println("X: " + e.getX() + " Y: " + e.getY());
        //Main.Main.gameObjects.add(new GameObject(e.getX(), e.getY(), 100, 100, Main.Main.Platform, "block"));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseClicked = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseClicked = false;
        //System.out.println("X: " + e.getX() + " Y: " + e.getY());
        //Main.Main.gameObjects.add(new GameObject(e.getX(), e.getY(), 100, 100, Main.Main.Platform, "block"));

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        x = e.getX();
        y = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        x = e.getX();
        y = e.getY();
    }
}
