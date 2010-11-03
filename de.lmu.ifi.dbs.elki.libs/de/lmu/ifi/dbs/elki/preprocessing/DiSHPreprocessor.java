package de.lmu.ifi.dbs.elki.preprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.lmu.ifi.dbs.elki.algorithm.APRIORI;
import de.lmu.ifi.dbs.elki.data.Bit;
import de.lmu.ifi.dbs.elki.data.BitVector;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.DatabaseObjectMetadata;
import de.lmu.ifi.dbs.elki.database.DistanceResultPair;
import de.lmu.ifi.dbs.elki.database.SequentialDatabase;
import de.lmu.ifi.dbs.elki.database.datastore.DataStoreFactory;
import de.lmu.ifi.dbs.elki.database.datastore.DataStoreUtil;
import de.lmu.ifi.dbs.elki.database.datastore.WritableDataStore;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.ids.ModifiableDBIDs;
import de.lmu.ifi.dbs.elki.database.query.DistanceQuery;
import de.lmu.ifi.dbs.elki.database.query.PrimitiveDistanceQuery;
import de.lmu.ifi.dbs.elki.distance.distancefunction.subspace.DimensionSelectingDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancevalue.DoubleDistance;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.logging.progress.FiniteProgress;
import de.lmu.ifi.dbs.elki.result.AprioriResult;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.FormatUtil;
import de.lmu.ifi.dbs.elki.utilities.documentation.Description;
import de.lmu.ifi.dbs.elki.utilities.exceptions.ExceptionMessages;
import de.lmu.ifi.dbs.elki.utilities.exceptions.UnableToComplyException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.ParameterException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.WrongParameterValueException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.EqualStringConstraint;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.GreaterConstraint;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.DoubleListParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.IntParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.StringParameter;
import de.lmu.ifi.dbs.elki.utilities.pairs.Pair;

/**
 * Preprocessor for DiSH preference vector assignment to objects of a certain
 * database.
 * 
 * @author Elke Achtert
 */
@Description("Computes the preference vector of objects of a certain database according to the DiSH algorithm.")
public class DiSHPreprocessor implements PreferenceVectorPreprocessor<NumberVector<?,?>>, Parameterizable {
  /**
   * Logger to use
   */
  protected static final Logging logger = Logging.getLogger(DiSHPreprocessor.class);

  /**
   * Available strategies for determination of the preference vector.
   */
  public enum Strategy {
    /**
     * Apriori strategy
     */
    APRIORI,
    /**
     * Max intersection strategy
     */
    MAX_INTERSECTION
  }

  /**
   * The default value for epsilon.
   */
  public static final DoubleDistance DEFAULT_EPSILON = new DoubleDistance(0.001);

  /**
   * OptionID for {@link #EPSILON_PARAM}
   */
  public static final OptionID EPSILON_ID = OptionID.getOrCreateOptionID("dish.epsilon", "A comma separated list of positive doubles specifying the " + "maximum radius of the neighborhood to be " + "considered in each dimension for determination of " + "the preference vector " + "(default is " + DEFAULT_EPSILON + " in each dimension). " + "If only one value is specified, this value " + "will be used for each dimension.");

  /**
   * Option name for {@link DiSHPreprocessor#MINPTS_ID}.
   */
  public static final String MINPTS_P = "dish.minpts";

  /**
   * Description for the determination of the preference vector.
   */
  private static final String CONDITION = "The value of the preference vector in dimension d_i is set to 1 " + "if the epsilon neighborhood contains more than " + MINPTS_P + " points and the following condition holds: " + "for all dimensions d_j: " + "|neighbors(d_i) intersection neighbors(d_j)| >= " + MINPTS_P + ".";

  /**
   * OptionID for {@link #MINPTS_PARAM}
   */
  public static final OptionID MINPTS_ID = OptionID.getOrCreateOptionID(MINPTS_P, "Positive threshold for minumum numbers of points in the epsilon-" + "neighborhood of a point. " + CONDITION);

  /**
   * Default strategy.
   */
  public static Strategy DEFAULT_STRATEGY = Strategy.MAX_INTERSECTION;

  /**
   * OptionID for {@link #STRATEGY_PARAM}
   */
  public static final OptionID STRATEGY_ID = OptionID.getOrCreateOptionID("dish.strategy", "The strategy for determination of the preference vector, " + "available strategies are: [" + Strategy.APRIORI + "| " + Strategy.MAX_INTERSECTION + "]" + "(default is " + DEFAULT_STRATEGY + ")");

  /**
   * A comma separated list of positive doubles specifying the maximum radius of
   * the neighborhood to be considered in each dimension for determination of
   * the preference vector (default is {@link #DEFAULT_EPSILON} in each
   * dimension). If only one value is specified, this value will be used for
   * each dimension.
   * 
   * <p>
   * Key: {@code -dish.epsilon}
   * </p>
   * <p>
   * Default value: {@link #DEFAULT_EPSILON}
   * </p>
   */
  protected final DoubleListParameter EPSILON_PARAM = new DoubleListParameter(EPSILON_ID, true);

  /**
   * The epsilon value for each dimension;
   */
  protected DoubleDistance[] epsilon;

  /**
   * Positive threshold for minimum numbers of points in the
   * epsilon-neighborhood of a point, must satisfy following {@link #CONDITION}.
   * 
   * <p>
   * Key: {@code -dish.minpts}
   * </p>
   */
  protected final IntParameter MINPTS_PARAM = new IntParameter(MINPTS_ID, new GreaterConstraint(0));

  /**
   * Threshold for minimum number of points in the neighborhood.
   */
  protected int minpts;

  /**
   * The strategy for determination of the preference vector, available
   * strategies are: {@link Strategy#APRIORI } and
   * {@link Strategy#MAX_INTERSECTION}.
   * 
   * <p>
   * Key: {@code -dish.strategy}
   * </p>
   * <p>
   * Default value: {@link #DEFAULT_STRATEGY}
   * </p>
   */
  private final StringParameter STRATEGY_PARAM = new StringParameter(STRATEGY_ID, new EqualStringConstraint(new String[] { Strategy.APRIORI.toString(), Strategy.MAX_INTERSECTION.toString() }), DEFAULT_STRATEGY.toString());

  /**
   * The strategy to determine the preference vector.
   */
  protected Strategy strategy;

  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public DiSHPreprocessor(Parameterization config) {
    super();
    config = config.descend(this);
    // parameter min points
    if(config.grab(MINPTS_PARAM)) {
      minpts = MINPTS_PARAM.getValue();
    }

    // parameter epsilon
    // todo: constraint auf positive werte
    List<Double> defaultEps = new ArrayList<Double>();
    defaultEps.add(DEFAULT_EPSILON.doubleValue());
    EPSILON_PARAM.setDefaultValue(defaultEps);
    if(config.grab(EPSILON_PARAM)) {
      List<Double> eps_list = EPSILON_PARAM.getValue();
      epsilon = new DoubleDistance[eps_list.size()];

      for(int d = 0; d < eps_list.size(); d++) {
        epsilon[d] = new DoubleDistance(eps_list.get(d));
        if(epsilon[d].doubleValue() < 0) {
          config.reportError(new WrongParameterValueException(EPSILON_PARAM, eps_list.toString()));
        }
      }
    }

    // parameter strategy
    if(config.grab(STRATEGY_PARAM)) {
      String strategyString = STRATEGY_PARAM.getValue();
      if(strategyString.equals(Strategy.APRIORI.toString())) {
        strategy = Strategy.APRIORI;
      }
      else if(strategyString.equals(Strategy.MAX_INTERSECTION.toString())) {
        strategy = Strategy.MAX_INTERSECTION;
      }
      else {
        config.reportError(new WrongParameterValueException(STRATEGY_PARAM, strategyString));
      }
    }
  }

  @Override
  public <V extends NumberVector<?,?>> Instance<V> instantiate(Database<V> database) {
    return new Instance<V>(database);
  }

  /**
   * The actual preprocessor instance.
   * 
   * @author Erich Schubert
   * 
   * @param <V> The actual data type
   */
  public class Instance<V extends NumberVector<?,?>> implements PreferenceVectorPreprocessor.Instance<V> {
    /**
     * Data storage
     */
    protected WritableDataStore<BitSet> preferenceVectors;

    /**
     * Constructor
     * 
     * @param database Database to preprocess
     */
    public Instance(Database<V> database) {
      if(database == null || database.size() == 0) {
        throw new IllegalArgumentException(ExceptionMessages.DATABASE_EMPTY);
      }

      preferenceVectors = DataStoreUtil.makeStorage(database.getIDs(), DataStoreFactory.HINT_HOT | DataStoreFactory.HINT_TEMP, BitSet.class);

      if(logger.isDebugging()) {
        StringBuffer msg = new StringBuffer();
        msg.append("\n eps ").append(Arrays.asList(epsilon));
        msg.append("\n minpts ").append(minpts);
        msg.append("\n strategy ").append(strategy);
        logger.debugFine(msg.toString());
      }

      try {
        long start = System.currentTimeMillis();
        FiniteProgress progress = logger.isVerbose() ? new FiniteProgress("Preprocessing preference vector", database.size(), logger) : null;

        // only one epsilon value specified
        int dim = database.dimensionality();
        if(epsilon.length == 1 && dim != 1) {
          DoubleDistance eps = epsilon[0];
          epsilon = new DoubleDistance[dim];
          Arrays.fill(epsilon, eps);
        }

        // epsilons as string
        String[] epsString = new String[dim];
        for(int d = 0; d < dim; d++) {
          epsString[d] = epsilon[d].toString();
        }
        DistanceQuery<V, DoubleDistance>[] distanceFunctions = initDistanceFunctions(database, dim);

        for(Iterator<DBID> it = database.iterator(); it.hasNext();) {
          StringBuffer msg = new StringBuffer();
          final DBID id = it.next();

          if(logger.isDebugging()) {
            msg.append("\nid = ").append(id);
            // msg.append(" ").append(database.get(id));
            msg.append(" ").append(database.getObjectLabel(id));
          }

          // determine neighbors in each dimension
          ModifiableDBIDs[] allNeighbors = ClassGenericsUtil.newArrayOfNull(dim, ModifiableDBIDs.class);
          for(int d = 0; d < dim; d++) {
            List<DistanceResultPair<DoubleDistance>> qrList = database.rangeQuery(id, epsString[d], distanceFunctions[d]);
            allNeighbors[d] = DBIDUtil.newHashSet(qrList.size());
            for(DistanceResultPair<DoubleDistance> qr : qrList) {
              allNeighbors[d].add(qr.getID());
            }
          }

          if(logger.isDebugging()) {
            for(int d = 0; d < dim; d++) {
              msg.append("\n neighbors [").append(d).append("]");
              msg.append(" (").append(allNeighbors[d].size()).append(") = ");
              msg.append(allNeighbors[d]);
            }
          }

          BitSet preferenceVector = determinePreferenceVector(database, allNeighbors, msg);
          preferenceVectors.put(id, preferenceVector);

          if(logger.isDebugging()) {
            logger.debugFine(msg.toString());
          }

          if(progress != null) {
            progress.incrementProcessed(logger);
          }
        }
        if(progress != null) {
          progress.ensureCompleted(logger);
        }

        long end = System.currentTimeMillis();
        // TODO: re-add timing code!
        if(logger.isVerbose()) {
          long elapsedTime = end - start;
          logger.verbose(this.getClass().getName() + " runtime: " + elapsedTime + " milliseconds.");
        }
      }
      catch(ParameterException e) {
        throw new IllegalStateException(e);
      }
      catch(UnableToComplyException e) {
        throw new IllegalStateException(e);
      }

    }

    /**
     * Determines the preference vector according to the specified neighbor ids.
     * 
     * @param database the database storing the objects
     * @param neighborIDs the list of ids of the neighbors in each dimension
     * @param msg a string buffer for debug messages
     * @return the preference vector
     * @throws de.lmu.ifi.dbs.elki.utilities.optionhandling.ParameterException
     * 
     * @throws de.lmu.ifi.dbs.elki.utilities.exceptions.UnableToComplyException
     * 
     */
    private BitSet determinePreferenceVector(Database<V> database, ModifiableDBIDs[] neighborIDs, StringBuffer msg) throws ParameterException, UnableToComplyException {
      if(strategy.equals(Strategy.APRIORI)) {
        return determinePreferenceVectorByApriori(database, neighborIDs, msg);
      }
      else if(strategy.equals(Strategy.MAX_INTERSECTION)) {
        return determinePreferenceVectorByMaxIntersection(neighborIDs, msg);
      }
      else {
        throw new IllegalStateException("Should never happen!");
      }
    }

    /**
     * Determines the preference vector with the apriori strategy.
     * 
     * @param database the database storing the objects
     * @param neighborIDs the list of ids of the neighbors in each dimension
     * @param msg a string buffer for debug messages
     * @return the preference vector
     * @throws de.lmu.ifi.dbs.elki.utilities.optionhandling.ParameterException
     * 
     * @throws de.lmu.ifi.dbs.elki.utilities.exceptions.UnableToComplyException
     * 
     */
    private BitSet determinePreferenceVectorByApriori(Database<V> database, ModifiableDBIDs[] neighborIDs, StringBuffer msg) throws ParameterException, UnableToComplyException {
      int dimensionality = neighborIDs.length;

      APRIORI apriori = new APRIORI(minpts);

      // database for apriori
      Database<BitVector> apriori_db = new SequentialDatabase<BitVector>();
      for(Iterator<DBID> it = database.iterator(); it.hasNext();) {
        DBID id = it.next();
        Bit[] bits = new Bit[dimensionality];
        boolean allFalse = true;
        for(int d = 0; d < dimensionality; d++) {
          if(neighborIDs[d].contains(id)) {
            bits[d] = new Bit(true);
            allFalse = false;
          }
          else {
            bits[d] = new Bit(false);
          }
        }
        if(!allFalse) {
          Pair<BitVector, DatabaseObjectMetadata> oaa = new Pair<BitVector, DatabaseObjectMetadata>(new BitVector(bits), null);
          apriori_db.insert(oaa);
        }
      }
      AprioriResult aprioriResult = apriori.run(apriori_db);

      // result of apriori
      List<BitSet> frequentItemsets = aprioriResult.getSolution();
      Map<BitSet, Integer> supports = aprioriResult.getSupports();
      if(logger.isDebugging()) {
        msg.append("\n Frequent itemsets: " + frequentItemsets);
        msg.append("\n All supports: " + supports);
      }
      int maxSupport = 0;
      int maxCardinality = 0;
      BitSet preferenceVector = new BitSet();
      for(BitSet bitSet : frequentItemsets) {
        int cardinality = bitSet.cardinality();
        if((maxCardinality < cardinality) || (maxCardinality == cardinality && maxSupport == supports.get(bitSet))) {
          preferenceVector = bitSet;
          maxCardinality = cardinality;
          maxSupport = supports.get(bitSet);
        }
      }

      if(logger.isDebugging()) {
        msg.append("\n preference ");
        msg.append(FormatUtil.format(dimensionality, preferenceVector));
        msg.append("\n");
        logger.debugFine(msg.toString());
      }

      return preferenceVector;
    }

    /**
     * Determines the preference vector with the max intersection strategy.
     * 
     * @param neighborIDs the list of ids of the neighbors in each dimension
     * @param msg a string buffer for debug messages
     * @return the preference vector
     */
    private BitSet determinePreferenceVectorByMaxIntersection(ModifiableDBIDs[] neighborIDs, StringBuffer msg) {
      int dimensionality = neighborIDs.length;
      BitSet preferenceVector = new BitSet(dimensionality);

      Map<Integer, ModifiableDBIDs> candidates = new HashMap<Integer, ModifiableDBIDs>(dimensionality);
      for(int i = 0; i < dimensionality; i++) {
        ModifiableDBIDs s_i = neighborIDs[i];
        if(s_i.size() > minpts) {
          candidates.put(i, s_i);
        }
      }
      if(logger.isDebugging()) {
        msg.append("\n candidates " + candidates.keySet());
      }

      if(!candidates.isEmpty()) {
        int i = max(candidates);
        ModifiableDBIDs intersection = candidates.remove(i);
        preferenceVector.set(i);
        while(!candidates.isEmpty()) {
          ModifiableDBIDs newIntersection = DBIDUtil.newHashSet();
          i = maxIntersection(candidates, intersection, newIntersection);
          ModifiableDBIDs s_i = candidates.remove(i);
          // TODO: aren't we re-computing the same intersection here?
          newIntersection = DBIDUtil.intersection(intersection, s_i);
          intersection = newIntersection;

          if(intersection.size() < minpts) {
            break;
          }
          else {
            preferenceVector.set(i);
          }
        }
      }

      if(logger.isDebugging()) {
        msg.append("\n preference ");
        msg.append(FormatUtil.format(dimensionality, preferenceVector));
        msg.append("\n");
        logger.debug(msg.toString());
      }

      return preferenceVector;
    }

    /**
     * Returns the set with the maximum size contained in the specified map.
     * 
     * @param candidates the map containing the sets
     * @return the set with the maximum size
     */
    private int max(Map<Integer, ModifiableDBIDs> candidates) {
      DBIDs maxSet = null;
      Integer maxDim = null;
      for(Integer nextDim : candidates.keySet()) {
        DBIDs nextSet = candidates.get(nextDim);
        if(maxSet == null || maxSet.size() < nextSet.size()) {
          maxSet = nextSet;
          maxDim = nextDim;
        }
      }

      return maxDim;
    }

    /**
     * Returns the index of the set having the maximum intersection set with the
     * specified set contained in the specified map.
     * 
     * @param candidates the map containing the sets
     * @param set the set to intersect with
     * @param result the set to put the result in
     * @return the set with the maximum size
     */
    private int maxIntersection(Map<Integer, ModifiableDBIDs> candidates, DBIDs set, ModifiableDBIDs result) {
      Integer maxDim = null;
      for(Integer nextDim : candidates.keySet()) {
        DBIDs nextSet = candidates.get(nextDim);
        ModifiableDBIDs nextIntersection = DBIDUtil.intersection(set, nextSet);
        if(result.size() < nextIntersection.size()) {
          result = nextIntersection;
          maxDim = nextDim;
        }
      }

      return maxDim;
    }

    /**
     * Initializes the dimension selecting distancefunctions to determine the
     * preference vectors.
     * 
     * @param database the database storing the objects
     * @param dimensionality the dimensionality of the objects
     * @return the dimension selecting distancefunctions to determine the
     *         preference vectors
     * @throws ParameterException
     */
    private DistanceQuery<V, DoubleDistance>[] initDistanceFunctions(Database<V> database, int dimensionality) throws ParameterException {
      Class<DistanceQuery<V, DoubleDistance>> dfuncls = ClassGenericsUtil.uglyCastIntoSubclass(DistanceQuery.class);
      DistanceQuery<V, DoubleDistance>[] distanceFunctions = ClassGenericsUtil.newArrayOfNull(dimensionality, dfuncls);
      for(int d = 0; d < dimensionality; d++) {
        ListParameterization parameters = new ListParameterization();
        parameters.addParameter(DimensionSelectingDistanceFunction.DIM_ID, Integer.toString(d + 1));
        distanceFunctions[d] = new PrimitiveDistanceQuery<V, DoubleDistance>(database, new DimensionSelectingDistanceFunction(parameters));
        for(ParameterException e : parameters.getErrors()) {
          logger.warning("Error in internal parameterization: " + e.getMessage());
        }
      }
      return distanceFunctions;
    }

    @Override
    public BitSet get(DBID id) {
      return preferenceVectors.get(id);
    }
  }

  /**
   * Returns the value of the epsilon parameter.
   * 
   * @return the value of the epsilon parameter
   */
  public DoubleDistance[] getEpsilon() {
    return epsilon;
  }

  /**
   * Returns minpts.
   * 
   * @return minpts
   */
  public int getMinpts() {
    return minpts;
  }
}