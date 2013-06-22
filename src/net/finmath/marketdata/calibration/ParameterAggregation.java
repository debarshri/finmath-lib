/*
 * (c) Copyright Christian P. Fries, Germany. All rights reserved. Contact: email@christian-fries.de.
 *
 * Created on 26.11.2012
 */
package net.finmath.marketdata.calibration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Combine a set of parameter vectors to a single parameter vector.
 * 
 * The class is a little helper class which can combine a set of double[] parameter-vectors to a
 * single large double[] parameter-vector. More precisely: this class combines a set of objects
 * implementing ParameterObjectInterface to a single object by implementing ParameterObjectInterface
 * with the combined parameter.
 * 
 * An application is an optimization problem which depends on different parametrized objects, using a solver/optimizer
 * which operates an a single parameter vector.
 * 
 * @author Christian Fries
 */
public class ParameterAggregation<E extends ParameterObjectInterface> implements ParameterObjectInterface {

	private final Set<ParameterObjectInterface> parameters;
	
	/**
	 * Create a collection of parametrized objects.
	 */
	public ParameterAggregation() {
		this.parameters = new LinkedHashSet<ParameterObjectInterface>();
	}

	/**
	 * Create a collection of parametrized objects. The constructor will internally create a
	 * (shallow) copy of the set passed to it.
	 * 
	 * @param parameters A set of objects implementing ParameterObjectInterface to be combined to a single object.
	 */
	public ParameterAggregation(Set<E> parameters) {
		this.parameters = new LinkedHashSet<ParameterObjectInterface>(parameters);
	}

	/**
	 * Create a collection of parametrized objects. The constructor will internally create a
	 * (shallow) copy of the set passed to it.
	 * 
	 * @param parameters A set of objects implementing ParameterObjectInterface to be combined to a single object.
	 */
	public ParameterAggregation(E[] parameters) {
		this.parameters = new LinkedHashSet<ParameterObjectInterface>(Arrays.asList(parameters));
	}
	
	/**
	 * Add an object this parameterization.
	 * 
	 * @param parameterObject The object to add to tis parametrization
	 */
	public void add(E parameterObject) {
		parameters.add(parameterObject);
	}

	/**
	 * Remove an object from this parameterization.
	 */
	public void remove(E parameterObject) {
		parameters.remove(parameterObject);
	}

	/* (non-Javadoc)
	 * @see net.finmath.marketdata.calibration.UnconstrainedParameterVectorInterface#getParameter()
	 */
	@Override
	public double[] getParameter() {
		// Calculate the size of the total parameter vector
		int parameterArraySize = 0;
		for(ParameterObjectInterface parameterVector : parameters) {
			if(parameterVector.getParameter() != null) {
				parameterArraySize += parameterVector.getParameter().length;
			}
		}

		double[] parameterArray = new double[parameterArraySize];

		// Copy parameter object parameters to aggregated parameter vector
		int parameterIndex = 0;
		for(ParameterObjectInterface parameterVector : parameters) {
			double[] parameterVectorOfDouble = parameterVector.getParameter();
			if(parameterVectorOfDouble != null) {
				System.arraycopy(parameterVectorOfDouble, 0, parameterArray, parameterIndex, parameterVectorOfDouble.length);
				parameterIndex += parameterVectorOfDouble.length;
			}
		}

		return parameterArray;
	}

	/* (non-Javadoc)
	 * @see net.finmath.marketdata.calibration.UnconstrainedParameterVectorInterface#setParamter(double[])
	 */
	@Override
	public void setParameter(double[] parameter) {
		int parameterIndex = 0;
		for(ParameterObjectInterface parameterVector : parameters) {
			double[] parameterVectorOfDouble = parameterVector.getParameter();
			if(parameterVectorOfDouble != null) {
				// Copy parameter starting from parameterIndex to parameterVectorOfDouble
				System.arraycopy(parameter, parameterIndex, parameterVectorOfDouble, 0, parameterVectorOfDouble.length);
				parameterIndex += parameterVectorOfDouble.length;
				parameterVector.setParameter(parameterVectorOfDouble);
			}
		}
	}

	public Map<E, double[]> getObjectsToModifyForParameter(double[] parameter) {
		Map<E, double[]> result = new HashMap<E, double[]>();
		int parameterIndex = 0;
		for(ParameterObjectInterface parameterVector : parameters) {
			double[] parameterVectorOfDouble = parameterVector.getParameter().clone();
			if(parameterVectorOfDouble != null) {
				// Copy parameter starting from parameterIndex to parameterVectorOfDouble
				System.arraycopy(parameter, parameterIndex, parameterVectorOfDouble, 0, parameterVectorOfDouble.length);
				parameterIndex += parameterVectorOfDouble.length;
				result.put((E)parameterVector, parameterVectorOfDouble);
			}
		}
		return result;
	}
}
