package org.lilian.neural;

import static org.lilian.util.Series.series;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.lilian.data.real.CompositeMap;
import org.lilian.data.real.MVN;
import org.lilian.data.real.Map;
import org.lilian.data.real.Point;
import org.lilian.search.Builder;
import org.lilian.search.Parametrizable;
import org.lilian.util.MatrixTools;
import org.lilian.util.Series;

/**
 * A network consisting of three layers with in and output the same size. The
 * first layer has a sigmoid activation, the second a linear. 
 *  
 * @author Peter
 *
 * @param <Double>
 */
public class ThreeLayer extends AbstractList<Double> 
	implements Parametrizable, Neural<Double>, Map
{
	private Activation activation;
	
	// * Number of in/out nodes (minus bias nodes) 
	private int n;
	private int h;
	
	private RealVector stateIn, stateHidden, stateOut;
	private RealMatrix weights0, weights1;
	
	private RealVector gHidden, eHiddenL, eHidden;
	private RealMatrix weights0Delta, weights1Delta;
	
	private ThreeLayer()
	{
	}
	
	@Override
	public List<java.lang.Double> parameters()
	{
		List<Double> parameters = new ArrayList<Double>((n+1) * (h) + (h+1) * n);
		
		for(int i : series(h)) // to
			for(int j : series(n+1)) // from
				parameters.add(weights0.getEntry(i, j));
		

		for(int i : series(n)) // to
			for(int j : series(h+1)) // from
				parameters.add(weights1.getEntry(i, j));
				
		return parameters;
	}
	
	public int inputSize()
	{
		return n;
	}
	
	public int outputSize()
	{
		return n;
	}
	
	public int hiddenSize()
	{
		return h;
	}
	
	public void set(List<Double> in)
	{
		for(int j : series(inputSize()))
			set(j, in.get(j));
	}
	
	public List<Double> out()
	{
		return new Point(stateOut);
	}
	
	public static Builder<ThreeLayer> builder(int n, int h, Activation activation)
	{ 
		return new TLBuilder(n, h, activation);
	}

	private static class TLBuilder implements Builder<ThreeLayer>
	{
		private static final long serialVersionUID = 1L;
		private int n, h;
		private Activation activation;
		
		public TLBuilder(int n, int h, Activation activation)
		{
			this.n = n;
			this.h = h;
			this.activation = activation;
		}

		@Override
		public ThreeLayer build(List<Double> parameters)
		{
			ThreeLayer fnn = new ThreeLayer();
			
			fnn.n = n;
			fnn.h = h;
			fnn.activation = activation;
			
			fnn.stateIn = new ArrayRealVector(n+1);
			fnn.stateIn.setEntry(n, 1.0); // * bias node
			
			fnn.stateHidden = new ArrayRealVector(h+1);
			fnn.stateHidden.setEntry(h, 1.0); // * bias node
			
			fnn.stateOut = new ArrayRealVector(n);
			
			fnn.weights0 = new Array2DRowRealMatrix(h, n+1);
			fnn.weights1 = new Array2DRowRealMatrix(n, h+1);
			
			fnn.weights0Delta = new Array2DRowRealMatrix(h, n+1);
			fnn.weights1Delta = new Array2DRowRealMatrix(n, h+1);
			
			fnn.gHidden = new ArrayRealVector(h);
			fnn.eHiddenL = new ArrayRealVector(h+1);
			fnn.eHidden = new ArrayRealVector(h);
			
			int c = 0;
			for(int i : series(h)) // to
				for(int j : series(n+1)) // from
					fnn.weights0.setEntry(i, j, parameters.get(c ++));
			for(int i : series(n)) // to
				for(int j : series(h+1)) // from
					fnn.weights1.setEntry(i, j, parameters.get(c ++));
			
			return fnn;
		}
	
		@Override
		public int numParameters()
		{
			return (n+1) * (h) + (h+1) * n;
		}
	
	}

	@Override
	public void step()
	{
		stateHidden.setSubVector(0, weights0.operate(stateIn));
		
		for(int i : series(h))
			stateHidden.setEntry(i, activation.function(stateHidden.getEntry(i)));
		
		stateOut.setSubVector(0, weights1.operate(stateHidden));
	}

	@Override
	public Double get(int index)
	{
		if(index < n)
			return stateIn.getEntry(index);
		if(index < n + h)
			return stateHidden.getEntry(index - n);
		return stateOut.getEntry(index - n - h);
	}

	@Override
	public int size()
	{
		return n * 2 + h;
	}

	@Override
	public Double set(int index, Double element)
	{
		if(index < n)
			stateIn.setEntry(index, element);
		else if(index < n + h)
			stateHidden.setEntry(index - n, element);
		else 
			stateOut.setEntry(index - n - h, element);
		
		return element;
	}
	
	public static ThreeLayer random(int n, int h, double var, Activation activation)
	{
		Builder<ThreeLayer> builder = builder(n, h, activation);
		List<Double> parameters = Point.random(builder.numParameters(), var);
		
		return builder.build(parameters);
	}

	@Override
	public Point map(Point in)
	{
		set(in);
		step();
		return new Point(out());
	}

	@Override
	public boolean invertible()
	{
		return false;
	}

	@Override
	public Map inverse()
	{
		return null;
	}

	@Override
	public int dimension()
	{
		return n;
	}

	@Override
	public List<Point> map(List<Point> points)
	{
		List<Point> out = new ArrayList<Point>(points.size());
		for(Point p : points)
			out.add(map(p));
		
		return out;
	}

	@Override
	public Map compose(Map other)
	{
		return new CompositeMap(this, other);
	}
	
	/**
	 * Modifies this map through a single backpropagation iteration using the 
	 * given error values on the output nodes.
	 * 
	 * @param error
	 */
	public void train(List<Double> error, double learningRate)
	{
		RealVector eOut = new ArrayRealVector(error.size());
		for(int i : series(error.size()))
			eOut.setEntry(i, error.get(i));
		
		// * gHidden: delta for the non-bias nodes of the hidden layer
		gHidden.setSubVector(0, stateHidden.getSubVector(0, n)); // optimize
		
		for(int i : Series.series(gHidden.getDimension()))
			gHidden.setEntry(i, activation.derivative(gHidden.getEntry(i)));
		
		eHiddenL = weights1.transpose().operate(eOut);
		eHidden.setSubVector(0, eHiddenL.getSubVector(0, h));
		for(int i : series(h))
			eHidden.setEntry(i, eHidden.getEntry(i) * gHidden.getEntry(i));
		
		weights1Delta = MatrixTools.outer(eOut, stateHidden);
		weights1Delta = weights1Delta.scalarMultiply(-1.0 * learningRate); // optimize
		
		weights0Delta = MatrixTools.outer(eHidden, stateIn);
		weights0Delta = weights0Delta.scalarMultiply(-1.0 * learningRate);
		
		weights0 = weights0.add(weights0Delta);
		weights1 = weights1.add(weights1Delta);
	}
	
	public void train(Point in, Point out, double learningRate)
	{
		Point mapped = map(in);
		
		List<Double> error = new ArrayList<Double>(mapped.size());
		for(int j : series(out.size()))
		{
			double t = out.get(j),
			       o = mapped.get(j); 
			
			error.add(o - t);
		}
			
		train(error, learningRate);
	}
	
	/** 
	 * Runs a single epoch of backpropagation on the given input and output 
	 * points. 
	 * 
	 * @param in
	 * @param out
	 */
	public void train(List<Point> in, List<Point> out, double learningRate)
	{
		if(in.size() != out.size())
			throw new IllegalArgumentException("Input lists must be the same size (were "+in.size()+" and "+out.size()+").");
		
		for(int i : Series.series(in.size()))
			train(in.get(i), out.get(i), learningRate);

	}
	
	/** 
	 * Runs n epochs of backpropagation. 
	 * 
	 * @param in
	 * @param out
	 */
	public void train(List<Point> in, List<Point> out, double learningRate, int n)
	{
		if(in.size() != out.size())
			throw new IllegalArgumentException("Input lists must be the same size (were "+in.size()+" and "+out.size()+").");
		
		for(int i : Series.series(n))
			train(in, out, learningRate);
	}
	
	public static ThreeLayer copy(
			Map map, int hidden, int examples, 
			double learningRate, double initVar)
	{
		ThreeLayer copy = random(
				map.dimension(), hidden, initVar, Activations.sigmoid());
		
		MVN source = new MVN(map.dimension());
		for(int i : series(examples))
		{
			Point x = source.generate(),
			      y = map.map(x);
			
			copy.train(x, y, learningRate);
		}
		
		return copy;
	}
}
