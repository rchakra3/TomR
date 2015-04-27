package network.requests;

import network.NetworkConstants;
import network.NetworkConstants.Requests;

import org.codehaus.jackson.annotate.JsonProperty;

import edu.tomr.protocol.BreakFormationMessage;
import edu.tomr.protocol.BreakIncomingNeighborConnectionMessage;
import edu.tomr.protocol.ClientServiceMessage;
import edu.tomr.protocol.DBMessage;
import edu.tomr.protocol.InitRedistributionMessage;
import edu.tomr.protocol.NeighborMessage;
import edu.tomr.protocol.NewNeighborConnectionMessage;
import edu.tomr.protocol.RedistributionMessage;
import edu.tomr.protocol.StartupMessage;
import edu.tomr.protocol.UpdateConnMessage;
import edu.tomr.protocol.UpdateRingMessage;

public class NWRequest {

	@JsonProperty protected final String request_id;
	@JsonProperty protected final String request_type;
	@JsonProperty protected String destIP=null;
	@JsonProperty protected String srcIP=null;
	@JsonProperty protected StartupMessage startupMessage=null;
	@JsonProperty protected NewNeighborConnectionMessage newNeighborConnectionMessage=null;
	@JsonProperty protected NeighborMessage neighborMessage=null;
	@JsonProperty protected BreakFormationMessage breakFormMessage=null;
	@JsonProperty protected ClientServiceMessage serviceMessage=null;
	@JsonProperty protected DBMessage dBMessage=null;
	@JsonProperty protected BreakIncomingNeighborConnectionMessage breakIncomingNeighborMsg=null;
	@JsonProperty protected UpdateConnMessage updateConnMessage=null;
	@JsonProperty protected InitRedistributionMessage initRedisMessage=null;
	
	@JsonProperty protected RedistributionMessage redistributionMessage=null;
	@JsonProperty protected UpdateRingMessage updateRingMessage=null;
	
	public DBMessage getdBMessage() {
		return dBMessage;
	}
	
	public BreakFormationMessage getBreakFormMessage() {
		return breakFormMessage;
	}
	
	public UpdateRingMessage getUpdateRingMessage() {
		return updateRingMessage;
	}
	
	public RedistributionMessage getRedistributionMessage(){
		return redistributionMessage;
	}


	//only for JACKSON
	@SuppressWarnings("unused")
	private NWRequest(){
		this.request_type="UNKNOWN";
		this.request_id="NA";
	}
	
	public NWRequest(String req_id,BreakIncomingNeighborConnectionMessage msg,String destIP){
		this.request_id=req_id;
		this.breakIncomingNeighborMsg=msg;
		this.destIP=destIP;
		this.request_type=NetworkConstants.requestToString(Requests.BREAK_INCOMING_CONNECTION);
	}
	
	public NWRequest(String req_id, UpdateConnMessage msg){
		this.request_id=req_id;
		this.updateConnMessage=msg;
		this.request_type=NetworkConstants.requestToString(Requests.ADD_NODE);
	}

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
	
	public NWRequest(String req_id,RedistributionMessage msg,String SourceIP,String DestinationIP){
		this.request_id=req_id;
		this.redistributionMessage=msg;
		this.request_type=NetworkConstants.requestToString(Requests.REDISTRIBUTION);
		this.srcIP=SourceIP;
		this.destIP=DestinationIP;
	}
	
	//for client to Node requests
	public NWRequest(String req_id,DBMessage msg){
		this.request_id=req_id;
		this.dBMessage=msg;
		this.request_type=NetworkConstants.requestToString(Requests.DB_OPERATION);
	}

	public NWRequest(String req_id, ClientServiceMessage msg) {
		this.request_id=req_id;
		this.serviceMessage=msg;
		this.request_type=NetworkConstants.requestToString(Requests.SERVICE_MESSAGE);

	}

	public NWRequest(String req_id, BreakFormationMessage msg, String SourceIP){
		this.request_id=req_id;
		this.breakFormMessage=msg;
		this.request_type=NetworkConstants.requestToString(Requests.BREAK_FORM);
		this.srcIP=SourceIP;
	}

	public NWRequest(String req_id, UpdateRingMessage msg){
		this.request_id=req_id;
		this.updateRingMessage=msg;
		this.request_type=NetworkConstants.requestToString(Requests.UPDATE_RING);
	}

	public NWRequest(String req_id, RedistributionMessage msg){
		this.request_id=req_id;
		this.redistributionMessage=msg;
		this.request_type=NetworkConstants.requestToString(Requests.REDISTRIBUTION);
	}

	public NWRequest(String req_id, InitRedistributionMessage msg){
		this.request_id=req_id;
		this.initRedisMessage=msg;
		this.request_type=NetworkConstants.requestToString(Requests.REDISTRIBUTION);
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

	@JsonProperty("updateConnMessage")
	public UpdateConnMessage getupdateConnMessage() {
		return updateConnMessage;
	}

	@JsonProperty("initRedisMessage")
	public InitRedistributionMessage getInitRedisMessage() {
		return initRedisMessage;
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
