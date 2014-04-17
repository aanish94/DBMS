package simpledb;

import java.io.Serializable;
//import java.util.Arrays;
import java.util.Iterator;


/**
 * Tuple maintains information about the contents of a tuple. Tuples have a
 * specified schema specified by a TupleDesc object and contain Field objects
 * with the data for each field.
 */
public class Tuple implements Serializable {

	private static final long serialVersionUID = 1L;

	//PRIVATE
	private TupleDesc m_tupDesc; 
	private RecordId m_rid;
	private Field [] m_field;
	//PRIVATE

	/**
	 * Create a new tuple with the specified schema (type).
	 * 
	 * @param td
	 *            the schema of this tuple. It must be a valid TupleDesc
	 *            instance with at least one field.
	 */
	public Tuple(TupleDesc td) 
	{  
		m_field = new Field[td.numFields()];
		m_tupDesc = td;
	}

	/**
	 * @return The TupleDesc representing the schema of this tuple.
	 */
	public TupleDesc getTupleDesc() 
	{
		return m_tupDesc;
	}

	/**
	 * @return The RecordId representing the location of this tuple on disk. May
	 *         be null.
	 */
	public RecordId getRecordId() 
	{
		return m_rid;
	}

	/**
	 * Set the RecordId information for this tuple.
	 * 
	 * @param rid
	 *            the new RecordId for this tuple.
	 */
	public void setRecordId(RecordId rid)
	{
		m_rid = rid;
	}

	/**
	 * Change the value of the ith field of this tuple.
	 * 
	 * @param i
	 *            index of the field to change. It must be a valid index.
	 * @param f
	 *            new value for the field.
	 */
	public void setField(int i, Field f)
	{
		m_field[i] = f;
	}

	/**
	 * @return the value of the ith field, or null if it has not been set.
	 * 
	 * @param i
	 *            field index to return. Must be a valid index.
	 */
	public Field getField(int i) 
	{
		return m_field[i];
	}

	/**
	 * Returns the contents of this Tuple as a string. Note that to pass the
	 * system tests, the format needs to be as follows:
	 * 
	 * column1\tcolumn2\tcolumn3\t...\tcolumnN\n
	 * 
	 * where \t is any whitespace, except newline, and \n is a newline
	 */
	public String toString() 
	{
		String result = "";
		for(int i = 0; i < m_field.length; i++)
		{
			result = result + m_field[i] + "\t";
		}
		return result;    
	}

	/**
	 * @return
	 *        An iterator which iterates over all the fields of this tuple
	 * */
	public Iterator<Field> fields()
	{
		return new FieldIterator(this);
	}

	/**
	 * reset the TupleDesc of this tuple
	 * */
	public void resetTupleDesc(TupleDesc td)
	{
		m_tupDesc = td;
	}

	class FieldIterator implements Iterator<Field>
	{
		private Tuple m_iterField;
		private int m_numTup;
		private int m_curTup;

		public FieldIterator(Tuple f) 
		{ 
			this.m_iterField = f;
			m_curTup = 0;
			m_numTup = m_field.length;
		}

		public boolean hasNext() 
		{
			return m_curTup <= m_numTup;
		}
		public Field next() 
		{
			if (m_numTup-m_curTup <= 1) return null; 
			m_curTup++;
			return m_iterField.m_field[m_curTup];
		}
	}
}
