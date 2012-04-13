package org.lilian.data.real.fractal;

import java.util.ArrayList;
import java.util.List; 

import org.lilian.data.real.Map;
import org.lilian.data.real.Point;
import org.lilian.data.real.Similitude;
import org.lilian.data.real.classification.AbstractClassifier;
import org.lilian.data.real.classification.DensityClassifier;
import org.lilian.search.Parametrizable;
import org.lilian.util.distance.Distance;
import org.lilian.util.distance.SquaredEuclideanDistance;

/**
 * A more efficient implementation of the IFSDensityClassifier, which stores a 
 * great deal of information to speed up classification.
 *   
 * NOTE: The bhavior of this classifier is only technically correct when 
 * similitudes are used a the parameter mode.
 *
 */
public class IFSClassifier extends AbstractClassifier
{
	
	protected List<IFS<Similitude>> models = new ArrayList<IFS<Similitude>>();
	protected List<Double> priors = new ArrayList<Double>();
	protected double priorSum = 0.0;
	
	protected Distance<Point> distance = new SquaredEuclideanDistance();
	public List<Store> stores = new ArrayList<Store>();
	protected int depth;
	
	public IFSClassifier(IFS<Similitude> firstModel, double firstPrior, int depth)
	{
		super(firstModel.dimension(), 1);
		
		addModel(firstModel, firstPrior);
		this.depth = depth;
		
		checkStore();		
	}
	
	public void addModel(IFS<Similitude> model, double prior)
	{
		models.add(model);
		priors.add(prior);
		priorSum += prior;
		
		super.numClasses++;
		
		if(stores != null) // stores == null if we're in the superconstructor
			checkStore();
	}
	
	public List<Double> probabilities(Point point) 
	{
		List<Double> probs = new ArrayList<Double>(numClasses);
		List<Double> backups = new ArrayList<Double>(numClasses);
		
		boolean allZero = true;
		
		double pSum = 0.0, bSum = 0.0, value, backup;
		for(int i = 0; i < numClasses; i++)
		{
			double[] d = density(point, i);
			value  = prior(i) * d[0];
			backup = prior(i) * d[1]; 
			
			probs.add(value);
			backups.add(backup);
			
			allZero = allZero && (value == 0.0 || Double.isNaN(value));
			
			bSum += backup;
			pSum += value;
		}
		
		// normalize
		for(int i = 0; i < numClasses; i++)
		{
			if(allZero)
				backups.set(i, backups.get(i)/bSum);
			else
				probs.set(i, probs.get(i)/pSum);
		}
		
		if(allZero)
			return backups;
		
		return probs;
	}	
	
	public double prior(int i)
	{
		return priors.get(i) / priorSum;		
	}

	/**
	 * This method doesn't return a true density (for reasons of speed and 
	 * stability), but for the purposes of classification, it's good enough).
	 */
	protected double[] density(Point point, int index) {
		int size = (int)Math.ceil(Math.pow(models.get(index).size(), depth));

		Store store = stores.get(index);
		
		double sum = 0.0;
		double prod, sqDist;
		
		double backup = 0.0;
		
		for(int i  = 0; i < size; i++)
		{
			double scale = store.scales.get(i);

			sqDist = distance.distance(point, store.means.get(i));

			prod =  Math.pow(scale, -dimension());
			prod *= Math.exp(-(1.0/(2.0 * scale * scale) * sqDist));
			prod *= store.priors.get(i);
			
			// This value is used when the prob density is zero for all points
			backup += Math.exp(sqDist) * store.priors.get(i);
			sum += prod;
		}		
		
		return new double[] {sum, backup};
	}
	
	private void checkStore()
	{
		while(stores.size() < numClasses)
			stores.add(null);
		
		for(int i = 0; i < numClasses; i++)
		{
			if(stores.get(i) == null)
			{
				IFS<Similitude> model = models.get(i);
				
				int size = (int)Math.ceil(Math.pow(model.size(), depth));
		
				List<Point> means = new ArrayList<Point>(size);
				List<Double> priors = new ArrayList<Double>(size);
				List<Double> scales = new ArrayList<Double>(size);		
		
				endPoints(model, depth, means, priors, scales);
		
				Store store = new Store(means, scales, priors);
		
				stores.set(i, store);
			}
		}
	}
	
	
	public class Store {
		public List<Point> means;
		public List<Double> scales;		
		public List<Double> priors;
		
		public Store(List<Point> means, List<Double> scales,
				List<Double> priors) 
		{
			super();
			this.means = means;
			this.scales = scales;
			this.priors = priors;
		}
		
		public void out()
		{
			for(int i = 0; i < means.size(); i++)
				System.out.println(means.get(i) + " " + scales.get(i) + " " + priors.get(i));
		}
	}
	
	public void endPoints(IFS<Similitude> model, int depth, 
			List<Point> points, List<Double> weights, List<Double> scales)
	{
		endPoints(model, new Point(dimension()), 1.0, 1.0, depth, points, weights, scales);
	}
	
	private void endPoints(
			IFS<Similitude> model, Point point, double weight, double scale, int depth, 
			List<Point> points, List<Double> weights, 
			List<Double> scales)
	{
		if(	depth <= 0)
		{
			points.add(new Point(point));
			if(weights != null)
				weights.add(weight);
			
			if(scales != null)
				scales.add(scale);
			
			return;
		}
		
		for(int i = 0; i < model.size(); i++)
		{
			Similitude map = model.get(i);			
			double prob   = model.probability(i);
			
			double sim = map.scalar();
			
			endPoints(model, map.map(point), weight * prob, scale * sim, 
					  depth - 1, points, weights, scales);
		}
	}
	
}
