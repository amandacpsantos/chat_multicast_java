package multicast;

import java.net.*;
import java.io.*;

public class MulticastPeer extends Thread {
	private volatile boolean isRunning = true;
	private MulticastSocket mSocket = null;
	private InetAddress groupIp = null;
	public int socket = 0;
	
	public MulticastPeer(int socket, String groupIp) {
		
		try {
			this.socket = socket; 
			this.groupIp =  InetAddress.getByName(groupIp);
			this.mSocket = new MulticastSocket(this.socket);
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}
	}
	
	public void joinChat() {
		//Entrar do grupo Multicast
		try {
			this.mSocket.joinGroup(this.groupIp);
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}
	}
	
	public void leaveChat() {
		//Sair do grupo Multicast
		try {
			this.mSocket.leaveGroup(this.groupIp);
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}
	}
	
	public void sendMsg(String user, String txt) {
		//Envia mensagem para o grupo Multicast
		String msg = new String("\n"+user+": "+txt);
		byte[] message = msg.getBytes();
		DatagramPacket messageOut = new DatagramPacket(message, message.length, this.groupIp, this.socket);
		try {
			this.mSocket.send(messageOut);
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}		
	}
	
	public void run() {
		//Escuta o grupo Multicast e envia as mensagens
		byte[] buffer = new byte[1000];
		while(this.isRunning) {
			DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
			try {
				this.mSocket.receive(messageIn);
			} catch (IOException e) {
				System.out.println("IO: " + e.getMessage());
			}
			System.out.println(new String(messageIn.getData()).trim());
			buffer = new byte[1000];
		}
		
	}
	
	public void stopListening() {
		this.isRunning = false;
	}
	
	
}
