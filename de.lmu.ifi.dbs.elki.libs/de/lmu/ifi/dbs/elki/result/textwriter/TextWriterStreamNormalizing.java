package de.lmu.ifi.dbs.elki.result.textwriter;

import java.io.PrintStream;

import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.normalization.NonNumericFeaturesException;
import de.lmu.ifi.dbs.elki.normalization.Normalization;
import de.lmu.ifi.dbs.elki.utilities.HandlerList;

/**
 * Normalizing version of {@link TextWriterStream}
 * 
 * @author Erich Schubert
 * 
 * @param <O> Object type that can be normalized.
 */
// TODO: Allow multiple normalization functions for different objects AND/OR
// databases.
public class TextWriterStreamNormalizing<O extends DatabaseObject> extends TextWriterStream {
  /**
   * Normalization function
   */
  private Normalization<O> normalization;
  
  /**
   * Constructor.
   * 
   * @param out Output stream
   * @param writers Object writers
   * @param normalization Normalization
   */
  public TextWriterStreamNormalizing(PrintStream out, HandlerList<TextWriterWriterInterface<?>> writers, Normalization<O> normalization) {
    super(out, writers);
    this.normalization = normalization;
  }

  /**
   * De-Normalize output.
   * 
   * @param v Vector to de-normalize
   * @return de-normalized vector
   * @throws NonNumericFeaturesException on denormalization errors
   */
  public O normalizationRestore(O v) throws NonNumericFeaturesException {
    if(getNormalization() == null) {
      return v;
    }
    return getNormalization().restore(v);
  }

  /**
   * Setter for normalization.
   * 
   * @param normalization Normalization to use
   */
  public void setNormalization(Normalization<O> normalization) {
    this.normalization = normalization;
  }

  /**
   * Getter for normalization class.
   * 
   * @return normalization object
   */
  public Normalization<O> getNormalization() {
    return normalization;
  }
}