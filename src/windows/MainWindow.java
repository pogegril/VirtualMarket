package windows;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import store.Cashier;

/**
 * Program's main window class
 * @author Daniel Salgueiro
 */
public class MainWindow extends JFrame {
	//Final access key to be altered exclusively manually
	private final int ACCESS_KEY = 50789;
	protected static JLabel balance;

	/**
	 * Main window object
	 */
	public MainWindow() {
        setTitle("VirtualMarket");
        setSize(800, 600);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
       
        //Title
        JLabel titleLabel = new JLabel("VirtualMarket");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        //Footer
        balance = new JLabel("Balance: " + EntityDisplay.round(Cashier.getBalance(), 2) + "€", JLabel.CENTER);
        add(balance, BorderLayout.SOUTH);

        //Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
       
        //Image handling
        ImageIcon icon = new ImageIcon(new ImageIcon("img/market.png").getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH));
        JLabel JIcon = new JLabel(icon);
        JIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        JIcon.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(JIcon);
        mainPanel.add(Box.createVerticalStrut(10));

        //Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        JButton entityButton = new JButton("Manage Entities");
        JButton productButton = new JButton("Manage Products");
        JButton quickCheckOut = new JButton("Quick CheckOut");
        
        buttonPanel.add(entityButton);
        buttonPanel.add(productButton);
        buttonPanel.add(quickCheckOut);

        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(10)); 

        //Subtitle
        JLabel welcomeLabel = new JLabel("Welcome to VirtualMarket!", JLabel.CENTER);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(welcomeLabel);
        mainPanel.add(Box.createVerticalGlue());

        entityButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int managerKey = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter access key:", "Access Key", JOptionPane.PLAIN_MESSAGE));
                if (managerKey == ACCESS_KEY) {
                    new EntityManager();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid access key.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        productButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {        
            	int managerKey = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter access key:", "Access Key", JOptionPane.PLAIN_MESSAGE));
                if (managerKey == ACCESS_KEY) {
                    new ProductManager();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid access key.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }    
        });
        quickCheckOut.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		new QuickCheckOut();
        		dispose();
        	}
        });
        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    /**
     * Refreshes the window
     */
    public static void refresh() {
    	balance.setText("Balance: " + EntityDisplay.round(Cashier.getBalance() , 2) + "€");
    }
}
