package de.lmu.ifi.dbs.elki.visualization.visualizers.vis2d;

import java.util.Iterator;

import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Element;

import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.cluster.Cluster;
import de.lmu.ifi.dbs.elki.data.model.Model;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.DatabaseEvent;
import de.lmu.ifi.dbs.elki.database.DatabaseListener;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.result.outlier.OutlierResult;
import de.lmu.ifi.dbs.elki.utilities.documentation.Reference;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.DoubleParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.Flag;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ObjectParameter;
import de.lmu.ifi.dbs.elki.utilities.scaling.GammaScaling;
import de.lmu.ifi.dbs.elki.utilities.scaling.ScalingFunction;
import de.lmu.ifi.dbs.elki.utilities.scaling.outlier.OutlierScalingFunction;
import de.lmu.ifi.dbs.elki.visualization.colors.ColorLibrary;
import de.lmu.ifi.dbs.elki.visualization.css.CSSClass;
import de.lmu.ifi.dbs.elki.visualization.projections.Projection2D;
import de.lmu.ifi.dbs.elki.visualization.style.StyleLibrary;
import de.lmu.ifi.dbs.elki.visualization.svg.SVGPlot;
import de.lmu.ifi.dbs.elki.visualization.svg.SVGUtil;
import de.lmu.ifi.dbs.elki.visualization.visualizers.Visualization;
import de.lmu.ifi.dbs.elki.visualization.visualizers.Visualizer;
import de.lmu.ifi.dbs.elki.visualization.visualizers.VisualizerContext;
import de.lmu.ifi.dbs.elki.visualization.visualizers.thumbs.ProjectedThumbnail;
import de.lmu.ifi.dbs.elki.visualization.visualizers.thumbs.ThumbnailVisualization;

/**
 * Generates a SVG-Element containing bubbles. A Bubble is a circle visualizing
 * an outlierness-score, with its center at the position of the visualized
 * object and its radius depending on the objects score.
 * 
 * @author Remigius Wojdanowski
 * @author Erich Schubert
 * 
 * @param <NV> Type of the DatabaseObject being visualized.
 */
@Reference(authors = "E. Achtert, H.-P. Kriegel, L. Reichert, E. Schubert, R. Wojdanowski, A. Zimek", title = "Visual Evaluation of Outlier Detection Models", booktitle = "Proceedings of the 15th International Conference on Database Systems for Advanced Applications (DASFAA), Tsukuba, Japan, 2010", url = "http://dx.doi.org/10.1007%2F978-3-642-12098-5_34")
public class BubbleVisualizer<NV extends NumberVector<NV, ?>> extends Projection2DVisualizer<NV> {
  /**
   * OptionID for {@link #GAMMA_PARAM}.
   */
  public static final OptionID GAMMA_ID = OptionID.getOrCreateOptionID("bubble.gamma", "A gamma-correction.");

  /**
   * Parameter for the gamma-correction.
   * 
   * <p>
   * Key: {@code -bubble.gamma}
   * </p>
   * 
   * <p>
   * Default value: 1.0
   * < /p>
   */
  private final DoubleParameter GAMMA_PARAM = new DoubleParameter(GAMMA_ID, 1.0);

  /**
   * Gamma parameter.
   */
  private double gamma;

  /**
   * OptionID for {@link #FILL_FLAG}.
   */
  public static final OptionID FILL_ID = OptionID.getOrCreateOptionID("bubble.fill", "Half-transparent filling of bubbles.");

  /**
   * Flag for half-transparent filling of bubbles.
   * 
   * <p>
   * Key: {@code -bubble.fill}
   * </p>
   */
  private final Flag FILL_FLAG = new Flag(FILL_ID);

  /**
   * Fill parameter.
   */
  protected boolean fill;

  /**
   * OptionID for {@link #SCALING_PARAM}
   */
  public static final OptionID SCALING_ID = OptionID.getOrCreateOptionID("bubble.scaling", "Additional scaling function for bubbles.");

  /**
   * Parameter for scaling functions
   * 
   * <p>
   * Key: {@code -bubble.scaling}
   * </p>
   */
  private final ObjectParameter<ScalingFunction> SCALING_PARAM = new ObjectParameter<ScalingFunction>(SCALING_ID, OutlierScalingFunction.class, true);

  /**
   * Scaling function to use for Bubbles
   */
  protected ScalingFunction scaling;

  /**
   * The outlier result to visualize
   */
  protected OutlierResult result;

  /**
   * Used for Gamma-Correction.
   * 
   * TODO: Make the gamma-function exchangeable (inc. Parameter etc.).
   */
  protected GammaScaling gammaScaling;

  /**
   * Generic tag to indicate the type of element. Used in IDs, CSS-Classes etc.
   */
  public static final String BUBBLE = "bubble";

  /**
   * A short name characterizing this Visualizer.
   */
  public static final String NAME = "Outlier Bubbles";

  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public BubbleVisualizer(Parameterization config) {
    super(NAME);
    config = config.descend(this);
    if(config.grab(FILL_FLAG)) {
      fill = FILL_FLAG.getValue();
    }
    if(config.grab(SCALING_PARAM)) {
      scaling = SCALING_PARAM.instantiateClass(config);
    }
    if(config.grab(GAMMA_PARAM)) {
      gamma = GAMMA_PARAM.getValue();
    }
  }

  /**
   * Initializes this Visualizer.
   * 
   * @param context Visualization context
   * @param result contains "outlierness-scores", corresponding to the database.
   */
  public void init(VisualizerContext<? extends NV> context, OutlierResult result) {
    super.init(context);
    this.result = result;
    this.gammaScaling = new GammaScaling(gamma);

    if(this.scaling != null && this.scaling instanceof OutlierScalingFunction) {
      ((OutlierScalingFunction) this.scaling).prepare(context.getDatabase(), result);
    }
  }

  @Override
  public Visualization visualize(SVGPlot svgp, Projection2D proj, double width, double height) {
    return new BubbleVisualization(context, svgp, proj, width, height);
  }

  @Override
  public Visualization makeThumbnail(SVGPlot svgp, Projection2D proj, double width, double height, int tresolution) {
    return new ProjectedThumbnail<NV, Projection2D>(this, context, svgp, proj, width, height, tresolution, ThumbnailVisualization.ON_DATA);
  }

  /**
   * The actual visualization instance, for a single projection
   * 
   * @author Erich Schubert
   */
  protected class BubbleVisualization extends Projection2DVisualization<NV> implements DatabaseListener<NV> {
    /**
     * Constructor.
     * 
     * @param context Context
     * @param svgp Plot
     * @param proj Projection
     * @param width Width
     * @param height Height
     */
    public BubbleVisualization(VisualizerContext<? extends NV> context, SVGPlot svgp, Projection2D proj, double width, double height) {
      super(context, svgp, proj, width, height, Visualizer.LEVEL_DATA);
      context.addDatabaseListener(this);
      incrementalRedraw();
    }

    @Override
    public void destroy() {
      super.destroy();
      context.removeDatabaseListener(this);
    }

    @Override
    public void redraw() {
      // get the Database
      Database<? extends NV> database = context.getDatabase();
      Clustering<Model> clustering = context.getOrCreateDefaultClustering();
      setupCSS(svgp, clustering);
      // bubble size
      double bubble_size = context.getStyleLibrary().getSize(StyleLibrary.BUBBLEPLOT);
      // draw data
      Iterator<Cluster<Model>> ci = clustering.getAllClusters().iterator();
      for(int cnum = 0; cnum < clustering.getAllClusters().size(); cnum++) {
        Cluster<?> clus = ci.next();
        for(DBID objId : clus.getIDs()) {
          final Double radius = getScaledForId(objId);
          if(radius > 0.01) {
            final NV vec = database.get(objId);
            if(vec != null) {
              double[] v = proj.fastProjectDataToRenderSpace(vec);
              Element circle = svgp.svgCircle(v[0], v[1], radius * bubble_size);
              SVGUtil.addCSSClass(circle, BUBBLE + cnum);
              layer.appendChild(circle);
            }
          }
        }
      }
    }

    @Override
    public void objectsChanged(@SuppressWarnings("unused") DatabaseEvent<NV> e) {
      synchronizedRedraw();
    }

    @Override
    public void objectsInserted(@SuppressWarnings("unused") DatabaseEvent<NV> e) {
      synchronizedRedraw();
    }

    @Override
    public void objectsRemoved(@SuppressWarnings("unused") DatabaseEvent<NV> e) {
      synchronizedRedraw();
    }

    /**
     * Registers the Bubble-CSS-Class at a SVGPlot. This class depends on the
     * {@link #FILL_FLAG}.
     * 
     * @param svgp the SVGPlot to register the Tooltip-CSS-Class.
     */
    private void setupCSS(SVGPlot svgp, Clustering<? extends Model> clustering) {
      ColorLibrary colors = context.getStyleLibrary().getColorSet(StyleLibrary.PLOT);

      // creating IDs manually because cluster often return a null-ID.
      int clusterID = 0;

      for(@SuppressWarnings("unused")
      Cluster<?> cluster : clustering.getAllClusters()) {
        CSSClass bubble = new CSSClass(svgp, BUBBLE + clusterID);
        bubble.setStatement(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, context.getStyleLibrary().getLineWidth(StyleLibrary.PLOT));

        String color;

        if(clustering.getAllClusters().size() == 1) {
          color = "black";
        }
        else {
          color = colors.getColor(clusterID);
        }

        if(fill) {
          bubble.setStatement(SVGConstants.CSS_FILL_PROPERTY, color);
          bubble.setStatement(SVGConstants.CSS_FILL_OPACITY_PROPERTY, 0.5);
        }
        else {
          // for diamond-shaped strokes, see bugs.sun.com, bug ID 6294396
          bubble.setStatement(SVGConstants.CSS_STROKE_VALUE, color);
          bubble.setStatement(SVGConstants.CSS_FILL_PROPERTY, SVGConstants.CSS_NONE_VALUE);
        }

        svgp.addCSSClassOrLogError(bubble);
        clusterID += 1;
      }
    }

    /**
     * Convenience method to apply scalings in the right order.
     * 
     * @param id object ID to get scaled score for
     * @return a Double representing a outlierness-score, after it has modified
     *         by the given scales.
     */
    protected Double getScaledForId(DBID id) {
      Double d = result.getScores().getValueFor(id).doubleValue();
      if(d == null) {
        return 0.0;
      }
      if(scaling == null) {
        return gammaScaling.getScaled(result.getOutlierMeta().normalizeScore(d));
      }
      else {
        return gammaScaling.getScaled(scaling.getScaled(d));
      }
    }
  }
}