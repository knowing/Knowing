package de.lmu.ifi.dbs.elki.normalization;

import java.util.List;

import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.database.DatabaseObjectMetadata;
import de.lmu.ifi.dbs.elki.math.linearalgebra.LinearEquationSystem;
import de.lmu.ifi.dbs.elki.utilities.documentation.Description;
import de.lmu.ifi.dbs.elki.utilities.documentation.Title;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable;
import de.lmu.ifi.dbs.elki.utilities.pairs.Pair;

/**
 * Dummy normalization that does nothing. This class is used at normalization of
 * multi-represented objects if one representation needs no normalization.
 * 
 * @author Elke Achtert
 * @param <O> object type
 */
@Title("Dummy normalization that does nothing")
@Description("This class is used at normalization of multi-represented objects if one representation needs no normalization.")
public class DummyNormalization<O extends DatabaseObject> implements Normalization<O>, Parameterizable {
  /**
   * @return the specified objectAndAssociationsList
   */
  @Override
  public List<Pair<O, DatabaseObjectMetadata>> normalizeObjects(List<Pair<O, DatabaseObjectMetadata>> objectAndAssociationsList) {
    return objectAndAssociationsList;
  }

  /**
   * @return the specified featureVectors
   */
  @Override
  public List<O> normalize(List<O> featureVectors) {
    return featureVectors;
  }

  /**
   * @return the specified featureVectors
   */
  @Override
  public List<O> restore(List<O> featureVectors) {
    return featureVectors;
  }

  /**
   * @return the specified featureVector
   */
  @Override
  public O restore(O featureVector) {
    return featureVector;
  }

  /**
   * @return the specified linear equation system
   */
  @Override
  public LinearEquationSystem transform(LinearEquationSystem linearEquationSystem) {
    return linearEquationSystem;
  }

  @Override
  public String toString(String pre) {
    return pre + toString();
  }

  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object
   */
  @Override
  public String toString() {
    return this.getClass().getName();
  }
}
