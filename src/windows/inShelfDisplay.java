package windows;

import store.Cashier;
import store.Product;
import store.Shelf;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Product Display Window
 * @author Daniel Salgueiro
 */
public class inShelfDisplay extends ProductDisplay {

	/**
	 * Product Display (in Shelf) window object
	 * @param product - Product from shelf to display
	 * @param shelfDisplay - Shelf window object
	 */
	public inShelfDisplay(Product product, ShelfDisplay shelfDisplay) {
		super(product);
		//In-Shelf commands
		JButton removeButton = new JButton("Remove");
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (product.getShelf() != null) {
					product.getShelf().remProduct(product);
					Cashier.saveData();
					MainWindow.refresh();
					shelfDisplay.refresh();
					JOptionPane.showMessageDialog(inShelfDisplay.this, "Product removed from shelf.", "Product removed.", JOptionPane.INFORMATION_MESSAGE);
					dispose();
				} else {
					JOptionPane.showMessageDialog(inShelfDisplay.this, "Product is not on any shelf.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		for (ActionListener al : moveTo.getActionListeners()) {
			moveTo.removeActionListener(al);
		}

		moveTo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = new JDialog(inShelfDisplay.this, "Select Shelf", true);
				dialog.setSize(300, 400);
				dialog.setLayout(new BorderLayout());

				JPanel shelfPanel = new JPanel();
				shelfPanel.setLayout(new GridLayout(0, 1));

				for (Shelf shelf : Cashier.getShelves()) {
					JButton shelfButton = new JButton(shelf.getName());
					shelfButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if (product.getShelf() != null) { product.getShelf().remProduct(product);}
							product.setShelf(shelf);
							shelf.addProduct(product);
							Cashier.saveData();
							shelfDisplay.refresh();
							dialog.dispose();
						}
					});
					shelfPanel.add(shelfButton);
				}
				JScrollPane scrollPane = new JScrollPane(shelfPanel);
				dialog.add(scrollPane, BorderLayout.CENTER);
				dialog.setVisible(true);
			}
		});
		productControls.add(removeButton);
		validate();
		repaint();
	}
}
