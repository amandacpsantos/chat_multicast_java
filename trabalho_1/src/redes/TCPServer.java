package redes;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

class Room {
	private ArrayList<String> roomsList = new ArrayList<>();

	public void addRoom(String room) {
		this.roomsList.add(room);
	}
	
	public String getRooms() {
		return Arrays.toString(this.roomsList.toArray());
	}
	
	public int getSize() {
		return this.roomsList.size();
	}
	
	public String getRoom(String name) {
		int index = this.roomsList.indexOf(name);
		if(index==-1) {
			return "-1";
		}
		else {
			String room = this.roomsList.get(index);
			String port = room.split("#")[1];
			return port;
		}
	}
}

public class TCPServer {
	
	public static Room rooms = new Room();
	
	public static void main(String args[]) {
		ServerSocket listenSocket = null;
		
		try {
			// Porta do servidor
			int serverPort = 7896;
			
			// Fica ouvindo a porta do servidor esperando uma conexao.
			listenSocket = new ServerSocket(serverPort);
			System.out.println("Servidor: ouvindo porta TCP/7896.");

			while (true) {
				Socket clientSocket = listenSocket.accept();
				new Connection(clientSocket, rooms, serverPort);
				System.out.print(clientSocket.getLocalPort());
			}
		} catch (IOException e) {
			System.out.println("Listen socket:" + e.getMessage());
		} finally {
			if (listenSocket != null)
				try {
					listenSocket.close();
					System.out.println("Servidor: liberando porta TCP/7896.");
				} catch (IOException e) {
					/* close falhou */
				}
		}
	}
}

class Connection extends Thread {
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	Room roomsObj;
	int serverPort;

	public Connection(Socket aClientSocket, Room rooms, int serverPort) {
		try {
			roomsObj = rooms;
			this.serverPort = serverPort;
			clientSocket = aClientSocket;
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			this.start();
			
		} catch (IOException e) {
			System.out.println("Conexão:" + e.getMessage());
		}
	}
	
	public void run() {
		try {
			Boolean off = false;
			while(!off) {
				String data = in.readUTF(); // le a linha da entrada				
				System.out.println("Recebido pelo server: " + data);
				
				if(data.contains("!criar")) {
			        String[] name = data.split("@");
					roomsObj.addRoom(name[1]);
					out.writeUTF("\nSala " + name[1] + " criada!");
				}
				else if (data.equalsIgnoreCase("!ver")) {
					out.writeUTF("\nSalas disponíveis: " + roomsObj.getRooms());
				}
				else if(data.contains("!entrar")) {
			        String[] name = data.split("@");
					String room = roomsObj.getRoom(name[1]);
					out.writeUTF(room);
				}
				else if (data.equalsIgnoreCase("!sair")) {
					off = true;
					
				}
			}
			clientSocket.close();
			
		} catch (EOFException e) {
			System.out.println("EOF:" + e.getMessage());
		} catch (IOException e) {
			System.out.println("readline:" + e.getMessage());
		} finally {
			try {
				clientSocket.close();
				System.out.println("Servidor: fechando conexão com cliente.");
			} catch (IOException e) {
				/* close falhou */
			}
		}

	}
}




