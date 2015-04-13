package network;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

public class NetworkResponseHandler extends ConnectionHandler implements Runnable{

	public NetworkResponseHandler(int incoming_port) {
		super(incoming_port);
		// TODO Auto-generated constructor stub
	}
		
	private NodeNetworkModule networkModule=null;

	@Override
	public void run() { //This needs to listen to incoming neighbor connections and requests
				
		//setup the connection
		try {
			initializeClientSocket();
		} catch (NetworkException e) {
			e.printStackTrace();
		}
		String ownIP=networkModule.utils.getSelfIP();
		//listen and handle all requests
		while(true){
			NWResponse response=getNextResponse();
			
			if(response!=null){
				System.out.println("Response recieved");
				if(ownIP.equals(response.getDestIP())){
					//call method to handle response
				}
				else{
					//add to response queue
				}
			}
		}
	}
		
	public NetworkResponseHandler(int incoming_port,NodeNetworkModule module) throws NetworkException{
		super(incoming_port);	
		this.networkModule=module;
	}
	
	protected NWResponse getNextResponse(){
		
		ObjectMapper mapper = new ObjectMapper();
		NWResponse response=null;
		//currently using scanner. Scanner waits for a newLine character which marks the end of an object
		if(inputScanner.hasNextLine()){
			try {
				response=(NWResponse) mapper.readValue(inputScanner.nextLine(), NWResponse.class);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return response;
	}


}
