package fr.iutna.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class URLExplorer implements Runnable {
    //Liste blockingqueue de string
    private static final int SIZE_CURRENT_TO_EXPLORE = 10;
    private final BlockingQueue<String> toExploreURLs;
    private final ConcurrentHashMap<String, Boolean> exploredURLs;
    private final AtomicInteger threadActives;
    private boolean isActive = true;

    private final List<String> currentToExplore = new ArrayList<>();



    public URLExplorer(BlockingQueue<String> toExploreURLs, ConcurrentHashMap<String, Boolean> exploredURLs, AtomicInteger threadActives) {
        this.toExploreURLs = toExploreURLs;
        this.exploredURLs = exploredURLs;
        this.threadActives = threadActives;
        threadActives.incrementAndGet();
    }

    /**
     * Boucle principale du thread
     * Récupère les urls à explorer
     * Explore et valides et ajoute les hrefs des urls
     */
    @Override
    public void run() {
        //On boucle tant qu'il y a au moins un thread actif
        do {
            try {
                //Si la liste est vide et que le thread est actif, on décrémente le nombre de thread actif et on le desactive
                if(toExploreURLs.isEmpty() && isActive) {
                    threadActives.decrementAndGet();
                    isActive = false;
                } else if(!toExploreURLs.isEmpty()) {
                    //Si la liste n'est pas vide et que le thread est inactif, on l'active et on incrémente le nombre de thread actif
                    if(!isActive) {
                        threadActives.incrementAndGet();
                        isActive = true;
                    }

                    //Récupère les urls à explorer
                    getExplorer();
                    //Explore les urls et les valides
                    search();
                    //Vide la liste des urls du thread à explorer
                    currentToExplore.clear();
                }
            } catch (Exception e) {
                //set error to 1 or increment if already exists
                if (WebGrep.errors.containsKey(e)) {
                    WebGrep.errors.get(e).incrementAndGet();
                } else {
                    WebGrep.errors.put(e, new AtomicInteger(1));
                }
            }
        } while (threadActives.get() > 0);
    }

    /**
     * Récupère une partie des urls à explorer dans la liste partagée toExploreURLs
     */
    private void getExplorer() {
        //get explorer
        toExploreURLs.drainTo(currentToExplore, SIZE_CURRENT_TO_EXPLORE);
        currentToExplore.forEach(url -> exploredURLs.putIfAbsent(url, false));
        toExploreURLs.removeIf(toExploreURLs::contains);
    }

    /**
     * Explore les urls récupérées dans la liste currentToExplore
     * Valide les urls dans exploredURLs
     * Ajoute les hrefs des urls valides trouvées dans la liste toExploreURLs
     */
    private void search() throws IOException {
        for (String address: currentToExplore) {

            // Read the page to obtain the matching expressions and the hyperlinks
            Tools.ParsedPage p = Tools.parsePage(address);

            if (!p.matches().isEmpty()) {
                this.exploredURLs.put(address, true);
                for (String href: p.hrefs()) {
                    href = href.split("#")[0];
                    //href = href.split("#")[0].replaceAll(":/", "_"); //mode offline
                    if(href.isEmpty() ||href.contains(".png") || href.contains(".jpg") || href.contains(".jpeg") || href.contains(".gif") || href.contains("index.php")) {
                        continue;
                    }
                    if(!this.exploredURLs.containsKey(href) && !this.toExploreURLs.contains(href)) {
                        this.toExploreURLs.add(href);
                    }
                }
            }
        }
    }

}
