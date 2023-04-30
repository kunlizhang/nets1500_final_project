import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ServerWorker implements Runnable {
    private PrintWriter out;
    private Scanner in;
    private Socket client;

    public ServerWorker(Socket client) {
        try {
            this.client = client;
            out = new PrintWriter(client.getOutputStream(), true);
            in = new Scanner(client.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        doSomething();

        closeConnections();

        System.out.println("Client disconnected");
    }

    private void closeConnections() {
        try {
            client.close();
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doSomething() {

    }
}
