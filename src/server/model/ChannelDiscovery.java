package server.model;


/**
 * Class used for representing a user's query to search for channels
 * @author Ilia and Michael
 *
 */
public class ChannelDiscovery {
	
	private String query; // the query that the user entered
	
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