package simpledb;

/** A class to represent a fixed-width histogram over a single integer-based field.
 */
public class IntHistogram {

	private int[] m_histogram;
	private int m_buckets; //Number of buckets
	private int m_min;
	private int m_max;
<<<<<<< HEAD
	private int m_epsilon; //Number of "items" per bucket
=======
	private int[] m_histogram;
	private int m_epsilon;
	private boolean OVER;
	private int OUT_OF_BOUNDS;
>>>>>>> temp

	private boolean OVER; //true => OVER number of buckets
	private int OUT_OF_BOUNDS; // UNDER/OVER number of buckets
	/**
	 * Create a new IntHistogram.
	 * 
	 * This IntHistogram should maintain a histogram of integer values that it receives.
	 * It should split the histogram into "buckets" buckets.
	 * 
	 * The values that are being histogrammed will be provided one-at-a-time through the "addValue()" function.
	 * 
	 * Your implementation should use space and have execution time that are both
	 * constant with respect to the number of values being histogrammed.  For example, you shouldn't 
	 * simply store every value that you see in a sorted list.
	 * 
	 * @param buckets The number of buckets to split the input value into.
	 * @param min The minimum integer value that will ever be passed to this class for histogramming
	 * @param max The maximum integer value that will ever be passed to this class for histogramming
	 */
	public IntHistogram(int buckets, int min, int max) {
		m_buckets = buckets;
		m_min = min;
		m_max = max;
<<<<<<< HEAD
		m_histogram = new int[m_buckets]; //Filled with 0 by default

		OVER = false;

		int range = m_max - m_min;	
		m_epsilon = (int) Math.ceil((double)range / (double)m_buckets); 
	}
=======
		m_histogram = new int[m_buckets]; //0 by default
		m_epsilon = (int) Math.ceil((double)(m_max - m_min) / (double)m_buckets); 
		OVER = false;
	}	
>>>>>>> temp

	/**
	 * Add a value to the set of values that you are keeping a histogram of.
	 * @param v Value to add to the histogram
	 */
	public void addValue(int v) {
<<<<<<< HEAD
		int index = identifyBucket(v);
=======
		int index = findBucket(v);
>>>>>>> temp
		m_histogram[index] += 1;
	}

	/**
	 * Estimate the selectivity of a particular predicate and operand on this table.
	 * 
	 * For example, if "op" is "GREATER_THAN" and "v" is 5, 
	 * return your estimate of the fraction of elements that are greater than 5.
	 * 
	 * @param op Operator
	 * @param v Value
	 * @return Predicted selectivity of this particular operator and value
	 */
	public double estimateSelectivity(Predicate.Op op, int v) {
<<<<<<< HEAD

		int bucketIndex = identifyBucket(v);
		double totalValue = histogramTotal();
		double valinBucket = valOfBucket(bucketIndex);

		if (op == Predicate.Op.EQUALS)
		{
			return valinBucket/totalValue;
		}
		else if (op == Predicate.Op.GREATER_THAN)
		{
			return allGreaterValues(bucketIndex) / totalValue;
		}
		else if (op == Predicate.Op.GREATER_THAN_OR_EQ)
		{
			double answer = allGreaterValues(bucketIndex) + valinBucket;
			return answer/totalValue;
		}
		else if (op == Predicate.Op.LESS_THAN)
		{
			double answer = totalValue - allGreaterValues(bucketIndex) - valinBucket;
			return answer / totalValue;
		}
		else if (op == Predicate.Op.LESS_THAN_OR_EQ)
		{
			double answer = totalValue - allGreaterValues(bucketIndex);
			return answer / totalValue;
		}
		else if (op == Predicate.Op.NOT_EQUALS)
		{
			return (totalValue - valinBucket) / totalValue;
		}
		return 0;
	}


=======
		int bucketIndex = findBucket(v);
		double bucketValue = bucketCount(bucketIndex);

		double hist_total = histogramTotalSummation();

		if (op == Predicate.Op.EQUALS)
		{
			return bucketValue / hist_total;
		}
		else if (op == Predicate.Op.GREATER_THAN)
		{
			return greaterSummation(bucketIndex) / hist_total;
		}
		else if (op == Predicate.Op.GREATER_THAN_OR_EQ)
		{
			double ans = greaterSummation(bucketIndex) + bucketValue;
			return ans / hist_total;
		}
		else if (op == Predicate.Op.LESS_THAN)
		{
			double ans = hist_total - greaterSummation(bucketIndex) - bucketValue;
			return ans / hist_total;
		}
		else if (op == Predicate.Op.LESS_THAN_OR_EQ)
		{	
			double ans = hist_total - greaterSummation(bucketIndex);
			return ans / hist_total;
		}
		else if (op == Predicate.Op.NOT_EQUALS)
		{
			return (hist_total - bucketValue) / hist_total;
		}
		return -1;
	}

>>>>>>> temp
	//************************NOT IMPLEMENTED FOR NOW************************
	/**
	 * @return
	 *     the average selectivity of this histogram.
	 *     
	 *     This is not an indispensable method to implement the basic
	 *     join optimization. It may be needed if you want to
	 *     implement a more efficient optimization
	 * */
	public double avgSelectivity()
	{
		// some code goes here
		return 1.0;
	}

	/**
	 * @return A string describing this histogram, for debugging purposes
	 */
	public String toString() {

		// some code goes here
		return null;
	}

<<<<<<< HEAD
	//******************HELPER FUNCTIONS******************
	/**
	 * Determine which bucket corresponds to value
	 * @param v Value to find in to the histogram
	 * @return bucket corresponding to v
	 */
	private int identifyBucket(int value)
	{
		if (value < m_min) { OVER = false; return OUT_OF_BOUNDS; }
		else if (value > m_max) { OVER = true; return OUT_OF_BOUNDS; }
		else if (value == m_max) value --;

		return (value - m_min) / m_epsilon;
	}
	
	/**
	 * Sums up all the values in the histogram
	 * @return total Sum of all values
	 */
	private int histogramTotal()	
	{
		int total = 0;
		for (int i = 0 ; i < m_histogram.length; i++)
		{
			total += m_histogram[i];
		}
		return total;
	}
	/**
	 * GETTER used in TableStats.java
	 * @return total Sum of all values in histogram
	 */
	public int statsHistogramTotal()
	{
		return histogramTotal();
	}
	/**
	 * Determines value of specific bucket
	 * @param index of bucket
	 * @return bucket's value
	 */
	private int valOfBucket(int index)
=======
	//*************************** HELPER FUNCTIONS *************************** 

	/**
	 * Determines the bucket where value can be found
	 * @param value
	 * @return The bucket where value is found
	 */
	private int findBucket(int value)
	{
		if (value < m_min) return OUT_OF_BOUNDS;
		if (value > m_max) { OVER = true; return OUT_OF_BOUNDS; }
		if (value == m_max) value--;

		return (value - m_min) / m_epsilon;
	}
	/**
	 * Public getter for histogramTotalSummation; Used in TableStats
	 * @return Total summation of values in histogram.
	 */
	public double statsHistogramTotal()
	{
		return histogramTotalSummation();
	}
	
	/**
	 * @return Sum of all values in the histogram
	 */
	private int histogramTotalSummation()	
	{
		int summation = 0;
		for (int i = 0 ; i < m_histogram.length; i++)
		{
			summation += m_histogram[i];
		}
		return summation;
	}
	
	/**
	 * Gets the value of the bucket associated with index
	 * @param index of bucket
	 * @return an integer value
	 */
	private int bucketCount(int index)
>>>>>>> temp
	{
		if (index == OUT_OF_BOUNDS) return 0;
		return m_histogram[index];
	}
<<<<<<< HEAD

	/**
	 * Determines the total value of all buckets larger than current
	 * @param index Bucket of histogram
	 * @return sum of all buckets to the right of index
	 */
	private int allGreaterValues(int index)
	{
		if (index == OUT_OF_BOUNDS) 
		{
			if (!OVER) return histogramTotal();
			else return 0;
		}

		int total = 0;
		for (int j = index + 1; j < m_histogram.length; j++)
		{
			total+=m_histogram[j];
		}
		return total;
	}
	//****************************************************************
=======
	
	/**
	 * Determines the sum of all buckets to the right of the bucket corresponding to index
	 * @param index of bucket
	 * @return an integer summation 
	 */
	private int greaterSummation(int index)
	{
		if (index == OUT_OF_BOUNDS)
		{
			if (!OVER) return histogramTotalSummation();
			return 0;
		}
		int sum = 0;
		for (int j = index + 1; j < m_histogram.length; j++)
		{
			sum += m_histogram[j];
		}
		return sum;
	}
>>>>>>> temp
}
