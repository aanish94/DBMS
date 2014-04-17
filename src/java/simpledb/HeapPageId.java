package simpledb;

/** Unique identifier for HeapPage objects. */
public class HeapPageId implements PageId {

	//PRIVATE
	private int m_tableID;
	private int m_pgNo;
	//PRIVATE
	/**
	 * Constructor. Create a page id structure for a specific page of a
	 * specific table.
	 *
	 * @param tableId The table that is being referenced
	 * @param pgNo The page number in that table.
	 */
	public HeapPageId(int tableId, int pgNo) 
	{
		this.m_tableID = tableId;
		this.m_pgNo = pgNo;
	}

	/** @return the table associated with this PageId */
	public int getTableId() 
	{
		return m_tableID;
	}

	/**
	 * @return the page number in the table getTableId() associated with
	 *   this PageId
	 */
	public int pageNumber() 
	{
		return m_pgNo;
	}

	/**
	 * @return a hash code for this page, represented by the concatenation of
	 *   the table number and the page number (needed if a PageId is used as a
	 *   key in a hash table in the BufferPool, for example.)
	 * @see BufferPool
	 */
	public int hashCode() 
	{
		String hasher = "" + this.m_pgNo + this.m_tableID;
		return hasher.hashCode();
	}

	/**
	 * Compares one PageId to another.
	 *
	 * @param o The object to compare against (must be a PageId)
	 * @return true if the objects are equal (e.g., page numbers and table
	 *   ids are the same)
	 */
	public boolean equals(Object o) 
	{
		try 
		{
			if (o == this) return true;
			if (!(o instanceof HeapPageId)) return false;
			HeapPageId cur = (HeapPageId) o; 
			return (cur.m_pgNo == this.m_pgNo && cur.m_tableID==this.m_tableID);
		}
		catch (Exception e)
		{
			return false;
		}
	}

	/**
	 *  Return a representation of this object as an array of
	 *  integers, for writing to disk.  Size of returned array must contain
	 *  number of integers that corresponds to number of args to one of the
	 *  constructors.
	 */
	public int[] serialize() {
		int data[] = new int[2];

		data[0] = getTableId();
		data[1] = pageNumber();

		return data;
	}

}
