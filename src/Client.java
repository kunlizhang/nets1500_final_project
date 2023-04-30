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
        b = new Browser(name, this);
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
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream());
            System.out.println("Connected to " + servername + ":" + port);
            doSomething();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    public void getResource(String path) {
        sendRequest("GET " + path);
    }

    public void sendRequest(String request) {
        out.println(request);
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

    private void doSomething() {
        Scanner userInput = new Scanner(System.in); // To change to some other input

        while (true) {
            System.out.println("Enter a path: ");
            String path = userInput.nextLine();
            System.out.println("Path: " + path);

            getResource(path);

            String response = in.nextLine();
            if (response.startsWith("200")) {
                String content = response.substring(4);
                System.out.println("Content: " + content);
//                b.displayContent(content);
            } else if (response.startsWith("404")) {
                b.displayContent("404 Not Found");
            }
        }
    }

    public static void main(String[] args) {
        Client c = new Client("client");
        c.connect("www.sample.com");
    }
}
