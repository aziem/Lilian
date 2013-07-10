package org.lilian.graphs.compression;

import static org.lilian.util.Functions.log2;
import static org.lilian.util.Series.series;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.math.random.BitsStreamGenerator;
import org.lilian.graphs.DGraph;
import org.lilian.graphs.DLink;
import org.lilian.graphs.UGraph;
import org.lilian.graphs.ULink;
import org.lilian.graphs.draw.Draw;
import org.lilian.util.BitString;
import org.lilian.util.Series;

public class Functions
{
	
	/**
	 * The cost of storing the given value in prefix coding
	 * @param bits
	 * @return
	 */
	public static double prefix(int value)
	{

		return prefix(value, 10);
	}
	
	public static int prefix(int value, int d)
	{
		if(value < 0)
			System.out.println(value + " " + d);
		
		if(d == 0)
			return 2 * length(value) + 1;
		
		return length(value) + length(prefix(length(value), d - 1));
	}
	
	/**
	 * The length of the given value in the canonical bitstring representation 
	 * of integers. (0 = "", 1="0", 2="1", 3="00", etc).
	 * 
	 * @param in
	 * @return
	 */
	public static int length(int in)
	{
		if(in == 0)
			return 0;
		
		return (int)Math.ceil(log2(in + 1));
	}	
	
	public static <L> BitString toBits(UGraph<L> graph)
	{
		return toBits(graph, Series.series(graph.size()));
	}
	
	public static <L> BitString toBits(UGraph<L> graph, List<Integer> order)
	{	
		int n = graph.size();
		BitString string = BitString.zeros( (n * n + n) / 2 );
		
		for(ULink<L> link : graph.links())
		{
			int i = order.get(link.first().index());
			int j = order.get(link.second().index());
			
			if(j > i)
			{
				int t = j;
				j = i;
				i = t;
			}
			
			int rowStart = (i * (i + 1)) / 2;
			int index = rowStart + j;
			
			string.set(index, true);
		}
		
		return string;
	}	
	
	public static <L> BitString toBits(DGraph<L> graph)
	{
		return toBits(graph, Series.series(graph.size()));
	}
	
	public static <L> BitString toBits(DGraph<L> graph, List<Integer> order)
	{	
		int n = graph.size();
		BitString string = BitString.zeros(n * n);
		
		for(DLink<L> link : graph.links())
		{
			int i = order.get(link.first().index());
			int j = order.get(link.second().index());
			
			int rowStart = i * n;
			int index = rowStart + j;
			try	{
				string.set(index, true);
			} catch(ArrayIndexOutOfBoundsException e)
			{
				System.out.println(n + " " + i + " " + j);
				throw e;
			}
		}
		
		return string;
	}
	
	public static <L> void toBits(OutputStream stream, UGraph<L> graph)
			throws IOException	
	{
		toBits(stream, graph, Series.series(graph.size()));
	}
	
	public static <L> void toBits(OutputStream stream, UGraph<L> graph, List<Integer> order)
			throws IOException
	{	
		int BUFFER_SIZE = 65536;
		
		List<Integer> inv = Draw.inverse(order);
		
		BitString buffer = new BitString(BUFFER_SIZE);
		
		int n = graph.size();
		for(int i : series(n))
			for(int j : series(i+1, n))
			{
				int ii = inv.get(i),
				    jj = inv.get(j);
				
				boolean bit = graph.nodes().get(ii).connected(graph.nodes().get(jj));
				buffer.add(bit);
				
				if(buffer.size() == BUFFER_SIZE)
				{
					stream.write(buffer.rawData());
					buffer.clear();
				}
			}
		stream.flush();
	}	
	
	public static <L> void toBits(OutputStream stream, DGraph<L> graph)
			throws IOException
	{
		toBits(stream, graph, Series.series(graph.size()));
	}
	
	public static <L> void toBits(OutputStream stream, DGraph<L> graph, List<Integer> order)
			throws IOException
	{	
		int BUFFER_SIZE = 65536;
		
		List<Integer> inv = Draw.inverse(order);
		
		BitString buffer = new BitString(BUFFER_SIZE);
		
		int n = graph.size();
		for(int i : series(n))
			for(int j : series(n))
			{
				int ii = inv.get(i),
				    jj = inv.get(j);
				
				boolean bit = graph.nodes().get(ii).connected(graph.nodes().get(jj));
				buffer.add(bit);
				
				if(buffer.size() == BUFFER_SIZE)
				{
					stream.write(buffer.rawData());
					buffer.clear();
				}
			}
		
		stream.flush();
	}	
}
