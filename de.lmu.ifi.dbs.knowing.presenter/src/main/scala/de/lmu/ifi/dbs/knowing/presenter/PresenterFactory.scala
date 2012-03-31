package de.lmu.ifi.dbs.knowing.presenter

import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor

/**
 * Default PresenterFactory. Takes two parameters:
 * 
 * presenter: the actual implementation
 * presenterTrait: the implemented presenter trait
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 */
class PresenterFactory(presenter: Class[_ <: TProcessor], presenterTrait: Class[_ <: TProcessor]) extends ProcessorFactory(presenter) {
  override val id = presenterTrait.getName
}