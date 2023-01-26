package fr.iutna.example;
import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class WebGrep {

	//toExploreURL : liste partagée à explorer
	//exploredURL : liste partagée des url explorées
	//validURL : liste valide des url (contenant les matches)
	//voir blockedQueue ==> ProducerConsumer.

	public static void main(String[] args) {
		// Initialize the program using the options given in argument
		if(args.length == 0) Tools.initialize("-cT --threads=1000 Nantes https://fr.wikipedia.org/wiki/Nantes");
		else Tools.initialize(args);

		System.err.println("You must search recursively!");
		System.err.println("You must parallelize the application between " + Tools.numberThreads() + " threads!");

		//Blocking queue
		BlockingQueue<String> toExploreURLs = new LinkedBlockingQueue<>(Tools.startingURL());
		BlockingQueue<String> exploredURLs = new LinkedBlockingQueue<>();
		BlockingQueue<String> validURLs = new LinkedBlockingQueue<>();


		for (int i = 0; i < Tools.numberThreads(); i++) {
			new Thread(new URLExplorer(toExploreURLs, exploredURLs, validURLs)).start();
		}

		/*
		ExecutorService es = Executors.newFixedThreadPool(Tools.numberThreads());

		Future<?> f = es.submit(new URLExplorer(toExploreURL, exploredURL, validURL));
		*/
		//while true, clear la console puis affiche le nombre d'url explorées
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