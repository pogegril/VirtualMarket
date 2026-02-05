package windows;

import store.Cashier;
import store.Shelf;
import store.Product;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Program's main window class
 * @author Daniel Salgueiro
 */
public class ShelfDisplay extends JFrame {
	private Shelf shelf;
	//Refresh-able panels
	private JPanel storagePanel;
	private JLabel shelfTitle;
	private JLabel shelfDesc;
	private JLabel footer;

	/**
	 * Shelf Display window object
	 * @param shelf - Shelf object to display
	 * @param productManager - Product Manager window object
	 */
	public ShelfDisplay(Shelf shelf, ProductManager productManager) {
		this.shelf = shelf;
		setTitle(shelf.getName());
		setSize(500, 350);
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((screenSize.getWidth() - this.getWidth()) / 2) + 70; 
		int y = (int) ((screenSize.getHeight() - this.getHeight()) / 2) + 60; 
		setLocation(x, y);   
		setLayout(new BorderLayout());

		//Header & Image handling
		JPanel header = new JPanel(new BorderLayout());
		ImageIcon icon = new ImageIcon(new ImageIcon("img/shelf.png").getImage().getScaledInstance(65, 65, Image.SCALE_SMOOTH));
		JLabel JIcon = new JLabel(icon);
		JIcon.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 10));

		shelfTitle = new JLabel(shelf.getName() + "     " + shelf.getStorage() + "/" + shelf.getSize());
		shelfTitle.setHorizontalAlignment(SwingConstants.LEFT);
		JLabel shelfCost = new JLabel("Cost: " + shelf.getCost() + "€");
		shelfCost.setHorizontalAlignment(SwingConstants.LEFT);
		shelfDesc = new JLabel(shelf.getDesc());
		shelfDesc.setHorizontalAlignment(SwingConstants.LEFT);

		//Shelf panel
		JPanel shelfPanel = new JPanel(new GridLayout(0, 1));
		shelfPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		shelfPanel.add(shelfTitle);
		shelfPanel.add(shelfCost);
		shelfPanel.add(shelfDesc);
		header.add(shelfPanel, BorderLayout.CENTER);

		//Main Panel
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		//Storage & commands
		storagePanel = new JPanel();
		storagePanel.setLayout(new BoxLayout(storagePanel, BoxLayout.Y_AXIS));
		JScrollPane storage = new JScrollPane(storagePanel);

		JButton addProduct = new JButton("Add Product +");
		JButton discount = new JButton("Discount %");
		JButton changeDesc = new JButton("Change Description");
		JButton remove = new JButton("Remove");
		JPanel shelfControls = new JPanel(new GridLayout(0, 1));
		shelfControls.setBorder(BorderFactory.createEmptyBorder(15, 130, 15, 15));
		shelfControls.add(addProduct);
		shelfControls.add(discount);
		shelfControls.add(changeDesc);
		shelfControls.add(remove);

		addProduct.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = new JDialog(ShelfDisplay.this, "Select Product", true);
				dialog.setSize(400, 300);
				dialog.setLocation(x + 55, y + 35);
				dialog.setLayout(new BorderLayout());

				JLabel label = new JLabel("Choose a Product:", JLabel.CENTER);
				label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
				dialog.add(label, BorderLayout.NORTH);

				JPanel productsPanel = new JPanel(new GridLayout(0, 2, 5, 5));
				for (Product product : Cashier.getProducts()) {
					JButton productButton = new JButton(product.getName());
					productButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							for (Product prod : shelf.getShelf()) {
								if (prod.equals(product)) {
									JOptionPane.showMessageDialog(ShelfDisplay.this, product.getName() + " is already in the shelf.", "Error", JOptionPane.ERROR_MESSAGE);
									return;
								}
								if (product.getShelf() != null) {
									JOptionPane.showMessageDialog(ShelfDisplay.this, "The product already is on a shelf.", "Error", JOptionPane.ERROR_MESSAGE);
									return;
								}
							}
							shelf.addProduct(product);
							Cashier.saveData();
							refresh();
							MainWindow.refresh();
							dialog.dispose();
						}
					});
					productsPanel.add(productButton);
				}
				JScrollPane products = new JScrollPane(productsPanel);
				dialog.add(products, BorderLayout.CENTER);
				dialog.setVisible(true);
			}});    
		discount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String discount = JOptionPane.showInputDialog(ShelfDisplay.this, "Enter the discount:", "Discount", JOptionPane.PLAIN_MESSAGE);
				if (discount != null) {
					try {
						double disc = Double.parseDouble(discount);
						shelf.discount(disc);
						Cashier.saveData();
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(ShelfDisplay.this, "Invalid price received.", "Error", JOptionPane.ERROR_MESSAGE);
					}}
			} 
		});
		changeDesc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String desc = JOptionPane.showInputDialog(ShelfDisplay.this, "Enter the new description:", "Change Description", JOptionPane.PLAIN_MESSAGE);
				if (desc != null && !desc.equals("")) {
					shelf.setDesc(desc);
				} else if (desc.equals("")){
					shelf.remDesc();
				}
				Cashier.saveData();
				refresh();
			}
		});
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Cashier.remShelf(shelf);
				Cashier.saveData();
				MainWindow.refresh();
				productManager.refresh();
				dispose();
			}
		});

		//Footer
		footer = new JLabel("Balance: " + EntityDisplay.round(shelf.getBalance(), 2) + "€", JLabel.CENTER);
		add(footer, BorderLayout.SOUTH);
		header.add(JIcon, BorderLayout.WEST);
		mainPanel.add(storage, BorderLayout.WEST);
		refresh();
		mainPanel.add(shelfControls, BorderLayout.EAST);
		add(header, BorderLayout.NORTH);
		add(mainPanel, BorderLayout.WEST);
		setVisible(true);
	}

	/**
	 * Refreshes the window
	 */
	public void refresh() {
		storagePanel.removeAll();

		footer.setText("Balance: " + EntityDisplay.round(shelf.getBalance(), 2) + "€");
		shelfTitle.setText(shelf.getName() + "     " + shelf.getStorage() + "/" + shelf.getSize());
		shelfDesc.setText(shelf.getDesc());
		//Shelf
		JLabel label = new JLabel("Shelf");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		label.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 30));
		label.setOpaque(true);
		storagePanel.add(label);

		for (Product product : shelf.getShelf()) {
			JButton productButton = new JButton(product.getName());
			productButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
			productButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			productButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new inShelfDisplay(product, ShelfDisplay.this);
				}
			});
			storagePanel.add(productButton);
		}
		storagePanel.revalidate();
		storagePanel.repaint();
	}
}
