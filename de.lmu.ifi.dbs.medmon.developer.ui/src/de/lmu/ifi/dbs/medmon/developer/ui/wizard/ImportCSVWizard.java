package de.lmu.ifi.dbs.medmon.developer.ui.wizard;

import java.io.IOException;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import de.lmu.ifi.dbs.medmon.base.ui.widgets.SensorSourceWidget;
import de.lmu.ifi.dbs.medmon.base.ui.wizard.pages.SelectDataSourcePage;
import de.lmu.ifi.dbs.medmon.datamining.core.csv.io.CSVFileWriter;

public class ImportCSVWizard extends Wizard implements IImportWizard {

	private IStructuredSelection selection;
	private IWorkbench workbench;

	public ImportCSVWizard() {
	}

	@Override
	public void addPages() {
		// addPage(new CSVImportPage(selection));
		addPage(new SelectDataSourcePage());
	}

	@Override
	public boolean performFinish() {
		SelectDataSourcePage page = (SelectDataSourcePage) getPages()[0];
		SensorSourceWidget cfg = (SensorSourceWidget) page.getConfiguration();
		try {
			FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
			String path = dialog.open();
			if(path == null || path.isEmpty())
				return false;
			CSVFileWriter writer = new CSVFileWriter(path);
			

/*			ISensorDataContainer container = cfg.getSensor().getData();
			IConverter converter = cfg.getSensor().getSensorExtension().getConverter();
			DateFormat df = DateFormat.getDateTimeInstance();
			for (ISensorDataContainer c : container.getChildren()) {
				Object[] data = container.getSensorData(converter);
				RawData rawData = DataConverter.convert(data);
				if (rawData != null && rawData.dimension() > 0 && rawData.getDimension(0) != null) {
					int dim = rawData.dimension();
					int size = rawData.getDimension(0).length;
					for (int i = 0; i < size; i++) {
						List<String> list = new ArrayList<String>();
						// Date
						list.add(df.format(new Date(rawData.getTimestamp()[i])));
						// Values
						for (int j = 0; j < dim; j++)
							list.add(String.valueOf(rawData.getDimension(j)[i]));
						writer.writeFields(list);
					}
				}
			}*/
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;

	}

}
