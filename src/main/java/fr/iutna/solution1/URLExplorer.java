package fr.iutna.solution1;

import fr.iutna.concurrence.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class URLExplorer implements Runnable {

    //Blocking queue pour les urls à explorer
    private final BlockingQueue<String> toExploreURLs;

    //Liste des urls explorées partagée par tous les threads
    private final SharedSet exploredURLs;

    //Liste des urls explorées partagée par tous les threads
    private final SharedSet validURLs;

    //Nombre d'URL que le thread va prendre lorsqu'il a fini d'explorer les URL qu'il a
    private static final int SIZE_CURRENT_TO_EXPLORE = 1;

    //Liste des urls à explorer par le thread
    private final List<String> currentToExplore = new ArrayList<>();

    public URLExplorer(BlockingQueue<String> toExploreURLs, SharedSet exploredURLs, SharedSet validURLs) {
        this.toExploreURLs = toExploreURLs;
        this.exploredURLs = exploredURLs;
        this.validURLs = validURLs;
    }

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
            } catch (Exception ignored) {
                //En cas d'erreur, on vide la liste des urls du thread à explorer
                currentToExplore.clear();
            }
        }
    }
    /**
     * Récupère une partie des urls à explorer dans la liste partagée toExploreURLs
     */
    private void getExplorer() {
        //récupère les SIZE_CURRENT_TO_EXPLORE premières urls de la liste partagée toExploreURLs
        toExploreURLs.drainTo(currentToExplore, SIZE_CURRENT_TO_EXPLORE);

        //Ajoute tous les URLs à explorer du Thread dans la liste des URLs explorées (pour éviter de les explorer plusieurs fois)
        if(currentToExplore.size() > 0) {
            synchronized (exploredURLs) {
                exploredURLs.addAll(currentToExplore);
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

                    //On valide l'URL dans la liste partagée validURLs
                    this.validURLs.add(address);

                    //Pour chaque href de l'URL
                    for (String href : p.hrefs()) {

                        //On nettoie l'URL pour ne pas explorer les images, les index.php, les #...
                        href = clearUrl(href);
                        //Si l'URL est vide, on passe à l'URL suivante
                        if (href.isEmpty()) {
                            continue;
                        }
                        synchronized (this.toExploreURLs) {
                            //Si l'URL n'est pas dans la liste partagée exploredURLs et toExploreURLs
                            if (!this.exploredURLs.contains(href) && !this.toExploreURLs.contains(href)) {
                                //On ajoute l'URL dans la liste partagée toExploreURLs
                                this.toExploreURLs.add(href);
                            }
                        }
                    }
                }
            } catch (Exception ignored) {
                //Problème lors du parse de la page
                //On passe donc à l'URL suivante
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
