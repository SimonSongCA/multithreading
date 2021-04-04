package performanceOptimization;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Optimizing for Throughput Part 2 - HTTP server + Jmeter
 * https://www.udemy.com/java-multithreading-concurrency-performance-optimization
 */

/** how to use this function:
 * 1. run the Java code of this file
 * 2. open a browser and enter: http://localhost:8000/search?talk=I
 * 3. modify the content after the '=' sign to change the content that we want to search
 */

public class ThroughputHttpServer {
    private static final String INPUT_FILE = "./src/performanceOptimization/resources/war_and_peace.txt";
    private static final int NUMBER_OF_THREADS = 8;

    public static void main(String[] args) throws IOException {
        String text = new String(Files.readAllBytes(Paths.get(INPUT_FILE)));
        startServer(text);
    }

    public static void startServer(String text) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        // make a route to /search
        server.createContext("/search", new WordCountHandler(text));
        // initialize an executor with a specific number of threads
        Executor executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        // set the executor and start the server
        server.setExecutor(executor);
        server.start();
    }

    private static class WordCountHandler implements HttpHandler {
        // store the book's content
        private String text;
        // put the text into the constructor
        public WordCountHandler(String text) {
            this.text = text;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            // get the query part of the URI
            String query = httpExchange.getRequestURI().getQuery();
            // break the query into key and value
            String[] keyValue = query.split("=");
            // the action is the key
            String action = keyValue[0];
            // the word is the actual content that we want to search for
            String word = keyValue[1];
            // return a 400 error if something goes wrong(action != "word")
            if (!action.equals("word")) {
                httpExchange.sendResponseHeaders(400, 0);
                return;
            }
            // count the word appearances and store in the variable "count"
            long count = countWord(word);
            // serialization of the variable count to send over the wire.
            byte[] response = Long.toString(count).getBytes();
            // set 200 in the http header & pass the response length
            httpExchange.sendResponseHeaders(200, response.length);
            // set a stream
            OutputStream outputStream = httpExchange.getResponseBody();
            // write the stream with the response that we get.
            outputStream.write(response);
            // close the stream. --> the stream will send the response to the client upstream.
            outputStream.close();
        }

        // a method that counts the word within the text file.
        // This is a brute force method that iterates the entire text content one by one.
        private long countWord(String word) {
            long count = 0;
            int index = 0;
            while (index >= 0) {
                // assign the index of the word we want to search in the 'index' variable
                index = text.indexOf(word, index);

                // increment the variable count and index if there is a match starting from the
                // specific index location.
                if (index >= 0) {
                    count++;
                    index++;
                }
                // exit the loop if the index is negative.
                // The index will become negative when there is no matching substring that equals 'word' starting from 'index'
            }
            // return the total number of appearances of a specific 'word' variable.
            return count;
        }
    }
}
