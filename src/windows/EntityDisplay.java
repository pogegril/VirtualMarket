package windows;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import entities.Client;
import entities.Employee;
import store.Cashier;
import store.Product;

/**
 * Entity Window
 * @author Daniel Salgueiro
 */
public class EntityDisplay extends JFrame{
	//Client object to display
	private Client client;
	//Refresh-able JavaSwing objects
	protected JPanel header;
	protected JPanel entityPanel;
	protected JPanel entityControls;
	private JPanel basketPanel;
	private JToggleButton usePoints;
	private JLabel entityPoints;

	/**
	 * Entity Display window
	 * @param client - Client object
	 * @param entityManager - Entity manager window
	 */
	public EntityDisplay(Client client, EntityManager entityManager) {		
		this.client = client;
		setTitle(client.getName());
		setSize(500, 350);
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((screenSize.getWidth() - this.getWidth()) / 2) + 70; 
		int y = (int) ((screenSize.getHeight() - this.getHeight()) / 2) + 60; 
		setLocation(x, y);   
		setLayout(new BorderLayout());

		//Header
		header = new JPanel(new BorderLayout());
		JLabel entityName = new JLabel(client.getName());
		entityName.setHorizontalAlignment(SwingConstants.LEFT); 
		JLabel entityID = new JLabel("NUM: " + client.getCode());
		entityID.setHorizontalAlignment(SwingConstants.LEFT);
		entityPoints = new JLabel("Points: " + client.getPoints());
		entityPoints.setHorizontalAlignment(SwingConstants.LEFT);       
		entityPanel = new JPanel(new GridLayout(0, 1));
		entityPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		entityPanel.add(entityName);
		entityPanel.add(entityID);
		entityPanel.add(entityPoints);
		header.add(entityPanel, BorderLayout.CENTER);

		//Main Panel
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		basketPanel= new JPanel();
		basketPanel.setLayout(new BoxLayout(basketPanel, BoxLayout.Y_AXIS));
		JScrollPane basket = new JScrollPane(basketPanel);
		JButton addToBasket = new JButton("Add to Basket +");
		JButton checkOut = new JButton("Check Out");
		usePoints = new JToggleButton("Save Points");
		usePoints.setAlignmentX(Component.CENTER_ALIGNMENT);

		usePoints.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refresh();
				if (usePoints.isSelected()) {
					usePoints.setText("Use Points") ;
				} else {
					usePoints.setText("Save Points");
				}   		
			}
		});
		addToBasket.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = new JDialog(EntityDisplay.this, "Select Product", true);
				dialog.setSize(400, 300);
				dialog.setLocation(x + 55, y + 35);
				dialog.setLayout(new BorderLayout());

				JLabel label = new JLabel("Choose a Product:", JLabel.CENTER);
				label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
				dialog.add(label, BorderLayout.NORTH);

				JPanel productPicker = new JPanel(new GridLayout(0, 2, 5, 5));
				for (Product product : Cashier.getProducts()) {
					if (product.getShelf() != null) {
						JButton productButton = new JButton(product.getName());
						productButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								JDialog quantityDialog = new JDialog(dialog, "Select Quantity", true);
								quantityDialog.setSize(300, 200);
								quantityDialog.setLocation(x + 100, y + 80);
								quantityDialog.setLayout(new BorderLayout());

								JLabel quantityLabel = new JLabel("Select Quantity:", JLabel.CENTER);
								quantityLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
								quantityDialog.add(quantityLabel, BorderLayout.NORTH);

								SpinnerNumberModel model = new SpinnerNumberModel(1, 1, product.getStock(), 1);
								JSpinner spinner = new JSpinner(model);
								JPanel spinnerPanel = new JPanel();
								spinnerPanel.add(spinner);
								quantityDialog.add(spinnerPanel, BorderLayout.CENTER);

								JButton addButton = new JButton("Add to Basket");
								addButton.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										int quantity = (Integer) spinner.getValue();
										client.addProduct(product, quantity);
										Cashier.saveData();
										refresh();
										quantityDialog.dispose();
									}
								});

								JPanel buttonPanel = new JPanel();
								buttonPanel.add(addButton);
								quantityDialog.add(buttonPanel, BorderLayout.SOUTH);

								quantityDialog.setVisible(true);
							}
						});
						productPicker.add(productButton);
					}          
				}
				JScrollPane products = new JScrollPane(productPicker);
				dialog.add(products, BorderLayout.CENTER);
				dialog.setVisible(true);
			}
		});        
		checkOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.checkOut(usePoints.isSelected());
				Cashier.saveData();
				MainWindow.refresh();
				refresh();
			}
		});
		//Basket & Commands Panels
		JPanel basketCheck = new JPanel(new BorderLayout());
		basketCheck.add(usePoints, BorderLayout.NORTH);
		basketCheck.add(basket, BorderLayout.CENTER);

		entityControls = new JPanel(new GridLayout(0, 1));
		entityControls.setBorder(BorderFactory.createEmptyBorder(15, 130, 15, 15));
		entityControls.add(addToBasket);  
		entityControls.add(checkOut);

		mainPanel.add(basketCheck, BorderLayout.WEST);
		mainPanel.add(entityControls, BorderLayout.EAST);
		add(header, BorderLayout.NORTH);
		add(mainPanel, BorderLayout.CENTER);
		refresh();  
	}

	/**
	 * Refreshes the window
	 */
	public void refresh() {
		basketPanel.removeAll();
		entityPoints.setText("Points: " + client.getPoints());

		//Check display handling
		double check = 0;
		for (Product product : client.getBasket()) {
			check += product.getPrice()*product.getStock();
		}
		if (client instanceof Employee) {
			Employee employee = (Employee) client;
			check = check - check*employee.getPosition().discount()/100;
		}
		if (usePoints.isSelected() ) {
			double points = client.getPoints();
			double pointRate = Client.POINT_RATE;
			if (points/pointRate >= check) {
				check = 0;
			} else {
				check -= points/pointRate;
			}
		}

		//Basket
		JLabel label = new JLabel("Basket | " + round(check, 2) + "€");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		label.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 30));
		label.setOpaque(true);
		basketPanel.add(label);
		for (Product product : this.client.getBasket()) {
			JButton productButton = new JButton(product.getStock() + " " + product.getName());
			productButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
			productButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			productButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					client.remProduct(product);
					refresh();
					Cashier.saveData();
				}
			});
			basketPanel.add(productButton);
		}
		basketPanel.revalidate();
		basketPanel.repaint();
	}

	/**
	 * Rounds doubles to the desired number of decimal digits.
	 * Used to round up values to the cents
	 * @param value - Value to round
	 * @param places - Number of decimal digits
	 * @return rounded value
	 */
	public static double round(double value, int places) {
		if (places < 0) return value;
		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long x= Math.round(value);
		return (double) x / factor;
	}
}
