package Client;

import Online.Connection;

import java.io.IOException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        /*try (Connection server = new Connection("127.0.0.1", 26780)) {
            double x = 152.5;
            double y = 35.6;
            int w = 140;
            int h = 120;
            int r = 255, g = 128, b = 0;
            String nick = "Abobus";
            StringBuilder data = new StringBuilder();

            Scanner s = new Scanner(System.in);
            System.out.println("Connected to server!");
            data.append("LOGIN$").append(nick).append("$")
                    .append(x).append("$").append(y).append("$")
                    .append(w).append("$").append(h).append("$")
                    .append(r).append("$").append(g).append("$").append(b);
            server.writeLine(data.toString());
            System.out.println("Logged in with nickname: " + nick);

            while (true) {
                server.writeLine(s.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
