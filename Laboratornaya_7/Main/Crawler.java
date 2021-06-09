package com.Main;

import java.net.*;
import java.io.*;
import java.util.*;

public class Crawler {
    public static final String URL_INDICATOR = "a href=\"";
    public static final String URL_ENDING = "\"";

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int maxDepth = 0;
        String[] strings;
        System.out.println("usage: <URL> <depth> (0 - for exit)");

        while (true) {
            strings = br.readLine().split(" ");
            if (strings[0].equals("0"))return;
            if (strings.length != 2) {
                System.out.println("usage: <URL> <depth> (0 - for exit)");
            } else {
                try {
                    maxDepth = Integer.parseInt(strings[1]);
                } catch (NumberFormatException e) {
                    System.out.println("usage: <URL> <depth> (0 - for exit)");
                }
            }
            break;
        }

        LinkedList <URLDepthPair> inQueueURLs = new LinkedList <URLDepthPair>();
        LinkedList <URLDepthPair> processedURLs = new LinkedList <URLDepthPair>();
        ArrayList<String> seenURLs = new ArrayList<String>();

        inQueueURLs.add(new URLDepthPair(strings[0],0));
        seenURLs.add(strings[0]);

        while (inQueueURLs.size() != 0){
            URLDepthPair pair = inQueueURLs.pop();
            processedURLs.add(pair);
            int depth = pair.getDepth();
            ArrayList<String> links = new ArrayList<String>();
            links = getURLs(pair);
            if(maxDepth > depth){
                for (String link:links){
                    if(!seenURLs.contains(link)){
                        inQueueURLs.add(new URLDepthPair(link,depth + 1));
                        seenURLs.add(link);
                    }
                }

            }
        }
        for (URLDepthPair pair:processedURLs) {
            System.out.println(pair);
        }
    }

    private static ArrayList<String> getURLs(URLDepthPair pair){
        ArrayList<String> URLs = new ArrayList<String>();
        Socket socket;
        try {
            socket = new Socket(pair.getHost(),80);
        } catch (UnknownHostException e) {
            System.out.println("UnknownHostException: " + e.getMessage());
            return URLs;
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            return URLs;
        }

        try {
            socket.setSoTimeout(3000);
        } catch (SocketException e) {
            System.out.println("SocketException: " + e.getMessage());
            return URLs;
        }

        InputStream inputStream;
        OutputStream outputStream;

        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            return URLs;
        }

        PrintStream printStream = new PrintStream(outputStream, true);
        printStream.println("GET " + pair.getPath() + " HTTP/1.1");
        printStream.println("Host: " + pair.getHost() + ":80");
        printStream.println("Connection: close");
        printStream.println();

        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            return URLs;
        }
        InputStreamReader in = new InputStreamReader (inputStream);
        BufferedReader reader = new BufferedReader(in);
        while (true){
            String string;
            int beginIndex = 0;
            int endIndex = 0;

            try {
                string = reader.readLine();
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
                return URLs;
            }
            if(string == null)return URLs;
            while (true){
                beginIndex = string.indexOf(URL_INDICATOR,beginIndex);
                if(beginIndex == -1)break;
                if(string.contains("https://"))break;
                beginIndex += URL_INDICATOR.length();
                endIndex = string.indexOf(URL_ENDING, beginIndex);
                String temp = string.substring(beginIndex,endIndex);
                if(!temp.contains("http"))temp = "http://" + pair.getHost() + temp;
                URLs.add(temp);
                beginIndex = endIndex;
            }
        }
    }
}
