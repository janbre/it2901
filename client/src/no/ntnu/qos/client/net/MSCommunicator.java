package no.ntnu.qos.client.net;

import java.net.URI;

import no.ntnu.qos.client.DataObject;

/**
 * Interface for communicating with the Monitoring Service on a tactical router
 * in order to fetch link and bandwidth-data.
 * @author H�vard
 *
 */
public interface MSCommunicator {

	/**
	 * method for acquiring route information (link and bandwidth) from the MS given an URI
	 * 
	 * @param destination - URI containing required information 
	 * @return RouteInfo object containing the route information
	 */
	public RouteInfo getRouteInfo(URI destination);
	
	/**
	 * method for acquiring route information (link and bandwidth) from the MS given a DataObject containing
	 * all required information. The route information will be inserted to the DataObject.
	 *  
	 * @param dataObj DataObject containing the required information
	 */
	public void getRouteInfo(DataObject dataObj);
}
