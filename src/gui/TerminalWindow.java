package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import logic.Terminal;
import java.awt.CardLayout;
import javax.swing.ScrollPaneConstants;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;

public class TerminalWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	private Terminal terminal;
	private JTextField hostTxt;
	private JTextField portTxt;
	private JTextField loginTxt;
	private JTextField txt1;
	private JTextField txt2;
	private JTextField txt3;
	
	private TerminalWindow me = this;
	private JPanel errorPanel;
	private JLabel errorLbl;
	private Map<Integer, RoomPanel> myRooms = new HashMap<>();
	private JPanel rooms;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TerminalWindow frame = new TerminalWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public TerminalWindow() {
		setTitle("Terminal Klienta");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 614, 325);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new CardLayout(0, 0));
		
		JPanel loginPanel = new JPanel();
		contentPane.add(loginPanel, "LOGIN");
		loginPanel.setLayout(null);
		
		JLabel hostLbl = new JLabel("host:");
		hostLbl.setHorizontalAlignment(SwingConstants.TRAILING);
		hostLbl.setBounds(159, 125, 96, 14);
		loginPanel.add(hostLbl);
		
		hostTxt = new JTextField();
		hostTxt.setText("localhost");
		hostTxt.setBounds(263, 122, 96, 20);
		loginPanel.add(hostTxt);
		hostTxt.setColumns(10);
		
		JLabel portLbl = new JLabel("hotel port:");
		portLbl.setHorizontalAlignment(SwingConstants.TRAILING);
		portLbl.setBounds(159, 155, 96, 14);
		loginPanel.add(portLbl);
		
		portTxt = new JTextField();
		portTxt.setBounds(263, 152, 96, 20);
		loginPanel.add(portTxt);
		portTxt.setColumns(10);
		
		JLabel nameLbl = new JLabel("Login:");
		nameLbl.setHorizontalAlignment(SwingConstants.TRAILING);
		nameLbl.setBounds(159, 71, 96, 14);
		loginPanel.add(nameLbl);
		
		loginTxt = new JTextField();
		loginTxt.setBounds(263, 68, 96, 20);
		loginPanel.add(loginTxt);
		loginTxt.setColumns(10);
		
		JButton loginBtn = new JButton("Loguj");
		loginBtn.setBounds(303, 194, 89, 23);
		loginBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				terminal = new Terminal(loginTxt.getText(), hostTxt.getText(), Integer.parseInt(portTxt.getText()), me);
				CardLayout cl = (CardLayout)contentPane.getLayout();
				cl.next(contentPane);
			}
		});
		loginPanel.add(loginBtn);
		
		JPanel terminalPanel = new JPanel();
		contentPane.add(terminalPanel, "TERMINAL");
		terminalPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel bookForm = new JPanel();
		terminalPanel.add(bookForm, BorderLayout.NORTH);
		bookForm.setLayout(new GridLayout(0, 7, 0, 0));
		
		JLabel lbl1 = new JLabel("1os.");
		lbl1.setHorizontalAlignment(SwingConstants.TRAILING);
		bookForm.add(lbl1);
		
		txt1 = new JTextField();
		bookForm.add(txt1);
		txt1.setColumns(10);
		
		JLabel lbl2 = new JLabel("2os.");
		lbl2.setHorizontalAlignment(SwingConstants.TRAILING);
		bookForm.add(lbl2);
		
		txt2 = new JTextField();
		bookForm.add(txt2);
		txt2.setColumns(10);
		
		JLabel lbl3 = new JLabel("3os.");
		lbl3.setHorizontalAlignment(SwingConstants.TRAILING);
		bookForm.add(lbl3);
		
		txt3 = new JTextField();
		bookForm.add(txt3);
		txt3.setColumns(10);
		
		JButton bookBtn = new JButton("Rezerwuj");
		bookBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int one, two, three;
				if(!txt1.getText().equals("")&&txt1.getText()!=null) {
					one = Integer.parseInt(txt1.getText());
				}else {
					one = 0;
				}
				if(!txt2.getText().equals("")&&txt2.getText()!=null) {
					two = Integer.parseInt(txt2.getText());
				}else {
					two = 0;
				}
				if(!txt3.getText().equals("")&&txt3.getText()!=null) {
					three = Integer.parseInt(txt3.getText());
				}else {
					three = 0;
				}
				
				terminal.requestRooms(one, two, three);
				txt1.setText("");
				txt2.setText("");
				txt3.setText("");
			}
		});
		bookForm.add(bookBtn);
		
		JPanel myRoomsPanel = new JPanel();
		terminalPanel.add(myRoomsPanel, BorderLayout.CENTER);
		myRoomsPanel.setLayout(new BorderLayout(0, 0));
		
		errorPanel = new JPanel();
		myRoomsPanel.add(errorPanel, BorderLayout.NORTH);
		
		errorLbl = new JLabel("");
		errorLbl.setForeground(Color.RED);
		errorLbl.setFont(new Font("Tahoma", Font.ITALIC, 11));
		errorPanel.add(errorLbl);
		
		rooms = new JPanel();
		rooms.setLayout(new BorderLayout());
		myRoomsPanel.add(rooms, BorderLayout.CENTER);
	}

	public void errorMsg(String msg) {
		errorLbl.setText(msg);
	}

	public void addRoom(RoomPanel rp) {
		this.myRooms.put(rp.getNumber(), rp);
	}

	public void showRooms() {
		rooms.removeAll();
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setPreferredSize(new Dimension(rooms.getWidth(), RoomPanel.height*myRooms.size()));
		for(RoomPanel r: myRooms.values()) {
			p.add(r);
		}
		p.validate();
		p.repaint();
		
		JScrollPane scroll = new JScrollPane(p);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		rooms.add(scroll, BorderLayout.CENTER);
		scroll.validate();
		scroll.repaint();
		rooms.validate();
		rooms.repaint();
	}

	public void removeRoom(int number) {
		myRooms.remove(number);
		showRooms();
	}
}



















