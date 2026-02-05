package windows;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
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
import store.Cashier;
import store.Product;

/**
 * Quick CheckOut Window
 * @author Daniel Salgueiro
 */
public class QuickCheckOut extends JFrame {
	//Refresh-able panels
	private JPanel basketPanel;
	private Client client;

	/**
	 * Quick CheckOut window object
	 */
	public QuickCheckOut() {
		this.client = new Client("client", -1);
		setTitle("Quick CheckOut");
		setSize(450, 250);
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((screenSize.getWidth() - this.getWidth()) / 2) + 70; 
		int y = (int) ((screenSize.getHeight() - this.getHeight()) / 2) + 60; 
		setLocation(x, y);   
		setLayout(new BorderLayout());

		//Header, Basket & Main panels
		JPanel header = new JPanel(new BorderLayout());
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		basketPanel = new JPanel();
		basketPanel.setLayout(new BoxLayout(basketPanel, BoxLayout.Y_AXIS));
		JScrollPane basket = new JScrollPane(basketPanel);

		JButton addToBasket = new JButton("Add to Basket +");
		JButton checkOut = new JButton("Check Out");

		addToBasket.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = new JDialog(QuickCheckOut.this, "Select Product", true);
				dialog.setLocation(x + 55, y + 35);
				dialog.setSize(400, 300);
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
				client.checkOut(false);
				Cashier.saveData();
				new MainWindow();
				dispose();
			}
		});
		//Basket & Check display
		JPanel basketCheck = new JPanel(new BorderLayout());
		basketCheck.add(basket, BorderLayout.CENTER);

		//CheckOut commands panel
		JPanel entityControls = new JPanel(new GridLayout(0, 1));
		entityControls.setBorder(BorderFactory.createEmptyBorder(15, 130, 15, 15));

		entityControls.add(addToBasket);  
		entityControls.add(checkOut);
		mainPanel.add(basketCheck, BorderLayout.WEST);
		mainPanel.add(entityControls, BorderLayout.EAST);
		add(header, BorderLayout.NORTH);
		add(mainPanel, BorderLayout.CENTER);
		refresh();
		setVisible(true);
	}

	/**
	 * Refreshes the window
	 */
	private void refresh() {
		basketPanel.removeAll();
		//Check display handling
		double check = 0;
		for (Product product : client.getBasket()) {
			check += product.getPrice()*product.getStock();
		}
		JLabel label = new JLabel("Basket | " + EntityDisplay.round(check, 2) + "€");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		label.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 30));
		label.setOpaque(true);
		basketPanel.add(label);

		//Basket display
		for (Product product : this.client.getBasket()) {
			JButton productButton = new JButton(product.getStock() + " " + product.getName());
			productButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
			productButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			productButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					client.remProduct(product);
					refresh();
				}
			});
			basketPanel.add(productButton);
		}
		basketPanel.revalidate();
		basketPanel.repaint();
	}
}
