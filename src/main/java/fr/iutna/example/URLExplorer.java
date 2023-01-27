package fr.iutna.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class URLExplorer implements Runnable {
    //Liste blockingqueue de string
    private static final int SIZE_CURRENT_TO_EXPLORE = 1;
    private final BlockingQueue<String> toExploreURLs;
    private final BlockingQueue<String> exploredURLs;
    private final BlockingQueue<String> validURLs;

    private final List<String> currentToExplore = new ArrayList<>();


    public URLExplorer(BlockingQueue<String> toExploreURLs, BlockingQueue<String> exploredURLs, BlockingQueue<String> validURLs) {
        this.toExploreURLs = toExploreURLs;
        this.exploredURLs = exploredURLs;
        this.validURLs = validURLs;
    }

    @Override
    public void run() {
        while (true) {
            try {
                //get explorer
                String url = toExploreURLs.take();
                exploredURLs.add(url);
                search(url);
                //exploredURLs.addAll(currentToExplore);

                //explore
                /*
                for (String urlToExplore : currentToExplore) {
                    search(urlToExplore);
                }*/

                //clear
                currentToExplore.clear();
            } catch (Exception e) {
                //set error to 1 or increment if already exists
                if (WebGrep.errors.containsKey(e)) {
                    WebGrep.errors.get(e).incrementAndGet();
                } else {
                    WebGrep.errors.put(e, new AtomicInteger(1));
                }
            }
        }
    }

    private void search(String address) throws IOException {
        // Read the page to obtain the matching expressions and the hyperlinks
        Tools.ParsedPage p = Tools.parsePage(address);

        if (!p.matches().isEmpty()) {
            this.validURLs.add(address);
            this.toExploreURLs.addAll(p.hrefs().stream().map(href -> href.replaceAll("#(.)*", "").replaceAll("://", "_/")).filter(href -> !this.exploredURLs.contains(href) && !this.toExploreURLs.contains(href)).collect(Collectors.toList()));
        }
    }

}
