package network.requests.incoming;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import network.NetworkConstants;
import network.NetworkConstants.Requests;
import network.NetworkUtilities;
import network.incoming.nonpersistent.NonPersistentIncomingConnectionHandler;
import network.outgoing.NeighborConnection;
import network.requests.NWRequest;
import network.requests.outgoing.NodeNeighborModule;
import network.responses.NWResponse;
import network.responses.outgoing.NodeResponseModule;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import edu.tomr.node.base.INode;
import edu.tomr.protocol.BreakIncomingNeighborConnectionMessage;
import edu.tomr.utils.Constants;
//handler for incoming central server requests
public class NodeCentralServerMessageHandler extends NonPersistentIncomingConnectionHandler implements Runnable{

	NodeNeighborModule neighborModule=null;
	NetworkUtilities utils=null;
	List<NeighborConnection> neighborRequestConns=null;
	List<NeighborConnection> neighborResponseConns=null;
	INode mainNode=null;

	public NodeCentralServerMessageHandler(int incoming_port,NodeNeighborModule neighborModule,NodeResponseModule neighborResponseModule,NetworkUtilities utils,INode mainNode) {
		super(incoming_port);
		this.neighborModule=neighborModule;
		this.neighborRequestConns=neighborModule.getOutgoingNeighborConnections();
		this.neighborResponseConns=neighborResponseModule.getOutgoingNeighborConnections();
		this.utils=utils;
		this.mainNode=mainNode;
	}

	@Override
	public void run() {
		while(true){
			Socket clientSocket=super.getNextSocket();
			handleConnection(clientSocket);
		}
	}

	private void handleConnection(Socket socket){
		NWRequest centralRequest=getSingleRequest(socket);

		if(centralRequest.getRequestType().equals(NetworkConstants.requestToString(Requests.BREAK_FORM))){ //it's a break message
			Constants.globalLog.debug("Received a BREAK FORMATION MESSAGE");

			handleBreakRequestFormation(centralRequest);

			Constants.globalLog.debug("Connected to a new outgoing neighbor request connection");
			
			Constants.globalLog.debug("Now need to wait for ACK that new neighbor has successfully made outgoing request conn");
			
			waitForRequestAck();
			
			handleBreakResponseFormation(centralRequest);
			
			Constants.globalLog.debug("Connected to a new outgoing neighbor response connection");
			
			Constants.globalLog.debug("Now need to wait for ACK that new neighbor has successfully made outgoing response conn");
			
			//waitForAck();
			waitForResponseAck();
			
			//send ACK back to server:
			NWResponse serverResponse=new NWResponse(this.utils.getSelfIP(),socket.getInetAddress().getHostAddress());
			
			sendOutgoingResponse(socket,serverResponse);

		}
		else if(centralRequest.getRequestType().equals(NetworkConstants.requestToString(Requests.UPDATE_RING))){
			this.mainNode.handleUpdateRingRequest(centralRequest.getUpdateRingMessage());
		}
		else if(centralRequest.getRequestType().equals(NetworkConstants.requestToString(Requests.INIT_REDISTRIBUTION))){
			Constants.globalLog.debug("Received init redistribution request for node to be removed");
			this.mainNode.handleInitRedistribtion(centralRequest.getInitRedisMessage());
		}
	}

	private void waitForRequestAck() {
		
		for(NeighborConnection con:neighborRequestConns){
			//any response here will be the right one since there are no other direct responses over this conn
			con.getnextResponse();
		}
		
	}
	
	private void waitForResponseAck(){
		for(NeighborConnection con:neighborResponseConns){
			//any response here will be the right one since there are no other direct responses over this conn
			con.getnextResponse();
		}
	}

	private void handleBreakResponseFormation(NWRequest centralRequest) {
		//generate a response with this message
		NWResponse outgoingResponse=new NWResponse(this.utils.getSelfIP(),centralRequest.getBreakFormMessage().getWaitForConnIpAddress());
		Constants.globalLog.debug("Response Conn-sending Break incoming to:"+centralRequest.getBreakFormMessage().getWaitForConnIpAddress());
		//ensure the next node understands its a break and reset formation msg
		outgoingResponse.setResetIncomingResponseMsg();
		//send the response over the neighbor connection
		//currently only one neighbor
		NeighborConnection conn=null;
		for(NeighborConnection nCon:neighborResponseConns){
			conn=nCon;
		}
		conn.send_response(outgoingResponse);
		//close the socket to neighbor
		conn.closeSocket();
		Constants.globalLog.debug("Closed outgoing neighbor response connection");
		synchronized(neighborResponseConns){
			//remove current connection in list
			neighborResponseConns.remove(conn);
			//to be sure
			neighborResponseConns.clear();
			//get new neighbor's IP address
			//create a NeighborConnection with Neighbor on the Response Port
			//add new connection to list
			ArrayList<String> newNeighbors=centralRequest.getBreakFormMessage().getNewNeighborList();
			for(String IP:newNeighbors){
				NeighborConnection newNeighborConnection=new NeighborConnection(IP, NetworkConstants.INCOMING_RESPONSE_PORT);
				neighborResponseConns.add(newNeighborConnection);
				Constants.globalLog.debug("Response- Connected to new neighbor:"+IP);
			}
		}

	}

	private void handleBreakRequestFormation(NWRequest centralRequest){
		//generate a message of type break incoming neighbor connection
		BreakIncomingNeighborConnectionMessage msg=new BreakIncomingNeighborConnectionMessage("So long sucker");
		//generate a request with this message
		NWRequest outgoingRequest=new NWRequest(utils.generate_req_id(),msg,centralRequest.getBreakFormMessage().getWaitForConnIpAddress());
		Constants.globalLog.debug("Request Conn-sending Break incoming to:"+centralRequest.getBreakFormMessage().getWaitForConnIpAddress());
		//send the request over the neighbor connection
		//currently only one neighbor
		NeighborConnection conn=null;
		for(NeighborConnection nCon:neighborRequestConns){
			conn=nCon;
		}
		conn.send_request(outgoingRequest);
		//close the socket to neighbor
		conn.closeSocket();
		Constants.globalLog.debug("Closed outgoing neighbor connection");
		synchronized(neighborRequestConns){
			//remove current connection in list
			neighborRequestConns.remove(conn);
			//to be sure
			neighborRequestConns.clear();
			//get new neighbor's IP address
			//create a NeighborConnection with Neighbor
			//add new connection to list
			ArrayList<String> newNeighbors=centralRequest.getBreakFormMessage().getNewNeighborList();
			for(String IP:newNeighbors){
				NeighborConnection newNeighborConnection=new NeighborConnection(IP, NetworkConstants.INCOMING_NEIGHBOR_PORT);
				neighborRequestConns.add(newNeighborConnection);
				Constants.globalLog.debug("Request- Connected to new neighbor:"+IP);
			}
		}
	}

	private NWRequest getSingleRequest(Socket socket){

		Scanner inputScanner=null;
		try {
			//not closing since if I close the scanner, it closes the inputStream, which closes the socket
			inputScanner = new Scanner(socket.getInputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		ObjectMapper mapper = new ObjectMapper();
		NWRequest request=null;
		//currently using scanner. Scanner waits for a newLine character which marks the end of an object
		Constants.globalLog.debug("Waiting for a message from the server");
		while(!inputScanner.hasNextLine());
		Constants.globalLog.debug("Got message from server");

		try {
			request=mapper.readValue(inputScanner.nextLine(), NWRequest.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//closing the socket
		inputScanner.close();

		return request;
	}

}

