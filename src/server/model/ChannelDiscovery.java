package server.model;


/**
 * Class used for check users login credentials before they enter the chat
 * @author Michael
 *
 */
public class ChannelDiscovery {
	private String Query;
	
	public ChannelDiscovery(String query) {
		this.Query = query;
	}
	
	/**
	 * @return the query
	 */
	public String getQuery() {
		return Query;
	}

	/**
	 * @param query the query to set
	 */
	public void setQuery(String query) {
		Query = query;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ChannelDiscovery [Query=" + Query + "]";
	}
}