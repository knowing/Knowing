package de.lmu.ifi.dbs.knowing.core.processing

import java.util.Collection
import weka.core.Instances
import weka.core.Instance
import weka.core.Attribute
import java.util.Random

/**
 * <p>Immutable Instances class</p>
 *
 * @author Nepomuk Seiler
 * @since 2011-07-24
 */
class ImmutableInstances(instances: Instances) extends Instances(instances, instances.numInstances) {

  //Add instances to this instance without creating a copy
  val enum = instances.enumerateInstances
  while (enum hasMoreElements) {
    val inst = enum.nextElement.asInstanceOf[Instance]
    super.add(inst)
    inst.setDataset(instances)
  }

  override def add(inst: Instance) = throw new UnsupportedOperationException("Instances is immutable!")
  override def add(index: Int, inst: Instance) = throw new UnsupportedOperationException("Instances is immutable!")
  override def addAll(collection: Collection[_ <: Instance]) = throw new UnsupportedOperationException("Instances is immutable!")
  override def addAll(index: Int, collection: Collection[_ <: Instance]) = throw new UnsupportedOperationException("Instances is immutable!")

  override def insertAttributeAt(attribute: Attribute, index: Int) = throw new UnsupportedOperationException("Instances is immutable!")

  override def delete(index: Int) = throw new UnsupportedOperationException("Instances is immutable!")
  override def deleteAttributeAt(index: Int) = throw new UnsupportedOperationException("Instances is immutable!")
  override def deleteAttributeType(index: Int) = throw new UnsupportedOperationException("Instances is immutable!")
  override def deleteStringAttributes = throw new UnsupportedOperationException("Instances is immutable!")
  override def deleteWithMissing(index: Int) = throw new UnsupportedOperationException("Instances is immutable!")
  override def deleteWithMissing(attribute: Attribute) = throw new UnsupportedOperationException("Instances is immutable!")
  override def deleteWithMissingClass = throw new UnsupportedOperationException("Instances is immutable!")

  override def set(index: Int, inst: Instance) = throw new UnsupportedOperationException("Instances is immutable!")
  override def setRelationName(name: String) = throw new UnsupportedOperationException("Instances is immutable!")

  override def sort(index: Int) = throw new UnsupportedOperationException("Instances is immutable!")
  override def sort(attribute: Attribute) = throw new UnsupportedOperationException("Instances is immutable!")
  override def stratify(numFolds: Int) = throw new UnsupportedOperationException("Instances is immutable!")

  override def randomize(random: Random) = throw new UnsupportedOperationException("Instances is immutable!")
  override def resample(random: Random) = throw new UnsupportedOperationException("Instances is immutable!")
  override def resampleWithWeights(random: Random) = throw new UnsupportedOperationException("Instances is immutable!")
  override def resampleWithWeights(random: Random, weights: Array[Double]) = throw new UnsupportedOperationException("Instances is immutable!")

  override def remove(element: Any) = throw new UnsupportedOperationException("Instances is immutable!")
  override def remove(index: Int) = throw new UnsupportedOperationException("Instances is immutable!")
  override def removeAll(collection: Collection[_]) = throw new UnsupportedOperationException("Instances is immutable!")
  override def removeRange(start: Int, offset: Int) = throw new UnsupportedOperationException("Instances is immutable!")

  override def renameAttribute(index: Int, name: String) = throw new UnsupportedOperationException("Instances is immutable!")
  override def renameAttribute(attribute: Attribute, name: String) = throw new UnsupportedOperationException("Instances is immutable!")
  override def renameAttributeValue(attribute: Int, value: Int, name: String) = throw new UnsupportedOperationException("Instances is immutable!")
  override def renameAttributeValue(attribute: Attribute, value: String, name: String) = throw new UnsupportedOperationException("Instances is immutable!")

  //ClassIndex should be mutable
  //  override def setClass(attribute: Attribute) = throw new UnsupportedOperationException("Instances is immutable!")
  //  override def setClassIndex(index: Int) = throw new UnsupportedOperationException("Instances is immutable!")
  
  override def toString() = relationName
  
  def toStringComplete() = super.toString
}
    
