/*
 * (c) Copyright Christian P. Fries, Germany. All rights reserved. Contact: email@christian-fries.de.
 *
 * Created on 30.11.2012
 */
package net.finmath.marketdata.calibration;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Vector;

import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.model.AnalyticModelInterface;
import net.finmath.marketdata.model.curves.Curve;
import net.finmath.marketdata.model.curves.CurveInterface;
import net.finmath.marketdata.model.curves.DiscountCurve;
import net.finmath.marketdata.model.curves.DiscountCurveInterface;
import net.finmath.marketdata.model.curves.ForwardCurve;
import net.finmath.marketdata.model.curves.ForwardCurveFromDiscountCurve;
import net.finmath.marketdata.model.curves.ForwardCurveInterface;
import net.finmath.marketdata.products.AnalyticProductInterface;
import net.finmath.marketdata.products.Swap;
import net.finmath.marketdata.products.SwapLeg;
import net.finmath.optimizer.SolverException;
import net.finmath.time.RegularSchedule;
import net.finmath.time.ScheduleInterface;
import net.finmath.time.TimeDiscretization;

/**
 * Generate a collection of calibrated curves (discount curves, forward curves)
 * from a vector of calibration products.
 * 
 * @author Christian Fries
 */
public class CalibratedCurves {

	/**
	 * Specification of calibration product.
	 * 
     * @author Christian Fries
     */
    public static class CalibrationSpec {

    	private String				type;
    	
    	private	ScheduleInterface	swapTenorDefinitionReceiver;
		private String				forwardCurveReceiverName;
		private double				spreadReceiver;
		private String				discountCurveReceiverName;

		private ScheduleInterface	swapTenorDefinitionPayer;
		private String				forwardCurvePayerName;
		private double				spreadPayer;
		private String				discountCurvePayerName;
		
		private String				calibrationCurveName;
		private double				calibrationTime;

		/**
         * Calibration specification.
         * 
         * @param type The type of the calibration product.
         * @param swapTenorDefinitionReceiver The schedule of periods of the receiver leg.
         * @param forwardCurveReceiverName The forward curve of the receiver leg (may be null).
         * @param spreadReceiver The spread or fixed coupon of the receiver leg.
         * @param discountCurveReceiverName The discount curve of the receiver leg.
         * @param swapTenorDefinitionPayer The schedule of periods of the payer leg.
         * @param forwardCurvePayerName The forward curve of the payer leg (may be null).
         * @param spreadPayer The spread or fixed coupon of the payer leg.
         * @param discountCurvePayerName The discount curve of the payer leg.
         * @param calibrationCurveName The curve to calibrate, by this product.
         * @param calibrationTime The time point in calibrationCurveName used to calibrate, by this product.
         */
        public CalibrationSpec(
        		String type,
        		ScheduleInterface swapTenorDefinitionReceiver,
                String forwardCurveReceiverName, double spreadReceiver,
                String discountCurveReceiverName,
                ScheduleInterface swapTenorDefinitionPayer,
                String forwardCurvePayerName, double spreadPayer,
                String discountCurvePayerName,
                String calibrationCurveName,
                double calibrationTime) {
	        super();
	        this.type = type;
	        this.swapTenorDefinitionReceiver = swapTenorDefinitionReceiver;
	        this.forwardCurveReceiverName = forwardCurveReceiverName;
	        this.spreadReceiver = spreadReceiver;
	        this.discountCurveReceiverName = discountCurveReceiverName;
	        this.swapTenorDefinitionPayer = swapTenorDefinitionPayer;
	        this.forwardCurvePayerName = forwardCurvePayerName;
	        this.spreadPayer = spreadPayer;
	        this.discountCurvePayerName = discountCurvePayerName;
	        this.calibrationCurveName = calibrationCurveName;
	        this.calibrationTime = calibrationTime;
        }

        /**
         * Calibration specification.
         * 
         * @param type The type of the calibration product.
         * @param swapTenorDefinitionReceiver The schedule of periods of the receiver leg.
         * @param forwardCurveReceiverName The forward curve of the receiver leg (may be null).
         * @param spreadReceiver The spread or fixed coupon of the receiver leg.
         * @param discountCurveReceiverName The discount curve of the receiver leg.
         * @param swapTenorDefinitionPayer The schedule of periods of the payer leg.
         * @param forwardCurvePayerName The forward curve of the payer leg (may be null).
         * @param spreadPayer The spread or fixed coupon of the payer leg.
         * @param discountCurvePayerName The discount curve of the payer leg.
         * @param calibrationCurveName The curve to calibrate, by this product.
         * @param calibrationTime The time point in calibrationCurveName used to calibrate, by this product.
         */
        public CalibrationSpec(
        		String type,
        		double[] swapTenorDefinitionReceiver,
                String forwardCurveReceiverName, double spreadReceiver,
                String discountCurveReceiverName,
                double[] swapTenorDefinitionPayer,
                String forwardCurvePayerName, double spreadPayer,
                String discountCurvePayerName,
                String calibrationCurveName,
                double calibrationTime) {
	        super();
	        this.type = type;
	        this.swapTenorDefinitionReceiver = new RegularSchedule(new TimeDiscretization(swapTenorDefinitionReceiver[0] /* initial */, swapTenorDefinitionReceiver[1] /* numberOfTimeSteps */, swapTenorDefinitionReceiver[2] /* deltaT */, TimeDiscretization.ShortPeriodLocation.SHORT_PERIOD_AT_START));
	        this.forwardCurveReceiverName = forwardCurveReceiverName;
	        this.spreadReceiver = spreadReceiver;
	        this.discountCurveReceiverName = discountCurveReceiverName;
	        this.swapTenorDefinitionPayer = new RegularSchedule(new TimeDiscretization(swapTenorDefinitionPayer[0] /* initial */, swapTenorDefinitionPayer[1] /* numberOfTimeSteps */, swapTenorDefinitionPayer[2] /* deltaT */, TimeDiscretization.ShortPeriodLocation.SHORT_PERIOD_AT_START));
	        this.forwardCurvePayerName = forwardCurvePayerName;
	        this.spreadPayer = spreadPayer;
	        this.discountCurvePayerName = discountCurvePayerName;
	        this.calibrationCurveName = calibrationCurveName;
	        this.calibrationTime = calibrationTime;
        }

        /**
         * Calibration specification.
         * 
         * @param type The type of the calibration product.
         * @param swapTenorDefinitionReceiver The schedule of periods of the receiver leg.
         * @param forwardCurveReceiverName The forward curve of the receiver leg (may be null).
         * @param spreadReceiver The spread or fixed coupon of the receiver leg.
         * @param discountCurveReceiverName The discount curve of the receiver leg.
         * @param calibrationCurveName The curve to calibrate, by this product.
         * @param calibrationTime The time point in calibrationCurveName used to calibrate, by this product.
         */
        public CalibrationSpec(
        		String type,
        		double[] swapTenorDefinitionReceiver,
                String forwardCurveReceiverName, double spreadReceiver,
                String discountCurveReceiverName,
                String calibrationCurveName,
                double calibrationTime) {
	        super();
	        this.type = type;
	        this.swapTenorDefinitionReceiver = new RegularSchedule(new TimeDiscretization(swapTenorDefinitionReceiver[0] /* initial */, swapTenorDefinitionReceiver[1] /* numberOfTimeSteps */, swapTenorDefinitionReceiver[2] /* deltaT */, TimeDiscretization.ShortPeriodLocation.SHORT_PERIOD_AT_START));
	        this.forwardCurveReceiverName = forwardCurveReceiverName;
	        this.spreadReceiver = spreadReceiver;
	        this.discountCurveReceiverName = discountCurveReceiverName;
	        this.calibrationCurveName = calibrationCurveName;
	        this.calibrationTime = calibrationTime;
        }

		@Override
		public String toString() {
			return "CalibrationSpec [type=" + type
					+ ", swapTenorDefinitionReceiver="
					+ swapTenorDefinitionReceiver
					+ ", forwardCurveReceiverName=" + forwardCurveReceiverName
					+ ", spreadReceiver=" + spreadReceiver
					+ ", discountCurveReceiverName="
					+ discountCurveReceiverName + ", swapTenorDefinitionPayer="
					+ swapTenorDefinitionPayer + ", forwardCurvePayerName="
					+ forwardCurvePayerName + ", spreadPayer=" + spreadPayer
					+ ", discountCurvePayerName=" + discountCurvePayerName
					+ ", calibrationCurveName=" + calibrationCurveName
					+ ", calibrationTime=" + calibrationTime + "]";
		}
      }

	private AnalyticModelInterface				model				= new AnalyticModel();
    private Set<CurveInterface>					curvesToCalibrate	= new LinkedHashSet<CurveInterface>();
	private Vector<AnalyticProductInterface>	calibrationProducts	= new Vector<AnalyticProductInterface>();
	
	private double evaluationTime = 0.0;		// Default value. Should be changed if depending on the context.
	private int lastNumberOfInterations;
	private double lastAccuracy;

	/**
	 * Generate a collection of calibrated curves (discount curves, forward curves)
	 * from a vector of calibration products and a given model.
	 * 
	 * If the model already contains a curve referenced as calibration curve that
	 * curve is replaced by a clone, retaining the given curve information and
	 * adding a new calibration point.
	 * 
	 * If the model does not contain the curve referenced as calibration curve, the
	 * curve will be added to the model. 
	 * 
	 * Use case: You already have a discount curve as part of the model and like
	 * to calibrate an additional curve to an additional set of instruments.
	 * 
	 * @param calibrationSpecs Array of calibration specs.
	 * @param calibrationModel A given model used to value the calibration products.
     * @param evaluationTime Evaluation time applied to the calibration products.
	 * @param calibrationAccuracy Error tolerance of the solver. Set to 0 if you need machine precision.
	 * @throws net.finmath.optimizer.SolverException May be thrown if the solver does not cannot find a solution of the calibration problem. 
	 * @throws CloneNotSupportedException Thrown, when a curve could not be cloned.
	 */
	public CalibratedCurves(CalibrationSpec[] calibrationSpecs, AnalyticModel calibrationModel, double evaluationTime, double calibrationAccuracy) throws SolverException, CloneNotSupportedException {
		if(calibrationModel != null)	model	= calibrationModel.getCloneForParameter(null);
		this.evaluationTime = evaluationTime;

		for(CalibrationSpec calibrationSpec : calibrationSpecs) {
			add(calibrationSpec);
		}

		lastNumberOfInterations = calibrate(calibrationAccuracy);
	}

	/**
	 * Generate a collection of calibrated curves (discount curves, forward curves)
	 * from a vector of calibration products and a given model.
	 * 
	 * If the model already contains a curve referenced as calibration curve that
	 * curve is replaced by a clone, retaining the given curve information and
	 * adding a new calibration point.
	 * 
	 * If the model does not contain the curve referenced as calibration curve, the
	 * curve will be added to the model. 
	 * 
	 * Use case: You already have a discount curve as part of the model and like
	 * to calibrate an additional curve to an additional set of instruments.
	 * 
	 * @param calibrationSpecs Array of calibration specs.
	 * @param calibrationModel A given model used to value the calibration products.
	 * @param calibrationAccuracy Error tolerance of the solver. Set to 0 if you need machine precision.
	 * @throws net.finmath.optimizer.SolverException May be thrown if the solver does not cannot find a solution of the calibration problem. 
	 * @throws CloneNotSupportedException Thrown, when a curve could not be cloned.
	 */
	public CalibratedCurves(CalibrationSpec[] calibrationSpecs, AnalyticModel calibrationModel, double calibrationAccuracy) throws SolverException, CloneNotSupportedException {
		this(calibrationSpecs, calibrationModel, 0.0, calibrationAccuracy);
	}

	/**
	 * Generate a collection of calibrated curves (discount curves, forward curves)
	 * from a vector of calibration products and a given model.
	 * 
	 * If the model already contains a curve referenced as calibration curve that
	 * curve is replaced by a clone, retaining the given curve information and
	 * adding a new calibration point.
	 * 
	 * If the model does not contain the curve referenced as calibration curve, the
	 * curve will be added to the model. 
	 * 
	 * Use case: You already have a discount curve as part of the model and like
	 * to calibrate an additional curve to an additional set of instruments.
	 * 
	 * @param calibrationSpecs Array of calibration specs.
	 * @param calibrationModel A given model used to value the calibration products.
	 * @throws net.finmath.optimizer.SolverException May be thrown if the solver does not cannot find a solution of the calibration problem. 
	 * @throws CloneNotSupportedException Thrown, when a curve could not be cloned.
	 */
	public CalibratedCurves(CalibrationSpec[] calibrationSpecs, AnalyticModel calibrationModel) throws SolverException, CloneNotSupportedException {
		this(calibrationSpecs, calibrationModel, 0.0);
	}

	/**
	 * Generate a collection of calibrated curves (discount curves, forward curves)
	 * from a vector of calibration products.
	 * 
	 * @param calibrationSpecs Array of calibration specs.
	 * @throws net.finmath.optimizer.SolverException May be thrown if the solver does not cannot find a solution of the calibration problem. 
	 * @throws CloneNotSupportedException Thrown, when a curve could not be cloned.
	 */
	public CalibratedCurves(Collection<CalibrationSpec> calibrationSpecs) throws SolverException, CloneNotSupportedException {
		this(calibrationSpecs.toArray(new CalibrationSpec[calibrationSpecs.size()]), null);
	}

	/**
	 * Generate a collection of calibrated curves (discount curves, forward curves)
	 * from a vector of calibration products.
	 * 
	 * @param calibrationSpecs Array of calibration specs.
	 * @throws net.finmath.optimizer.SolverException May be thrown if the solver does not cannot find a solution of the calibration problem. 
	 * @throws CloneNotSupportedException Thrown, when a curve could not be cloned.
	 */
	public CalibratedCurves(CalibrationSpec[] calibrationSpecs) throws SolverException, CloneNotSupportedException {
		this(calibrationSpecs, null, 0.0);
	}
	
	public AnalyticProductInterface getCalibrationProductForSpec(CalibrationSpec calibrationSpec) {
		createDiscountCurve(calibrationSpec.discountCurveReceiverName);
		createDiscountCurve(calibrationSpec.discountCurvePayerName);
		
		String forwardCurveReceiverName = createForwardCurve(calibrationSpec.swapTenorDefinitionReceiver, calibrationSpec.forwardCurveReceiverName);
		String forwardCurvePayerName	= createForwardCurve(calibrationSpec.swapTenorDefinitionPayer, calibrationSpec.forwardCurvePayerName);
		
		ScheduleInterface tenorReceiver = calibrationSpec.swapTenorDefinitionReceiver;
		ScheduleInterface tenorPayer	= calibrationSpec.swapTenorDefinitionPayer;

		AnalyticProductInterface product = null;
		if(calibrationSpec.type.toLowerCase().equals("swap")) {
			product = new Swap(tenorReceiver, forwardCurveReceiverName, calibrationSpec.spreadReceiver, calibrationSpec.discountCurveReceiverName, tenorPayer, forwardCurvePayerName, calibrationSpec.spreadPayer, calibrationSpec.discountCurvePayerName);
		}
		else if(calibrationSpec.type.toLowerCase().equals("swapleg")) {
			product = new SwapLeg(tenorReceiver, forwardCurveReceiverName, calibrationSpec.spreadReceiver, calibrationSpec.discountCurveReceiverName, true);
		}
		else {
			throw new RuntimeException("Product of type " + calibrationSpec.type + " unknown.");
		}

		return product;
	}
	
	/**
	 * Return the calibrated model, i.e., the model maintaining a collection of curves calibrated to the
	 * given calibration specifications.
	 * 
	 * @return The calibrated model.
	 */
	public AnalyticModelInterface getModel() {
		return model;
	}

	/**
	 * Get a curve for a given name.
	 * 
	 * @param name Name of the curve
	 * @return The curve model.
	 */
	public CurveInterface getCurve(String name) {
		return model.getCurve(name);
	}

	/**
	 * Return the number of iterations needed to calibrate the model.
	 * 
	 * @return The number of iterations needed to calibrate the model.
	 */
	public int getLastNumberOfInterations() {
		return lastNumberOfInterations;
	}

	/**
	 * Return the accuracy achieved in the last calibration.
	 * 
	 * @return The accuracy achieved in the last calibration.
	 */
	public double getLastAccuracy() {
		return lastAccuracy;
	}
	
	private int calibrate(double accuracy) throws SolverException {
		Solver solver = new Solver(model, calibrationProducts, evaluationTime, accuracy);
		model = solver.getCalibratedModel(curvesToCalibrate);

		lastAccuracy = solver.getAccuracy();

		return solver.getIterations();
	}

	/**
	 * Add a calibration product to the set of calibration instruments.
	 * 
	 * @param calibrationSpec The spec of the calibration product.
	 */
	private String add(CalibrationSpec calibrationSpec)
	{
		/* 
		 * Add one point to the calibration curve and one new objective function
		 */
		
		// Create calibration product (will also create the curve if necessary)
		calibrationProducts.add(getCalibrationProductForSpec(calibrationSpec));

		// Create parameter to calibrate

		Curve calibrationCurve = null;
		try {
			Curve calibrationCurveOld = (Curve)model.getCurve(calibrationSpec.calibrationCurveName);
			curvesToCalibrate.remove(calibrationCurveOld);
			calibrationCurve = (Curve)(calibrationCurveOld.clone());
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(DiscountCurveInterface.class.isInstance(calibrationCurve)) {
			double paymentTime	= calibrationSpec.swapTenorDefinitionReceiver.getPayment(calibrationSpec.swapTenorDefinitionReceiver.getNumberOfPeriods()-1);

			calibrationCurve.addPoint(calibrationSpec.calibrationTime, 1.0, true);
		}
		else if(ForwardCurveInterface.class.isInstance(calibrationCurve)) {
/*
			double fixingTime	= calibrationSpec.swapTenorDefinitionReceiver.getFixing(calibrationSpec.swapTenorDefinitionReceiver.getNumberOfPeriods()-1);
			double paymentTime	= calibrationSpec.swapTenorDefinitionReceiver.getPayment(calibrationSpec.swapTenorDefinitionReceiver.getNumberOfPeriods()-1);
			if(ForwardCurve.class.isInstance(calibrationCurve) && ((ForwardCurve)calibrationCurve).getInterpolationEntityForward() == InterpolationEntityForward.ZERO) {
				calibrationCurve.addPoint(paymentTime, 0.5, true);
			}
			else {
				calibrationCurve.addPoint(fixingTime, 0.5, true);
			}
*/
			calibrationCurve.addPoint(calibrationSpec.calibrationTime, 0.0, true);
		}
		else {
			calibrationCurve.addPoint(calibrationSpec.calibrationTime, 1.0, true);
		}
		model.setCurve(calibrationCurve);
		curvesToCalibrate.add(calibrationCurve);
	
		return calibrationSpec.type;
	}

	/**
	 * Get a discount curve from the model, if not existing create a discount curve.
	 * 
	 * @param discountCurveName The name of the discount curve to create.
	 * @return The discount factor curve associated with the given name.
	 */
    private DiscountCurveInterface createDiscountCurve(String discountCurveName) {
		DiscountCurveInterface discountCurve	= model.getDiscountCurve(discountCurveName);
		if(discountCurve == null) {
			discountCurve = DiscountCurve.createDiscountCurveFromDiscountFactors(discountCurveName, new double[] { 0.0 }, new double[] { 1.0 });
			model.setCurve(discountCurve);
		}

		return discountCurve;
    }

	/**
	 * Get a forward curve from the model, if not existing create a forward curve.
	 * 
	 * @param swapTenorDefinition The swap tenor associated with the forward curve.
	 * @param forwardCurveName The name of the forward curve to create.
	 * @return The forward curve associated with the given name.
	 */
    private String createForwardCurve(ScheduleInterface swapTenorDefinition, String forwardCurveName) {

		/*
		 * Temporary "hack" - we try to infer index maturity codes from curve name.
		 */
		String indexMaturityCode = null;
		if(forwardCurveName.contains("_12M") || forwardCurveName.contains("-12M") || forwardCurveName.contains(" 12M"))	indexMaturityCode = "12M";
		if(forwardCurveName.contains("_1M")	|| forwardCurveName.contains("-1M")	|| forwardCurveName.contains(" 1M"))	indexMaturityCode = "1M";
		if(forwardCurveName.contains("_6M")	|| forwardCurveName.contains("-6M")	|| forwardCurveName.contains(" 6M"))	indexMaturityCode = "6M";
		if(forwardCurveName.contains("_3M") || forwardCurveName.contains("-3M") || forwardCurveName.contains(" 3M"))	indexMaturityCode = "3M";

		if(forwardCurveName == null || forwardCurveName.isEmpty()) return null;

		// Check if the curves exists, if not create it
		CurveInterface	curve = model.getCurve(forwardCurveName);

		CurveInterface	forwardCurve = null;
		if(curve == null) {
			// Create a new forward curve
			boolean isUseForwardCurve = true;
			if(isUseForwardCurve) {
				curve = new ForwardCurve(forwardCurveName, swapTenorDefinition.getReferenceDate(), indexMaturityCode, ForwardCurve.InterpolationEntityForward.FORWARD, null);
			}
			else {
				// Alternative: Model the forward curve through an underlying discount curve.
				curve = DiscountCurve.createDiscountCurveFromDiscountFactors(forwardCurveName, new double[] { 0.0 }, new double[] { 1.0 });
				model.setCurve(curve);
			}
		}
		
		// Check if the curve is a discount curve, if yes - create a forward curve wrapper.
		if(DiscountCurveInterface.class.isInstance(curve)) {
			/*
			 *  If the specified forward curve exits as a discount curve, we generate a forward curve
			 *  by wrapping the discount curve and calculating the
			 *  forward from discount factors using the formula (df(T)/df(T+Delta T) - 1) / Delta T).
			 *  
			 *  If no index maturity is used, the forward curve is interpreted "single curve", i.e.
			 *  T+Delta T is always the payment.
			 */
			forwardCurve = new ForwardCurveFromDiscountCurve(curve.getName(), swapTenorDefinition.getReferenceDate(), indexMaturityCode);
		}
		else {
			// Use a given forward curve
			forwardCurve = curve;
		}

		model.setCurve(forwardCurve);

		return forwardCurve.getName();
    }
}
