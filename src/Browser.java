import javax.swing.*;

public class Browser {
    private JFrame frame;
    private Client client;

    /**
     * The GUI browser for each client.
     * @param name Name for the browser.
     * @param client The client that the browser is for.
     */
    public Browser(String name, Client client) {
        frame = new JFrame("Browser: " + name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,400);
        JButton button = new JButton("Press");
        frame.getContentPane().add(button); // Adds Button to content pane of frame
        frame.setVisible(true);
    }

    public void displayContent(String content) {
        System.out.println(content);
    }

    public static void main(String[] args) {
        new Browser("Test", null);
    }
}
