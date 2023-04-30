import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    private Lookup l;
    private Scanner userInput;

    private boolean commandLine = false;

    private Browser b;

    /**
     * Creates a client with a name (primarily for the GUI window).
     * @param name
     */
    public Client(String name) {
        l = new Lookup();
        b = new Browser(name, this);
    }

    /**
     * Creates a client with a name and whether or not it is a command line client.
     * @param name
     * @param commandLine
     */
    public Client(String name, boolean commandLine) {
        this.commandLine = commandLine;
        l = new Lookup();
        if (!commandLine) {
            b = new Browser(name, this);
        }
    }

    /**
     * Connects to the server to send requests.
     * @param addr The address of the server.
     */
    public void connect(String addr) {
        InetSocketAddress socketAddress = l.recLookup(addr);
        String servername = socketAddress.getHostName();
        int port = socketAddress.getPort();
        try {
            socket = new Socket(servername, port);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connected to " + servername + ":" + port);

            if (commandLine) {
                doSomething();
                closeConnections();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a GET request to the server.
     * @param path The path to the resource.
     */
    public void getResource(String path) {
        sendRequest("GET " + path);
    }

    /**
     * Sends a request to the server.
     * @param request
     */
    private void sendRequest(String request) {
        out.println(request);
    }

    /**
     * Processes the response from the server.
     */
    private void processResponse() {
        String response = in.nextLine();
        if (response.startsWith("200")) {
            String content = "";
            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.equals("END")) {
                    break;
                }
                content += line + "\n";
            }
            if (commandLine) {
                System.out.println("Content: " + content);
            } else {
                b.displayContent(content);
            }
        } else if (response.startsWith("404")) {
            if (commandLine) {
                System.out.println("404 Not Found");
            } else {
                b.displayContent("404 Not Found");
            }
        } else if (response.startsWith("500")) {
            if (commandLine) {
                System.out.println("500 Internal Server Error");
            } else {
                b.displayContent("500 Internal Server Error");
            }
        }
    }

    /**
     * Closes connections to the server.
     */
    public void closeConnections() {
        try {
            System.out.println("Disconnecting from server...");
            socket.close();
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doSomething() {
        userInput = new Scanner(System.in); // To change to some other input

        while (true) {
            System.out.println("Enter a path: ");
            String path = userInput.nextLine();
            getResource(path);
            processResponse();
        }
    }

    public static void main(String[] args) {
        Client c = new Client("client");
        c.connect("www.sample.com");
    }
}
