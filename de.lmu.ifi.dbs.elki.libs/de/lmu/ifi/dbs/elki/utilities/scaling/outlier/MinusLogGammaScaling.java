package de.lmu.ifi.dbs.elki.utilities.scaling.outlier;

import org.apache.commons.math.MathException;
import org.apache.commons.math.special.Gamma;

import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.logging.LoggingUtil;
import de.lmu.ifi.dbs.elki.math.MeanVariance;
import de.lmu.ifi.dbs.elki.math.MinMax;
import de.lmu.ifi.dbs.elki.result.outlier.OutlierResult;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.EmptyParameterization;

/**
 * Scaling that can map arbitrary values to a probability in the range of [0:1],
 * by assuming a Gamma distribution on the data and evaluating the Gamma CDF.
 * 
 * @author Erich Schubert
 * 
 */
public class MinusLogGammaScaling extends OutlierGammaScaling {
  /**
   * Maximum value seen
   */
  double max;
  
  /**
   * Minimum value (after log step, so maximum again)
   */
  double mlogmax;
  
  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   */
  public MinusLogGammaScaling() {
    super(new EmptyParameterization());
    // We don't support the normalize flag of OutlierGammaScaling.
    // By using EmptyParameterization, it will not be found.
    // We should be handling errors, but this isn't worth the effort.
  }

  @Override
  protected double preScale(double score) {
    return - Math.log(score / max) / mlogmax;
  }

  @Override
  public void prepare(Database<?> db, OutlierResult or) {
    meta = or.getOutlierMeta();
    // Determine Minimum and Maximum.
    MinMax<Double> mm = new MinMax<Double>();
    for(DBID id : db) {
      double score = or.getScores().getValueFor(id);
      mm.put(score);
    }
    max = mm.getMax();
    mlogmax = - Math.log(mm.getMin() / max);
    // with the prescaling, do Gamma Scaling.
    MeanVariance mv = new MeanVariance();
    for(DBID id : db) {
      double score = or.getScores().getValueFor(id);
      score = preScale(score);
      if(!Double.isNaN(score) && !Double.isInfinite(score)) {
        mv.put(score);
      }
    }
    final double mean = mv.getMean();
    final double var = mv.getVariance();
    k = (mean*mean) / var;
    theta = var / mean;
    try {
      atmean = Gamma.regularizedGammaP(k, mean/theta);
    }
    catch(MathException e) {
      LoggingUtil.exception(e);
    }
    //logger.warning("Mean:"+mean+" Var:"+var+" Theta: "+theta+" k: "+k+" valatmean"+atmean);
  }
}