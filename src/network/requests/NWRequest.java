package network.requests;

import static network.NetworkConstants.Requests;

import org.codehaus.jackson.annotate.JsonProperty;

import edu.tomr.protocol.NeighborMessage;
import edu.tomr.protocol.NewNeighborConnectionMessage;
import edu.tomr.protocol.StartupMessage;
import edu.tomr.protocol.DBMessage;
import network.NetworkConstants;

public class NWRequest {
	
	@JsonProperty protected final String request_id;
	@JsonProperty protected final String request_type;
	@JsonProperty protected String destIP=null;
	@JsonProperty protected String srcIP=null;
	
	

	@JsonProperty protected StartupMessage startupMessage=null;
	@JsonProperty protected NewNeighborConnectionMessage newNeighborConnectionMessage=null;
	@JsonProperty protected NeighborMessage neighborMessage=null;
	@JsonProperty protected DBMessage dBMessage=null;
	
	public DBMessage getdBMessage() {
		return dBMessage;
	}

	//only for JACKSON
	private NWRequest(){
		this.request_type="UNKNOWN";
		this.request_id="NA";
	}
	
	//a startup message has no use for source or destiantion IP
	public NWRequest(String req_id,StartupMessage msg){
		this.request_id=req_id;
		this.startupMessage=msg;
		this.request_type=NetworkConstants.requestToString(Requests.STARTUP);
	}
	
	public NWRequest(String req_id,NewNeighborConnectionMessage msg){
		this.request_id=req_id;
		this.newNeighborConnectionMessage=msg;
		this.request_type=NetworkConstants.requestToString(Requests.NEW_NEIGHBOR_CONNECTION);
	}
	
	public NWRequest(String req_id,NeighborMessage msg){
		this.request_id=req_id;
		this.neighborMessage=msg;
		this.request_type=NetworkConstants.requestToString(Requests.NEIGHBOR_MESSAGE);
	}
	
	public NWRequest(String req_id,DBMessage msg,String SourceIP,String DestinationIP){
		this.request_id=req_id;
		this.dBMessage=msg;
		this.request_type=NetworkConstants.requestToString(Requests.DB_OPERATION);
		this.srcIP=SourceIP;
		this.destIP=DestinationIP;
	}
	
	@JsonProperty("request_type")
	public String getRequestType(){
		return this.request_type;
	}
	
	@JsonProperty("request_id")
	public String getRequestID(){
		return this.request_id;
	}
	
	@JsonProperty("startupMessage")
	public StartupMessage getStartupMessage() {
		return this.startupMessage;
	}

	@JsonProperty("newNeighborConnectionMessage")
	public NewNeighborConnectionMessage getNewNeighborConnectionMessage() {
		return this.newNeighborConnectionMessage;
	}

	@JsonProperty("neighborMessage")
	public NeighborMessage getNeighborMessage() {
		return this.neighborMessage;
	}
	
	@JsonProperty("destIP")
	public String getDestinationIP() {
		return destIP;
	}

	@JsonProperty("srcIP")
	public String getSourceIP() {
		return srcIP;
	}
	
	
}
