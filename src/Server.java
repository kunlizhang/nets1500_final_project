import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket socket;
    private Socket client;
    private String rootPath;
    private int numClients;

    /**
     * The server for the web browser.
     * @param port      The port to be hosted on.
     * @param rootPath  The root path of the server containing the files.
     */
    public Server(int port, String rootPath) {
        this(port, rootPath, 300);
    }

    /**
     * The server for the web browser.
     * @param port      The port to be hosted on.
     * @param rootPath  The root path of the server containing the files.
     * @param numClients    The number of clients to be handled.
     */
    public Server(int port, String rootPath, int numClients) {
        this.numClients = numClients;
        try {
            socket = new ServerSocket(port);
            this.rootPath = rootPath;
            System.out.println("Server started on port " + port);

            waitForConnections();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    /**
     * Waits for connections from clients. Creates a new thread for each client.
     */
    private void waitForConnections() {
        int count = 0;
        while (count < numClients) {
            try {
                client = socket.accept();
                System.out.println("Client connected");
                count++;
                Runnable sw = new ServerWorker(client, rootPath);
                Thread t = new Thread(sw);
                t.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Closes the connections.
     */
    private void closeConnections() {
        try {
            socket.close();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the server.
     * @param args [port, rootPath]
     */
    public static void main(String[] args) {
        // Example for sample.com
//        new Server(8100, "exampleServer");
        int port = Integer.parseInt(args[0]);
        String rootPath = args[1];
        // Example for nets1500.upenn.edu
//        new Server(5315, "nets1500server");
        new Server(port, rootPath);
    }
}