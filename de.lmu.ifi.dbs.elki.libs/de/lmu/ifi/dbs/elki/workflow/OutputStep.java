package de.lmu.ifi.dbs.elki.workflow;

import java.util.ArrayList;
import java.util.List;

import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.normalization.Normalization;
import de.lmu.ifi.dbs.elki.result.AnyResult;
import de.lmu.ifi.dbs.elki.result.Result;
import de.lmu.ifi.dbs.elki.result.ResultHandler;
import de.lmu.ifi.dbs.elki.result.ResultWriter;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ObjectListParameter;

/**
 * The "output" step, where data is analyzed.
 * 
 * @author Erich Schubert
 *
 * @param <O> database object type
 */
public class OutputStep<O extends DatabaseObject> implements Parameterizable {
  /**
   * Output handler.
   */
  private List<ResultHandler<O, AnyResult>> resulthandlers = null;
  
  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public OutputStep(Parameterization config) {
    super();
    config = config.descend(this);
    // result handlers
    final ObjectListParameter<ResultHandler<O, AnyResult>> RESULT_HANDLER_PARAM = new ObjectListParameter<ResultHandler<O, AnyResult>>(OptionID.RESULT_HANDLER, ResultHandler.class);
    ArrayList<Class<? extends ResultHandler<O, AnyResult>>> defaultHandlers = new ArrayList<Class<? extends ResultHandler<O, AnyResult>>>(1);
    final Class<ResultHandler<O, AnyResult>> rwcls = ClassGenericsUtil.uglyCrossCast(ResultWriter.class, ResultHandler.class);
    defaultHandlers.add(rwcls);
    RESULT_HANDLER_PARAM.setDefaultValue(defaultHandlers);
    if(config.grab(RESULT_HANDLER_PARAM)) {
      resulthandlers = RESULT_HANDLER_PARAM.instantiateClasses(config);
    }
  }
  
  public void runResultHandlers(Result result, Database<O> db, boolean normalizationUndo, Normalization<O> normalization) {
    // Run result handlers
    for(ResultHandler<O, AnyResult> resulthandler : resulthandlers) {
      if(normalizationUndo) {
        resulthandler.setNormalization(normalization);
      }
      resulthandler.processResult(db, result);
    }
  }

}