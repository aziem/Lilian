package org.lilian.data.real;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.junit.Test;
import org.lilian.data.real.weighted.Weighted;
import org.lilian.data.real.weighted.WeightedLists;
import org.lilian.search.Builder;
import org.lilian.util.MatrixTools;
import org.lilian.util.Series;

public class MVNTest
{

	@Test
	public void test()
	{
		MVN mvn3 = new MVN(3);
		
        // * matlab:
		// cov = [0.4, 0.6; 0.2, 0.8]; 
		// cov2 = cov * cov';
		// x = [-0.5735, -0.3305; 2.1280,  1.6938; -0.1329, -0.0663]	
		
		RealMatrix rot = MatrixTools.toMatrix("0.4, 0.6; 0.2, 0.8");
		RealVector trans = MatrixTools.toVector(1.0, -1.0);
		MVN mvn2 = new MVN(new AffineMap(rot, trans));
		
		Point x1 = new Point(-0.5735 + 1.0, -0.3305 - 1.0);
		Point x2 = new Point( 2.1280 + 1.0,  1.6938 - 1.0);
		Point x3 = new Point(-0.1329 + 1.0, -0.0663 - 1.0);
		
		double 	t1 = 0.3394,
	    		t2 = 0.0010,
	    		t3 = 0.7529;
				
		assertEquals(mvn2.density(x1), t1, 0.0001);
		assertEquals(mvn2.density(x2), t2, 0.0001);
		assertEquals(mvn2.density(x3), t3, 0.0001);
		
		// m4 = [0.050000, 0.000000; 0.000000, 0.050000]; 
		// mean = [0.500, -0.500]; p = [-0.989, -0.921]; mvnpdf(p, mean, mat*mat')
		RealMatrix rot4 = MatrixTools.toMatrix("0.050000, 0.000000; 0.000000, 0.050000");
		RealVector trans4 = MatrixTools.toVector(0.500, -0.500);
		MVN mvn4 = new MVN(new AffineMap(rot4, trans4));
		
		Point x4 = new Point(-0.989, -0.921);
		
		double t4 = 6.7958e-207;
		
		assertTrue(Math.abs(mvn4.density(x4) - t4) < 0.00001);		
	}
	
	@Test
	public void findSphericalTest()
	{
		MVN source = new MVN(new Point(5.0, 1.0), MatrixTools.identity(2).scalarMultiply(0.1));
		
		MVN rec = MVN.findSpherical(source.generate(300));
		System.out.println(rec);
		
		List<Point> points = new ArrayList<Point>();
		Weighted<Point> weighted = WeightedLists.empty();
		
		Point p;
		
		p = new Point(10, 5, 1);
		points.add(p);
		points.add(p);
		weighted.add(p, 2.0);
		
		p = new Point(1, 3, -5);
		points.add(p);
		points.add(p);
		points.add(p);
		weighted.add(p, 3.0);
				
		p = new Point(0.1, 3, 0);
		points.add(p);
		weighted.add(p, 1.0);
		
		p = new Point(1, 1, 1);
		
		System.out.println(MVN.findSpherical(points));
		System.out.println(MVN.findSpherical(weighted));

		assertEquals(MVN.findSpherical(points).density(p), MVN.findSpherical(weighted).density(p), 0.000001);

	}
	
	@Test
	public void mapTest()
	{
		int dim = 2;
		MVN mvn = new MVN(new Point(-0.5, -0.5), 0.1);
		
		System.out.println(mvn.map());
		
		MVN mvn2 = MVN.find(mvn.generate(1000));
		
		System.out.println(mvn2.map());
		
		for(int i : Series.series(AffineMap.numParameters(dim)))
			assertEquals(mvn.map().parameters().get(i), mvn2.map().parameters().get(i), 0.02);
	}

	@Test
	public void mapTest2()
	{
		int dim = 2;
		Generator<Point> gen = Datasets.three();
		
		MVN mvnFrom = MVN.find(gen.generate(3000));
		MVN mvnTo = MVN.find(new MVN(new Point(-0.5, -0.5), 0.01).generate(1000));
		
		System.out.println(mvnFrom.map());
		System.out.println(mvnTo.map());
		
		
//		for(int i : Series.series(AffineMap.numParameters(dim)))
//			assertEquals(mvn.map().parameters().get(i), mvn2.map().parameters().get(i), 0.02);
	}	
	
	@Test
	public void parametersTest()
	{
		int dim = 4;
		Builder<MVN> builder = MVN.builder(dim);
		
		List<Double> random = Point.random(MVN.numParameters(dim), 0.5);
		MVN mvn = builder.build(random);
		
		System.out.println(mvn.density(new Point(0, 0, 0, 0)));
		
		mvn = builder.build(mvn.parameters());
		
		System.out.println(mvn.density(new Point(0, 0, 0, 0)));
		
	}
	
	@Test
	public void transformTest()
	{
		MVN basis = new MVN(2);
		System.out.println(basis.map());
		
		MVN other = new MVN(2, 0.3);
		System.out.println(other.map());
		
	}
	
	@Test
	public void findTest()
	{
		MVN source = new MVN(new Point(5.0, 1.0), MatrixTools.identity(2).scalarMultiply(0.1));
		
		MVN rec = MVN.find(source.generate(300));
		System.out.println(rec);
		
		List<Point> points = new ArrayList<Point>();
		Weighted<Point> weighted = WeightedLists.empty();
		
		Point p;
		
		p = source.generate();
		points.add(p);
		points.add(p);
		weighted.add(p, 2.0);
		
		p = source.generate();
		points.add(p);
		points.add(p);
		points.add(p);
		weighted.add(p, 3.0);
				
		p = source.generate();
		points.add(p);
		weighted.add(p, 1.0);
		
		p = source.generate();
		points.add(p);
		weighted.add(p, 1.0);		
		
		p = source.generate();
		points.add(p);
		weighted.add(p, 1.0);	

		p = source.generate();
		points.add(p);
		weighted.add(p, 1.0);	
		
		System.out.println(MVN.find(points, true));
		System.out.println(MVN.find(weighted));

		assertEquals(MVN.find(points, true).density(p), MVN.find(weighted).density(p), 0.000001);
	}
	
}
