package redes;

import java.net.*;
import java.util.Scanner;

import multicast.MulticastPeer;

import java.io.*;

public class TCPClient {
	public static void main(String args[]) {
		// arguments supply message and hostname
		Socket s = null;
		try {
			int serverPort = 7896;
			InetAddress ip = InetAddress.getLocalHost();
			String hostname = ip.getHostName();
			s = new Socket(hostname, serverPort);
            System.out.println("Your current IP address : " + hostname);
            
            Scanner read = new Scanner(System.in);
            String op;
    		Boolean exit = false;
    		while(!exit) {
    			System.out.println("\n\nChat - Trabalho Multicast \n"
    					+ "0) Fechar aplicação \n"
    					+ "1) Ver salas; \n"
    					+ "2) Criar sala; \n"
    					+ "3) Entrar na sala; \n"
    					);

    			System.out.printf("Informe a opção: ");
    			op = read.nextLine(); 
    			
    			if (op.equalsIgnoreCase("0")) {
    				exit = true;
    			}
    			
    			else if(op.equalsIgnoreCase("1")) {
    				//Ver salas
    				DataOutputStream out = new DataOutputStream(s.getOutputStream());
    				out.writeUTF("!ver");
    				DataInputStream in = new DataInputStream(s.getInputStream());
    				String data = in.readUTF();
    				System.out.println(data);
    			}

    			else if(op.equalsIgnoreCase("2")) {
    				//Criar na sala
    				System.out.printf("\nDigite o nome da sala e a porta da sala. Ex: name#port: ");
        			String roomName = read.nextLine();
        			roomName = roomName.replaceAll("\\s+","");
    				DataOutputStream out = new DataOutputStream(s.getOutputStream());
    				out.writeUTF("!criar@"+roomName);
    				DataInputStream in = new DataInputStream(s.getInputStream());
    				String data = in.readUTF(); 
    				System.out.println(data);
    			}
    			
    			else if(op.equalsIgnoreCase("3")) {
    				//Entrar na sala
    				
    				System.out.printf("Digite o nome da sala que deseja entrar. Ex: name#port : ");
        			String roomName = read.nextLine();
    				DataOutputStream out = new DataOutputStream(s.getOutputStream());
    				out.writeUTF("!entrar@"+roomName);
    				
    				DataInputStream in = new DataInputStream(s.getInputStream());
    				String roomIP = in.readUTF();
    				
    				if(!roomIP.contains("-1")) {
    					//Entrar na sala
        				MulticastPeer mp = new MulticastPeer(serverPort, roomIP);
        				mp.joinChat();
        				mp.start();    				
        				Boolean start = true;
        				
        				System.out.printf("Digite seu nome: ");
            			String name = read.nextLine();
            			System.out.printf("Digite !sair a qualquer momento para sair da conversa.");
            			mp.sendMsg(name, "Entrou na sala.");
        				while(start) {
                			op = read.nextLine();
                			DataOutputStream outMsg = new DataOutputStream(s.getOutputStream());
                			if(op.equalsIgnoreCase("!sair")) {
                				start=false;
            					outMsg.writeUTF("!sair");
            					mp.stopListening();
            					mp.leaveChat();
            					}
                			else {
                				mp.sendMsg(name, op);
                			}
        				}
    				}else {
    					System.out.printf("Não foi encontrado a sala.");
    				}
    			}

    		}
    		read.close();
		} catch (UnknownHostException e) {
			System.out.println("Socket:" + e.getMessage());
		} catch (EOFException e) {
			System.out.println("EOF:" + e.getMessage());
		} catch (IOException e) {
			System.out.println("readline:" + e.getMessage());
		} finally {
			if (s != null)
				try {
					s.close();
				} catch (IOException e) {
					System.out.println("close:" + e.getMessage());
				}
		}
	}
}

