import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket socket;
    private Socket client;

    public Server(int port) {
        try {
            socket = new ServerSocket(port);
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
                Runnable sw = new ServerWorker(client);
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
}