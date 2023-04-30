import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    private Lookup l;

    private Browser b;

    public Client(String name) {
        l = new Lookup();
        b = new Browser(name);
    }

    /**
     * Connects to the server.
     * @param addr The address of the server.
     */
    public void connect(String addr) {
        InetSocketAddress socketAddress = l.recLookup(addr);
        String servername = socketAddress.getHostName();
        int port = socketAddress.getPort();
        try {
            socket = new Socket(servername, port);
            System.out.println("Connected to " + servername + ":" + port);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    /**
     * Closes connections to the server.
     */
    private void closeConnections() {
        try {
            socket.close();
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client c = new Client("client");
        c.connect("www.sample.com");
    }
}
