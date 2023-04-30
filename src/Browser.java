import javax.swing.*;

public class Browser {
    private JFrame frame;

    /**
     * The GUI browser for each client.
     * @param name Name for the client.
     */
    public Browser(String name) {
        frame = new JFrame("Browser: " + name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,400);
        JButton button = new JButton("Press");
        frame.getContentPane().add(button); // Adds Button to content pane of frame
        frame.setVisible(true);
    }
    public static void main(String[] args) {
        new Browser("Test");
    }
}
