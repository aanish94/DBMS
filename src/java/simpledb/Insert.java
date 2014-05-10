package simpledb;

import java.io.IOException;

/**
 * Inserts tuples read from the child operator into the tableid specified in the
 * constructor
 */
public class Insert extends Operator {

	private static final long serialVersionUID = 1L;
	private TransactionId m_t;
	private DbIterator m_child;
	private int m_tid;
	private boolean flag;
	/**
	 * Constructor.
	 * 
	 * @param t
	 *            The transaction running the insert.
	 * @param child
	 *            The child operator from which to read tuples to be inserted.
	 * @param tableid
	 *            The table in which to insert tuples.
	 * @throws DbException
	 *             if TupleDesc of child differs from table into which we are to
	 *             insert.
	 */
	public Insert(TransactionId t,DbIterator child, int tableid)
			throws DbException {
		m_t = t;
		m_child = child;
		m_tid = tableid;
		flag = false;
	}

	public TupleDesc getTupleDesc() {
		Type [] tempTypeAr = new Type[1];
		tempTypeAr[0] = Type.INT_TYPE;
		TupleDesc tempTD = new TupleDesc(tempTypeAr);
		return tempTD;
	}

	public void open() throws DbException, TransactionAbortedException {
		m_child.open();
		super.open();
		flag = true;
	}

	public void close() {
		m_child.close();
		super.close();
	}

	public void rewind() throws DbException, TransactionAbortedException {
		m_child.rewind();
	}

	/**
	 * Inserts tuples read from child into the tableid specified by the
	 * constructor. It returns a one field tuple containing the number of
	 * inserted records. Inserts should be passed through BufferPool. An
	 * instances of BufferPool is available via Database.getBufferPool(). Note
	 * that insert DOES NOT need check to see if a particular tuple is a
	 * duplicate before inserting it.
	 * 
	 * @return A 1-field tuple containing the number of inserted records, or
	 *         null if called more than once.
	 * @see Database#getBufferPool
	 * @see BufferPool#insertTuple
	 */
	protected Tuple fetchNext() throws TransactionAbortedException, DbException {
		BufferPool curPool = Database.getBufferPool();
		int ctr = 0;
		if (flag)
		{
			while (m_child.hasNext())
			{
				ctr++;
				Tuple cur = m_child.next();
				try {
					curPool.insertTuple(m_t, m_tid, cur);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Tuple newTup = new Tuple(getTupleDesc());
			IntField temp = new IntField(ctr);
			newTup.setField(0, temp);
			flag = false;
			return newTup;
		}
		return null;
	}

	@Override
	public DbIterator[] getChildren() {
		// some code goes here
		DbIterator[] temp = new DbIterator[1];
		temp[0] = m_child;
		return temp;
	}

	@Override
	public void setChildren(DbIterator[] children) {
		// some code goes here
		m_child = children[0];
	}
}
