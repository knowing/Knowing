package de.lmu.ifi.dbs.elki.math.linearalgebra.pca;

import java.util.List;

import de.lmu.ifi.dbs.elki.math.linearalgebra.SortedEigenPairs;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ClassListParameter;

/**
 * The <code>CompositeEigenPairFilter</code> can be used to build a chain of
 * eigenpair filters.
 * 
 * @author Elke Achtert
 */
// todo parameter comments
public class CompositeEigenPairFilter implements EigenPairFilter {
  /**
   * OptionID for {@link #FILTERS_PARAM}
   */
  public static final OptionID EIGENPAIR_FILTER_COMPOSITE_LIST = OptionID.getOrCreateOptionID("pca.filter.composite.list", "A comma separated list of the class names of the filters to be used. " + "The specified filters will be applied sequentially in the given order.");

  private final ClassListParameter<EigenPairFilter> FILTERS_PARAM = new ClassListParameter<EigenPairFilter>(EIGENPAIR_FILTER_COMPOSITE_LIST, EigenPairFilter.class);

  /**
   * The filters to be applied.
   */
  private List<EigenPairFilter> filters;

  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public CompositeEigenPairFilter(Parameterization config) {
    super();
    config = config.descend(this);

    if (config.grab(FILTERS_PARAM)) {
      filters = FILTERS_PARAM.instantiateClasses(config);
    }
  }

  /**
   * Filters the specified eigenpairs into strong and weak eigenpairs, where
   * strong eigenpairs having high variances and weak eigenpairs having small
   * variances.
   * 
   * @param eigenPairs the eigenPairs (i.e. the eigenvectors and
   * @return the filtered eigenpairs
   */
  @Override
  public FilteredEigenPairs filter(SortedEigenPairs eigenPairs) {
    FilteredEigenPairs result = null;
    for(EigenPairFilter f : filters) {
      result = f.filter(eigenPairs);
      eigenPairs = new SortedEigenPairs(result.getStrongEigenPairs());
    }
    return result;
  }
}