package gui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingConstants;

import logic.Terminal;

import javax.swing.JButton;
import javax.swing.border.LineBorder;
import java.awt.Color;

public class RoomPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public static final int height = 60;
	private JLabel numberLbl;
	private JLabel stateLbl;
	
	private Terminal terminal;
	private int number, state, size;

	private JButton b1;

	public RoomPanel(int number, int state, int size) {
		setBorder(new LineBorder(Color.LIGHT_GRAY));
		this.number = number;
		this.state = state;
		this.size = size;
		setLayout(new GridLayout(0, 4, 5, 0));
		
		JLabel lbl1 = new JLabel("Pok\u00F3j nr: ");
		lbl1.setHorizontalAlignment(SwingConstants.TRAILING);
		add(lbl1);
		
		numberLbl = new JLabel(""+number);
		add(numberLbl);
		
		JLabel lbl2 = new JLabel("Stan:");
		lbl2.setHorizontalAlignment(SwingConstants.TRAILING);
		add(lbl2);
		
		stateLbl = new JLabel("Wolny");
		add(stateLbl);
		
		JLabel lbl3 = new JLabel("Rozmiar: ");
		lbl3.setHorizontalAlignment(SwingConstants.TRAILING);
		add(lbl3);
		
		JLabel sizeLbl = new JLabel(""+size);
		add(sizeLbl);

	}
	/**
	 * @wbp.parser.constructor
	 */
	public RoomPanel(int number, int state, int size, Terminal t) {
		this(number, state, size);
		terminal = t;
		updateState(1);
		JButton b = new JButton("Wejdü");
		b.setActionCommand("IN");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(b.getActionCommand().equals("IN")) {
					b.setActionCommand("OUT");
					b.setText("Wyjdü");
					b1.setEnabled(false);
					terminal.walkIn(number);
					updateState(2);
				}else {
					b.setActionCommand("IN");
					b.setText("Wejdü");
					b1.setEnabled(true);
					terminal.walkOut(number);
					updateState(1);
				}
			}
		});
		this.add(b);
		b1 = new JButton("Rezygnuj");
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				terminal.resign(number);
			}
		});
		this.add(b1);
	}

	public void updateState(int newState) {
		switch (newState) {
		case 0:
			this.state = 0;
			stateLbl.setText("Wolny");
			break;
		case 1:
			this.state = 1;
			stateLbl.setText("ZajÍty(pusty)");
			break;
		case 2:
			this.state = 2;
			stateLbl.setText("ZajÍty");
		default:
			break;
		}
	}

	public int getNumber() {
		return number;
	}

}
