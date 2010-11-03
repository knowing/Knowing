package de.lmu.ifi.dbs.elki.visualization.visualizers.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.logging.LoggingUtil;
import de.lmu.ifi.dbs.elki.result.ResultUtil;
import de.lmu.ifi.dbs.elki.result.outlier.OutlierResult;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.ParameterException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.MergedParameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.visualization.visualizers.Visualizer;
import de.lmu.ifi.dbs.elki.visualization.visualizers.VisualizerContext;
import de.lmu.ifi.dbs.elki.visualization.visualizers.VisualizerTree;
import de.lmu.ifi.dbs.elki.visualization.visualizers.vis2d.BubbleVisualizer;
import de.lmu.ifi.dbs.elki.visualization.visualizers.vis2d.TooltipVisualizer;

/**
 * This class activates bubble and tooltip visualizers when there is an Outlier
 * result found.
 * 
 * @author Erich Schubert
 * @author Remigius Wojdanowski
 * 
 * @param <NV> Vector type
 */
public class OutlierScoreAdapter<NV extends NumberVector<NV, ?>> implements AlgorithmAdapter<NV> {
  /**
   * Visualizes outlier-scores with bubbles.
   */
  private BubbleVisualizer<NV> bubbleVisualizer;

  /**
   * Visualizes outlier-scores with tooltips.
   */
  private TooltipVisualizer<NV> tooltipVisualizer;

  /**
   * Track parameters for subclasses for "replay".
   */
  private MergedParameterization reconfig;

  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public OutlierScoreAdapter(Parameterization config) {
    super();
    config = config.descend(this);
    this.reconfig = new MergedParameterization(config);
    reconfig.rewind();
    tooltipVisualizer = new TooltipVisualizer<NV>(reconfig);
    reconfig.rewind();
    bubbleVisualizer = new BubbleVisualizer<NV>(reconfig);
  }

  @Override
  public boolean canVisualize(VisualizerContext<? extends NV> context) {
    return ResultUtil.filterResults(context.getResult(), OutlierResult.class).size() > 0;
  }

  @Override
  public Collection<Visualizer> getProvidedVisualizers() {
    Collection<Visualizer> c = new ArrayList<Visualizer>(2);
    c.add(tooltipVisualizer);
    c.add(bubbleVisualizer);
    return c;
  }

  @Override
  public void addVisualizers(VisualizerContext<? extends NV> context, VisualizerTree<? extends NV> vistree) {
    List<OutlierResult> ors = ResultUtil.filterResults(context.getResult(), OutlierResult.class);
    for(OutlierResult o : ors) {
      // Clone visualizers:
      reconfig.rewind();
      TooltipVisualizer<NV> tv = new TooltipVisualizer<NV>(reconfig);
      reconfig.rewind();
      BubbleVisualizer<NV> bv = new BubbleVisualizer<NV>(reconfig);
      if(reconfig.getErrors().size() != 0) {
        for(ParameterException err : reconfig.getErrors()) {
          LoggingUtil.warning("Error in reconfiguration:", err);
        }
      }
      bv.init(context, o);
      tv.init(context, o);
      vistree.addVisualization(o.getScores(), bv);
      vistree.addVisualization(o.getScores(), tv);
    }
  }
}