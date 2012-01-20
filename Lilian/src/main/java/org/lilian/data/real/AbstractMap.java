package org.lilian.data.real;

import java.util.*;

public abstract class AbstractMap implements Map 
{

	private static final long serialVersionUID = -6916542676143180479L;
	
	public List<Point> map(List<Point> in)
	{
		List<Point> out = new ArrayList<Point>(in.size());
		for(Point p : in)
			out.add(map(p));
		
		return out;
	}
	
	public Map compose(Map other)
	{
		return new CompositeMap(this, other);
	}
}
