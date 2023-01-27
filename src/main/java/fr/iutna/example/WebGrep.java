package fr.iutna.example;

import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class WebGrep {

	//toExploreURL : liste partagée à explorer
	//exploredURL : liste partagée des url explorées
	//validURL : liste valide des url (contenant les matches)
	//voir blockedQueue ==> ProducerConsumer.

	public static HashMap<Exception, AtomicInteger> errors = new HashMap<>();

	public static void main(String[] args) {
		// Initialize the program using the options given in argument
		//if(args.length == 0) Tools.initialize("-cT --threads=100 Nantes https://fr.wikipedia.org/wiki/Nantes");
		if(args.length == 0) Tools.initialize("-cTO --threads=100 Nantes https_/fr.wikipedia.org/wiki/Nantes");
		else Tools.initialize(args);

		//Blocking queue
		BlockingQueue<String> toExploreURLs = new LinkedBlockingQueue<>(Tools.startingURL());
		BlockingQueue<String> exploredURLs = new LinkedBlockingQueue<>();
		BlockingQueue<String> validURLs = new LinkedBlockingQueue<>();


		for (int i = 0; i < Tools.numberThreads(); i++) {
			new Thread(new URLExplorer(toExploreURLs, exploredURLs, validURLs)).start();
		}

		while(true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.print("\033[H\033[2J");
			System.out.flush();
			System.out.println("toExploreURL : " + toExploreURLs.size());
			System.out.println("exploredURL : " + exploredURLs.size());
			System.out.println("validURL : " + validURLs.size());
			System.out.println();
		}
	}


	
}