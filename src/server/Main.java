package server;

import java.lang.Thread;

public class Main {
    public static void main(String[] args) {

        /**Create an object of our class HTTPServer and
         * set a path to index.html file**/

        final HTTPServer server = new HTTPServer(8081, "/home/lionliliya/первая страница/");
        server.start();

        System.out.println("Server started...");

        /**When client push ctrl+c - server will stop**/

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                server.stop();
                System.out.println("Server stopped!");
            }
        });
    }
}
