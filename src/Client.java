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

    /**
     * Creates a client.
     */
    public Client() {
        l = new Lookup();
    }

    /**
     * Creates a client.
     */
    public Client(boolean commandLine) {
        this.commandLine = commandLine;
        l = new Lookup();
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
    public String getResource(String path) {
        sendRequest("GET " + path);
        return processResponse();
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
    private String processResponse() {
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
            return content;
        } else if (response.startsWith("404")) {
            return "404 Not Found";
        } else if (response.startsWith("500")) {
            return "500 Internal Server Error";
        } else {
            return "Unknown response";
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
            System.out.println(processResponse());
        }
    }

    public static void main(String[] args) {
        // Tests the command line based client.
        Client c = new Client(true);
        c.connect("www.sample.com");
    }
}
