package de.lmu.ifi.dbs.medmon.base.ui.analyzed;

import java.awt.Font;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;

public class PieAnalyzedData extends AbstractAnalyzedData {

	/* ========================== */
	/* == Dataset manipulation == */
	/* ========================== */
	
	public void setValue(Comparable key, double value) {
		((DefaultPieDataset) dataset).setValue(key, value);
	}

	/* ========================== */
	/* ===== Chart creation ===== */
	/* ========================== */
	
	@Override
	protected JFreeChart createChart(Dataset dataset) {
		JFreeChart chart = ChartFactory.createPieChart3D("Pie Chart", (PieDataset) dataset, true, false, false);
		//JFreeChart chart = new JFreeChart("Pie Chart", plot);
		return chart;
	}

	@Override
	protected Dataset createDataset() {
		return new DefaultPieDataset();
	}

	@Override
	protected void configurePlot(Plot plot) {
		PiePlot3D Plot3D = (PiePlot3D) plot;
		Plot3D.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		Plot3D.setNoDataMessage("No data available");
		Plot3D.setCircular(false);
		Plot3D.setLabelGap(0.02);

		Plot3D.setStartAngle(270);
		Plot3D.setDirection(Rotation.ANTICLOCKWISE);
		Plot3D.setForegroundAlpha(0.60f);

	}

}
