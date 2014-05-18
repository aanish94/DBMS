package simpledb;

import java.util.ArrayList;

/**
 * Knows how to compute some aggregate over a set of IntFields.
 */
public class IntegerAggregator implements Aggregator {

	private static final long serialVersionUID = 1L;
	//Group field, type
	private int m_gbfield;
	private Type m_gbfieldtype;
	//Aggregate field
	private int m_afield;
	//Op
	private Op m_op;
	private TupleDesc m_td;
	private ArrayList<Tuple> tupleGroup;
	//NEEDED FOR THE AVERAGE CALCULATION
	ArrayList<Integer> count;
	ArrayList<Integer> sum;
	int avgIndex;
	/**
	 * Aggregate constructor
	 * 
	 * @param gbfield
	 *            the 0-based index of the group-by field in the tuple, or
	 *            NO_GROUPING if there is no grouping
	 * @param gbfieldtype
	 *            the type of the group by field (e.g., Type.INT_TYPE), or null
	 *            if there is no grouping
	 * @param afield
	 *            the 0-based index of the aggregate field in the tuple
	 * @param what
	 *            the aggregation operator
	 */

	public IntegerAggregator(int gbfield, Type gbfieldtype, int afield, Op what) 
	{
		m_gbfield = gbfield;
		m_gbfieldtype = gbfieldtype;
		m_afield = afield;
		m_op = what;

		//If no grouping => tuple is of form ( aggregate Value ) 
		if (m_gbfield == Aggregator.NO_GROUPING)
		{
			Type [] temp = new Type[1];
			temp[0] = Type.INT_TYPE;
			m_td = new TupleDesc(temp);
		}
		//If grouping => tuple is of form ( groupValue, aggregate Value ) 
		else
		{
			Type [] temp = new Type[2];
			temp[0] = m_gbfieldtype;
			temp[1] = Type.INT_TYPE;
			m_td = new TupleDesc(temp);
		}
		
		tupleGroup = new ArrayList<Tuple>();
		count = new ArrayList<Integer>();
		sum = new ArrayList<Integer>();
	}
	//================HELPER FUNCTION ==> MERGETUPLEINTOGROUP ================
	//PARAMETERS: Tuple Tup => the tuple currently considered
	//Two IntField's corresponding to the aggregate field of the tuple and the comparison field
	//Index => if no grouping, tupleDesc has one field and index is 0; if grouping, tupleDesc has two fields and index is 1
	//first indicates whether or not tuple is first being considered ( relevant to average calculation ) 
	private void tupleAggregator(Tuple Tup, IntField Val, IntField cmpField,int index, boolean first)
	{
		//HELPER FUNCTION
		Tuple curTup = Tup;
		IntField newValue = Val;

		int curVal = newValue.getValue();
		//flag is set true if a valid comparison field is supplied
		boolean flag = false;
		int cmpVal = 0;
		if (cmpField != null) { cmpVal = cmpField.getValue(); flag = true;}

		//Relevant only for Average Determination
		
		
		if (m_op == Op.MIN)
		{
			//MINIMUM DETERMINATION
			if (flag)  
			{
				if (cmpVal > curVal)
				{
					//The current value is smaller and replaces comparison value
					curTup.setField(index, newValue);
				}
			}
			//By default, the first ( & only ) value is the minimum
			else { curTup.setField(index, newValue); }
		}
		else if (m_op == Op.MAX)
		{
			//MAXIMUM DETERMINATION
			if (flag)
			{
				if (cmpVal < curVal)
				{
					//The current value is larger and replaces the comparison value
					curTup.setField(index, newValue);
				}
			}
			//By default, the first ( & only ) value is the maximum 
			else { curTup.setField(index, newValue); }
		}
		else if (m_op == Op.SUM)
		{
			//SUM DETERMINATION
			if (flag)
			{
				//Replace the cmp value with the sum of current and comparison
				curTup.setField(index, new IntField(cmpVal + curVal));
			}
			else
			{ curTup.setField(index, newValue); }
		}
		else if (m_op == Op.COUNT)
		{
			//COUNT DETERMINATION
			if (flag)
			{
				//Increment count by 1
				curTup.setField(index, new IntField(cmpVal+1));
			}
			else
			//Initialize count
			{ curTup.setField(index, new IntField(1)); }
		}
		else if (m_op == Op.AVG)
		{
			//AVERAGE DETERMINATION	
			if (flag) //There is a comparison value
			{
				if (first) //This is the first value
				{
					avgIndex = 0;
					count.add(1);
					sum.add(curVal);
					curTup.setField(index, new IntField(curVal));
				}
				else
				{
					int total = sum.get(avgIndex) + curVal;
					int counter = count.get(avgIndex) + 1;
					count.set(avgIndex,counter);
					sum.set(avgIndex,total);
					curTup.setField(index, new IntField(total/counter));
				}
			}
			else
			{
				if (first)
				{
					avgIndex = 0;
					count.add(1);
					sum.add(curVal);
					curTup.setField(index, new IntField(curVal));
				}
				else 
				{
					int total = sum.get(avgIndex) + curVal;
					int counter = count.get(avgIndex) + 1;
					count.set(avgIndex,counter);
					sum.set(avgIndex,total);
					curTup.setField(index, new IntField(total/counter));
				}
			}
		}
	}
	
	/**
	 * Merge a new tuple into the aggregate, grouping as indicated in the
	 * constructor
	 * 
	 * @param tup
	 *            the Tuple containing an aggregate field and a group-by field
	 */
	public void mergeTupleIntoGroup(Tuple tup) {
		//Field and Value we are considering
		IntField curField = (IntField) tup.getField(m_afield);
		int curVal = curField.getValue();
		//List of Grouped Tuples

		if (m_gbfield == Aggregator.NO_GROUPING)
		{
			//NO GROUPING
			if (tupleGroup.size() == 0)
			{
				//FIRST TUPLE
				Tuple curTup = new Tuple(m_td);
				IntField newValue = new IntField(curVal);
				tupleAggregator(curTup,newValue,null,0,true);
				tupleGroup.add(curTup);
			}
			else
			{
				//There are already other tuples inserted.
				Tuple first = tupleGroup.get(0);
				IntField compareField = (IntField) first.getField(0);
				IntField newValue = new IntField(curVal);
				tupleAggregator(first, newValue, compareField, 0,false);
			}
		}
		else
		{
			//GROUPING
			Field tupGroup = tup.getField(m_gbfield);
			Tuple current = null;
			//Find tuple to Group
			for (int i = 0; i < tupleGroup.size(); i++)
			{
				Tuple temp = tupleGroup.get(i);
				Field tempField = temp.getField(0);
				if (tempField.equals(tupGroup))
				{
					current = temp;
					avgIndex = i;
					
				}
			}
			//First Tuple to group
			if (current == null)
			{
				current = new Tuple(m_td);
				IntField newValue = new IntField(curVal);
				current.setField(0, tupGroup);
				tupleAggregator(current,newValue,null,1,true);
				tupleGroup.add(current);
			}
			//There are already grouped tuples
			else
			{
				IntField compareField = (IntField) current.getField(1);
				IntField newValue = new IntField(curVal);
				tupleAggregator(current, newValue, compareField, 1,false);
			}
		}
	}
	/**
	 * Create a DbIterator over group aggregate results.
	 * 
	 * @return a DbIterator whose tuples are the pair (groupVal, aggregateVal)
	 *         if using group, or a single (aggregateVal) if no grouping. The
	 *         aggregateVal is determined by the type of aggregate specified in
	 *         the constructor.
	 */
	public DbIterator iterator() {
		return new TupleIterator(m_td,tupleGroup);
	}

}
