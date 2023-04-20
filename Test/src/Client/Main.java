package Client;

//основной игровой класс

import GameObjects.Game;
import GameObjects.Player;
import Online.Connection;
import Online.Message;
import Online.MessagePayloadObjects.PayloadStringData;
import Online.MessagePayloadObjects.PlayerMessagesPayloadObjects.PayloadLoginData;
import Online.MessagePayloadObjects.PlayerMessagesPayloadObjects.PayloadSpeedXY;
import Online.MessagePayloadObjects.ServerMessagesPayloadObjects.PayloadGameData;
import Online.MessageType;
import Online.OnlinePlayer;
import UserInput.Keyboard;
import UserInput.Mouse;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {
    //клавиатура + мышь
    public static final Mouse mouse = new Mouse();
    public static final Keyboard keyboard = new Keyboard();
    //размер кастомного шрифта (на потом)
    private static final float FONTSIZE = 35f;
    //смещение камеры (положения игрока) относительно левого верхнего угла экрана
    public static double cameraX = 500;
    public static double cameraY = 28;
    //размер JFrame
    public static Dimension frameSize;
    //для анимаций
    public static long frames = 0;
    //изображения
    private Game game = null;
    private OnlinePlayer player;

    //функция загрузки изображений
    public static void loadImages() {
        System.out.println("Loading images");
        System.out.println("Finished loading images");
    }

    public static void reload() {
        System.out.println("Reloading...");
        loadImages();
        frames = 0;
        System.out.println("Reloading finished");
    }

    public void initGame() {
        game = new Game();
    }

    public OnlinePlayer connect(String ip, int port, double x, double y, int w, int h, Color color, String nick) {
        Connection server = new Connection(ip, port);
        OnlinePlayer player = new OnlinePlayer(server, nick, Thread.currentThread());
        PayloadLoginData loginData = new PayloadLoginData(nick, x, y, w, h, color);
        Message loginMessage = new Message(MessageType.LOGIN_DATA, loginData);
        player.player = new Player("INVALID_LOGIN_PLAYER_NICKNAME", x, y, w, h, color);
        System.out.println("Connected to server!");

        try {
            player.writeMessage(loginMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Logged in with nickname: " + nick);

        return player;
    }

    //начало игры ()
    public void startDrawing(JFrame frame) {
        new Thread(() -> {
            //подгружаем изображения и прогружаем игру
            loadImages();
            Random r = new Random(System.currentTimeMillis());
            player = connect("127.0.0.1", 26780, 152.5, 35.6, 90, 60, new Color(r.nextInt()), "Livefish" + Math.random());
            initGame();
            game.add(player.player);

            //setFont();
            //привязываем слушатели
            frame.addKeyListener(keyboard);
            frame.addMouseListener(mouse);
            frame.addMouseMotionListener(mouse);

            //изображение для отрисовки (для изменения пикселей после рисования объектов)
            BufferedImage frameImage = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);

            //создание буфера
            frame.createBufferStrategy(2);
            BufferStrategy bs = frame.getBufferStrategy();

            //для использования tab, alt и т.д
            frame.setFocusTraversalKeysEnabled(false);

            //для стабилизации и ограничения фпс
            long start, end, len;
            double frameLength;

            int innerX, innerY;
            //графика итогового окна
            Graphics2D frameGraphics;

            //длина кадра (число после дроби - фпс)
            frameLength = 1000.0 / 60;

            //главный игровой цикл
            while (true) {
                //время начала кадра
                start = System.currentTimeMillis();

                //обновление размера JFrame
                frameSize = frame.getContentPane().getSize();

                if (Display.isFullScreen) {
                    innerX = 0;
                    innerY = 0;
                } else {
                    innerX = 8;
                    innerY = 30;
                }
                //получение информации о буфере
                frameGraphics = (Graphics2D) bs.getDrawGraphics();

                //очистка экрана перед рисованием
                frameGraphics.clearRect(0, 0, frame.getWidth(), frame.getHeight());
                frameImage.getGraphics().clearRect(0, 0, frameImage.getWidth(), frameImage.getHeight());
                //фон
                //frameImage.getGraphics().drawImage(BackGround, 0, 0, frame.getWidth(), frame.getHeight(), null);

                //рисование на предварительном изображении
                for (Player pl : game.players.values()) {
                    pl.draw(frameImage.getGraphics());
                }

                frameGraphics.setColor(Color.BLACK);
                frameGraphics.fillRect(innerX, innerY, 1600, 900);
                frameGraphics.drawImage(frameImage, 0, 0, null);

                //очистка мусора
                frameImage.getGraphics().dispose();
                frameGraphics.dispose();


                //показ буфера на холсте
                bs.show();

                //разворот на полный экран
                if (keyboard.getF11()) {
                    while (keyboard.getF11()) {
                        keyboard.update();
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    frame.dispose();
                    if (Display.isFullScreen) {
                        frame.setUndecorated(false);
                        frame.setExtendedState(Frame.NORMAL);
                        frame.setBounds(Display.x, Display.y, Display.w, Display.h);
                        cameraX = 500;
                    } else {
                        cameraX = frameSize.getWidth() / 1.2;
                        frame.setUndecorated(true);
                        frame.setExtendedState(6);
                    }
                    Display.isFullScreen = !Display.isFullScreen;
                    frame.setVisible(true);
                }

                //код для выхода из игры
                if (keyboard.getQ()) {
                    System.out.println("Выход");
                    System.exit(20);
                }

                if (keyboard.getH()) {
                    /*for (Player p : game.players.values()) {
                        System.out.println(p);
                    }*/
                    Message gamedump = new Message(MessageType.INFO, new PayloadStringData("gamedump"));
                    try {
                        player.writeMessage(gamedump);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                frames++;
                keyboard.update();

                //замер времени, ушедшего на отрисовку кадра
                end = System.currentTimeMillis();
                len = end - start;

                //стабилизация фпс
                if (len < frameLength) {
                    try {
                        Thread.sleep((long) (frameLength - len));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();

        ArrayBlockingQueue<Message> messageQueue = new ArrayBlockingQueue<>(50, true);

        /* Server messages handle thread */
        new Thread(() -> {
            // FIXME: 19.04.2023 Replace this with countdown latch
            while (player == null || player.player == null)
                Thread.yield();
            try {
                while (true) {
                    Message msg = player.readMessage();
                    messageQueue.add(msg);
                }
            } catch (IOException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                     InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).start();

        new Thread(() -> {
            // FIXME: 19.04.2023 Replace this with countdown latch
            while (player == null || player.player == null)
                Thread.yield();
            try {
                System.out.println("Entered speed sending mode:");
                int speedSentCount = 0;
                long lastUpdate = 0;
                final long REQUEST_TIMEOUT = 10;

                while (true) {
                    double sx, sy;
                    keyboard.update();
                    if (keyboard.getA()) {
                        sx = -2;
                    } else if (keyboard.getD()) {
                        sx = 2;
                    } else {
                        sx = 0;
                    }
                    if (keyboard.getW()) {
                        sy = -2;
                    } else if (keyboard.getS()) {
                        sy = 2;
                    } else {
                        sy = 0;
                    }
                    if (player.player.getSpeedX() != sx || player.player.getSpeedY() != sy) {
                        player.setSpeed(sx, sy);
                        player.sendSpeed();
                        System.out.println("Speed sent" + (speedSentCount++) + " times");

                    }

                    if (System.currentTimeMillis() - lastUpdate > REQUEST_TIMEOUT) {
                        /* read client data */
                        /* FORMAT: SPEED_XY$speedX$speedY */
                        // TODO: 20.04.2023 Priority queue for messages
                        lastUpdate = System.currentTimeMillis();
                        handleMessage(messageQueue.take(), player);
                        //System.out.println(msg);
                    }
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void handleMessage(Message msg, OnlinePlayer player) throws IOException {
        switch (msg.type) {
            case INVALID -> {
                player.writeMessage(Message.ErrorMessage("INVALID MESSAGE SENT!"));
                System.out.println("Server: sent invalid message with payload: " + msg.payload);
            }
            case ERROR -> {
                throw new RuntimeException(((PayloadStringData)msg.payload).str);
            }
            case INFO -> {
                System.out.println("Received info from server: " + ((PayloadStringData)msg.payload).str);
            }
            case LOGIN_DATA -> {
                player.writeMessage(Message.ErrorMessage("LOGIN DATA SENT!"));
                System.out.println("Server: sent login data!");
            }
            case SPEED_XY -> {
                player.writeMessage(Message.ErrorMessage("SPEED DATA SENT!"));
                System.out.println("Server: sent speed data!");
            }
            case GAME_DATA -> {
                PayloadGameData gameData = (PayloadGameData) msg.payload;
                Game received = gameData.game;
                game = received;
                /*System.out.println("Read a game from server: ");
                if (game.players.size() == 0) {
                    System.out.println("GAME OF ZERO PLAYERS");
                } else {
                    game.players.values().forEach(System.out::println);
                }*/
            }
        }
    }

    //загрузка кастомного шрифта (на потом)
    private void setFont() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            File fontFile = new File(Utils.getFileUrl(Main.class, "Fonts/Undertale Font.ttf").toURI());
            Font undertaleFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(Font.BOLD, FONTSIZE);

            ge.registerFont(undertaleFont);
        } catch (IOException | FontFormatException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}