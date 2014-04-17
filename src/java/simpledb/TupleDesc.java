package simpledb;

import java.io.Serializable;
import java.util.*;


/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc implements Serializable {

	/**
	 * A help class to facilitate organizing the information of each field
	 * */
	public static class TDItem implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * The type of the field
		 * */
		public final Type fieldType;

		/**
		 * The name of the field
		 * */
		public final String fieldName;

		public TDItem(Type t, String n) {
			this.fieldName = n;
			this.fieldType = t;
		}

		public String toString() {
			return fieldName + "(" + fieldType + ")";
		}
	}

	//PRIVATE
	private Type [] m_type; //Stores type of ith field. 
	private String [] m_name; //Stores name of ith field.
	//PRIVATE    

	/**
	 * @return
	 *        An iterator which iterates over all the field TDItems
	 *        that are included in this TupleDesc
	 * */
	public Iterator<TDItem> iterator() {
		// some code goes here
		return null;
	}

	private static final long serialVersionUID = 1L;

	/**
	 * Create a new TupleDesc with typeAr.length fields with fields of the
	 * specified types, with associated named fields.
	 * 
	 * @param typeAr
	 *            array specifying the number of and types of fields in this
	 *            TupleDesc. It must contain at least one entry.
	 * @param fieldAr
	 *            array specifying the names of the fields. Note that names may
	 *            be null.
	 */
	public TupleDesc(Type[] typeAr, String[] fieldAr) 
	{	
		this.m_type = typeAr;
		this.m_name = fieldAr;
	}

	/**
	 * Constructor. Create a new tuple desc with typeAr.length fields with
	 * fields of the specified types, with anonymous (unnamed) fields.
	 * 
	 * @param typeAr
	 *            array specifying the number of and types of fields in this
	 *            TupleDesc. It must contain at least one entry.
	 */
	public TupleDesc(Type[] typeAr) 
	{	
		this.m_type = typeAr;
		this.m_name = new String[typeAr.length]; 
	}

	/**
	 * @return the number of fields in this TupleDesc
	 */
	public int numFields() 
	{
		return m_type.length;
	}

	/**
	 * Gets the (possibly null) field name of the ith field of this TupleDesc.
	 * 
	 * @param i
	 *            index of the field name to return. It must be a valid index.
	 * @return the name of the ith field
	 * @throws NoSuchElementException
	 *             if i is not a valid field reference.
	 */
	public String getFieldName(int i) throws NoSuchElementException 
	{
		return m_name[i]; 
	}

	/**
	 * Gets the type of the ith field of this TupleDesc.
	 * 
	 * @param i
	 *            The index of the field to get the type of. It must be a valid
	 *            index.
	 * @return the type of the ith field
	 * @throws NoSuchElementException
	 *             if i is not a valid field reference.
	 */
	public Type getFieldType(int i) throws NoSuchElementException 
	{	
		return m_type[i]; 
	}

	/**
	 * Find the index of the field with a given name.
	 * 
	 * @param name
	 *            name of the field.
	 * @return the index of the field that is first to have the given name.
	 * @throws NoSuchElementException
	 *             if no field with a matching name is found.
	 */
	public int fieldNameToIndex(String name) throws NoSuchElementException 
	{	
		if (name == null || name == null) throw new NoSuchElementException();

		for (int i = 0; i < m_name.length; i++) 
		{
			if (m_name[i] != null && m_name[i].equals(name)) return i;
		}
		throw new NoSuchElementException();
	}

	/**
	 * @return The size (in bytes) of tuples corresponding to this TupleDesc.
	 *         Note that tuples from a given TupleDesc are of a fixed size.
	 */
	public int getSize() 
	{
		int size = 0;
		for (Type t : m_type) 
		{
			size += t.getLen();
		}
		return size;
	}

	/**
	 * Merge two TupleDescs into one, with td1.numFields + td2.numFields fields,
	 * with the first td1.numFields coming from td1 and the remaining from td2.
	 * 
	 * @param td1
	 *            The TupleDesc with the first fields of the new TupleDesc
	 * @param td2
	 *            The TupleDesc with the last fields of the TupleDesc
	 * @return the new TupleDesc
	 */
	public static TupleDesc merge(TupleDesc td1, TupleDesc td2) 
	{	
		int length = td1.numFields() + td2.numFields();
		int offset = td1.numFields();
		Type[] typeAr = new Type[length];
		String[] names = new String[length];

		for (int i = 0; i < td1.numFields(); i++)
		{
			typeAr[i] = td1.getFieldType(i);
			names[i] = td1.getFieldName(i);
		}

		for (int i = 0; i < td2.numFields(); i++)
		{
			typeAr[i+offset] = td2.getFieldType(i);
			names[i+offset] = td2.getFieldName(i);
		}

		return new TupleDesc(typeAr, names);
	}

	/**
	 * Compares the specified object with this TupleDesc for equality. Two
	 * TupleDescs are considered equal if they are the same size and if the n-th
	 * type in this TupleDesc is equal to the n-th type in td.
	 * 
	 * @param o
	 *            the Object to be compared for equality with this TupleDesc.
	 * @return true if the object is equal to this TupleDesc.
	 */
	public boolean equals(Object o) 
	{
		try {
			TupleDesc cur = (TupleDesc) o; 

			if (cur.getSize() != this.getSize()) return false; 

			for (int i = 0; i < m_type.length; i++)
			{
				if (m_type[i] != cur.m_type[i]) return false; 
			}
		}
		catch (Exception e) {return false;}

		return true;
	}

	public int hashCode() 
	{
		String hasher = "" + this.m_name + this.m_type;
		return hasher.hashCode();
	}

	/**
	 * Returns a String describing this descriptor. It should be of the form
	 * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
	 * the exact format does not matter.
	 * 
	 * @return String describing this descriptor.
	 */
	public String toString() 
	{
		String result = "";
		for(int i = 0; i < this.numFields(); i++)
		{
			String type1 = getFieldType(i).toString();
			String type2 = getFieldName(i).toString();
			result = result + type1 + "[" + i + "](" + type2 + "[" + i + "]), "; 
		}
		return result;
	}
}
