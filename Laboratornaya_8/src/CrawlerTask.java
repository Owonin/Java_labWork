import java.util.ArrayList;

public class CrawlerTask implements Runnable {
    public URLDepthPair pair;
    public URLPool pool;

    CrawlerTask(URLPool pool) {
        this.pool = pool;
    }

    public void run() {
        pair = pool.get();

        ArrayList<String> links = new ArrayList<String>();
        links = Crawler.getURLs(pair);

        for (String link : links) {
            URLDepthPair newPair = new URLDepthPair(link, pair.getDepth() + 1);
            if (!pool.getSeenList().contains(link)) {
                pool.put(newPair);
            }
        }
    }
}