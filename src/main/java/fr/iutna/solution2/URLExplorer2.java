package fr.iutna.solution2;

import fr.iutna.concurrence.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;


public class URLExplorer2 implements Runnable {

    //Liste des urls à explorer par le thread
    private final List<String> currentToExplore = new ArrayList<>();

    //Liste des urls explorées partagée par tous les threads
    private final BlockingQueue<String> toExploreURLs;

    //Liste des urls explorées partagée par tous les threads
    private final ConcurrentHashMap<String, Boolean> exploredURLs;


    public URLExplorer2(BlockingQueue<String> toExploreURLs, ConcurrentHashMap<String, Boolean> exploredURLs) {
        this.toExploreURLs = toExploreURLs;
        this.exploredURLs = exploredURLs;
    }

    /**
     * Boucle principale du thread
     * Récupère les urls à explorer
     * Explore et valides et ajoute les hrefs des urls
     */
    @Override
    public void run() {
        //On boucle tant qu'il y a au moins un thread actif
        while(true) {
            try {
                //Récupère les urls à explorer
                getExplorer();
                //Explore les urls et les valides
                search();
                //Vide la liste des urls du thread à explorer
                currentToExplore.clear();

            } catch (Exception e) {
                //En cas d'erreur, on vide la liste des urls du thread à explorer
                currentToExplore.clear();
            }
        }
    }

    /**
     * Récupère une partie des urls à explorer dans la liste partagée toExploreURLs
     */
    private void getExplorer() {
        //récupère 1/5 premières urls de la liste partagée toExploreURLs
        toExploreURLs.drainTo(currentToExplore, (toExploreURLs.size()/5) + 1);
        //ajoute les urls de la liste partagée toExploreURLs dans la liste des urls à explorer du thread
        //Permet de ne pas explorer deux fois la même url
        if(currentToExplore.size() > 0) {
            synchronized (exploredURLs) {
                currentToExplore.forEach(url -> exploredURLs.putIfAbsent(url, false));
            }
        }
    }

    /**
     * Explore les urls récupérées dans la liste currentToExplore
     * Valide les urls dans exploredURLs
     * Ajoute les hrefs des urls valides trouvées dans la liste toExploreURLs
     */
    private void search() {
        //Pour chaque url à explorer du thread
        for (String address: currentToExplore) {
            try {
                //Explore l'url
                Tools.ParsedPage p = Tools.parsePage(address);

                //Si l'URL contient des matches
                if (!p.matches().isEmpty()) {
                    //On valide l'URL dans la liste partagée exploredURLs
                    this.exploredURLs.put(address, true);

                    //Pour chaque href de l'URL
                    for (String href : p.hrefs()) {

                        //On nettoie l'URL pour ne pas explorer les images, les index.php, les #...
                        href = clearUrl(href);
                        //Si l'URL est vide, on passe à l'URL suivante
                        if (href.isEmpty()) {
                            continue;
                        }

                        //Si l'URL n'est pas dans la liste partagée exploredURLs et n'est pas dans la liste partagée toExploreURLs
                        synchronized (toExploreURLs) {
                            if (!this.exploredURLs.containsKey(href) && !this.toExploreURLs.contains(href)) {
                                //On ajoute l'URL dans la liste partagée toExploreURLs
                                this.toExploreURLs.add(href);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                //En cas d'erreur, on la compte comme invalide, donc on passe à l'URL suivante
            }
        }
    }

    private String clearUrl(String url) {
        url = url.split("#")[0];
        //url = url.replaceAll(":/", "_"); //mode offline pour windows (car pas droit dossier nommé "https:")
        if (url.isEmpty() ||
                url.contains(".png") ||
                url.contains(".jpg") ||
                url.contains(".jpeg") ||
                url.contains(".gif") ||
                url.contains("index.php")) {
            return "";
        }
        return url;
    }
}
