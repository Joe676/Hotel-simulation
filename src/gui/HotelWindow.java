package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import logic.Hotel;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.CardLayout;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class HotelWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	private Hotel hotel;
	private JTextField portTxt;
	private JTextField hostTxt;
	private JTextField roomPort1;
	private JTextField roomPort2;
	private JButton openBtn;
	private JLabel errorLbl;
	private JLabel hostInfoLbl;
	private JLabel portInfoLbl;
	private JPanel roomsPanel;
	private Map<Integer, RoomPanel> roomPanels = new HashMap<>();

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HotelWindow frame = new HotelWindow();
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
	public HotelWindow() {
		setTitle("Hotel");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 455, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new CardLayout(0, 0));
		
		JPanel card1 = new JPanel();
		contentPane.add(card1, "FRONT");
		card1.setLayout(null);
		
		portTxt = new JTextField();
		portTxt.setBounds(208, 67, 96, 20);
		portTxt.setColumns(10);
		card1.add(portTxt);
		
		JLabel portLbl = new JLabel("port:");
		portLbl.setHorizontalAlignment(SwingConstants.TRAILING);
		portLbl.setBounds(66, 70, 131, 14);
		card1.add(portLbl);
		
		openBtn = new JButton("Start");
		openBtn.setBounds(284, 197, 90, 23);
		
		openBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String host = hostTxt.getText();
				int port;
				int port1;
				int port2;
				try {
					port = Integer.parseInt(portTxt.getText());
					port1 = Integer.parseInt(roomPort1.getText());
					port2 = Integer.parseInt(roomPort2.getText());
				} catch (NumberFormatException ne) {
					errorLbl.setText("Port text fields must contain a number");
					return;
				}
				if(!checkPorts(port, port1, port2)) {
					errorLbl.setText("Invalid port numbers");
					return;
				}
				hotel = new Hotel(host, port, port1, port2);
				addWindowToHotel(hotel);
				CardLayout cl = (CardLayout)contentPane.getLayout();
				cl.next(contentPane);
				hostInfoLbl.setText("host: "+host);
				portInfoLbl.setText("port: "+port);
				hotel.start();
			}
		});
		
		card1.add(openBtn);
		
		JLabel hostLbl = new JLabel("host:");
		hostLbl.setHorizontalAlignment(SwingConstants.TRAILING);
		hostLbl.setBounds(66, 28, 131, 14);
		card1.add(hostLbl);
		
		hostTxt = new JTextField();
		hostTxt.setBounds(208, 25, 96, 20);
		hostTxt.setText("localhost");
		hostTxt.setColumns(10);
		card1.add(hostTxt);
		
		errorLbl = new JLabel("");
		errorLbl.setFont(new Font("Tahoma", Font.ITALIC, 11));
		errorLbl.setForeground(Color.RED);
		errorLbl.setHorizontalAlignment(SwingConstants.CENTER);
		errorLbl.setBounds(66, 196, 298, 28);
		card1.add(errorLbl);
		
		JLabel lblNewLabel = new JLabel("Rooms ports from:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		lblNewLabel.setBounds(66, 112, 131, 14);
		card1.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("to:");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.TRAILING);
		lblNewLabel_1.setBounds(66, 154, 131, 14);
		card1.add(lblNewLabel_1);
		
		roomPort1 = new JTextField();
		roomPort1.setBounds(208, 109, 96, 20);
		card1.add(roomPort1);
		roomPort1.setColumns(10);
		
		roomPort2 = new JTextField();
		roomPort2.setBounds(208, 151, 96, 20);
		card1.add(roomPort2);
		roomPort2.setColumns(10);
		
		JPanel card2 = new JPanel();
		contentPane.add(card2, "ROOMS");
		card2.setLayout(new BorderLayout(0, 0));
		
		JPanel hotelInfoPanel = new JPanel();
		card2.add(hotelInfoPanel, BorderLayout.NORTH);
		
		hostInfoLbl = new JLabel("    ");
		hotelInfoPanel.add(hostInfoLbl);
		
		portInfoLbl = new JLabel("     ");
		hotelInfoPanel.add(portInfoLbl);
		
		roomsPanel = new JPanel();
		card2.add(roomsPanel, BorderLayout.CENTER);
		roomsPanel.setLayout(new BorderLayout(0, 0));
	}
	
	protected boolean checkPorts(int p, int p1, int p2) {
		return (p>1024 && p1>1024 && p2>1024 && p<65536 && p1<65536 && p2<65536 && (p<p1 || p>p2) && p1<=p2);
	}

	void addWindowToHotel(Hotel h){
		h.setWindow(this);
	}

	public void addRoom(int number, int port, int size) {
		roomPanels.put(number, new RoomPanel(number, port, size));
		showRooms();
	}
	
	public void deleteRoom(int number) {
		roomPanels.remove(number);
		showRooms();
	}
	
	private void showRooms() {
		roomsPanel.removeAll();

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setPreferredSize(new Dimension(roomsPanel.getWidth(), RoomPanel.height*roomPanels.size()) );
		for(RoomPanel r: roomPanels.values()) {
			p.add(r);
		}
		p.validate();
		p.repaint();
		
		JScrollPane scroll = new JScrollPane(p);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		roomsPanel.add(scroll, BorderLayout.CENTER);
		scroll.validate();
		scroll.repaint();
		roomsPanel.validate();
		roomsPanel.repaint();
	}

	public void updateRoomState(int n, int state) {
		RoomPanel rp = roomPanels.get(n);
		rp.updateState(state);
	}

	public void resign(int n) {
		updateRoomState(n, 0);
	}
}
