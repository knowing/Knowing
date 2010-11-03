package de.lmu.ifi.dbs.elki.application;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.logging.Level;

import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.logging.LoggingConfiguration;
import de.lmu.ifi.dbs.elki.utilities.FormatUtil;
import de.lmu.ifi.dbs.elki.utilities.exceptions.AbortException;
import de.lmu.ifi.dbs.elki.utilities.exceptions.UnableToComplyException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.ParameterException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.UnspecifiedParameterException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.WrongParameterValueException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.SerializedParameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.TrackParameters;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ClassParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.Flag;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.Parameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.StringParameter;
import de.lmu.ifi.dbs.elki.utilities.pairs.Pair;

/**
 * AbstractApplication sets the values for flags verbose and help.
 * <p/>
 * Any Wrapper class that makes use of these flags may extend this class. Beware
 * to make correct use of parameter settings via optionHandler as commented with
 * constructor and methods.
 * 
 * @author Elke Achtert
 * @author Erich Schubert
 */
public abstract class AbstractApplication implements Parameterizable {
  /**
   * We need a static logger in this class, for code used in "main" methods.
   */
  protected static Logging STATIC_LOGGER = Logging.getLogger(AbstractApplication.class);

  /**
   * The newline string according to system.
   */
  private static final String NEWLINE = System.getProperty("line.separator");

  /**
   * Information for citation and version.
   */
  public static final String INFORMATION = "ELKI Version 0.3 (2010, March)" + NEWLINE + NEWLINE + "published in:" + NEWLINE + "Elke Achtert, Hans-Peter Kriegel, Lisa Reichert, Erich Schubert, Remigius Wojdanowski, Arthur Zimek:" + NEWLINE + "Visual Evaluation of Outlier Detection Models." + NEWLINE + "In Proc. 15th International Conference on Database Systems for Advanced Applications (DASFAA), Tsukuba, Japan, 2010." + NEWLINE;

  /**
   * Flag to obtain help-message.
   * <p>
   * Key: {@code -h}
   * </p>
   */
  private static final Flag HELP_FLAG = new Flag(OptionID.HELP);

  /**
   * Flag to obtain help-message.
   * <p>
   * Key: {@code -help}
   * </p>
   */
  private static final Flag HELP_LONG_FLAG = new Flag(OptionID.HELP_LONG);

  /**
   * Optional Parameter to specify a class to obtain a description for.
   * <p>
   * Key: {@code -description}
   * </p>
   */
  private static final ClassParameter<Object> DESCRIPTION_PARAM = new ClassParameter<Object>(OptionID.DESCRIPTION, Object.class, true);

  /**
   * Optional Parameter to specify a class to enable debugging for.
   * <p>
   * Key: {@code -enableDebug}
   * </p>
   */
  private static final StringParameter DEBUG_PARAM = new StringParameter(OptionID.DEBUG, true);

  /**
   * Flag to allow verbose messages while running the application.
   * <p>
   * Key: {@code -verbose}
   * </p>
   */
  private final Flag VERBOSE_FLAG = new Flag(OptionID.ALGORITHM_VERBOSE);

  /**
   * Value of verbose flag.
   */
  private boolean verbose;

  /**
   * Tracks the parameters.
   */
  protected static TrackParameters config;

  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  protected AbstractApplication(Parameterization config) {
    config = config.descend(this);
    // Verbose flag.
    if(config.grab(VERBOSE_FLAG)) {
      verbose = VERBOSE_FLAG.getValue();
    }
    if(verbose) {
      // Note: do not unset verbose if not --verbose - someone else might
      // have set it intentionally. So don't setVerbose(verbose)!
      LoggingConfiguration.setVerbose(true);
    }
  }

  /**
   * Returns whether verbose messages should be printed while executing the
   * application.
   * 
   * @return whether verbose messages should be printed while executing the
   *         application
   */
  public final boolean isVerbose() {
    return verbose;
  }

  /**
   * Generic command line invocation.
   * 
   * Refactored to have a central place for outermost exception handling.
   * 
   * @param cls Application class to run.
   * @param args the arguments to run this application with
   */
  public static void runCLIApplication(Class<?> cls, String[] args) {
    SerializedParameterization params = new SerializedParameterization(args);
    try {
      params.grab(HELP_FLAG);
      params.grab(HELP_LONG_FLAG);
      params.grab(DESCRIPTION_PARAM);
      params.grab(DEBUG_PARAM);
      if(DESCRIPTION_PARAM.isDefined()) {
        params.clearErrors();
        printDescription(DESCRIPTION_PARAM.getValue());
        return;
      }
      // Fail silently on errors.
      if(params.getErrors().size() > 0) {
        params.logAndClearReportedErrors();
        return;
      }
      if(DEBUG_PARAM.isDefined()) {
        String[] opts = DEBUG_PARAM.getValue().split(",");
        for(String opt : opts) {
          try {
            String[] chunks = opt.split("=");
            if(chunks.length == 1) {
              LoggingConfiguration.setLevelFor(chunks[0], Level.FINEST.getName());
            }
            else if(chunks.length == 2) {
              LoggingConfiguration.setLevelFor(chunks[0], chunks[1]);
            }
            else {
              throw new IllegalArgumentException("More than one '=' in debug parameter.");
            }
          }
          catch(IllegalArgumentException e) {
            printErrorMessage(new WrongParameterValueException(DEBUG_PARAM, DEBUG_PARAM.getValue(), "Could not process value.", e));
            return;
          }
        }
      }
    }
    catch(Exception e) {
      printErrorMessage(e);
      return;
    }
    try {
      config = new TrackParameters(params);
      Constructor<?> constructor = cls.getConstructor(Parameterization.class);
      AbstractApplication task = (AbstractApplication) (constructor.newInstance(config));

      if((HELP_FLAG.isDefined() && HELP_FLAG.getValue()) || (HELP_LONG_FLAG.isDefined() && HELP_LONG_FLAG.getValue())) {
        LoggingConfiguration.setVerbose(true);
        STATIC_LOGGER.verbose(usage(config.getAllParameters()));
      }
      else {
        params.logUnusedParameters();
        if(params.getErrors().size() > 0) {
          LoggingConfiguration.setVerbose(true);
          STATIC_LOGGER.verbose("The following configuration errors prevented execution:\n");
          for(ParameterException e : params.getErrors()) {
            STATIC_LOGGER.verbose(e.getMessage());
          }
          STATIC_LOGGER.verbose("\n");
          STATIC_LOGGER.verbose("Stopping execution because of configuration errors.");
        }
        else {
          task.run();
        }
      }
    }
    catch(Exception e) {
      printErrorMessage(e);
    }
  }

  /**
   * Returns a usage message, explaining all known options
   * 
   * @param options Options to show in usage.
   * @return a usage message explaining all known options
   */
  public static String usage(Collection<Pair<Object, Parameter<?, ?>>> options) {
    StringBuffer usage = new StringBuffer();
    usage.append(INFORMATION);

    // Collect options
    usage.append(NEWLINE).append("Parameters:").append(NEWLINE);
    OptionUtil.formatForConsole(usage, FormatUtil.getConsoleWidth(), "   ", options);

    // FIXME: re-add constraints!
    return usage.toString();
  }

  /**
   * Print an error message for the given error.
   * 
   * @param e Error Exception.
   */
  protected static void printErrorMessage(Exception e) {
    if(e instanceof AbortException) {
      // ensure we actually show the message:
      LoggingConfiguration.setVerbose(true);
      STATIC_LOGGER.verbose(e.getMessage());
    }
    else if(e instanceof UnspecifiedParameterException) {
      STATIC_LOGGER.error(e.getMessage());
    }
    else if(e instanceof ParameterException) {
      STATIC_LOGGER.error(e.getMessage());
    }
    else {
      STATIC_LOGGER.exception(e);
    }
  }

  /**
   * Print the description for the given parameter
   */
  private static void printDescription(Class<?> descriptionClass) {
    if(descriptionClass != null) {
      LoggingConfiguration.setVerbose(true);
      STATIC_LOGGER.verbose(OptionUtil.describeParameterizable(new StringBuffer(), descriptionClass, FormatUtil.getConsoleWidth(), "    ").toString());
    }
  }

  /**
   * Runs the application.
   * 
   * @throws de.lmu.ifi.dbs.elki.utilities.exceptions.UnableToComplyException if
   *         an error occurs during running the application
   */
  public abstract void run() throws UnableToComplyException;
}