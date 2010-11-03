package de.lmu.ifi.dbs.elki.visualization.visualizers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.lmu.ifi.dbs.elki.algorithm.AbstractDistanceBasedAlgorithm;
import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.connection.FileBasedDatabaseConnection;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.result.Result;
import de.lmu.ifi.dbs.elki.result.ResultUtil;
import de.lmu.ifi.dbs.elki.result.SettingsResult;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.InspectionUtil;
import de.lmu.ifi.dbs.elki.utilities.exceptions.AbortException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.WrongParameterValueException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.MergedParameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.Parameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.StringParameter;
import de.lmu.ifi.dbs.elki.utilities.pairs.Pair;
import de.lmu.ifi.dbs.elki.visualization.style.PropertiesBasedStyleLibrary;
import de.lmu.ifi.dbs.elki.visualization.style.StyleLibrary;
import de.lmu.ifi.dbs.elki.visualization.visualizers.adapter.AlgorithmAdapter;

/**
 * Utility class to determine the visualizers for a result class.
 * 
 * @author Erich Schubert
 * @author Remigius Wojdanowski
 */
public class VisualizersForResult<O extends DatabaseObject> implements Parameterizable {
  /**
   * Get a logger for this class.
   */
  protected final static Logging logger = Logging.getLogger(VisualizersForResult.class);

  /**
   * Option ID for the style properties to use, {@link #STYLELIB_PARAM}
   */
  public final static OptionID STYLELIB_ID = OptionID.getOrCreateOptionID("visualizer.stylesheet", "Style properties file to use");

  /**
   * Parameter to get the style properties file.
   * 
   * <p>
   * Key: -visualizer.stylesheet
   * 
   * Default: default properties file
   * </p>
   */
  private StringParameter STYLELIB_PARAM = new StringParameter(STYLELIB_ID, PropertiesBasedStyleLibrary.DEFAULT_SCHEME_FILENAME);

  /**
   * Style library to use.
   */
  private StyleLibrary stylelib;

  /**
   * (Result-to-visualization) Adapters
   */
  private Collection<AlgorithmAdapter<O>> adapters;

  /**
   * Visualizer instances.
   */
  private VisualizerTree<O> visualizers;

  /**
   * Visualizer context
   */
  private VisualizerContext<O> context;

  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public VisualizersForResult(Parameterization config) {
    super();
    config = config.descend(this);
    if(config.grab(STYLELIB_PARAM)) {
      String filename = STYLELIB_PARAM.getValue();
      try {
        stylelib = new PropertiesBasedStyleLibrary(filename, "Command line style");
      }
      catch(AbortException e) {
        config.reportError(new WrongParameterValueException(STYLELIB_PARAM, filename, e));
      }
    }
    MergedParameterization merged = new MergedParameterization(config);
    this.adapters = collectAlgorithmAdapters(merged);
  }

  /**
   * Process a particular result.
   * 
   * @param db Database context
   * @param result Result
   */
  public void processResult(Database<O> db, Result result) {
    context = new VisualizerContext<O>(db, result);
    visualizers = new VisualizerTree<O>(context);
    context.put(VisualizerContext.STYLE_LIBRARY, stylelib);
    context.put(VisualizerContext.VISUALIZER_LIST, visualizers);
    
    /*
    visualizers.register(new VisualizerGroup(Visualizer.GROUP_TOOLS), null);
    visualizers.register(new VisualizerGroup(Visualizer.GROUP_RAW_DATA), null);
    visualizers.register(new VisualizerGroup(Visualizer.GROUP_CLUSTERING), null);
    visualizers.register(new VisualizerGroup(Visualizer.GROUP_METADATA), null);
    */

    // Collect all visualizers.
    for(AlgorithmAdapter<O> a : adapters) {
      try {
        if (a.canVisualize(context)) {
          a.addVisualizers(context, visualizers);
        }
      } catch (RuntimeException e) {
        logger.warning("AlgorithmAdapter failed:", e);
      } catch (Exception e) {
        logger.warning("AlgorithmAdapter failed:", e);
      }
    }
    /*    for (Visualizer vis : avis) {
          String group = vis.getMetadata().getGenerics(Visualizer.META_GROUP, String.class);
          this.visualizers.register(vis, group);
        }*/
  }
  
  /**
   * Get the visualization context.
   * 
   * @return the context
   */
  public VisualizerContext<O> getContext() {
    return context;
  }

  /**
   * Get the visualizers found.
   * 
   * @return Visualizers found for result
   */
  public VisualizerTree<O> getVisualizers() {
    return visualizers;
  }

  /**
   * Collect and instantiate all adapters.
   * 
   * @param config Parameterization
   * @return List of all adapters found.
   */
  @SuppressWarnings("unchecked")
  private static <O extends DatabaseObject> Collection<AlgorithmAdapter<O>> collectAlgorithmAdapters(Parameterization config) {
    ArrayList<AlgorithmAdapter<O>> algorithmAdapters = new ArrayList<AlgorithmAdapter<O>>();
    for(Class<?> c : InspectionUtil.cachedFindAllImplementations(AlgorithmAdapter.class)) {
      try {
        AlgorithmAdapter<O> a = ClassGenericsUtil.tryInstantiate(AlgorithmAdapter.class, c, config);
        algorithmAdapters.add(a);
      }
      catch(Exception e) {
        logger.exception("Error instantiating AlgorithmAdapter " + c.getName(), e);
      }
    }
    return algorithmAdapters;
  }

  /**
   * Try to automatically generate a title for this.
   * 
   * @param db Database
   * @param result Result object
   * @return generated title
   */
  public String getTitle(Database<? extends DatabaseObject> db, Result result) {
    List<Pair<Object, Parameter<?, ?>>> settings = new ArrayList<Pair<Object, Parameter<?, ?>>>();
    for(SettingsResult sr : ResultUtil.getSettingsResults(result)) {
      settings.addAll(sr.getSettings());
    }
    String algorithm = null;
    String distance = null;
    String dataset = null;

    for(Pair<Object, Parameter<?, ?>> setting : settings) {
      if(setting.second.equals(OptionID.ALGORITHM)) {
        algorithm = setting.second.getValue().toString();
      }
      if(setting.second.equals(AbstractDistanceBasedAlgorithm.DISTANCE_FUNCTION_ID)) {
        distance = setting.second.getValue().toString();
      }
      if(setting.second.equals(FileBasedDatabaseConnection.INPUT_ID)) {
        dataset = setting.second.getValue().toString();
      }
    }
    StringBuilder buf = new StringBuilder();
    if(algorithm != null) {
      // shorten the algorithm
      if(algorithm.contains(".")) {
        algorithm = algorithm.substring(algorithm.lastIndexOf(".") + 1);
      }
      buf.append(algorithm);
    }
    if(distance != null) {
      // shorten the distance
      if(distance.contains(".")) {
        distance = distance.substring(distance.lastIndexOf(".") + 1);
      }
      if(buf.length() > 0) {
        buf.append(" using ");
      }
      buf.append(distance);
    }
    if(dataset != null) {
      // shorten the data set filename
      if(dataset.contains(File.separator)) {
        dataset = dataset.substring(dataset.lastIndexOf(File.separator) + 1);
      }
      if(buf.length() > 0) {
        buf.append(" on ");
      }
      buf.append(dataset);
    }
    if(buf.length() > 0) {
      return buf.toString();
    }
    return null;
  }
}