package org.lilian.graphs.random;

import static java.lang.Math.log;
import static org.lilian.graphs.compression.Functions.toPairUndirected;
import static org.lilian.util.Series.series;

import java.util.ArrayList;
import java.util.List;

import org.lilian.Global;
import org.lilian.data.real.Generator;
import org.lilian.graphs.DTGraph;
import org.lilian.graphs.Graph;
import org.lilian.graphs.MapDTGraph;
import org.lilian.graphs.MapUTGraph;
import org.lilian.graphs.UTGraph;
import org.lilian.graphs.UTNode;
import org.lilian.util.Functions;
import org.lilian.util.Pair;


/**
 * 
 * TODO: This required an UndirectedGraph implementation.
 * @author Peter
 *
 */
public class RandomGraphs
{
	public static final int BA_INITIAL = 3; 
	
	public static UTGraph<String, String> preferentialAttachment(int nodes, int toAttach)
	{
		BAGenerator bag = new BAGenerator(BA_INITIAL, toAttach);
		bag.iterate((nodes - BA_INITIAL));
		
		return bag.graph();
	}
	
	public static DTGraph<String, String> preferentialAttachmentDirected(int nodes, int toAttach)
	{
		DBAGenerator bag = new DBAGenerator(BA_INITIAL, toAttach);
		bag.iterate((nodes - BA_INITIAL));
		
		return bag.graph();
	}
	
	public static UTGraph<String, String> random(int n, double prob)
	{
		MapUTGraph<String, String> graph = new MapUTGraph<String, String>();
		List<UTNode<String, String>> nodes = new ArrayList<UTNode<String, String>>(n);

		for(int i : series(n))
			nodes.add(graph.add("x"));
		
		for(int i : series(n))
			for(int j : series(i+1, n))
				if(Global.random.nextDouble() < prob)
					nodes.get(i).connect(nodes.get(j));
		
		return graph;
	}

	/**
	 * Makes a uniform random selection from the set of all graphs with exactly
	 * n nodes and m links. Nodes will not connect to themselves
	 * 
	 * @param n
	 * @param m
	 * @return
	 */
	public static UTGraph<String, String> random(int n, int m)
	{		
		UTGraph<String, String> graph = new MapUTGraph<String, String>();
		for(int i : series(n))
			graph.add("x");
		
		List<Integer> indices = Functions.sample(m, (n*n - n)/2);
		
		for(int index : indices)
		{
			Pair<Integer, Integer> ij = toPairUndirected(index, false);
			graph.nodes().get(ij.first()).connect(graph.nodes().get(ij.second()));
		}
		
		return graph;
	}
	
	/**
	 * No self-loops
	 * @param n
	 * @param prob
	 * @return
	 */
	public static DTGraph<String, String> randomDirected(int n, double prob)
	{
		MapDTGraph<String, String> graph = new MapDTGraph<String, String>();

		for(int i : series(n))
			graph.add("x");
		
		for(int i : series(n))
			for(int j : series(n))
				if(i != j && Global.random.nextDouble() < prob)
					graph.get(i).connect(graph.get(j));
		
		return graph;
	}
	
	public static UTGraph<String, String> fractal(
			int depth, int offspring, int interLinks, double hubProb)
	{
		FractalGenerator gen = new FractalGenerator(offspring, interLinks, hubProb);
		
		for(int i : series(depth))
			gen.iterate();
		
		return gen.graph();
	}
}
