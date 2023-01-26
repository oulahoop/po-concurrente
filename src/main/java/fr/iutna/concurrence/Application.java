package fr.iutna.concurrence;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) {
        //read user input
        Scanner sc = new Scanner(System.in);
        List<String> l = new ArrayList<>();
        while (sc.hasNext()) {
            String line = sc.nextLine();
            if(l.contains(line))
                System.out.println(line + " =========== already exists");
            else
                l.add(line);
        }
    }
}
