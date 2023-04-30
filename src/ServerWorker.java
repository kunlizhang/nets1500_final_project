import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ServerWorker implements Runnable {
    private PrintWriter out;
    private Scanner in;
    private Socket client;
    private String rootPath;

    public ServerWorker(Socket client, String rootPath) {
        try {
            this.client = client;
            this.rootPath = rootPath;
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
        while(in.hasNextLine()) {
            String request = in.nextLine();

            if (request.startsWith("GET")) {
                String path = request.substring(4);
                System.out.println("GET request for " + path);
                if (path.equals("/")) {
                    path = "/index.txt";
                }
                String response = "";
                try {
                    BufferedReader br = new BufferedReader(new FileReader(rootPath + path));
                    response = "200\n";
                    String line;
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } catch (FileNotFoundException e) {
                    response = "404\n";
                    e.printStackTrace();
                } catch (IOException e) {
                    response = "500\n";
                    e.printStackTrace();
                } finally {
                    out.println(response);
                }
            } else {
                System.out.println("Unknown request: " + request);
            }
        }
    }
}
