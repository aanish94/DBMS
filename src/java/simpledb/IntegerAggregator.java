package simpledb;

import java.util.ArrayList;

/**
 * Knows how to compute some aggregate over a set of IntFields.
 */
public class IntegerAggregator implements Aggregator {

	private static final long serialVersionUID = 1L;

	private int m_gbfield;
	private Type m_gbfieldtype;
	private int m_afield;
	private Op m_op;
	private TupleDesc m_td;
	private ArrayList<Tuple> tupleGroup;

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

		if (m_gbfield == Aggregator.NO_GROUPING)
		{
			Type [] temp = new Type[1];
			temp[0] = Type.INT_TYPE;
			m_td = new TupleDesc(temp);
		}
		else
		{
			Type [] temp = new Type[2];
			temp[0] = m_gbfieldtype;
			temp[1] = Type.INT_TYPE;
			m_td = new TupleDesc(temp);
		}
		tupleGroup = new ArrayList<Tuple>();
	}
	
	private void firstTupleAdded(Tuple Tup, IntField Val, IntField cmpField,int index)
	{
		//helps
		Tuple curTup = Tup;
		IntField newValue = Val;
		
		int curVal = newValue.getValue();
		
		boolean flag = false;
		int cmpVal=0;
		if (cmpField != null) { cmpVal = cmpField.getValue(); flag = true;}
		
		if (m_op == Op.MIN)
		{
			if (flag) 
			{
				if (cmpVal > curVal)
				{
					curTup.setField(index, newValue);
				}
			}
			else { curTup.setField(index, newValue); }
		}
		else if (m_op == Op.MAX)
		{
			if (flag)
			{
				if (cmpVal < curVal)
				{
					curTup.setField(index, newValue);
				}
			}
			else { curTup.setField(index, newValue); }
		}
		else if (m_op == Op.SUM)
		{
			if (flag)
			{
				curTup.setField(0, new IntField(cmpVal + curVal));
			}
			{ curTup.setField(index, newValue); }
		}
		else if (m_op == Op.COUNT)
		{
			if (flag)
			{
				curTup.setField(0, new IntField(cmpVal+1));
			}
			{ curTup.setField(index, new IntField(1)); }
		}
		else if (m_op == Op.AVG)
		{
			//MORE COMPLICATED THAN OTHERS. WILL IMPLEMENT LATER
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
				firstTupleAdded(curTup,newValue,null,0);
				tupleGroup.add(curTup);
			}
			else
			{
				//There are already other tuples inserted.
				Tuple first = tupleGroup.get(0);
				IntField compareField = (IntField) first.getField(0);
				//int compareVal = compareField.getValue();
				IntField newValue = new IntField(curVal);
				firstTupleAdded(first, newValue, compareField, 0);
				/*if (m_op == Op.MIN)
				{
					if (compareVal > curVal)
					{
						first.setField(0, newValue);
					}
				}
				else if (m_op == Op.MAX)
				{
					if (compareVal < curVal)
					{
						first.setField(0, newValue);
					}
				}
				else if (m_op == Op.SUM)
				{
					first.setField(0, new IntField(compareVal + curVal));
				}
				else if (m_op == Op.COUNT)
				{
					first.setField(0, new IntField(compareVal+1));
				}	
				else if (m_op == Op.AVG)
				{
					//MORE COMPLICATED THAN OTHERS. WILL IMPLEMENT LATER
				}*/
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
				}
			}
			//First Tuple to group
			if (current == null)
			{
				current = new Tuple(m_td);
				IntField newValue = new IntField(curVal);
				current.setField(0, tupGroup);
				firstTupleAdded(current,newValue,null,1);
				tupleGroup.add(current);
			}
			//There are already grouped tuples
			else
			{
				IntField compareField = (IntField) current.getField(1);
				int compareVal = compareField.getValue();
				IntField newValue = new IntField(curVal);
				if (m_op == Op.MIN)
				{
					if (compareVal > curVal)
					{
						current.setField(1, newValue);
					}
				}
				else if (m_op == Op.MAX)
				{
					if (compareVal < curVal)
					{
						current.setField(1, newValue);
					}
				}
				else if (m_op == Op.SUM)
				{
					current.setField(1, new IntField(compareVal + curVal));
				}
				else if (m_op == Op.COUNT)
				{
					current.setField(1, new IntField(compareVal+1));
				}
				else if (m_op == Op.AVG)
				{
					//MORE COMPLICATED THAN OTHERS. WILL IMPLEMENT LATER
				}
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
