import javax.swing.*;

public class Browser {
    private JFrame frame;
    private Client client;
    private JTextField searchbar;
    private JButton searchButton;
    private JTextArea textArea;

    /**
     * Initialises he GUI browser for each client.
     * @param name Name for the browser.
     */
    public Browser(String name) {
        // Prepare frame
        frame = new JFrame("Browser: " + name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,500);

        // Prepare search bar
        JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setBounds(10, 10, 50, 20);
        frame.getContentPane().add(searchLabel);

        searchbar = new JTextField();
        searchbar.setBounds(65, 10, 300, 20);
        frame.getContentPane().add(searchbar);
        searchbar.requestFocus();

        // Prepare search button
        searchButton = new JButton("Search");
        searchButton.setBounds(370, 10, 100, 20);
        searchButton.addActionListener(e -> {
            String path = searchbar.getText();
            String domain = path.split("/")[0];
            if (!client.connect(domain)) {
                textArea.setText("Could not find " + domain);
            } else {
                String resourcePath = path.substring(domain.length());
                String resource = client.getResource(resourcePath);
                textArea.setText(resource);
            }
            client.closeConnections();
        });
        frame.getContentPane().add(searchButton);

        // Prepare text area
        textArea = new JTextArea();
        textArea.setBounds(10, 40, 480, 410);
        textArea.setEditable(false);
        JScrollPane scroll = new JScrollPane (textArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setBounds(10, 40, 480, 410);
        frame.getContentPane().add(scroll);

        frame.setLayout(null);
        frame.setVisible(true);

        this.client = new Client();
    }

    public static void main(String[] args) {
        new Browser("Test");
    }
}
