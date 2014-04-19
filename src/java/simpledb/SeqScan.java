package simpledb;

import java.util.*;

/**
 * SeqScan is an implementation of a sequential scan access method that reads
 * each tuple of a table in no particular order (e.g., as they are laid out on
 * disk).
 */
public class SeqScan implements DbIterator {

    private static final long serialVersionUID = 1L;
    
    private String tablealias;
    private int _tableid;
    private TransactionId _tid;
    private HeapFile heapfile;
    private DbFileIterator dbit;
    private DbFile file;

    /**
     * Creates a sequential scan over the specified table as a part of the
     * specified transaction.
     * 
     * @param tid
     *            The transaction this scan is running as a part of.
     * @param tableid
     *            the table to scan.
     * @param tableAlias
     *            the alias of this table (needed by the parser); the returned
     *            tupleDesc should have fields with name tableAlias.fieldName
     *            (note: this class is not responsible for handling a case where
     *            tableAlias or fieldName are null. It shouldn't crash if they
     *            are, but the resulting name can be null.fieldName,
     *            tableAlias.null, or null.null).
     */
    public SeqScan(TransactionId tid, int tableid, String tableAlias) {
    	_tableid = (int) tableid;
    	tablealias = tableAlias;
    	_tid = tid;
    	file = Database.getCatalog().getDatabaseFile(tableid);
    	heapfile = (HeapFile) file;
    	dbit = heapfile.iterator(tid);
    }

    /**
     * @return
     *       return the table name of the table the operator scans. This should
     *       be the actual name of the table in the catalog of the database
     * */
    public String getTableName() {
    	return Database.getCatalog().getTableName(_tableid);
    }
    
    /**
     * @return Return the alias of the table this operator scans. 
     * */
    public String getAlias()
    {
    	return this.tablealias;
    }

    /**
     * Reset the tableid, and tableAlias of this operator.
     * @param tableid
     *            the table to scan.
     * @param tableAlias
     *            the alias of this table (needed by the parser); the returned
     *            tupleDesc should have fields with name tableAlias.fieldName
     *            (note: this class is not responsible for handling a case where
     *            tableAlias or fieldName are null. It shouldn't crash if they
     *            are, but the resulting name can be null.fieldName,
     *            tableAlias.null, or null.null).
     */
    public void reset(int tableid, String tableAlias) {
    	this._tableid = (int) tableid;
    	this.tablealias = tableAlias;
    }

    public SeqScan(TransactionId tid, int tableid) {
        this(tid, tableid, Database.getCatalog().getTableName(tableid));
    }

    public void open() throws DbException, TransactionAbortedException {
    	dbit.open();
    }

    /**
     * Returns the TupleDesc with field names from the underlying HeapFile,
     * prefixed with the tableAlias string from the constructor. This prefix
     * becomes useful when joining tables containing a field(s) with the same
     * name.
     * 
     * @return the TupleDesc with field names from the underlying HeapFile,
     *         prefixed with the tableAlias string from the constructor.
     */
    public TupleDesc getTupleDesc() 
    {
        TupleDesc td = file.getTupleDesc();
        
        Type[] types = new Type[td.numFields()];
        String[] names = new String[td.numFields()];
        for (int i = 0; i < td.numFields(); i++) 
        {
            types[i] = td.getFieldType(i);
            names[i] = this.tablealias + "." + td.getFieldName(i);
        }
       TupleDesc td1 = new TupleDesc(types, names);
       return td1;
        
    }

    public boolean hasNext() throws TransactionAbortedException, DbException {
    	return dbit.hasNext();
    }

    public Tuple next() throws NoSuchElementException,
            TransactionAbortedException, DbException {
    	return dbit.next();
    }

    public void close() {
    	dbit.close();
    }

    public void rewind() throws DbException, NoSuchElementException,
            TransactionAbortedException {
    	dbit.rewind();
    }
}