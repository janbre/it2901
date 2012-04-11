package no.ntnu.qos.client.net;

import java.io.UnsupportedEncodingException;

import no.ntnu.qos.client.DataObject;

/**
 * Interface for classes that handle messages and sends them over the network.
 * @author Håvard
 *
 */
public interface MessageHandler {

	/**
	 * sends the given DataObject
	 * @param data - DataObject containing recipient info and other data
	 * @throws UnsupportedEncodingException 
	 */
	public Runnable sendData(DataObject data) throws UnsupportedEncodingException;
}
