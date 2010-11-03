package de.lmu.ifi.dbs.elki.gui.minigui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.logging.Level;

import javax.swing.AbstractListModel;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import de.lmu.ifi.dbs.elki.KDDTask;
import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.gui.util.DynamicParameters;
import de.lmu.ifi.dbs.elki.gui.util.LogPanel;
import de.lmu.ifi.dbs.elki.gui.util.ParameterTable;
import de.lmu.ifi.dbs.elki.gui.util.ParametersModel;
import de.lmu.ifi.dbs.elki.gui.util.SavedSettingsFile;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.utilities.FormatUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.ParameterException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.SerializedParameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.TrackParameters;
import de.lmu.ifi.dbs.elki.workflow.LoggingStep;

/**
 * Minimal GUI built around a table-based parameter editor.
 * 
 * @author Erich Schubert
 */
public class MiniGUI extends JPanel {
  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /**
   * Filename for saved settings
   */
  public static final String SAVED_SETTINGS_FILENAME = "MiniGUI-saved-settings.txt";

  /**
   * Newline used in output.
   */
  public static final String NEWLINE = System.getProperty("line.separator");

  /**
   * ELKI logger for the GUI
   */
  protected static final Logging logger = Logging.getLogger(MiniGUI.class);

  /**
   * Logging output area.
   */
  protected LogPanel outputArea;

  /**
   * The parameter table
   */
  protected ParameterTable parameterTable;

  /**
   * Parameter storage
   */
  protected DynamicParameters parameters;

  /**
   * Settings storage
   */
  protected SavedSettingsFile store = new SavedSettingsFile(SAVED_SETTINGS_FILENAME);

  /**
   * Combo box for saved settings
   */
  protected JComboBox savedCombo;

  /**
   * Model to link the combobox with
   */
  protected SettingsComboboxModel savedSettingsModel;

  /**
   * The "run" button.
   */
  protected JButton runButton;

  /**
   * Constructor
   */
  public MiniGUI() {
    super();
    this.setLayout(new GridBagLayout());

    {
      // Button panel
      JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

      // Combo box for saved settings
      savedSettingsModel = new SettingsComboboxModel(store);
      savedCombo = new JComboBox(savedSettingsModel);
      savedCombo.setEditable(true);
      savedCombo.setSelectedItem("[Saved Settings]");
      buttonPanel.add(savedCombo);

      // button to load settings
      JButton loadButton = new JButton("Load");
      loadButton.setMnemonic(KeyEvent.VK_L);
      loadButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(@SuppressWarnings("unused") ActionEvent e) {
          String key = savedSettingsModel.getSelectedItem();
          ArrayList<String> settings = store.get(key);
          if(settings != null) {
            outputArea.clear();
            outputArea.publish("Parameters: " + FormatUtil.format(settings, " ") + NEWLINE, Level.INFO);
            doSetParameters(settings);
          }
        }
      });
      buttonPanel.add(loadButton);
      // button to save settings
      JButton saveButton = new JButton("Save");
      saveButton.setMnemonic(KeyEvent.VK_S);
      saveButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(@SuppressWarnings("unused") ActionEvent e) {
          String key = savedSettingsModel.getSelectedItem();
          // Stop editing the table.
          parameterTable.editCellAt(-1, -1);
          store.put(key, parameters.serializeParameters());
          try {
            store.save();
          }
          catch(IOException e1) {
            logger.exception(e1);
          }
          savedSettingsModel.update();
        }
      });
      buttonPanel.add(saveButton);
      // button to remove saved settings
      JButton removeButton = new JButton("Remove");
      removeButton.setMnemonic(KeyEvent.VK_E);
      removeButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(@SuppressWarnings("unused") ActionEvent e) {
          String key = savedSettingsModel.getSelectedItem();
          store.remove(key);
          try {
            store.save();
          }
          catch(IOException e1) {
            logger.exception(e1);
          }
          savedCombo.setSelectedItem("[Saved Settings]");
          savedSettingsModel.update();
        }
      });
      buttonPanel.add(removeButton);

      // button to launch the task
      runButton = new JButton("Run Task");
      runButton.setMnemonic(KeyEvent.VK_R);
      runButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(@SuppressWarnings("unused") ActionEvent e) {
          startTask();
        }
      });
      buttonPanel.add(runButton);

      GridBagConstraints constraints = new GridBagConstraints();
      constraints.fill = GridBagConstraints.HORIZONTAL;
      constraints.gridx = 0;
      constraints.gridy = 1;
      constraints.weightx = 1.0;
      constraints.weighty = 0.01;
      add(buttonPanel, constraints);
    }

    {
      // Setup parameter storage and table model
      this.parameters = new DynamicParameters();
      ParametersModel parameterModel = new ParametersModel(parameters);
      parameterModel.addTableModelListener(new TableModelListener() {
        @Override
        public void tableChanged(@SuppressWarnings("unused") TableModelEvent e) {
          // logger.debug("Change event.");
          updateParameterTable();
        }
      });

      // Create parameter table
      parameterTable = new ParameterTable(parameterModel, parameters);
      // Create the scroll pane and add the table to it.
      JScrollPane scrollPane = new JScrollPane(parameterTable);

      // Add the scroll pane to this panel.
      GridBagConstraints constraints = new GridBagConstraints();
      constraints.fill = GridBagConstraints.BOTH;
      constraints.gridx = 0;
      constraints.gridy = 0;
      constraints.weightx = 1;
      constraints.weighty = 1;
      add(scrollPane, constraints);
    }

    {
      // setup text output area
      outputArea = new LogPanel();

      // Create the scroll pane and add the table to it.
      JScrollPane outputPane = new JScrollPane(outputArea);
      outputPane.setPreferredSize(new Dimension(800, 400));

      // Add the output pane to the bottom
      GridBagConstraints constraints = new GridBagConstraints();
      constraints.fill = GridBagConstraints.BOTH;
      constraints.gridx = 0;
      constraints.gridy = 2;
      constraints.weightx = 1;
      constraints.weighty = 1;
      add(outputPane, constraints);

      // reconfigure logging
      outputArea.becomeDefaultLogger();
    }

    // refresh Parameters
    ArrayList<String> ps = new ArrayList<String>();
    doSetParameters(ps);

    // load saved settings (we wanted to have the logger first!)
    try {
      store.load();
      savedSettingsModel.update();
    }
    catch(FileNotFoundException e) {
      // Ignore - probably didn't save any settings yet.
    }
    catch(IOException e) {
      logger.exception(e);
    }

  }

  /**
   * Serialize the parameter table and run setParameters()
   */
  protected void updateParameterTable() {
    parameterTable.setEnabled(false);
    ArrayList<String> params = parameters.serializeParameters();
    outputArea.clear();
    outputArea.publish("Parameters: " + FormatUtil.format(params, " ") + NEWLINE, Level.INFO);
    doSetParameters(params);
    parameterTable.setEnabled(true);
  }

  /**
   * Do the actual setParameters invocation.
   * 
   * @param params Parameters
   */
  protected void doSetParameters(ArrayList<String> params) {
    SerializedParameterization config = new SerializedParameterization(params);
    TrackParameters track = new TrackParameters(config);
    new LoggingStep(track);
    new KDDTask<DatabaseObject>(track);
    config.logUnusedParameters();
    // config.logAndClearReportedErrors();
    if(config.getErrors().size() > 0) {
      reportErrors(config);
      runButton.setEnabled(false);
    }
    else {
      runButton.setEnabled(true);
    }

    List<String> remainingParameters = config.getRemainingParameters();

    // update table:
    parameterTable.setEnabled(false);
    parameters.updateFromTrackParameters(track);
    // Add remaining parameters
    if(remainingParameters != null && !remainingParameters.isEmpty()) {
      DynamicParameters.RemainingOptions remo = new DynamicParameters.RemainingOptions();
      try {
        remo.setValue(FormatUtil.format(remainingParameters, " "));
      }
      catch(ParameterException e) {
        logger.exception(e);
      }
      BitSet bits = new BitSet();
      bits.set(DynamicParameters.BIT_INVALID);
      bits.set(DynamicParameters.BIT_SYNTAX_ERROR);
      parameters.addParameter(remo, remo.getValue(), bits, 0);
    }

    parameterTable.revalidate();
    parameterTable.setEnabled(true);
  }

  /**
   * Do a full run of the KDDTask with the specified parameters.
   */
  protected void startTask() {
    parameterTable.editCellAt(-1, -1);
    parameterTable.setEnabled(false);
    final ArrayList<String> params = parameters.serializeParameters();
    parameterTable.setEnabled(true);

    runButton.setEnabled(false);

    outputArea.clear();
    outputArea.publish("Running: " + FormatUtil.format(params, " ") + NEWLINE, Level.INFO);

    SwingWorker<Void, Void> r = new SwingWorker<Void, Void>() {
      @Override
      public Void doInBackground() {
        SerializedParameterization config = new SerializedParameterization(params);
        new LoggingStep(config);
        KDDTask<DatabaseObject> task = new KDDTask<DatabaseObject>(config);
        try {
          config.logUnusedParameters();
          if(config.getErrors().size() == 0) {
            task.run();
          }
          else {
            reportErrors(config);
          }
        }
        catch(Exception e) {
          logger.exception(e);
        }
        return null;
      }

      @Override
      protected void done() {
        super.done();
        runButton.setEnabled(true);
      }
    };
    r.execute();
  }

  /**
   * Report errors in a single error log record.
   * 
   * @param config Parameterization
   */
  protected void reportErrors(SerializedParameterization config) {
    StringBuffer buf = new StringBuffer();
    buf.append("Could not run task because of configuration errors:" + NEWLINE + NEWLINE);
    for(ParameterException e : config.getErrors()) {
      buf.append(e.getMessage() + NEWLINE);
    }
    logger.warning(buf.toString());
    config.clearErrors();
  }

  /**
   * Create the GUI and show it. For thread safety, this method should be
   * invoked from the event-dispatching thread.
   */
  protected static void createAndShowGUI() {
    // Create and set up the window.
    JFrame frame = new JFrame("ELKI MiniGUI");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      // Class<?> cls =
      // ClassLoader.getSystemClassLoader().loadClass("org.jdesktop.swinghelper.debug.CheckThreadViolationRepaintManager");
      // RepaintManager.setCurrentManager((RepaintManager) cls.newInstance());
    }
    catch(Exception e) {
      // ignore
    }
    try {
      frame.setIconImage(new ImageIcon(KDDTask.class.getResource("elki-icon.png")).getImage());
    }
    catch(Exception e) {
      // Ignore - icon not found is not fatal.
    }

    // Create and set up the content pane.
    MiniGUI newContentPane = new MiniGUI();
    newContentPane.setOpaque(true); // content panes must be opaque
    frame.setContentPane(newContentPane);

    // Display the window.
    frame.pack();
    frame.setVisible(true);
  }

  /**
   * Main method that just spawns the UI.
   * 
   * @param args command line parameters
   */
  public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        createAndShowGUI();
      }
    });
  }

  /**
   * Class to interface between the saved settings list and a JComboBox
   * 
   * @author Erich Schubert
   */
  class SettingsComboboxModel extends AbstractListModel implements ComboBoxModel {
    /**
     * Serial version
     */
    private static final long serialVersionUID = 1L;

    /**
     * Settings storage
     */
    protected SavedSettingsFile store;

    /**
     * Selected entry
     */
    protected String selected = null;

    /**
     * Constructor
     * 
     * @param store Store to access
     */
    public SettingsComboboxModel(SavedSettingsFile store) {
      super();
      this.store = store;
    }

    @Override
    public String getSelectedItem() {
      return selected;
    }

    @Override
    public void setSelectedItem(Object anItem) {
      if(anItem instanceof String) {
        selected = (String) anItem;
      }
    }

    @Override
    public Object getElementAt(int index) {
      return store.getElementAt(index).first;
    }

    @Override
    public int getSize() {
      return store.size();
    }

    /**
     * Force an update
     */
    public void update() {
      fireContentsChanged(this, 0, getSize() + 1);
    }
  }
}