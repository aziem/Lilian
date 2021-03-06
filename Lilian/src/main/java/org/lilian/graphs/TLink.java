package org.lilian.graphs;

public interface TLink<L, T> extends Link<L>
{
	public T tag();
	
	public TGraph<L, T> graph();

}
