package windows;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import entities.Client;
import entities.Employee;
import entities.Positions;
import store.Cashier;

/**
 * Client Display Window
 * @author Daniel Salgueiro
 */
public class ClientDisplay extends EntityDisplay {

	/**
	 * Client Display window object
	 * @param client - Client object
	 * @param entityManager - Entity Manager window
	 */
	public ClientDisplay(Client client, EntityManager entityManager) {
		super(client, entityManager);
		//Image handling
		ImageIcon icon = new ImageIcon(new ImageIcon("img/client.png").getImage().getScaledInstance(65, 65, Image.SCALE_SMOOTH));
		JLabel JIcon = new JLabel(icon);
		JIcon.setBorder(BorderFactory.createEmptyBorder(15, 25, 5, 10));
		//Client specific command
		JButton hireButton = new JButton("Hire");
		hireButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox<Positions> positionPicker = new JComboBox<>(Positions.values());
				Object[] fields = {
					"Position", positionPicker,
				};    
				int option = JOptionPane.showConfirmDialog(null, fields, "New Employee +", JOptionPane.OK_CANCEL_OPTION);
				if (option == JOptionPane.OK_OPTION) {
					Positions pos = (Positions) positionPicker.getSelectedItem();
					Employee employee = new Employee(client.getName(), client.getCode(),  pos);
					employee.givePoints(client.getPoints());
					employee.setBasket(client.getBasket());
					Cashier.remEntity(client);
					Cashier.addEntity(employee);
					Cashier.saveData();
					refresh();
					entityManager.refresh();
					new EmployeeDisplay(employee, entityManager);
					dispose();
				}
			}
		});
		header.add(JIcon, BorderLayout.WEST);
		this.entityControls.add(hireButton);
		setVisible(true);
	}
}
