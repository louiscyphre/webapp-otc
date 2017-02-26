package server.model;


/**
 * Class used for check users login credentials before they enter the chat
 * @author Michael
 *
 */
public class ChannelDiscovery {
	
	private String query;
	
	public ChannelDiscovery(String query) {
		this.query = query;
	}
	
	/**
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * @param query the query to set
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ChannelDiscovery [Query=" + query + "]";
	}
}