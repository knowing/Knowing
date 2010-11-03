package de.lmu.ifi.dbs.elki.parser.meta;

import java.io.InputStream;

import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.parser.DoubleVectorLabelParser;
import de.lmu.ifi.dbs.elki.parser.Parser;
import de.lmu.ifi.dbs.elki.parser.ParsingResult;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ClassParameter;

/**
 * A MetaParser uses any {@link Parser} as specified by the user via parameter
 * setting as base parser and may perform certain transformations on the
 * retrieved {@link ParsingResult}.
 * 
 * 
 * @author Arthur Zimek
 * 
 * @param <O> the type of DatabaseObject used in the {@link ParsingResult}
 *        retrieved by the base parser and provided in the {@link ParsingResult}
 *        of the {@link #parse(InputStream)}-method of this Parser's subclass.
 */
public abstract class MetaParser<O extends DatabaseObject> implements Parser<O> {
  /**
   * OptionID for {@link #BASEPARSER_PARAM}.
   */
  public static final OptionID BASEPARSER_ID = OptionID.getOrCreateOptionID("metaparser.baseparser", "Parser to use as base parser");

  /**
   * The parameter for setting the base parser.
   * 
   * <p>
   * Key: {@code -metaparser.baseparser}
   * </p>
   * <p>
   * Default: {@link DoubleVectorLabelParser}
   * </p>
   */
  private final ClassParameter<? extends Parser<O>> BASEPARSER_PARAM = new ClassParameter<Parser<O>>(BASEPARSER_ID, Parser.class, DoubleVectorLabelParser.class);

  /**
   * Holds an instance of the current base parser.
   */
  private Parser<O> baseparser;

  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public MetaParser(Parameterization config) {
    config = config.descend(this);
    if(config.grab(BASEPARSER_PARAM)) {
      baseparser = BASEPARSER_PARAM.instantiateClass(config);
    }
  }

  /**
   * <p>
   * Retrieves the {@link ParsingResult} of the base parser.
   * </p>
   * 
   * @param in the {@link InputStream} to be parsed
   * @return the {@link ParsingResult} of the base parser
   */
  protected ParsingResult<O> retrieveBaseParsingresult(InputStream in) {
    return baseparser.parse(in);
  }

  /**
   * Returns a string representation of the object.
   * 
   * @return a string representation of the object.
   */
  @Override
  public String toString() {
    return getClass().getName();
  }
}