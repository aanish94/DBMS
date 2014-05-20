package simpledb;

/** A class to represent a fixed-width histogram over a single integer-based field.
 */
public class IntHistogram {

	private int m_buckets;
	private int m_min;
	private int m_max;
	private int[] m_histogram;
	private int m_range;
	private int m_epsilon;

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
		// some code goes here
		m_buckets = buckets;
		m_min = min;
		m_max = max;
		m_histogram = new int[m_buckets]; //0 by default
		m_range = m_max - m_min;
		
		//if (m_buckets > m_range)
	//	{
		//	m_buckets = m_range;
			//m_epsilon = 1;
		//}		
		m_epsilon = (int) Math.ceil((double)m_range / (double)m_buckets); 
		
		//assert(m_epsilon > 0);
		
		int j = 0;
		for (int i = m_min; i < m_max; i += m_epsilon)
		{
			m_histogram[j++] = 0;
		}
		//assert(j == m_buckets);
	}
	//Determine which bucket to insert val into/read from
	private int findBucket(int temp)
	{
		if (temp < m_min) return -1;
		if (temp > m_max) return -2;
		if ((temp == m_max) && (temp % m_epsilon == 0 )) temp --;
		
		int index =  (temp-m_min)/m_epsilon;
		//assert(index < m_buckets);
		return index;
	}
	/**
	 * Add a value to the set of values that you are keeping a histogram of.
	 * @param v Value to add to the histogram
	 */
	public void addValue(int v) {
		// some code goes here
		//assert(v<= m_max);
		//assert(v>=m_min);
	//	if (v >= m_max || v <= m_min) { return; } //Outside range
		int index = findBucket(v);
		int tempVal = m_histogram[index] + 1;
		m_histogram[index] = tempVal;
	}
	
	public double statsHistogramTotal()
	{
		return valueCount();
	}
	private int valueCount()	
	{
		int sum = 0;
		for (int i = 0 ; i < m_histogram.length; i++)
		{
			sum += m_histogram[i];
		}
		return sum;
	}
	private int bucketCount(int index)
	{
		if (index == -1) return 0;
		if (index == -2) return 0;
		//assert(index < m_histogram.length);
		return m_histogram[index];
	}
	private int checker(int temp)
	{
		if (temp == -1) return valueCount();
		if (temp == -2) return 0;
		
		int sum = 0;
		for (int j = temp + 1; j < m_histogram.length; j++)
		{
			sum+=m_histogram[j];
		}
		return sum;
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
		double answer = 0;
		// some code goes here
		int bucketIndex = findBucket(v);
		double totalValue = valueCount();
		double valinBucket = bucketCount(bucketIndex);
		
		if (op == Predicate.Op.EQUALS)
		{
			answer = valinBucket/totalValue;
		}
		else if (op == Predicate.Op.GREATER_THAN)
		{
			/*double answer = 0;
			for (int i = v + 1; i < m_histogram.length ; i++ )
			{
				answer += m_histogram[i];
			}*/
			answer = checker(bucketIndex) / totalValue;
		}
		else if (op == Predicate.Op.GREATER_THAN_OR_EQ)
		{
			answer = checker(bucketIndex) + valinBucket;
			/*for (int i = v; i < m_histogram.length ; i++ )
			{
				answer += m_histogram[i];
			}*/
			answer = answer/totalValue;
		}
		else if (op == Predicate.Op.LESS_THAN)
		{
			/*for (int i = v - 1; i > 0 ; i-- )
			{
				answer += m_histogram[i];
			}*/
			answer = totalValue - checker(bucketIndex) - valinBucket;
			answer = answer / totalValue;
		}
		else if (op == Predicate.Op.LESS_THAN_OR_EQ)
		{
			
			/*for (int i = v; i > 0 ; i-- )
			{
				answer += m_histogram[i];
			}*/
			 answer = totalValue - checker(bucketIndex);
			answer = answer / totalValue;
		}
		else if (op == Predicate.Op.LIKE)
		{
			return 0;
		}
		else if (op == Predicate.Op.NOT_EQUALS)
		{
			answer = (totalValue - valinBucket) / totalValue;
		}
		//assert(answer <=1);
		return answer;
	}

		
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
}
