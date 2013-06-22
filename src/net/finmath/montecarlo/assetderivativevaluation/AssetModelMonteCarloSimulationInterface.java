/*
 * (c) Copyright Christian P. Fries, Germany. All rights reserved. Contact: email@christian-fries.de.
 *
 * Created on 24.01.2004
 */
package net.finmath.montecarlo.assetderivativevaluation;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.MonteCarloSimulationInterface;
import net.finmath.stochastic.RandomVariableInterface;

/**
 * Basic interface which has to be implemented by Monte Carlo models for asset processes.
 * 
 * @author Christian Fries
 * @version 1.0
 */
public interface AssetModelMonteCarloSimulationInterface extends MonteCarloSimulationInterface {

	/**
	 * Returns the number of asset price processes.
	 * 
	 * @return The number of asset price processes
	 */
    int					getNumberOfAssets();

	/**
	 * Returns the random variable representing the asset's value at a given time for a given asset.
	 * 
	 * @param timeIndex		Index of simulation time
	 * @param assetIndex	Index of the asset (0 for a single asset model)
	 * @return				The asset process as seen on simulation time
	 * @throws net.finmath.exception.CalculationException
	 */
    RandomVariableInterface		getAssetValue(int timeIndex, int assetIndex) throws CalculationException;

	/**
	 * Returns the random variable representing the asset's value at a given time for a given asset.
	 * 
	 * @param time			Simulation time
	 * @param assetIndex	Index of the asset (0 for a single asset model)
	 * @return				The asset process as seen on simulation time
	 * @throws net.finmath.exception.CalculationException
	 */
    RandomVariableInterface		getAssetValue(double time, int assetIndex) throws CalculationException;

	RandomVariableInterface		getNumeraire(int timeIndex);
	RandomVariableInterface		getNumeraire(double time);
	
	RandomVariableInterface		getRandomVariableForConstant(double value);

	/**
	 * Create a clone of the object implementing <code>AssetModelMonteCarloSimulationInterface</code>
	 * using a different Monte-Carlo seed.
	 *
	 * @param seed
	 * @return Returns a clone of this model except for a modified Monte-Carlo seed.
	 */
    Object getCloneWithModifiedSeed(int seed);
}
