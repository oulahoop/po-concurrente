package fr.iutna;

import fr.iutna.solution2.WebGrep2;
import fr.iutna.solution1.WebGrep;

import java.util.Arrays;

public class Application {
    public static void main(String[] args) {
        if(args.length > 0) {
            String method = args[0];
            args = Arrays.copyOfRange(args, 1, args.length);
            switch (method) {
                case "1" -> {
                    WebGrep webGrep = new WebGrep();
                    webGrep.setArgs(args);
                    new Thread(webGrep).start();
                }
                case "2" -> {
                    WebGrep2 webGrep2 = new WebGrep2();
                    webGrep2.setArgs(args);
                    new Thread(webGrep2).start();
                }

                default -> System.out.println("Usage: [1|2] [options] [word] [url]");
            }
        } else {
            System.out.println("Usage: [1|2] [options] [word] [url]");
        }

    }
}
