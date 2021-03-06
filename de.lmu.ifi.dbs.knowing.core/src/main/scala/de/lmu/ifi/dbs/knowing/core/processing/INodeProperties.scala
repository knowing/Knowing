/*																*\
** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|	**
** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---,	**
** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|	**
** 																**
** Knowing Framework											**
** Apache License - http://www.apache.org/licenses/				**
** LMU Munich - Database Systems Group							**
** http://www.dbs.ifi.lmu.de/									**
\*																*/
package de.lmu.ifi.dbs.knowing.core.processing

import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil

/**
 * <p> This object holds constant properties wich are used
 * to configure a INode in a IDataProcessingUnit </p>
 *
 * @author Nepomuk Seiler
 * @version 1.0
 * @since 2011-09-13
 */
object INodeProperties {

	/* ========================= */
	/* ===== TSerializable ===== */
	/* ========================= */
	val SERIALIZE = "serialize"
	val DESERIALIZE = "deserialize"

	/* ========================= */
	/* === TLoader / TSaver ==== */
	/* ========================= */
	val ABSOLUTE_PATH = "absolute-path"
	val FILE = "file"
	val URL = "url"
	val DIR = "dir"
	val FILE_EXTENSIONS = "extensions"

	val SOURCE_ATTRIBUTE = ResultsUtil.ATTRIBUTE_SOURCE
	val CLASS_ATTRIBUTE = ResultsUtil.ATTRIBUTE_CLASS

	val OUTPUT = "output"
	val OUTPUT_SINGLE = "single"
	val OUTPUT_MULTIPLE = "multiple"

	val WRITE_MODE = "mode"
	val WRITE_MODE_NONE = "none"
	val WRITE_MODE_BATCH = "batch"
	val WRITE_MODE_INCREMENTAL = "incremental"
	val WRITE_OVERRIDE_EXISTING = "override"

	/* ========================= */
	/* ====== Classifier ======= */
	/* ========================= */

	val SET_CLASS = "setClass"

	/* ========================= */
	/* == By GraphSupervisor === */
	/* ========================= */
	val EXE_PATH = "execution"

	/* ========================= */
	/* ==== General purpose ==== */
	/* ========================= */
	val DEBUG = "debug"
}

object IProcessorPorts {

	/* ========================= */
	/* ====== Classifier ======= */
	/* ========================= */

	val TRAIN = "train"
	val TEST = "test"

}
