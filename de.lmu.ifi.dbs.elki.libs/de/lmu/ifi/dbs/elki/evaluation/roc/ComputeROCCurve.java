package de.lmu.ifi.dbs.elki.evaluation.roc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.database.AssociationID;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.ids.ArrayModifiableDBIDs;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.evaluation.Evaluator;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.normalization.Normalization;
import de.lmu.ifi.dbs.elki.result.CollectionResult;
import de.lmu.ifi.dbs.elki.result.IterableResult;
import de.lmu.ifi.dbs.elki.result.Result;
import de.lmu.ifi.dbs.elki.result.OrderingResult;
import de.lmu.ifi.dbs.elki.result.ResultUtil;
import de.lmu.ifi.dbs.elki.result.outlier.OutlierResult;
import de.lmu.ifi.dbs.elki.utilities.DatabaseUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.PatternParameter;
import de.lmu.ifi.dbs.elki.utilities.pairs.DoubleDoublePair;

/**
 * Compute a ROC curve to evaluate a ranking algorithm and compute the
 * corresponding ROCAUC value.
 * 
 * The parameter {@code -rocauc.positive} specifies the class label of
 * "positive" hits.
 * 
 * The nested algorithm {@code -algorithm} will be run, the result will be
 * searched for an iterable or ordering result, which then is compared with the
 * clustering obtained via the given class label.
 * 
 * @author Erich Schubert
 * 
 * @param <O> Database object type
 */
// TODO: maybe add a way to process clustering results as well?
public class ComputeROCCurve<O extends DatabaseObject> implements Evaluator<O> {
  /**
   * The logger.
   */
  static final Logging logger = Logging.getLogger(ComputeROCCurve.class);
  
  /**
   * OptionID for {@link #POSITIVE_CLASS_NAME_PARAM}
   */
  public static final OptionID POSITIVE_CLASS_NAME_ID = OptionID.getOrCreateOptionID("rocauc.positive", "Class label for the 'positive' class.");

  /**
   * The pattern to identify positive classes.
   * 
   * <p>
   * Key: {@code -rocauc.positive}
   * </p>
   */
  private final PatternParameter POSITIVE_CLASS_NAME_PARAM = new PatternParameter(POSITIVE_CLASS_NAME_ID);

  /**
   * Stores the "positive" class.
   */
  private Pattern positive_class_name;

  /**
   * The association id to associate the ROC Area-under-Curve.
   */
  public static final AssociationID<Double> ROC_AUC = AssociationID.getOrCreateAssociationID("ROC AUC", Double.class);

  /**
   * Constructor
   * 
   * @param config Parameters
   */
  public ComputeROCCurve(Parameterization config) {
    super();
    config = config.descend(this);
    if(config.grab(POSITIVE_CLASS_NAME_PARAM)) {
      positive_class_name = POSITIVE_CLASS_NAME_PARAM.getValue();
    }
  }

  private ROCResult computeROCResult(Database<O> database, DBIDs positiveids, Iterator<DBID> iter) {
    ArrayModifiableDBIDs order = DBIDUtil.newArray(database.size());
    while(iter.hasNext()) {
      Object o = iter.next();
      if(!(o instanceof DBID)) {
        throw new IllegalStateException("Iterable result contained non-DBID - result didn't satisfy requirements");
      }
      else {
        order.add((DBID) o);
      }
    }
    if(order.size() != database.size()) {
      throw new IllegalStateException("Iterable result doesn't match database size - incomplete ordering?");
    }
    List<DoubleDoublePair> roccurve = ROC.materializeROC(database.size(), positiveids, new ROC.SimpleAdapter(order.iterator()));
    double rocauc = ROC.computeAUC(roccurve);
    if (logger.isVerbose()) {
      logger.verbose("ROCAUC: "+rocauc);
    }

    List<String> header = new ArrayList<String>(1);
    header.add(ROC_AUC.getLabel() + ": " + rocauc);
    final ROCResult rocresult = new ROCResult(roccurve, header);
    
    return rocresult;
  }

  /**
   * Wrap the uncheckable cast with the manual check.
   * 
   * @param ir Interable result
   * @return Iterator if Integer iterable, null otherwise.
   */
  @SuppressWarnings("unchecked")
  private Iterator<DBID> getDBIDIterator(IterableResult<?> ir) {
    Iterator<?> testit = ir.iterator();
    if(testit.hasNext() && (testit.next() instanceof DBID)) {
      // note: we DO want a fresh iterator here!
      return (Iterator<DBID>) ir.iterator();
    }
    return null;
  }

  @Override
  public void processResult(Database<O> db, Result result) {
    // Prepare
    DBIDs positiveids = DatabaseUtil.getObjectsByLabelMatch(db, positive_class_name);

    boolean nonefound = true;
    List<OutlierResult> oresults = ResultUtil.getOutlierResults(result);
    List<IterableResult<?>> iterables = ResultUtil.getIterableResults(result);
    List<OrderingResult> orderings = ResultUtil.getOrderingResults(result);
    // Outlier results are the main use case.
    for (OutlierResult o : oresults) {
      final OrderingResult or = o.getOrdering();
      o.addDerivedResult(computeROCResult(db, positiveids, or.iter(db.getIDs())));
      // Process them only once.
      orderings.remove(or);
      nonefound = false;
    }
    
    // try iterable results first
    // FIXME: find the appropriate place to call addDerivedResult
    for(IterableResult<?> ir : iterables) {
      Iterator<DBID> iter = getDBIDIterator(ir);
      if (iter != null) {
        result.addDerivedResult(computeROCResult(db, positiveids, iter));
        nonefound = false;
      }
    }
    // FIXME: find appropriate place to add the derived result
    // otherwise apply an ordering to the database IDs.
    for(OrderingResult or : orderings) {
      Iterator<DBID> iter = or.iter(db.getIDs());
      result.addDerivedResult(computeROCResult(db, positiveids, iter));
      nonefound = false;
    }
    
    if (nonefound) {
      logger.warning("No results found to process with ROC curve analyzer. Got "+iterables.size()+" iterables, "+orderings.size()+" orderings.");
    }
  }

  @Override
  public void setNormalization(@SuppressWarnings("unused") Normalization<O> normalization) {
    // Normalizations are ignored
  }

  /**
   * Result object for ROC curves.
   * 
   * @author Erich Schubert
   */
  public static class ROCResult extends CollectionResult<DoubleDoublePair> {
    /**
     * Constructor.
     * 
     * @param col roc curve
     * @param header header
     */
    public ROCResult(Collection<DoubleDoublePair> col, Collection<String> header) {
      super("ROC Curve", "roc", col, header);
    }
  }
}