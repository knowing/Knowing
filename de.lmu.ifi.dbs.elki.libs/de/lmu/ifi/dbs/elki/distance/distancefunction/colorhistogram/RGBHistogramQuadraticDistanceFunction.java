package de.lmu.ifi.dbs.elki.distance.distancefunction.colorhistogram;

import de.lmu.ifi.dbs.elki.distance.distancefunction.WeightedDistanceFunction;
import de.lmu.ifi.dbs.elki.math.linearalgebra.Matrix;
import de.lmu.ifi.dbs.elki.utilities.documentation.Reference;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.IntParameter;

/**
 * Distance function for RGB color histograms based on a quadratic form and
 * color similarity.
 * 
 * This is (unverified) attributed to
 * <p>
 * James Hafner, Harpreet S.Sawhney, Will Equits, Myron Flickner and Wayne
 * Niblack<br />
 * Efficient Color Histogram Indexing for Quadratic Form Distance Functions<br />
 * IEEE Trans. on Pattern Analysis and Machine Intelligence, Vol. 17, No. 7,
 * July 1995
 * </p>
 * 
 * @author Erich Schubert
 */
@Reference(authors = "J. Hafner, H. S.Sawhney, W. Equits, M. Flickner, W. Niblack", title = "Efficient Color Histogram Indexing for Quadratic Form Distance Functions", booktitle = "IEEE Trans. on Pattern Analysis and Machine Intelligence, Vol. 17, No. 7, July 1995", url = "http://dx.doi.org/10.1109/34.391417")
public class RGBHistogramQuadraticDistanceFunction extends WeightedDistanceFunction {
  /**
   * OptionID for {@link #BPP_PARAM}
   */
  public static final OptionID BPP_ID = OptionID.getOrCreateOptionID("rgbhist.bpp", "The dimensionality of the histogram in each color");

  /**
   * Parameter for the kernel dimensionality.
   */
  IntParameter BPP_PARAM = new IntParameter(BPP_ID);

  /**
   * Stores the (full = to the power of three) dimensionality
   */
  int dim;

  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public RGBHistogramQuadraticDistanceFunction(Parameterization config) {
    super(null);
    config = config.descend(this);
    if(config.grab(BPP_PARAM)) {
      int bpp = BPP_PARAM.getValue();
      dim = bpp * bpp * bpp;

      Matrix m = new Matrix(dim, dim);
      // maximum occurring distance in manhattan between bins:
      final double max = 3. * (bpp - 1.);
      for(int x = 0; x < dim; x++) {
        final int rx = (x / bpp) / bpp;
        final int gx = (x / bpp) % bpp;
        final int bx = x % bpp;
        for(int y = 0; y < dim; y++) {
          final int ry = (y / bpp) / bpp;
          final int gy = (y / bpp) % bpp;
          final int by = y % bpp;

          final double dr = Math.abs(rx - ry);
          final double dg = Math.abs(gx - gy);
          final double db = Math.abs(bx - by);

          final double val = 1 - (dr + dg + db) / max;
          m.set(x, y, val);
        }
      }
      weightMatrix = m;
    }
  }
}
