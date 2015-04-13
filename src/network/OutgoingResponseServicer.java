package network;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OutgoingResponseServicer implements Runnable{
	
	private ConcurrentLinkedQueue<NWResponse> responseQueue=null;
	private ArrayList<NeighborConnection> outgoingNeighborConnections=null;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			if(!responseQueue.isEmpty()){
				sendResponse(responseQueue.poll());
			}
		}
	}
	
	private void sendResponse(NWResponse response){
		//this will obviously change when a better overlay network is designed
		for(NeighborConnection nc:outgoingNeighborConnections){
			nc.send_response(response);
		}
	}
	
	public OutgoingResponseServicer(ConcurrentLinkedQueue<NWResponse> queue,List<NeighborConnection>outgoingNeighborConnections){
		this.responseQueue=queue;
		this.outgoingNeighborConnections.addAll(outgoingNeighborConnections);
	}

}
