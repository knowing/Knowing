package de.lmu.ifi.dbs.elki.application.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.logging.LoggingConfiguration;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.InspectionUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;

/**
 * Perform some consistency checks on classes that cannot be specified as Java
 * interface.
 * 
 * @author Erich Schubert
 */
public class CheckParameterizables {
  /**
   * The logger for this class.
   */
  private static final Logging logger = Logging.getLogger(CheckParameterizables.class);
  
  /**
   * Validate all "Parameterizable" objects for parts of the API contract that
   * cannot be specified in Java interfaces (such as constructors, static
   * methods)
   */
  public void checkParameterizables() {
    LoggingConfiguration.setVerbose(true);
    for(final Class<?> cls : InspectionUtil.findAllImplementations(Object.class, false)) {
      final Constructor<?> constructor;
      try {
        constructor = cls.getDeclaredConstructor(Parameterization.class);
      }
      catch(NoClassDefFoundError e) {
        logger.verbose("Class discovered but not found?!? " + cls.getName());
        // Not found ?!?
        continue;
      }
      catch(Exception e) {
        // Not parameterizable.
        continue;
      }
      checkParameterizable(cls, constructor);
    }
    for(final Class<?> cls : InspectionUtil.findAllImplementations(Parameterizable.class, false)) {
      boolean hasConstructor = false;
      // check for a factory method.
      try {
        ClassGenericsUtil.getParameterizationFactoryMethod(cls, Object.class);
        hasConstructor = true;
        //logger.debugFine("Found factory method for class: "+ cls.getName());
      }
      catch(NoClassDefFoundError e) {
        logger.verbose("Class discovered but not found?!? " + cls.getName());
        // Not found ?!?
        continue;
      }
      catch(Exception e) {
        // do nothing.
      }
      try {
        cls.getConstructor(Parameterization.class);
        hasConstructor = true;
      }
      catch(NoClassDefFoundError e) {
        logger.verbose("Class discovered but not found?!? " + cls.getName());
        // Not found ?!?
        continue;
      }
      catch(Exception e) {
        // do nothing.
      }
      try {
        cls.getConstructor();
        hasConstructor = true;
      }
      catch(NoClassDefFoundError e) {
        logger.verbose("Class discovered but not found?!? " + cls.getName());
        // Not found ?!?
        continue;
      }
      catch(Exception e) {
        // do nothing.
      }
      if(!hasConstructor) {
        logger.verbose("Class " + cls.getName() + " is Parameterizable but doesn't have a constructor with the appropriate signature!");
      }
    }
  }

  private void checkParameterizable(Class<?> cls, Constructor<?> constructor) {
    // Classes in the same package are special and don't cause warnings.
    if(!cls.getName().startsWith(Parameterizable.class.getPackage().getName())) {
      if(!Modifier.isPublic(constructor.getModifiers())) {
        logger.verbose("Constructor for class " + cls.getName() + " is not public!");
      }
      if(!Parameterizable.class.isAssignableFrom(cls)) {
        logger.verbose("Class " + cls.getName() + " should implement Parameterizable!");
      }
    }
  }

  /**
   * @param args Command line arguments
   */
  public static void main(String[] args) {
    new CheckParameterizables().checkParameterizables();
  }
}
