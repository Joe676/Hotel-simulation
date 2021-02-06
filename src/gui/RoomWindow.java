package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import logic.Room;

import java.awt.CardLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;

public class RoomWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField hostTxt;
	private JTextField portTxt;
	private JLabel roomNumberLbl;
	private ButtonGroup group;
	private Room room;
	private JLabel errorLbl;
	protected RoomWindow me;
	private JLabel typeLbl;
	private JLabel stateLbl;
	private JLabel guestLbl;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RoomWindow frame = new RoomWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public RoomWindow() {
		me = this;
		setTitle("Pok\u00F3j");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 236);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new CardLayout(0, 0));
		
		JPanel createPanel = new JPanel();
		contentPane.add(createPanel, "name_406068217701700");
		createPanel.setLayout(null);
		
		JLabel hostLbl = new JLabel("host:");
		hostLbl.setHorizontalAlignment(SwingConstants.CENTER);
		hostLbl.setBounds(8, 64, 96, 14);
		createPanel.add(hostLbl);
		
		hostTxt = new JTextField();
		hostTxt.setText("localhost");
		hostTxt.setBounds(112, 61, 96, 20);
		createPanel.add(hostTxt);
		hostTxt.setColumns(10);
		
		JLabel portLbl = new JLabel("hotel port:");
		portLbl.setHorizontalAlignment(SwingConstants.CENTER);
		portLbl.setBounds(216, 64, 96, 14);
		createPanel.add(portLbl);
		
		portTxt = new JTextField();
		portTxt.setBounds(320, 61, 96, 20);
		createPanel.add(portTxt);
		portTxt.setColumns(10);
		
		JLabel sizeLbl = new JLabel("Rozmiar");
		sizeLbl.setBounds(52, 116, 49, 14);
		createPanel.add(sizeLbl);
		
		JRadioButton radio1 = new JRadioButton("1");
		radio1.setActionCommand("1");
		radio1.setSelected(true);
		radio1.setBounds(153, 112, 38, 23);
		createPanel.add(radio1);
		
		JRadioButton radio2 = new JRadioButton("2");
		radio2.setActionCommand("2");
		radio2.setBounds(243, 112, 38, 23);
		createPanel.add(radio2);
		
		JRadioButton radio3 = new JRadioButton("3");
		radio3.setActionCommand("3");
		radio3.setBounds(333, 112, 38, 23);
		createPanel.add(radio3);
		
		group = new ButtonGroup();
		group.add(radio1);
		group.add(radio2);
		group.add(radio3);
		
		JButton joinBtn = new JButton("Do\u0142\u0105cz");
		joinBtn.setBounds(302, 143, 89, 23);
		
		joinBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String host = hostTxt.getText();
				int port;
				int size;
				try {
					port = Integer.parseInt(portTxt.getText());
					size = Integer.parseInt(group.getSelection().getActionCommand());
				} catch (NumberFormatException e1) {
					errorLbl.setText("W pole port nale¿y wpisaæ numer portu");
					return;
				}
				typeLbl.setText("Rozmiar: "+size);
				room = new Room(host, port, size, me); 
			}
		});
		
		createPanel.add(joinBtn);
		
		errorLbl = new JLabel("");
		errorLbl.setForeground(Color.RED);
		errorLbl.setFont(new Font("Tahoma", Font.ITALIC, 11));
		errorLbl.setHorizontalAlignment(SwingConstants.CENTER);
		errorLbl.setBounds(99, 147, 227, 31);
		createPanel.add(errorLbl);
		
		JPanel statePanel = new JPanel();
		contentPane.add(statePanel, "name_406071926706800");
		statePanel.setLayout(null);
		
		roomNumberLbl = new JLabel("Nr pokoju: 000");
		roomNumberLbl.setFont(new Font("Tahoma", Font.PLAIN, 16));
		roomNumberLbl.setBounds(59, 27, 187, 37);
		statePanel.add(roomNumberLbl);
		
		stateLbl = new JLabel("Stan: Wolny");
		stateLbl.setFont(new Font("Tahoma", Font.PLAIN, 16));
		stateLbl.setBounds(59, 75, 187, 37);
		statePanel.add(stateLbl);
		
		guestLbl = new JLabel("Go\u015B\u0107: ------");
		guestLbl.setFont(new Font("Tahoma", Font.PLAIN, 16));
		guestLbl.setBounds(59, 123, 187, 37);
		statePanel.add(guestLbl);
		
		JButton deleteBtn = new JButton("Usu\u0144 pok\u00F3j");
		deleteBtn.setBounds(297, 132, 89, 23);
		deleteBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				room.tryDelete();
			}
		});
		statePanel.add(deleteBtn);
		
		typeLbl = new JLabel("Rozmiar: ");
		typeLbl.setFont(new Font("Tahoma", Font.PLAIN, 16));
		typeLbl.setBounds(229, 27, 187, 37);
		statePanel.add(typeLbl);
	}

	public void updateNumber(int id) {
		roomNumberLbl.setText("Nr pokoju: "+id);
		CardLayout cl = (CardLayout)contentPane.getLayout();
		cl.next(contentPane);
	}

	public void errorMsg(String msg) {
		errorLbl.setText(msg);
	}

	public void booked(String name) {
		changeState(1);
		guestLbl.setText("Goœæ: "+name);
	}
	
	public void changeState(int newState) {
		switch (newState) {
		case 0:
			stateLbl.setText("Stan: Wolny");
			break;
		case 1:
			stateLbl.setText("Stan: Zajêty(pusty)");
			break;
		case 2:
			stateLbl.setText("Stan: Zajêty");
		default:
			break;
		}
	}

	public void resign() {
		changeState(0);
		guestLbl.setText("Goœæ: ------");
	}
}

