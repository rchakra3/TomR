package edu.tomr.hash;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import edu.tomr.utils.ConfigParams;

public class ConsistentHashing {
	static TreeMap<Double, String> unitCircle = new TreeMap<Double, String>();

	// Method to calculate the topology as a unit circle on each node
	public static void calculateCircle(List<String> nodes){
		// Evenly distributing the nodes on the unit circle using the concept of virtual replicas
		try{
			for(String node : nodes){
				unitCircle.put(getHash(node), node);
				unitCircle.put((getHash(node) + .33)%.99, node);
				//unitCircle.put((getHash(node) + .6)%.99, node);
			}
		}
		catch(NullPointerException e){
			System.out.println("The nodes have been hashed in a wrong manner");
			e.printStackTrace();

		}


	}

	public static TreeMap<Double, String> getCircle(List<String> nodes){
		calculateCircle(nodes);
		return unitCircle;

	}

	// Method to get the Node which the String has to be redirected to
	public static String getNode(String key){

		//Initializing the unit circle for the first time
		if(unitCircle.isEmpty()){

			getCircle(ConfigParams.getIpAddresses());
		}

		try{
			// Find the position of the key on the unit circle representation.
			Double hashOfKey = getKeyHash(key);
			// Get the nearest node in the Anti Clock-wise direction on the unit circle
			Double nodeNumber = getFirstNode(hashOfKey, unitCircle);
			//Get the IP address of the node nearest to the key
			String nodeName = unitCircle.get(nodeNumber);
			return nodeName;
		}
		catch(NullPointerException e){
			System.out.println("Error while trying to find the node to send the string to");
			e.printStackTrace();
			return null;

		}


	}

	// Method to get the nearest node to the key
	static Double getFirstNode(Double key, TreeMap<Double, String> circle ){
		Double firstKey = 0.0;
		for(Entry<Double, String> nodeHash : circle.entrySet()) {
			Double nodeValue = nodeHash.getKey();
			if(nodeValue < key){
				if(nodeValue > firstKey){
					firstKey = nodeValue;
				}

			}
		}
		// If there is no node before this key, then this key is assigned to the last node
		if(firstKey == 0.0){
			firstKey = circle.lastKey();
		}

		return firstKey;

	}
	// Method that is used to hash the nodes onto the unit circle
	static double getHash(String newString){
		double hashofString = 0;
		try{
			// The murmur hash is an effective way to uniformly hash different string
			HashFunction hf = Hashing.murmur3_128();
			HashCode hc = hf.newHasher().putString(newString).hash();
			byte[] byteArray = hc.asBytes();
			ByteBuffer buffer = ByteBuffer.wrap(byteArray);
			hashofString = buffer.getShort();

		}
		catch(Exception e){
			e.printStackTrace();
		}
		return Math.abs(hashofString)%.99;

	}
	// Method that is used to hash the keys onto the unit circle
	static double getKeyHash(String newString){
		double hashofString = 0;
		try{
			// The murmur hash is an effective way to uniformly hash different string
			HashFunction hf = Hashing.murmur3_128();
			HashCode hc = hf.newHasher().putString(newString).hash();
			byte[] byteArray = hc.asBytes();
			ByteBuffer buffer = ByteBuffer.wrap(byteArray);
			hashofString = buffer.getShort();

		}
		catch(Exception e){
			e.printStackTrace();
		}
		return Math.abs(hashofString)%.99;

	}

	/**
	 * Returns the keys to be redistributed to other nodes
	 * @param keys
	 * @return
	 */
	public static Map<String, List<String>> redistributeKeys(Set<String> keys) {
		Map<String, List<String>> keyNodeMap = new HashMap<String, List<String>>();
		/*for(Entry<Double, String> entry : unitCircle.entrySet()){
			Double key = entry.getKey();
		}*/

		for(String key : keys){
			if(keyNodeMap.containsKey(getNode(key))){
				keyNodeMap.get(getNode(key)).add(key);
			}
			else{
				List<String> keyList = new ArrayList<>();
				keyList.add(key);
				keyNodeMap.put(getNode(key), keyList);
			}
		}

		return keyNodeMap;
	}

	/**
	 * Method to update the unit circle after addition or removal of nodes
	 * @param ipAddresses
	 */
	public static void updateCircle(List<String> ipAddresses) {
		calculateCircle(ipAddresses);

	}
}
