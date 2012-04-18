/*                                                               *\
 ** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|  **
 ** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---, **
 ** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|  **
 **                                                              **
 ** Knowing Framework                                            **
 ** Apache License - http://www.apache.org/licenses/             **
 ** LMU Munich - Database Systems Group                          **
 ** http://www.dbs.ifi.lmu.de/                                   **
\*                                                               */
package de.lmu.ifi.dbs.knowing.debug.ui.editor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import de.lmu.ifi.dbs.knowing.core.model.IParameter;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 2012-04-18
 *
 */
public class ParameterEditingSupport extends EditingSupport {

	public ParameterEditingSupport(TableViewer viewer) {
		super(viewer);
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new TextCellEditor(((TableViewer)getViewer()).getTable());
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		String content = ((IParameter)element).getValue().getContent();
		return (content != null) ? content : "";
	}

	@Override
	protected void setValue(Object element, Object value) {
		IParameter p = (IParameter) element;
		p.setValue(value.toString());
		getViewer().refresh();
	}

}
