package network;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

//starts listening on a particular port for incoming msgs. Use for persistent connections.
public class PersistentIncomingConnectionHandler extends IncomingConnectionHandler{
	
	
	protected Socket clientSocket;
	protected InputStream inputStream=null;
	protected Scanner inputScanner=null;
	
	public PersistentIncomingConnectionHandler(int incoming_port) {
		super(incoming_port);
	}

	protected void initializeClientSocket() throws NetworkException{
		try {
			clientSocket=serverSocket.accept();
			inputStream=clientSocket.getInputStream();
			inputScanner=new Scanner(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
			throw new NetworkException("Error accepting incoming connections\n");
		}
	}
	
	protected void closeClientSocket() throws NetworkException{
		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new NetworkException("There was an error closing the socket\n");
		}
	}


}
