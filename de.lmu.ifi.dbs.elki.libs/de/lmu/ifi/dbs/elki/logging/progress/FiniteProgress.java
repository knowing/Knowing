package de.lmu.ifi.dbs.elki.logging.progress;

import de.lmu.ifi.dbs.elki.logging.Logging;

/**
 * A progress object for a given overall number of items to process. The number
 * of already processed items at a point in time can be updated.
 * 
 * The main feature of this class is to provide a String representation of the
 * progress suitable as a message for printing to the command line interface.
 * 
 * @author Arthur Zimek
 */
public class FiniteProgress extends AbstractProgress {
  /**
   * The overall number of items to process.
   */
  private final int total;

  /**
   * Holds the length of a String describing the total number.
   */
  // TODO: move this to a console logging related class instead?
  private final int totalLength;

  /**
   * A progress object for a given overall number of items to process.
   * 
   * @param task the name of the task
   * @param total the overall number of items to process
   */
  @Deprecated
  public FiniteProgress(String task, int total) {
    super(task);
    this.total = total;
    this.totalLength = Integer.toString(total).length();
  }

  /**
   * Constructor with auto-reporting to logging.
   * 
   * @param task the name of the task
   * @param total the overall number of items to process
   * @param logger the logger to report to
   */
  public FiniteProgress(String task, int total, Logging logger) {
    super(task);
    this.total = total;
    this.totalLength = Integer.toString(total).length();
    logger.progress(this);
  }

  /**
   * Sets the number of items already processed at a time being.
   * 
   * @param processed the number of items already processed at a time being
   * @throws IllegalArgumentException if the given number is negative or exceeds
   *         the overall number of items to process
   */
  @Override
  public void setProcessed(int processed) throws IllegalArgumentException {
    if(processed > total) {
      throw new IllegalArgumentException(processed + " exceeds total: " + total);
    }
    if(processed < 0) {
      throw new IllegalArgumentException("Negative number of processed: " + processed);
    }
    super.setProcessed(processed);
  }

  /**
   * Append a string representation of the progress to the given string buffer.
   * 
   * @param buf Buffer to serialize to
   * @return Buffer the data was serialized to.
   */
  @Override
  public StringBuffer appendToBuffer(StringBuffer buf) {
    String processedString = Integer.toString(getProcessed());
    int percentage = (int) (getProcessed() * 100.0 / total);
    buf.append(getTask());
    buf.append(": ");
    for(int i = 0; i < totalLength - processedString.length(); i++) {
      buf.append(' ');
    }
    buf.append(getProcessed());
    buf.append(" [");
    if(percentage < 100) {
      buf.append(' ');
    }
    if(percentage < 10) {
      buf.append(' ');
    }
    buf.append(percentage);
    buf.append("%]");
    return buf;
  }

  /**
   * Test whether the progress was completed.
   */
  @Override
  public boolean isComplete() {
    return getProcessed() == total;
  }

  /**
   * Get the final value for the progress.
   * 
   * @return final value
   */
  public int getTotal() {
    return total;
  }

  /**
   * Ensure that the progress was completed, to make progress bars disappear
   * 
   * @param logger Logger to report to.
   */
  public void ensureCompleted(Logging logger) {
    if(!isComplete()) {
      logger.warning("Progress had not completed automatically as expected.", new Throwable());
      setProcessed(getTotal());
      logger.progress(this);
    }
  }
}