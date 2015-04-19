package edu.tomr.starter.server;

import java.util.ArrayList;
import java.util.List;

import network.Connection;
import network.NetworkUtilities;
import network.exception.NetworkException;
import network.requests.NWRequest;
import edu.tomr.network.heartbeat.server.ServerBeatController;
import edu.tomr.protocol.StartupMessage;
import edu.tomr.utils.ConfigParams;

public class LoadBalancer {

	static {
		 ConfigParams.loadProperties();
	}
	
	static int startupMsgPort=5000;
	private ServerBeatController beatController;
	
	public LoadBalancer() {
		beatController = new ServerBeatController();
	}
	
	public void startHeartBeatServer() {
		beatController.start();
	}
	
	public void startServer() {
	
		NetworkUtilities utils = null;
		
		try {
			utils = new NetworkUtilities();
		} catch (NetworkException e) {
			e.printStackTrace();
		}
		
		List<String> nodeAddresses = ConfigParams.getIpAddresses();
		
		//make the last one connectFirst
		int i;
		for(i=0;i<nodeAddresses.size()-1;i++){
			
			List<String> neighbors=generateNeighborList(i,nodeAddresses);
			StartupMessage msg=new StartupMessage("Testing123", neighbors, nodeAddresses);
			NWRequest startupRequest=utils.getNewStartupRequest(msg);
			
			Connection temp_connection=new Connection(nodeAddresses.get(i),startupMsgPort);
			
			temp_connection.send_request(startupRequest);
	
		}
		
		List<String> neighbors = generateNeighborList(i,nodeAddresses);
		StartupMessage msg = new StartupMessage("Testing123", neighbors, nodeAddresses, true);
		NWRequest startupRequest = utils.getNewStartupRequest(msg);
		
		Connection temp_connection=new Connection(nodeAddresses.get(i),startupMsgPort);
		
		temp_connection.send_request(startupRequest);
	}

	private static List<String> generateNeighborList(int i, List<String> iPAddresses) {
		
		List<String> neighbors=new ArrayList<String>();
		
		if(i+1>iPAddresses.size()-1){
			neighbors.add(iPAddresses.get(0));
		}
		else{
			neighbors.add(iPAddresses.get(i));
		}
					
		return neighbors;
	}
	
	public static void main(String[] args) {
		
		LoadBalancer loadBalancer = new LoadBalancer();
		//loadBalancer.startHeartBeatServer();
		loadBalancer.startServer();
	}
}
