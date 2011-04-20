package de.lmu.ifi.dbs.knowing.core.processing

import java.io.{ OutputStream, InputStream }
trait TStorable {

  def store(out:OutputStream)
  
  def load(in:InputStream)
}