package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import gui.RoomPanel;
import gui.TerminalWindow;

public class Terminal {
	private String clientName;
	private Map<Integer, RoomPanel> myRooms = new HashMap<Integer, RoomPanel>();
	private Map<Integer, Integer> roomPorts = new HashMap<Integer, Integer>();
	
	private String hotelHost;
	private int hotelPort;
	
	private TerminalWindow window;
	
	public Terminal(String name, String host, int port, TerminalWindow w) {
		window = w;
		clientName = name;
		hotelHost = host;
		hotelPort = port;
	}

	public void requestRooms(int one, int two, int three) {
		try {
			Socket s = new Socket(hotelHost, hotelPort);
			PrintWriter pw = new PrintWriter(s.getOutputStream(), false);
			pw.println("TB;"+one+";"+two+";"+three+";"+clientName);
			pw.flush();
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String response;
			do{
				response = br.readLine();
			}while(response==null);
			if(response.equals("N")) {
				window.errorMsg("Pokoje niedostêpne");
				s.close();
				return;
			}
			window.errorMsg("");
			String[] r = response.split(";");
			
			for(int i = 0; i<one+two+three; i++) {
				String[] room = r[i].split(":");
				RoomPanel rp =  new RoomPanel(Integer.parseInt(room[0]), 1, i<one?1:(i<one+two?2:3), this);
				myRooms.put(Integer.parseInt(room[0]), rp);
				roomPorts.put(Integer.parseInt(room[0]), Integer.parseInt(room[1]));
				window.addRoom(rp);
			}
			window.showRooms();
			pw.close();
			br.close();
			s.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void walkIn(int number) {
		changeState(number, 2);
	}

	public void walkOut(int number) {
		changeState(number, 1);
	}

	private void changeState(int number, int i) {
		try {
			Socket s = new Socket(hotelHost, roomPorts.get(number));
			PrintWriter pw = new PrintWriter(s.getOutputStream());
			pw.println("TS;"+i);
			pw.flush();
			pw.close();
			s.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void resign(int number) {
		try {
			Socket s = new Socket(hotelHost, hotelPort);
			PrintWriter pw = new PrintWriter(s.getOutputStream(), false);
			pw.println("TF;"+number);
			pw.flush();
			pw.close();
			s.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		myRooms.remove(number);
		roomPorts.remove(number);
		window.removeRoom(number);
	}
	
	
}
