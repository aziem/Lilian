package org.lilian.experiment;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Represents the basics of a running experiment, for instance the details of which
 * folder the experiment places its output, and the thread(s) of the experiment.
 * 
 * 
 * 
 * @author Peter
 *
 */
public class Environment
{
	protected List<Environment> children = new ArrayList<Environment>();
	protected File dir;
	protected Logger logger;
	
	public Environment(File dir)
	{
		super();
		this.dir = dir;
		
		
		this.logger = Logger.getLogger(this.getClass().toString());
		logger.setLevel(Level.INFO);
		
		FileHandler handler;
		try
		{
			handler = new FileHandler(dir.getAbsolutePath() + File.separator + "log.txt");
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		handler.setFormatter(new SimpleFormatter());
		logger.addHandler(handler);
		
		ConsoleHandler cHandler = new ConsoleHandler();
		cHandler.setFormatter(new SimpleFormatter());
		logger.addHandler(cHandler);
	}

	/**
	 * Generates a new environment in which to run an experiment. The environment
	 * resides in a subfolder of this environment, and is given its own 
	 * thread pool.
	 * 
	 * @return
	 */
	public Environment child()
	{
		return null;
	}
	
	/**
	 * The directory to which this experiment should write its results.
	 * @return
	 */
	public File directory()
	{
		return dir;
	}
	
	/**
	 * A printstream to write information to. 
	 * @return
	 */
	public Logger logger()
	{
		return logger;
	}
	
	/**
	 * Returns the environment of the currently running experiment.
	 * @return
	 */
	public static Environment current()
	{
		return current;
	}
	
	public static Environment current;
}
