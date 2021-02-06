package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import gui.RoomWindow;

public class Room{
	private int ID;
	private int type; //Room size - 1, 2 or 3
	private int state; //0 - free, 1 - taken(empty), 2 - taken(full)
	
	private Thread t;
	private ServerSocket server;
	private int port;
	private Socket hotelSocket;
	private String hHost;
	private int hPort;
	
	private RoomWindow window;
	
	public Room(String hotelHost, int hotelPort, int size, RoomWindow w) {
		window = w;
		hHost = hotelHost;
		hPort = hotelPort;
		type = size;
		state = 0;
		try {
			hotelSocket = new Socket(hotelHost, hotelPort);
			PrintWriter pw = new PrintWriter(hotelSocket.getOutputStream(), false);
			pw.println("RS;"+size); // Room Start - Request for a room number and port for server
			pw.flush();
			BufferedReader br = new BufferedReader(new InputStreamReader(hotelSocket.getInputStream()));
			String response;
			do{
				response = br.readLine();
			}while(response==null);
			if(response.equals("N")) {
				window.errorMsg("No more ports available");
				return;
			}
			String[] r = response.split(";");
			this.ID = Integer.parseInt(r[0]);
			this.port = Integer.parseInt(r[1]);
			server = new ServerSocket(port);
			t = new Thread(new Runnable() {
				@Override
				public void run() {
					while(true) {
						try {
							
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
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
				}
			});
			t.start();
			
			window.updateNumber(this.ID);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	protected String parseRequest(String r) {
		String[] rq = r.split(";");
		String type = rq[0];
		switch (type) {
		case "HB":
			return booked(rq);
		case "TS":
			return state(rq);
		case "TF":
			return resign();
		default:
			return null;
		}
	}
	
	private String resign() {
		state = 0;
		window.resign();
		return null;
	}

	private String state(String[] rq) {
		this.state = Integer.parseInt(rq[1]);
		window.changeState(Integer.parseInt(rq[1]));
		hotelUpdateState(rq[1]);
		return null;
	}

	private void hotelUpdateState(String state) {
		try {
			hotelSocket = new Socket(hHost, hPort);
			PrintWriter pw = new PrintWriter(hotelSocket.getOutputStream(), false);
			pw.println("RU;"+this.ID+";"+state);
			pw.flush();
			pw.close();
			hotelSocket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String booked(String[] rq) {
		state = 1;
		window.booked(rq[1]);
		return null;
	}

	public void tryDelete() {
		try {
			hotelSocket = new Socket(hHost, hPort);
			PrintWriter pw = new PrintWriter(hotelSocket.getOutputStream());
			pw.println("RD;"+this.ID);
			pw.flush();
			BufferedReader br = new BufferedReader(new InputStreamReader(hotelSocket.getInputStream()));
			String response;
			do{
				response = br.readLine();
			}while(response==null);
			if(response.equals("N")) {
				window.errorMsg("Delete attempt unsuccessful");
				return;
			}
			else {
				hotelSocket.close();
				pw.close();
				br.close();
				window.setVisible(false);
				window.dispose();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
