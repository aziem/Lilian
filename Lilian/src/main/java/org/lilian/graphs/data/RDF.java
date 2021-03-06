package org.lilian.graphs.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.lilian.Global;
import org.lilian.graphs.DTGraph;
import org.lilian.graphs.DTLink;
import org.lilian.graphs.DTNode;
import org.lilian.graphs.MapDTGraph;
import org.lilian.graphs.Node;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;

import weka.core.Debug;

public class RDF
{
	/**
	 * Reads the given file into a graph.
	 * 
	 * @param file
	 * @return
	 */
	public static MapDTGraph<String, String> read(File file)
	{
		return read(file, null);
	}
	
	public static MapDTGraph<String, String> read(File file, List<String> linkWhitelist)
	{
		RDFDataSet testSet = new RDFFileDataSet(file, RDFFormat.RDFXML);

		List<Statement> triples = testSet.getFullGraph();	
		
		return createDirectedGraph(triples, null, linkWhitelist);
	}
	
	public static MapDTGraph<String, String> readTurtle(File file)
	{
		return readTurtle(file, null);
	}
	
	public static MapDTGraph<String, String> readTurtle(File file, List<String> linkWhitelist)
	{
		RDFDataSet testSet = new RDFFileDataSet(file, RDFFormat.TURTLE);

		List<Statement> triples = testSet.getFullGraph();	
		
		return createDirectedGraph(triples, null, linkWhitelist);
	}
	
	public static MapDTGraph<String, String> createDirectedGraph(
			List<Statement> sesameGraph, 
			List<String> vWhiteList,
			List<String> eWhiteList)
	{
		List<Pattern> vertexWhiteList = null;
		
		if(vWhiteList != null) 
		{
			vertexWhiteList = new ArrayList<Pattern>(vWhiteList.size());
			for(String patternString : vWhiteList)
				vertexWhiteList.add(Pattern.compile(patternString));
		}
		
		
		List<Pattern> edgeWhiteList = null;
		if(eWhiteList != null)
		{
			edgeWhiteList = new ArrayList<Pattern>(eWhiteList.size());
			for(String patternString : eWhiteList)
				edgeWhiteList.add(Pattern.compile(patternString));
		}
		
		MapDTGraph<String, String> graph = new MapDTGraph<String, String>();
		DTNode<String, String> node1, node2;
		
		Global.log().info("Constructing graph");
		
		for (Statement statement : sesameGraph) 
		{
			
			if(vWhiteList != null)
			{
				if(! matches(statement.getObject().toString(), vertexWhiteList))
					continue;
				if(! matches(statement.getSubject().toString(), vertexWhiteList))
					continue;
			}
			
			if(eWhiteList != null)
			{
				if(! matches(statement.getPredicate().toString(), edgeWhiteList))
				{
// 					Global.log().info("Filtered predicate: " + statement.getPredicate().toString());
					continue;
				}
			}
			
			String subject = statement.getSubject().toString(), 
			       object = statement.getObject().toString(), 
			       predicate = statement.getPredicate().toString();
						
			node1 = graph.node(subject);
			node2 = graph.node(object);
		
			if (node1 == null) 
				node1 = graph.add(subject);
	
			
			if (node2 == null) 
				node2 = graph.add(object);
							
			node1.connect(node2, predicate);
		}	
		
		return graph;
	}
	
	/** TODO move this to a proper Utility class/package
	 * Returns true if the String matches one or more of the patterns in the list.
	 * @param string
	 * @param patterns
	 * @return
	 */
	public static boolean matches(String string, List<Pattern> patterns)
	{
		for(Pattern pattern : patterns)
			if(pattern.matcher(string).matches())
				return true;
		return false;
	}
	
	/**
	 * Simplifies an RDF URI to retain most of its information
	 * @param string
	 * @return
	 */
	public static String simplify(String string)
	{
		if(string == null)
			return null;
		
		if(! string.contains("/"))
			return string;
		
		String[] split = string.split("/");
		
		return split[split.length - 1]; 
	}
	
	public static DTGraph<String, String> simplify(DTGraph<String, String> graph)
	{
		DTGraph<String, String> out = new MapDTGraph<String, String>();
		
		for(Node<String> node : graph.nodes())
			out.add(simplify(node.label()));
		
		for(DTLink<String, String> link : graph.links())
			out.get(link.first().index()).connect(out.get(link.second().index()), simplify(link.tag()));
		
		return out;
	}
}
