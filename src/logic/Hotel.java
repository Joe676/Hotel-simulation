package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gui.HotelWindow;


public class Hotel {
	private ServerSocket server;
	private int port;
	private Thread t;
	private Map<Integer, Integer> roomStates = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> roomPorts = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> roomSizes = new HashMap<Integer, Integer>();
	private int nextRoomNumber = 1;
	private int nextRoomPort;
	private int maxRoomPort;
	
	HotelWindow window;
	
	public Hotel(String host, int port, int port1, int port2) {
		this.port = port;
		this.nextRoomPort = port1;
		this.maxRoomPort = port2;
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setWindow(HotelWindow w) {
		this.window = w;
	}

	public void start() {
		t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(true) {
						Socket s = server.accept();
						BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
						String response = parseRequest(br.readLine());
						
						if(response!=null) {
							PrintWriter pw = new PrintWriter(s.getOutputStream(), false);
							pw.println(response);
							pw.flush();
							pw.close();
						}
						br.close();
						s.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
	}
	
	private String parseRequest(String r) {
		String[] rq = r.split(";");
		String type = rq[0];
		switch (type) {
		case "RS":
			return roomStart(rq);
		case "RD":
			return roomDelete(rq);
		case "RU":
			return roomUpdate(rq);
		case "TB":
			return terminalBook(rq);
		case "TF":
			return terminalFinish(rq);
		default:
			return null;
		}
	}

	private String terminalFinish(String[] rq) {
		int n = Integer.parseInt(rq[1]);
		roomStates.put(n, 0);
		window.resign(n);
		try {
			Socket s = new Socket("localhost", roomPorts.get(n));
			PrintWriter pw = new PrintWriter(s.getOutputStream(), false);
			pw.println("TF");
			pw.flush();
			pw.close();
			s.close();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return null;
	}

	private String terminalBook(String[] rq) {
		List<Integer> numbers = getRoomNumbers(rq[1], rq[2], rq[3]);
		if(numbers==null)return "N";
		
		String r = "";
		for(int n: numbers) {
			roomBooked(n, rq[4]);
			r += n+":"+roomPorts.get(n);
			if(!(numbers.indexOf(n)==numbers.size()-1))r+=";";
		}
		
		return r;
	}

	private void roomBooked(int n, String client) {
		try {
			Socket s = new Socket("localhost", roomPorts.get(n));
			PrintWriter pw = new PrintWriter(s.getOutputStream(), false);
			pw.println("HB;"+client);
			pw.flush();
			pw.close();
			s.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		window.updateRoomState(n, 1);
	}

	private List<Integer> getRoomNumbers(String o, String tw, String th) {
		int[] rq= {Integer.parseInt(o), Integer.parseInt(tw), Integer.parseInt(th)};
		int[] count = {0, 0, 0};
		List<Integer> possibleNumbers = new ArrayList<Integer>();
		List<Integer> out = new ArrayList<Integer>();
		for(int number:roomSizes.keySet()) {
			int size = roomSizes.get(number);
			int state = roomStates.get(number);
			if(state==0) {
				count[size-1]++;
				possibleNumbers.add(number);
			}
		}
		for(int i = 0; i<3; i++) {
			if(count[i]<rq[i])return null;
			count[i]=0;
		}
		for(int i=0; i<3; i++) {
			for(int number: possibleNumbers) {
				if(roomSizes.get(number)==i+1) {
					out.add(number);
					count[i]++;
					roomStates.put(number, 1);
				}
				
				if(count[i]==rq[i])break;
			}
		}
		return out;
	}

	private String roomUpdate(String[] rq) {
		roomStates.put(Integer.parseInt(rq[1]), Integer.parseInt(rq[2]));
		window.updateRoomState(Integer.parseInt(rq[1]), Integer.parseInt(rq[2]));
		return null;
	}

	private String roomDelete(String[] rq) {
		int number = Integer.parseInt(rq[1]);
		roomSizes.remove(number);
		roomStates.remove(number);
		roomPorts.remove(number);
		window.deleteRoom(number);
		return "Y";
		/*TODO:
		 * inform terminal: terminal must have a socket ehhhhhhhhh
		 */
		
	}

	private String roomStart(String[] rq) {
		if(nextRoomPort>maxRoomPort)return "N";
		String r = ""+(nextRoomNumber)+";"+(nextRoomPort);
		roomSizes.put(nextRoomNumber, Integer.parseInt(rq[1]));
		roomStates.put(nextRoomNumber, 0);
		window.addRoom(nextRoomNumber, nextRoomPort, Integer.parseInt(rq[1]));
		roomPorts.put(nextRoomNumber++, nextRoomPort++);
		return r;
	}
	
}
