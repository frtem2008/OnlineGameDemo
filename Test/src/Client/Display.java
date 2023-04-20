package Client;

import javax.swing.*;

public class Display {
    //окно
    public static JFrame frame = new JFrame("Online game client");

    //дефолтные размеры окна
    public static int x = 300, y = 0, w = 400, h = 400;

    //отслеживание полноэкранного режима
    public static boolean isFullScreen = false;

    //экземпляр главного класса
    public static Main m;

    //подключение графической библиотеки
    static {
        System.setProperty("sun.java2d.opengl", "True");
    }

    //точка входа
    public static void main(String[] args) {
        //иконка для панели задач
        frame.setIconImage(Utils.getImage(Display.class, "tile.png"));
        //активация кнопки с крестиком
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //разворачиваем
        frame.setBounds(x, y, w, h);
        frame.setVisible(true);
        frame.setResizable(false);

        //создаём новый экземпляр игры и начинаем рисовать
        m = new Main();
        m.startDrawing(frame);
    }
}
