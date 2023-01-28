package fr.iutna.concurrence;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) {
        //read user input
        Scanner sc = new Scanner(System.in);
        List<String> l = new ArrayList<>();
        int i = 0;
        System.out.println("=======================================================");
        while (sc.hasNext()) {
            String line = sc.nextLine();
            if(l.contains(line)) {
                System.out.println(line);
                i++;
            }
            else {
                l.add(line);
            }
        }

        sc.close();
        System.out.println("=======================================================");

        if(i == 0)
            System.out.println("Aucune ligne dupliquée trouvée");
        else
            System.out.println(i + " ligne(s) dupliquée(s) trouvée(s)");

    }
}
