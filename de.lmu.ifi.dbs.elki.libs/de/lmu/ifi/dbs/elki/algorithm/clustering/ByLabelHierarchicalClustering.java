package de.lmu.ifi.dbs.elki.algorithm.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import de.lmu.ifi.dbs.elki.algorithm.AbstractAlgorithm;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.data.cluster.Cluster;
import de.lmu.ifi.dbs.elki.data.model.ClusterModel;
import de.lmu.ifi.dbs.elki.data.model.Model;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.database.ids.ModifiableDBIDs;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.utilities.DatabaseUtil;
import de.lmu.ifi.dbs.elki.utilities.documentation.Description;
import de.lmu.ifi.dbs.elki.utilities.documentation.Title;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;

/**
 * Pseudo clustering using labels.
 * 
 * This "algorithm" puts elements into the same cluster when they agree in their
 * labels. I.e. it just uses a predefined clustering, and is mostly useful for
 * testing and evaluation (e.g. comparing the result of a real algorithm to a
 * reference result / golden standard).
 * 
 * This variant derives a hierarchical result by doing a prefix comparison on
 * labels.
 * 
 * TODO: Noise handling (e.g. allow the user to specify a noise label pattern?)
 * 
 * @author Erich Schubert
 * 
 * @param <O> Object type
 */
@Title("Hierarchical clustering by label")
@Description("Cluster points by a (pre-assigned!) label. For comparing results with a reference clustering.")
public class ByLabelHierarchicalClustering<O extends DatabaseObject> extends AbstractAlgorithm<O, Clustering<Model>> implements ClusteringAlgorithm<Clustering<Model>, O> {
  /**
   * The logger for this class.
   */
  private static final Logging logger = Logging.getLogger(ByLabelHierarchicalClustering.class);
  
  /**
   * Constructor without parameters
   */
  public ByLabelHierarchicalClustering() {
    super();
  }

  /**
   * Run the actual clustering algorithm.
   * 
   * @param database The database to process
   */
  @Override
  protected Clustering<Model> runInTime(Database<O> database) throws IllegalStateException {
    HashMap<String, ModifiableDBIDs> labelmap = new HashMap<String, ModifiableDBIDs>();

    for(DBID id : database) {
      String label = DatabaseUtil.getClassOrObjectLabel(database, id);

      if(labelmap.containsKey(label)) {
        labelmap.get(label).add(id);
      }
      else {
        ModifiableDBIDs n = DBIDUtil.newHashSet();
        n.add(id);
        labelmap.put(label, n);
      }
    }

    ArrayList<Cluster<Model>> clusters = new ArrayList<Cluster<Model>>(labelmap.size());
    int i = 0;
    for(Entry<String, ModifiableDBIDs> entry : labelmap.entrySet()) {
      Cluster<Model> clus = new Cluster<Model>(entry.getKey(), entry.getValue(), ClusterModel.CLUSTER, new ArrayList<Cluster<Model>>(), new ArrayList<Cluster<Model>>());
      clusters.add(clus);
      i++;
    }

    for(Cluster<Model> cur : clusters) {
      for(Cluster<Model> oth : clusters) {
        if(oth != cur) {
          if(oth.getName().startsWith(cur.getName())) {
            oth.getParents().add(cur);
            cur.getChildren().add(oth);
            // System.err.println(oth.getLabel() + " is a child of " +
            // cur.getLabel());
          }
        }
      }
    }
    ArrayList<Cluster<Model>> rootclusters = new ArrayList<Cluster<Model>>();
    for(Cluster<Model> cur : clusters) {
      if(cur.getParents().size() == 0) {
        rootclusters.add(cur);
      }
    }
    assert (rootclusters.size() > 0);

    return new Clustering<Model>("By Label Hierarchical Clustering", "bylabel-clustering", rootclusters);
  }

  /**
   * Factory method for {@link Parameterizable}
   * 
   * @param config Parameterization
   * @return Clustering Algorithm
   */
  public static <O extends DatabaseObject> ByLabelHierarchicalClustering<O> parameterize(Parameterization config) {
    if(config.hasErrors()) {
      return null;
    }
    return new ByLabelHierarchicalClustering<O>();
  }

  @Override
  protected Logging getLogger() {
    return logger;
  }
}