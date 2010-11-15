package de.lmu.ifi.dbs.medmon.medic.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.FillLayout;

import de.lmu.ifi.dbs.medmon.medic.ui.action.SaveCommentAction;
import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;
import de.lmu.ifi.dbs.medmon.rcp.platform.util.ResourceManager;

public class CommentView extends ViewPart {

	public static final String ID = "de.lmu.ifi.dbs.medmon.medic.ui.views.CommentView"; //$NON-NLS-1$
	private Text text;
	private SaveCommentAction saveCommentAction;

	public CommentView() {
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		
		Group groupComment = new Group(container, SWT.NONE);
		groupComment.setText("Kommentar");
		FillLayout groupLayout = new FillLayout(SWT.HORIZONTAL);
		groupLayout.spacing = 5;
		groupLayout.marginWidth = 5;
		groupLayout.marginHeight = 5;
		groupComment.setLayout(groupLayout);
		groupComment.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		text = new Text(groupComment, SWT.MULTI);

		createActions();
		
		ActionContributionItem save = new ActionContributionItem(saveCommentAction);
		save.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		save.fill(container);
		
		Button bSave = new Button(container, SWT.NONE);
		bSave.setText("Speichern");
		bSave.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		bSave.setImage(ResourceManager.getPluginImage(IMedmonConstants.RCP_PLUGIN, IMedmonConstants.IMG_SAVE_AS_16));

		
		initializeToolBar();
		initializeMenu();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		saveCommentAction = new SaveCommentAction("Speichern");
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
		toolbarManager.add(saveCommentAction);
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
	}

	@Override
	public void setFocus() {
		text.setFocus();
	}

}
