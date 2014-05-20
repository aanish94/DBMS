package simpledb;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TableStats represents statistics (e.g., histograms) about base tables in a
 * query. 
 * 
 * This class is not needed in implementing lab1 and lab2.
 */
public class TableStats {

	private static final ConcurrentHashMap<String, TableStats> statsMap = new ConcurrentHashMap<String, TableStats>();

	static final int IOCOSTPERPAGE = 1000;

	public static TableStats getTableStats(String tablename) {
		return statsMap.get(tablename);
	}

	public static void setTableStats(String tablename, TableStats stats) {
		statsMap.put(tablename, stats);
	}

	public static void setStatsMap(HashMap<String,TableStats> s)
	{
		try {
			java.lang.reflect.Field statsMapF = TableStats.class.getDeclaredField("statsMap");
			statsMapF.setAccessible(true);
			statsMapF.set(null, s);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

	}

	public static Map<String, TableStats> getStatsMap() {
		return statsMap;
	}

	public static void computeStatistics() {
		Iterator<Integer> tableIt = Database.getCatalog().tableIdIterator();

		System.out.println("Computing table stats.");
		while (tableIt.hasNext()) {
			int tableid = tableIt.next();
			TableStats s = new TableStats(tableid, IOCOSTPERPAGE);
			setTableStats(Database.getCatalog().getTableName(tableid), s);
		}
		System.out.println("Done.");
	}

	/**
	 * Number of bins for the histogram.
	 * Feel free to increase this value over 100,
	 * though our tests assume that you have at least 100 bins in your histograms.
	 */
	static final int NUM_HIST_BINS = 100;

	private int m_ioCostPerPage;
	private DbFile m_file;
	private Object[] m_histograms; // One histogram per field in a table
	private int[] m_allMin; //The minimum value for all IntField's 
	private int[] m_allMax; //The maximum value for all IntField's
	private TupleDesc m_schema;
	private int m_numFields;
	private DbFileIterator m_iter;

	/**
	 * Create a new TableStats object, that keeps track of statistics on each column of a table
	 * 
	 * @param tableid The table over which to compute statistics
	 * @param ioCostPerPage The cost per page of IO.  
	 * 		                This doesn't differentiate between sequential-scan IO and disk seeks.
	 */
	public TableStats (int tableid, int ioCostPerPage) {
		// For this function, you'll have to get the DbFile for the table in question,
		// then scan through its tuples and calculate the values that you need.
		// You should try to do this reasonably efficiently, but you don't necessarily
		// have to (for example) do everything in a single scan of the table.
		// some code goes here
		m_ioCostPerPage = ioCostPerPage;
		m_file = Database.getCatalog().getDatabaseFile(tableid);

		m_schema = m_file.getTupleDesc();
		m_numFields = m_schema.numFields();
		m_iter = m_file.iterator(new TransactionId());
		try {
			m_iter.open();

			determineHistoRange(); //Determines the min & max value of each IntField
			initializeHistograms(); //Creates histogram for each IntField
		} catch (Exception e) {}
	}


	/** 
	 * Estimates the
	 * cost of sequentially scanning the file, given that the cost to read
	 * a page is costPerPageIO.  You can assume that there are no
	 * seeks and that no pages are in the buffer pool.
	 * 
	 * Also, assume that your hard drive can only read entire pages at once,
	 * so if the last page of the table only has one tuple on it, it's just as
	 * expensive to read as a full page.  (Most real hard drives can't efficiently
	 * address regions smaller than a page at a time.)
	 * 
	 * @return The estimated cost of scanning the table.
	 */ 
	public double estimateScanCost() {
		HeapFile temp = (HeapFile) m_file;
		return temp.numPages() * m_ioCostPerPage;
	}

	/** 
	 * This method returns the number of tuples in the relation,
	 * given that a predicate with selectivity selectivityFactor is
	 * applied.
	 *
	 * @param selectivityFactor The selectivity of any predicates over the table
	 * @return The estimated cardinality of the scan with the specified selectivityFactor
	 */
	public int estimateTableCardinality(double selectivityFactor) {
		double summation = 0;
		summation = ((IntHistogram) m_histograms[0]).statsHistogramTotal(); //Number is arbitrary
		return (int) Math.floor(selectivityFactor * summation);
	}

	/**
	 * The average selectivity of the field under op.
	 * @param field
	 *        the index of the field
	 * @param op
	 *        the operator in the predicate
	 * The semantic of the method is that, given the table, and then given a
	 * tuple, of which we do not know the value of the field, return the
	 * expected selectivity. You may estimate this value from the histograms.
	 * */
	public double avgSelectivity(int field, Predicate.Op op) {
		// some code goes here
		return 1.0;
	}

	/**
	 * Estimate the selectivity of predicate <tt>field op constant</tt> on the
	 * table.
	 * 
	 * @param field
	 *            The field over which the predicate ranges
	 * @param op
	 *            The logical operation in the predicate
	 * @param constant
	 *            The value against which the field is compared
	 * @return The estimated selectivity (fraction of tuples that satisfy) the
	 *         predicate
	 */
	public double estimateSelectivity(int field, Predicate.Op op, Field constant) {
		if (constant.getType() == Type.INT_TYPE)
		{
			int val = ((IntField) constant).getValue();
			return ((IntHistogram) m_histograms[field]).estimateSelectivity(op,val);
		}
		if (constant.getType() == Type.STRING_TYPE)
		{
			String val = ((StringField) constant).getValue();
			return ((StringHistogram) m_histograms[field]).estimateSelectivity(op,val);
		}
		return 1.0;
	}

	/**
	 * return the total number of tuples in this table
	 * */
	public int totalTuples() {
		int numT = 0;
		try {
			m_iter.rewind();
			while (m_iter.hasNext())
			{
				m_iter.next();
				numT++;
			}
		} catch (Exception e) {}

		return numT;
	}

	//*************************** HELPER FUNCTIONS *************************** 

	/**
	 * Determine the range (max & min) for each IntField in the table.
	 * Fills m_allMax and m_allMin array's with these values
	 * @return None
	 */
	private void determineHistoRange() throws NoSuchElementException, DbException, TransactionAbortedException {
		m_allMax = new int[m_numFields];
		m_allMin = new int[m_numFields];
		while (m_iter.hasNext()) {
			Tuple tuple = m_iter.next();
			for (int j = 0 ; j < m_numFields ; j++)
			{
				Field current = tuple.getField(j);
				if (current.getType() == Type.INT_TYPE)
				{
					int val = ((IntField) current).getValue();
					if (val < m_allMin[j]) m_allMin[j] = val;
					if (val > m_allMax[j]) m_allMax[j] = val;
				}
			}
		}
	}

	/**
	 * Initialize a histogram for each Field in the table.
	 * Fills m_histogram with either a IntHistogram or a StringHistogram
	 * @return None
	 */
	private void initializeHistograms() throws TransactionAbortedException, DbException {
		m_histograms = new Object[m_numFields];
		for (int i = 0 ; i < m_numFields ; i++ ) 
		{
			if (m_schema.getFieldType(i) == Type.INT_TYPE)
			{
				m_histograms[i] = new IntHistogram(NUM_HIST_BINS, m_allMin[i], m_allMax[i]);
			}
			if (m_schema.getFieldType(i) == Type.STRING_TYPE)
			{
				m_histograms[i] = new StringHistogram(NUM_HIST_BINS);
			}
		}
		m_iter.rewind();
		while (m_iter.hasNext())
		{
			Tuple next = m_iter.next();
			createHistogram(next);
		}
	}

	/**
	 * Creates a histogram for each Field in the table.
	 * Uses m_allMax and m_allMin for the low and high for IntFields
	 * Fills m_histogram[i] for all i with an appropriate histogram
	 * @return None
	 */
	private void createHistogram(Tuple t)
	{
		for (int j = 0 ; j < m_numFields ; j++ )
		{
			Field cur = t.getField(j);
			if (cur.getType() == Type.INT_TYPE)
			{
				int val = ((IntField) cur).getValue();
				((IntHistogram) m_histograms[j]).addValue(val);
			}
			if (cur.getType() == Type.STRING_TYPE)
			{
				String val = ((StringField) cur).getValue();
				((StringHistogram) m_histograms[j]).addValue(val);
			}
		}
	}

	//***************************
}
