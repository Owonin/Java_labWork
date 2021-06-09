
import java.net.MalformedURLException;
import java.net.URL;

public class URLDepthPair {
    private String url;
    private String path;
    private String host;
    private int depth;


    URLDepthPair(String url, int depth){
        this.url = url;
        this.depth = depth;
        try {
            URL myurl = new URL(url);
            path = myurl.getPath();
            host = myurl.getHost();
            if(path.length() == 0 || path.charAt(path.length() - 1) != '/'){
                path += "/";
            }
        } catch (MalformedURLException e) {
            System.out.println("MalformedURLException: " + e.getMessage());
        }
    }
    @Override
    public String toString() {
        return depth + "    " + url;
    }

    public int getDepth() {
        return depth;
    }

    public String getUrl() {
        return url;
    }

    public String getHost() {
        return host;
    }

    public String getPath() {
        return path;
    }
}
