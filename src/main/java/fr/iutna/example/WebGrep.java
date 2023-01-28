package fr.iutna.example;

import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class WebGrep {

	public static HashMap<Exception, AtomicInteger> errors = new HashMap<>();

	public static void main(String[] args) {
		// Initialize the program using the options given in argument
		if(args.length == 0) Tools.initialize("-cT --threads=100 Nantes https://fr.wikipedia.org/wiki/Nantes");
		//if(args.length == 0) Tools.initialize("-cTO --threads=1 Nantes https_/fr.wikipedia.org/wiki/Nantes");
		else Tools.initialize(args);

		//Blocking queue
		BlockingQueue<String> toExploreURLs = new LinkedBlockingQueue<>(Tools.startingURL());
		ConcurrentHashMap<String, Boolean> exploredURLs = new ConcurrentHashMap<>();
		AtomicInteger threadActives = new AtomicInteger(0);


		for (int i = 0; i < Tools.numberThreads(); i++) {
			new Thread(new URLExplorer(toExploreURLs, exploredURLs, threadActives)).start();
		}

		while(threadActives.get() > 0) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("toExploreURL : " + toExploreURLs.size());
			System.out.println("exploredURL : " + exploredURLs.size());
			System.out.println("validURL : " + exploredURLs.values().stream().filter(b -> b).count());
			System.out.println("nbThreadActifs : " + threadActives.get());
			System.out.println();
		}

		exploredURLs.values().stream().filter(b -> b).forEach(System.out::println);
	}


	
}