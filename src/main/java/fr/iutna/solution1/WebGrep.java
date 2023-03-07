package fr.iutna.solution1;

import fr.iutna.concurrence.Tools;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WebGrep implements Runnable {

    private String[] args;

    public void setArgs(String[] args) {
        this.args = args;
    }

    @Override
    public void run() {
        String couleur = "\u001B[31m";
        String reset = "\u001B[0m";
        System.out.println();
        System.out.println(couleur + "========================================");
        System.out.println("======== Solution 1 - SharedSet ========");
        System.out.println("========================================" + reset);
        System.out.println();
        // Initialize the program using the options given in argument
        if(args.length == 0) Tools.initialize("-cT --threads=100 Nantes https://fr.wikipedia.org/wiki/Nantes");
        //if(args.length == 0) Tools.initialize("-cTO --threads=100 Nantes https_/fr.wikipedia.org/wiki/Nantes");
        else Tools.initialize(args);

        //Blocking queue pour les urls à explorer
        BlockingQueue<String> toExploreURLs = new LinkedBlockingQueue<>(Tools.startingURL());
        //Liste partagée pour les urls explorées
        SharedSet exploredURLs = new SharedSet();
        //Liste partagée pour les urls valides
        SharedSet validURLs = new SharedSet();


        //On crée les threads et on les lance
        for (int i = 0; i < Tools.numberThreads(); i++) {
            new Thread(new URLExplorer(toExploreURLs, exploredURLs, validURLs)).start();
        }

        while(true) {
            //On attends 1 seconde
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("toExploreURL : " + toExploreURLs.size());
            System.out.println("exploredURL : " + exploredURLs.size());
            System.out.println("validURL : " + validURLs.size());
            System.out.println();
        }
    }
}
