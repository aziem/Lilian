package org.lilian.adios;

import java.util.*;
import java.io.*;

import org.lilian.*;
import org.lilian.corpora.*;
import org.lilian.corpora.wrappers.StripSentences;
import org.lilian.grammars.*;
import org.lilian.models.*;
import org.lilian.util.*;


/**
 * A collection of experiments based on the adios classes
 * 
 * TODO:
 * - This way of standardizing experiments doesn't work. The following should be implemented:
 *   An interface called experiment. A method to run the experiment. An interface results, which 
 *   decribes a set of results for one experiment as a list of numbers, and a list of string as 
 *   labels for the numbers. Repeating an experiment N times is simply a matter of calling run N 
 *   times.
 *    
 * @author Peter
 *
 */
public class MexFunctions
{
	
	// holds a collection of precisions found for each height
	public static Map<Integer, Vector<Double>> heightToPrecision = 
		new LinkedHashMap<Integer, Vector<Double>>();
	
	// holds a collection of amounts of symbols found for each height. If a 
	// generated grammar has n symbols at height 4, the vector for key 4 in
	// this map will contain n (as well as other values for other grammars) 
	public static Map<Integer, Vector<Integer>> heightToNumber = 
		new LinkedHashMap<Integer, Vector<Integer>>();

//	public static Pair<Double, Double> precisionRecall(
//			SequenceCorpus<String> trainCorpus,
//			Grammar<String> g0,
//			int maxDepth,
//			int trainSentences, 
//			int testSentences,
//			double drop,
//			double sig,
//			double over,
//			int win,
//			boolean context,
//			boolean verbose)
//			throws IOException									
//	{
//		Set<Collection<String>> trainSet = new HashSet<Collection<String>>();
//		Collection<String> sentence = new Vector<String>();
//
//		// create a collection of sentences to train on, maintain a set of all 
//		// distinct sentences generated
//		SequenceIterator<String> si = trainCorpus.iterator();
//		while(si.hasNext())
//		{
//			sentence.add(si.next());
//			if(si.atSequenceEnd())
//			{
//				trainSet.add(sentence);
//				sentence = new Vector<String>();
//			}
//		}
//
//		Vector<Collection<String>> testColl = new Vector<Collection<String>>(trainSentences);
//
//		// create a collection of sentences to test with
//		// make sure none of them were in the training set
//		while(testColl.size() < testSentences)
//		{
//			sentence = g0.generateSentence("S", 3, maxDepth);
//			if(! trainSet.contains(sentence))
//				testColl.add(sentence);
//		}
//
//		Corpus<String> teacherTest = new CollectionCorpus<String>(testColl);
//		Adios<String> model = new Adios<String>(trainCorpus);
//
//		model.writeResults(new File("D:\\Docs\\Voynich\\Lilian\\files\\results\\"), "model_basic");		
//
//		if(verbose) System.out.println("Starting pattern distillation");
//		// model.patternDistillation(drop, sig, context);
//
//		if(verbose) System.out.println("Starting pattern first generalization");
//		//	model.generalization(drop, sig, context, win);
//
//		if(verbose) System.out.println("Start further generalization");
//		model.generalizationBootstrap(drop, sig, over, context, win, 400);			
//
//		if(verbose) System.out.println();
//		if(verbose) System.out.println(model.ptokens.size() + " equivalence classes found.");
//
//		Grammar<String> g = model.toGrammar();
//
//		g.write(new File("D:\\Docs\\Voynich\\Lilian\\files\\results\\"), "g");
//		g0.write(new File("D:\\Docs\\Voynich\\Lilian\\files\\results\\"), "g0");
//		model.writeResults(new File("D:\\Docs\\Voynich\\Lilian\\files\\results\\"), "model");
//
//		Corpus<String> learnerTest =
//			new StripSentences<String>(
//					new GrammarCorpus<String>(g, "S", testSentences, 3, 0, 42));
//
//		if(verbose) System.out.println("precision:");		
//		double precision = proportionAccepted(g0, learnerTest, verbose);
//		
//		if(verbose) System.out.println("recall:");		
//		double recall = proportionAccepted(g, teacherTest, false);
//
//		return new Pair<Double, Double>(new Double(precision), new Double(recall));		
//	}
	
//	/**
//	 * Finds the precision and recall, based on this grammar. This method:
//	 *  - creates an Adios Model based on trainSentences sentences generated
//	 *    by the grammar g0
//	 *  - gets a new grammar, g, from the finished Adios model
//	 *  - generates two sets of sentences both of size testSentences. One from g0
//	 *    one from g. The sentences generated by g0 should not be in the training 
//	 *    set.
//	 *  - The precision is the proportion of sentences generated by g that are 
//	 *    accepted by g0. This is the first value in the pair that is returned
//	 *  - The recall is the proportion of sentences generated by g0 that are 
//	 *    accepted by g.
//	 *    
//	 * Note: if the amounts of sentences are set so large that g0 cannot generate 
//	 * any sentences that are not in the training set (ie. the training set 
//	 * contains all sentence the grammar can generate), this method will get stuck 
//	 * in an infinite loop.   
//	 *  
//	 * @param g0
//	 */
//	public static Pair<Double, Double> precisionRecall(
//			Grammar<String> g0,
//			int maxDepth,
//			int trainSentences, 
//			int testSentences,
//			double drop,
//			double sig,
//			double over,
//			int win,
//			boolean context,
//			boolean verbose)
//			throws IOException									
//			{
//		Vector<Collection<String>> trainColl = new Vector<Collection<String>>(trainSentences);
//		Set<Collection<String>> trainSet = new HashSet<Collection<String>>();
//		Collection<String> sentence;
//
//		// create a collection of sentences to train on, maintain a set of all 
//		// distinct sentences generated
//		for(int i = 0; i < trainSentences; i++)
//		{
//			sentence = g0.generateSentence("S", 3, maxDepth);
//			trainColl.add(sentence);
//			trainSet.add(sentence);			
//		}
//
//		Vector<Collection<String>> testColl = new Vector<Collection<String>>(trainSentences);
//
//		// create a collection of sentences to test with
//		// make sure none of them wre in the training set
//		while(testColl.size() < testSentences)
//		{
//			sentence = g0.generateSentence("S", 3, maxDepth);
//			if(! trainSet.contains(sentence))
//				testColl.add(sentence);
//		}
//
//		Corpus<String> trainCorpus = new CollectionCorpus<String>(trainColl);
//		Corpus<String> teacherTest = new CollectionCorpus<String>(testColl);
//		Adios<String> model = new Adios<String>(trainCorpus);
//
//		model.writeResults(new File("D:\\Docs\\Voynich\\Lilian\\files\\results\\"), "model_basic");		
//
//		if(verbose) System.out.println("Starting pattern distillation");
//		// model.patternDistillation(drop, sig, context);
//
//		if(verbose) System.out.println("Starting pattern first generalization");
//		//	model.generalization(drop, sig, context, win);
//
//		if(verbose) System.out.println("Start further generalization");
//		model.generalizationBootstrap(drop, sig, over, context, win, 400);			
//
//		if(verbose) System.out.println();
//		if(verbose) System.out.println(model.ptokens.size() + " equivalence classes found.");
//
//		Grammar<String> g = model.toGrammar();
//
//		g.write(new File("D:\\Docs\\Voynich\\Lilian\\files\\results\\"), "g");
//		g0.write(new File("D:\\Docs\\Voynich\\Lilian\\files\\results\\"), "g0");
//		model.writeResults(new File("D:\\Docs\\Voynich\\Lilian\\files\\results\\"), "model");
//
//		Corpus<String> learnerTest =
//			new StripSentenceCorpusWrapper<String>(
//					new GrammarCorpus<String>(g, "S", testSentences, 3, 0, 42));
//					
//
//		if(verbose) System.out.println("precision:");		
//		double precision = proportionAccepted(g0, learnerTest, verbose);
//		
//		if(verbose) System.out.println("recall:");		
//		double recall = proportionAccepted(g, teacherTest, false);
//
//		return new Pair<Double, Double>(new Double(precision), new Double(recall));
//	}

//	/**
//	 * returns the proportion of accepted sentences
//	 */
//	public static double proportionAccepted(Grammar<String> g, Corpus<String> c, boolean verbose)
//	throws IOException
//	{
//		Vector<String> sentence = new Vector<String>();
//		Parse<String> parse;
//		int accepted = 0;
//		int total = 0;
//		while(c.hasNext())
//		{
//			sentence.add(c.next());
//			if(c.atSentenceEnd())
//			{
//				total++;
//				parse = g.parse(sentence);
//				if(parse.isMember())
//				{
//					accepted++;
//					// if(verbose) System.out.println(" accepted");
//				}else
//				{
//					//if(verbose) System.out.println(sentence + " rejected");
//				}
//
//				// System.out.println(sentence + " " + parse.isMember());
//				sentence = new Vector<String>();
//			}			
//		}
//
//		return ((double)accepted) / ((double)total);
//	}

//	public static Pair<List<Pair<Integer, Double>>, Double> precisionRecallSplit(
//			Grammar<String> g0,
//			int maxDepth,
//			int trainSentences, 
//			int testSentences,
//			double drop,
//			double sig,
//			double over,
//			int win,
//			int sentenceLength,
//			int step,
//			int batch,
//			boolean context,
//			boolean longSentences,
//			boolean verbose)
//			throws IOException									
//			{
//		Vector<Collection<String>> trainColl = new Vector<Collection<String>>(trainSentences);
//		Set<Collection<String>> trainSet = new HashSet<Collection<String>>();
//		Collection<String> sentence;
//
////		create a collection of sentences to train on, maintain a set of all 
////		distinct sentences generated
//		for(int i = 0; i < trainSentences; i++)
//		{
//			sentence = g0.generateSentence("S", 3, maxDepth);
//			trainColl.add(sentence);
//			trainSet.add(sentence);			
//		}
//
//		Vector<Collection<String>> testColl = new Vector<Collection<String>>(trainSentences);
//
////		create a collection of sentences to test with
////		make sure none of them were in the training set
//		while(testColl.size() < testSentences)
//		{
//			sentence = g0.generateSentence("S", 3, maxDepth);
//			if(! trainSet.contains(sentence))
//				testColl.add(sentence);
//		}
//
//		Corpus<String> trainCorpus = new CollectionCorpus<String>(trainColl);
//		Corpus<String> teacherTest = new CollectionCorpus<String>(testColl);
//		
//		Adios <String> model; 
//		if(longSentences)
//			model = new AdiosLS<String>(
//					new NoSentenceWrapper<String>(trainCorpus), 
//					sentenceLength, step, batch);
//		else
//			model = new Adios<String>(trainCorpus);
//
//		model.writeResults(new File("D:\\Docs\\Voynich\\Lilian\\files\\results\\"), "model_basicLS");		
//
////		if(verbose) System.out.println("Starting pattern distillation");
////		model.patternDistillation(drop, sig, context);
//
////		if(verbose) System.out.println("Starting pattern first generalization");
////		model.generalization(drop, sig, context, win);
//
//		if(verbose) System.out.println("Start boosted generalization");
//		model.generalizationBootstrap(drop, sig, over, context, win, 400);			
//
//		if(verbose) System.out.println();
//		if(verbose) System.out.println(model.ptokens.size() + " equivalence classes found.");
//
//		Grammar<String> g = model.toGrammar();
//
//		g.write(new File("D:\\Docs\\Voynich\\Lilian\\files\\results\\"), "gLS");
//		g0.write(new File("D:\\Docs\\Voynich\\Lilian\\files\\results\\"), "g0LS");
//		model.writeResults(new File("D:\\Docs\\Voynich\\Lilian\\files\\results\\"), "modeLS");
//
//		if(verbose) System.out.println("recall:");		
//		double recall = proportionAccepted(g, teacherTest, false);
//		
//		Vector<Pair<Integer, Double>> precision = new Vector<Pair<Integer, Double>>();
//		Pair<Integer, Double> pair;
//		
//		Corpus<String> learnerTest;
//		String topSymbol;
//		Set<String> symbols;
//		Iterator<String> symbolIt;
//		
//		Vector<Integer> numbers;
//		Integer height;
//		Vector<Double> precisions;
//		
//		double prec, t;
//		
//		//TODO: The 100 is arbitrary
//		for(int i = 1; i <= 100;i++)
//		{
//			System.out.print("\nheight " + i + ":");
//			symbols = model.symbolsForHeight(i);
//			symbolIt = symbols.iterator();
//			prec = 0.0;
//			
//			height = new Integer(i);
//			if(! heightToPrecision.containsKey(height) )
//				heightToPrecision.put(new Integer(height), new Vector<Double>());
//			precisions = heightToPrecision.get(height);
//			
//			if(! heightToNumber.containsKey(height) )
//				heightToNumber.put(new Integer(height), new Vector<Integer>());
//			numbers = heightToNumber.get(height);					
//			
//			
//			while(symbolIt.hasNext())
//			{
//				topSymbol = symbolIt.next();
//				
//				//System.out.print(" " + topSymbol);
//				
//				learnerTest =
//					new StripSentenceCorpusWrapper<String>(
//							new GrammarCorpus<String>(
//									g, topSymbol, 
//									(int)(testSentences), 
//									3, 0, 42));
//							
//				t = proportionAccepted(g0, learnerTest, verbose);
//				
//				//System.out.print("(" + t + ")");
//				precisions.add(new Double(t));								
//				prec += t; 
//			}
//			
//			if(symbols.size() != 0)
//				prec = prec / (double)(symbols.size());
//			
//			numbers.add(new Integer(symbols.size()));
//			
//			pair = new Pair<Integer, Double>(new Integer(symbols.size()), new Double(prec));
//			precision.add(pair);
//		}
//		
//		//System.out.println("done");
//
//		return new Pair<List<Pair<Integer, Double>>, Double>(precision, new Double(recall));
//	}	

	/**
	 * Segments the MexGraph graph using pattern distillation. It starts with alphas[0] and when that 
	 * runs out, increases it to alphas[1] and so on. 
	 * 
	 * If Corpus<String> gold is not null, it should represent the properly segmented corpus. the resulting 
	 * segmented corpora are checked against the gold corpus, and a vector is returned that contains, 
	 * for each alpha value the proportion of incorrect spaces in the segmented corpus to the total 
	 * number of spaces in the gold corpus. The first number in the returned vector will represent the
	 * proportion of incorrect spaces on the untrained mexgraph (the proportion of spaces in the text to 
	 * characters in the text). The result for alpha value alpha[n] is in result.get(n + 1); 
	 * 
	 * If gold is null, this step is skipped (and null is returned);
	 * 
	 * @param graph
	 * @param alphas
	 * @param drop
	 * @param context
	 * @param base
	 * @param verbose 
	 */
	public static Vector<Double> segmentation(
			Adios<String> graph, 
			double[] alphas, 
			double drop, 
			boolean context,
			File dir,
			String base,
			SequenceCorpus<String> gold,
			boolean verbose)
		throws IOException
	{
		Vector<Double> result = new Vector<Double>(alphas.length + 1);
		int n, denom = -1;
		String current; 
		graph.writeResults(dir, base + ".segmentation.basic");

		// * Count the total number of characters in the gold corpus
		if(gold != null)
		{
		
			SequenceIterator<String> goldIt = gold.iterator();
			denom = 0;
			while(goldIt.hasNext())
			{
				current = goldIt.next();
				denom += current.length();
			}

			n = checkWrongSpaces(graph.stringCorpus(), gold);
			if(verbose) System.out.println("  number of incorrect spaces:" + n);
			if(verbose) System.out.println("  proportion:" + (double)n / (double)denom);
			result.add(new Double( ((double)n)/denom));
		}

		for(int i = 0; i < alphas.length; i++)
		{
			if(verbose) System.out.println("Starting alpha = " + alphas[i]);

			if(verbose) System.out.println("  Training Model");
			while(graph.patternDistillation(drop, alphas[i], context))
				if(verbose) System.out.println(".");
			if(verbose) System.out.println();			

			if(verbose) System.out.println("  Finished. Writing Results.");
			graph.writeResults(dir, base + ".segmentation." + alphas[i]);

			if(verbose) System.out.println("  Finished. Checking against Gold Corpus.");

			if(gold != null)
			{
				n = checkWrongSpaces(graph.stringCorpus(), gold);
				if(verbose) System.out.println("  number of incorrect spaces:" + n);
				if(verbose) System.out.println("  proportion:" + n / (double)denom);				
				result.add( n / (double)denom );
			}	
		}

		if(gold == null)
			return null;
		return result;
	}

	/**
	 * Counts the number of incorrect spaces in one corpus according to another.
	 * 
	 * This method expects line ends in the corpora to match. If they don't, the method
	 * will ignore parts of one of the corpora.
	 * 
	 * @param candidate The corpus for which the spaces are tested
	 * @param gold The corpus against which the spaces are tested 
	 * @return The number of mismatched spaces
	 */
	public static int checkWrongSpaces(
			SequenceCorpus<String> candidate, 
			SequenceCorpus<String> gold)
	{
		String candidateWord, goldWord;
		StringBuilder candidateLine, goldLine;

		int result = 0, full = 0, n, total;
		boolean first, firstToken;		
		
		SequenceIterator<String> candidateSI = candidate.iterator();
		SequenceIterator<String> goldSI = gold.iterator();

		while(candidateSI.hasNext() || goldSI.hasNext())
		{
			n = 0;
			total = 0;

			// * Convert the next line (sequence) from each corpus to a string 
			//   with an underscore between characters that don't have space 
			//   between them    
			
			candidateLine = new StringBuilder();
			goldLine = new StringBuilder();
			
			first = true;
			while(candidateSI.hasNext() && (!candidateSI.atSequenceEnd() || first))
			{
				first = false;
				candidateWord = candidateSI.next();
				firstToken = true;
				for(int i = 0; i < candidateWord.length(); i++)
				{					
					if(firstToken)
						firstToken = false;
					else
						candidateLine.append('_');
					candidateLine.append(candidateWord.charAt(i));
				}
				candidateLine.append(' ');
			}

			first = true;
			while(goldSI.hasNext() && (!goldSI.atSequenceEnd() || first))
			{
				first = false;
				goldWord = goldSI.next();
				total += goldWord.length();
				firstToken = true;
				for(int i = 0; i < goldWord.length(); i++)
				{
					if(firstToken)
						firstToken = false;
					else
						goldLine.append('_');
					goldLine.append(goldWord.charAt(i));
				}
				goldLine.append(' ');
			}

			for(int i = 1; i < Math.min(goldLine.length(), candidateLine.length()); i += 2)
			{
				if(candidateLine.charAt(i) != goldLine.charAt(i))
				{
					n++;
					candidateLine.insert(i, '*');
					goldLine.insert(i, '*');
					i++;
				}
			}

			result += n;
			full += total;

//			Global.log().info(candidateLine + " " + n + " (" + ((double)n)/((double)total)+ ")");
//			Global.log().info(goldLine + " " + total);
		}

//		Log.logln( result + "/" + full);
//		Log.logln( ((double)result) / ((double)full));

		return result;
	}

	public static final double[] STANDARD_ALPHAS = {0.001, 0.01, 0.1, 0.5};
}
