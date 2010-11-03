package de.lmu.ifi.dbs.elki.utilities.scaling.outlier;

import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.math.MinMax;
import de.lmu.ifi.dbs.elki.result.outlier.OutlierResult;

/**
 * Scaling function to invert values by computing -1 * Math.log(x)
 * 
 * @author Erich Schubert
 */
public class OutlierMinusLogScaling implements OutlierScalingFunction {
  /**
   * Maximum value seen, set by {@link #prepare}
   */
  double max;
  /**
   * Maximum -log value seen, set by {@link #prepare}
   */
  double mlogmax;
  
  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   */
  public OutlierMinusLogScaling() {
    super();
  }

  @Override
  public double getScaled(double value) {
    return - Math.log(value / max) / mlogmax;
  }

  @Override
  public double getMin() {
    return 0.0;
  }
  
  @Override
  public double getMax() {
    return 1.0;
  }

  @Override
  public void prepare(Database<?> db, OutlierResult or) {
    MinMax<Double> mm = new MinMax<Double>();
    for(DBID id : db) {
      double val = or.getScores().getValueFor(id);
      mm.put(val);
    }
    max = mm.getMax();
    mlogmax = - Math.log(mm.getMin() / max);
  }
}