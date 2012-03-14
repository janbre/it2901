package no.ntnu.qos.client.net.impl;


import no.ntnu.qos.client.DataObject;
import no.ntnu.qos.client.net.ClientMSCommunicator;
import no.ntnu.qos.ms.MSCommunicator;
import no.ntnu.qos.ms.impl.MSCommunicatorImpl;


/**
 * Implementation of the no.ntnu.qos.client.net.MSCommunicator interface
 * This implementation uses a dummy xml file, as we have no access to an actual ms
 * @author H�vard
 *
 */
public class ClientMSCommunicatorImpl implements ClientMSCommunicator{

	
	private String msXML;
	
	/**
	 * main constructor
	 * @param newMS - path of the xml file containing the routing info
	 */
	public ClientMSCommunicatorImpl(String newMS){
		msXML	= newMS;
	}
	
	@Override
	public Runnable getRouteInfo(DataObject dataObj) {		
		return new RouteFetcher(dataObj);
	}

	/**
	 * private runnable that will be returned by the getRouteInfo method
	 * @author H�vard
	 *
	 */
	private class RouteFetcher implements Runnable{

		private DataObject dataObj;
		
		public RouteFetcher(DataObject data){
			dataObj = data;
		}
		
		
		@Override
		public void run() {
			MSCommunicator msComm = new MSCommunicatorImpl(msXML);
			dataObj.setRoutingInfo(msComm.getRoutingInfo(dataObj.getDestination()));
		}
		
	}
}
