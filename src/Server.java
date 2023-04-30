import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket socket;
    private Socket client;
    private String rootPath;

    /**
     * The server for the web browser.
     * @param port      The port to be hosted on.
     * @param rootPath  The root path of the server containing the files.
     */
    public Server(int port, String rootPath) {
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

    private void waitForConnections() {
        int count = 0;
        while (count < 300) {
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

    private void closeConnections() {
        try {
            socket.close();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Example for sample.com
        new Server(8100, "exampleServer");
    }
}