
import java.util.ArrayList;
import java.util.LinkedList;

public class URLPool {
    private LinkedList<URLDepthPair> inQueueURLs = new LinkedList <URLDepthPair>();
    public LinkedList <URLDepthPair> processedURLs = new LinkedList <URLDepthPair>();
    private ArrayList<String> seenURLs = new ArrayList<String>();
    public int waitingThreads;
    private int maxDepth;

    URLPool(int maxDepth){
        this.maxDepth = maxDepth;
        waitingThreads = 0;
    }

    public synchronized void put(URLDepthPair depthPair) {
        if (depthPair.getDepth() < maxDepth && !seenURLs.contains(depthPair.getUrl())) {
            inQueueURLs.addLast(depthPair);
            seenURLs.add(depthPair.getUrl());
            if (waitingThreads > 0) waitingThreads--;
            this.notify();
        }
    }

    public synchronized URLDepthPair get() {
        if (inQueueURLs.size() == 0) {
            waitingThreads++;
            try {
                this.wait();
            }
            catch (InterruptedException e) {
                System.err.println("MalformedURLException: " + e.getMessage());
                return null;
            }
        }

        URLDepthPair myDepthPair = inQueueURLs.removeFirst();
        processedURLs.add(myDepthPair);
        return myDepthPair;
    }

    public synchronized int getWaitThreads() {
        return waitingThreads;
    }

    public synchronized ArrayList<String> getSeenList() {
        return seenURLs;
    }

}
