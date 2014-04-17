package simpledb;

import java.io.Serializable;

/**
 * A RecordId is a reference to a specific tuple on a specific page of a
 * specific table.
 */
public class RecordId implements Serializable {

	private static final long serialVersionUID = 1L;

	//PRIVATE
	private PageId m_pageID;
	private int m_tupleN;
	//PRIVATE
	/**
	 * Creates a new RecordId referring to the specified PageId and tuple
	 * number.
	 * 
	 * @param pid
	 *            the pageid of the page on which the tuple resides
	 * @param tupleno
	 *            the tuple number within the page.
	 */
	public RecordId(PageId pid, int tupleno) 
	{
		this.m_pageID = pid;
		this.m_tupleN = tupleno;
	}

	/**
	 * @return the tuple number this RecordId references.
	 */
	public int tupleno() 
	{
		return m_tupleN;
	}

	/**
	 * @return the page id this RecordId references.
	 */
	public PageId getPageId() 
	{
		return m_pageID;
	}

	/**
	 * Two RecordId objects are considered equal if they represent the same
	 * tuple.
	 * 
	 * @return True if this and o represent the same tuple
	 */
	@Override
	public boolean equals(Object o) 
	{
		try 
		{
			if (o == this) return true;
			if (!(o instanceof RecordId)) return false;
			RecordId cur = (RecordId) o; 
			return (cur.m_pageID.equals(this.m_pageID) && cur.m_tupleN == this.m_tupleN);
		} 
		catch (Exception e) 
		{
			return false;
		}
	}	

	/**
	 * You should implement the hashCode() so that two equal RecordId instances
	 * (with respect to equals()) have the same hashCode().
	 * 
	 * @return An int that is the same for equal RecordId objects.
	 */
	@Override
	public int hashCode() 
	{
		String hasher = "" + this.m_tupleN + this.m_pageID;
		return hasher.hashCode();
	}

}
