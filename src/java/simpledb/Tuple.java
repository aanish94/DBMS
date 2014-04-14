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
    private TupleDesc m_td; 
    private RecordId m_rid;
    private Field [] m_f;
    //PRIVATE
    
    /**
     * Create a new tuple with the specified schema (type).
     * 
     * @param td
     *            the schema of this tuple. It must be a valid TupleDesc
     *            instance with at least one field.
     */
    public Tuple(TupleDesc td) {  
    	m_f = new Field[td.numFields()];
    	m_td = td;
    }

    /**
     * @return The TupleDesc representing the schema of this tuple.
     */
    public TupleDesc getTupleDesc() {
        return m_td;
    }

    /**
     * @return The RecordId representing the location of this tuple on disk. May
     *         be null.
     */
    public RecordId getRecordId() {
        return m_rid;
    }

    /**
     * Set the RecordId information for this tuple.
     * 
     * @param rid
     *            the new RecordId for this tuple.
     */
    public void setRecordId(RecordId rid) {
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
    public void setField(int i, Field f) {
    	if (i < 0 || i > m_f.length) { return; }
    	m_f[i] = f;
    }

    /**
     * @return the value of the ith field, or null if it has not been set.
     * 
     * @param i
     *            field index to return. Must be a valid index.
     */
    public Field getField(int i) {
    	if (i < 0 || i > m_f.length) return null;
        return m_f[i];
    }

    /**
     * Returns the contents of this Tuple as a string. Note that to pass the
     * system tests, the format needs to be as follows:
     * 
     * column1\tcolumn2\tcolumn3\t...\tcolumnN\n
     * 
     * where \t is any whitespace, except newline, and \n is a newline
     */
    public String toString() {
        // some code goes here
        throw new UnsupportedOperationException("Implement this");
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
    	m_td = td;
    }
    
    class FieldIterator implements Iterator<Field>
    {
    	private Tuple m_if;
    	private int m_numT;
    	private int m_curT;
    	
    	public FieldIterator(Tuple f) { 
    		this.m_if = f;
    		m_curT = 0;
    		m_numT = m_f.length;
    		}
    	
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return m_curT <= m_numT;
		}
		public Field next() {
			// TODO Auto-generated method stub
			if (m_numT - m_curT <=1 ) { return null; }
			m_curT++;
			return m_if.m_f[m_curT];
		}
    }
}
