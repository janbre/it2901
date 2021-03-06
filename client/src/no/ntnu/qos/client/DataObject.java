package no.ntnu.qos.client;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Iterator;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import no.ntnu.qos.client.credentials.Token;
import no.ntnu.qos.client.credentials.TokenAxiom;
import no.ntnu.qos.client.impl.ConfigManager;


/**
 * Object containing the data clients wish to send
 * It will send itself when it detects that the criteria are met
 * (is sane, has a token)
 * @author Håvard, Stig Tore
 *
 */
public class DataObject {

	private int			diffServ;
	private int			priority;
	private Sequencer	sequencer;
	private Token		samlToken;
	private String		soapFromClient;
	private String		soapToSend;
	private boolean		prepared;
	private URI			destination;
	private ReceiveObject receiveObj;//added here to let the messageHandler get access to it 
	private ExceptionHandler exceptionHandler;


	/**
	 * main constructor
	 * @param sequencer	- the sequencer creating the object
	 * @param soapFromClient	- SOAP message from client
	 * @param destination	- destination of the message
	 * @param exceptionHandler
	 */
	public DataObject(Sequencer sequencer, String soapFromClient, URI destination, ExceptionHandler exceptionHandler){
		this.sequencer = sequencer;
		this.soapFromClient = soapFromClient;
		this.destination = destination;
		this.exceptionHandler = exceptionHandler;
		this.prepared = false;
		this.soapToSend = "";
	}

	/**
	 * sets the clients SAML-token, sends itself if other criteria are met
	 * @param token	- the Token
	 */
	public void setToken(Token token){
		samlToken = token;
		diffServ	= samlToken.getDiffServ();
		priority	= samlToken.getPriority();

		if (isReadyToSend()){
			sequencer.sendData(this);
		}
	}

	/**
	 *
	 * @return	- a SOAP message
	 * @throws UnsupportedEncodingException 
	 */
	public String getSoap() throws UnsupportedEncodingException{
		if(samlToken != null) {
			if(!prepared) {
					buildSoap();
					prepared = true;
			} 
			return soapToSend;
		}
		return "";
	}

	/**
	 * gets the message destination
	 * @return URI of the destination
	 */
	public URI getDestination(){
		return destination;
	}

	/**
	 * gets the diffServ value this message will have
	 * @return diffServ field for message
	 */
	public int getDiffServ(){
		return diffServ;
	}

	/**
	 * gets the priority value of the message
	 * @return Priority of the message
	 */
	public int getPriority(){
		return priority;
	}

	/**
	 * checks if all necessary data/criteria for sending are present/met
	 * @return - true if ready to send, false if not 
	 */
	private boolean isReadyToSend(){
		if(samlToken != null && destination!=null){
			return true;
		}
		return false;
	}

	/**
	 * returns the SAML token in this object
	 * @return the Token of this message
	 */
	public Token getSamlToken() {
		return samlToken;
	}

	/**
	 * sets the receiveObject that the reply to this message will be returned in
	 * @param receiveObj
	 */
	public void setReceiveObject(ReceiveObject receiveObj) {
		this.receiveObj = receiveObj;		
	}

	/**
	 * gets the receiveObject to return the reply in
	 * @return Receive object for this message
	 */
	public ReceiveObject getReceiveObject(){
		return receiveObj;
	}
	/**
	 * gets the exception handler defined by the client
	 * @return ExceptionHandler
	 */
	public ExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	/**
	 * Build the SOAP message to be sent.
	 * @throws UnsupportedEncodingException 
	 */
	private void buildSoap() throws UnsupportedEncodingException {
		ByteArrayInputStream stream = new ByteArrayInputStream(soapFromClient.getBytes());
		StAXOMBuilder builder = null;
		try {
			builder = new StAXOMBuilder(stream);
		} catch (XMLStreamException e) {
			ConfigManager.LOGGER.warning("Client tried to send invalid SOAP message");
			exceptionHandler.unsupportedEncodingExceptionThrown(new UnsupportedEncodingException());
			return;
		}
		OMElement root = builder.getDocumentElement();
		OMElement parsedToken = null;
		if(samlToken instanceof TokenAxiom) {
			parsedToken = ((TokenAxiom) samlToken).getOMElement();
		} else {
			ByteArrayInputStream tokenStream = new ByteArrayInputStream(samlToken.getXML().getBytes());
			StAXOMBuilder tokenBuilder = null;
			try {
				tokenBuilder = new StAXOMBuilder(tokenStream);
			} catch (XMLStreamException e) {
				ConfigManager.LOGGER.severe("Something went horribly wrong with the XML stream!");
				e.printStackTrace();
			}
			parsedToken = tokenBuilder.getDocumentElement();
		}
		@SuppressWarnings("unchecked")
		Iterator<OMElement> iter = root.getChildrenWithLocalName("Body");
		if(iter.hasNext()) {
			OMElement body = iter.next();
			body.addChild(parsedToken);
//			root.build();
			soapToSend = root.toString();
				
		} else {
			throw new UnsupportedEncodingException();
		}
	}
}