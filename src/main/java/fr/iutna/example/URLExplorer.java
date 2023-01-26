package fr.iutna.example;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;


public class URLExplorer implements Runnable {
    //Liste blockingqueue de string

    private BlockingQueue<String> toExploreURLs;
    private BlockingQueue<String> exploredURLs;
    private BlockingQueue<String> validURLs;


    public URLExplorer() {
    }

    public URLExplorer(BlockingQueue<String> toExploreURLs, BlockingQueue<String> exploredURLs, BlockingQueue<String> validURLs) {
        System.out.println("OK");
        this.toExploreURLs = toExploreURLs;
        this.exploredURLs = exploredURLs;
        this.validURLs = validURLs;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String urlToExplore = this.toExploreURLs.take();
                search(urlToExplore);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void search(String address) throws IOException, InterruptedException {
        // Read the page to obtain the matching expressions and the hyperlinks
        Tools.ParsedPage p = Tools.parsePage(address);
        // Check if matches were found
        String addrWithoutHashtag = address.replaceAll("#(.)*", "");

        if(!this.exploredURLs.contains(addrWithoutHashtag)) {
            this.exploredURLs.put(addrWithoutHashtag);
        }

        if(p.matches().isEmpty()) {
            return;
        }

        this.validURLs.put(addrWithoutHashtag);
        //ajoute les url de p.hrefs() dans toExploreURLs si elles ne sont pas dans exploredURLs et toExploreURLs
        for(String href: p.hrefs()) {
            if (toExploreURLs.contains(href) || exploredURLs.contains(href)) {
                continue;
            } else {
                toExploreURLs.put(href);
            }
        }
    }

}
