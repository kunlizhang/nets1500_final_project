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

    public void connect(String addr) {
        InetSocketAddress socketAddress = l.recLookup(addr);
        String servername = socketAddress.getHostName();
        int port = socketAddress.getPort();
        try {
            socket = new Socket(servername, port);

            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
}
