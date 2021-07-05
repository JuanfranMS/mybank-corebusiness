package com.mybank.corebusiness.api.transaction;

import java.io.Serializable;

/**
 * Request of information about a transaction previously processed by the system. 
 */
@SuppressWarnings("serial")
public class TransactionStatusRequest implements Serializable {

	/** Unique identifier of the transaction asking for. */
	private String reference;
	
	/** Code of the channel the request is coming from. */
	private String channel;

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}
}
