package fr.iutna.solution2;

import fr.iutna.concurrence.Tools;

import java.util.concurrent.*;


public class WebGrep2 implements Runnable{

	private String[] args;

	public void setArgs(String[] args) {
		this.args = args;
	}

	@Override
	public void run() {
		String couleur = "\u001B[33m";
		String reset = "\u001B[0m";
		System.out.println();
		System.out.println(couleur + "================================================");
		System.out.println("======== Solution 2 - ConcurrentHashMap ========");
		System.out.println("================================================" + reset);
		System.out.println();

		// Initialize the program using the options given in argument
		if(args.length == 0) Tools.initialize("-cT --threads=100 Nantes https://fr.wikipedia.org/wiki/Nantes");
		//if(args.length == 0) Tools.initialize("-cTO --threads=100 Nantes https_/fr.wikipedia.org/wiki/Nantes");
		else Tools.initialize(args);

		//Blocking queue pour les urls à explorer
		BlockingQueue<String> toExploreURLs = new LinkedBlockingQueue<>(Tools.startingURL());
		//ConcurrentHashMap pour les urls explorées (0 = non valide/pas encore explorée, 1 = valide)
		ConcurrentHashMap<String, Boolean> exploredURLs = new ConcurrentHashMap<>();


		//On crée les threads et on les lance
		for (int i = 0; i < Tools.numberThreads(); i++) {
			new Thread(new URLExplorer2(toExploreURLs, exploredURLs)).start();
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
			System.out.println("validURL : " + exploredURLs.values().stream().filter(b -> b).count());
		}
	}
}