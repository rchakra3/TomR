package edu.tomr.protocol;

import org.codehaus.jackson.annotate.JsonProperty;

public final class ClientRequestPayload {

	@JsonProperty private final String key;
	@JsonProperty private byte[] value;

	public ClientRequestPayload() {
		this.key = null;
	}

	public ClientRequestPayload(String key, byte[] value) {

		this.key = key;
		this.value = value;
	}

	public ClientRequestPayload(String key) {

		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("{ClientRequestPayload: key: ");
		builder.append(key);
		builder.append(" value size: ");
		if(value != null)
			builder.append(value.length);
		else
			builder.append(0);
		
		return builder.toString();
	}



}
