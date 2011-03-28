/**
 * 
 */
package de.lmu.ifi.dbs.medmon.medic.core.util;

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 28.03.2011
 */
public interface IMedmonConstants {

	public static final String BASE_UI_PLUGIN = "de.lmu.ifi.dbs.medmon.base.ui";
	
	/* ==================== */
	/* ====== Folders ===== */
	/* ==================== */
	
	public static final String DIR_USER_HOME = System.getProperty("user.home");
	public static final String DIR_SEPERATOR = System.getProperty("file.separator");
	public static final String DIR_MEDMON = DIR_USER_HOME + DIR_SEPERATOR + ".medmon";
	public static final String DIR_DERBY = DIR_MEDMON + DIR_SEPERATOR + "db";
	public static final String DIR_DPU = DIR_MEDMON + DIR_SEPERATOR + "dpu";
	public static final String DIR_CU = DIR_MEDMON + DIR_SEPERATOR + "cluster";
	
	/* ==================== */
	/* ====== Images ====== */
	/* ==================== */
	
	//Images 16x16
	public static final String IMG_ADD_16 = "icons/16/gtk-add.png";
	public static final String IMG_ARROW_DOWN_16 = "icons/16/gtk-go-down.png";
	public static final String IMG_ARROW_UP_16 = "icons/16/gtk-go-up.png";
	public static final String IMG_HELP_16 = "icons/16/help.png";
	public static final String IMG_IMAGE_16 = "icons/16/gtk-image.png";
	public static final String IMG_OPEN_16 = "icons/16/gtk-open.png";
	public static final String IMG_REMOVE_16 = "icons/16/gtk-remove.png";
	public static final String IMG_REFRESH_16 = "icons/16/gtk-refresh.png";
	public static final String IMG_SAVE_AS_16 = "icons/16/gtk-save-as.png";
	public static final String IMG_VIEW_DETAIL_16 = "icons/16/view_detailed.png";
	public static final String IMG_VIEW_TREE_16 = "icons/16/view_tree.png";
	//public static final String CHART_16 = "icons"; 
	
	//Images 24x24
	public static final String IMG_APPLY_24 = "icons/24/gtk-apply.png";
	public static final String IMG_ARROW_DOWN_24 = "icons/24/gtk-go-down.png";
	public static final String IMG_ARROW_UP_24 = "icons/24/gtk-go-up.png";
	public static final String IMG_REMOVE_24 = "icons/24/gtk-remove.png";
	public static final String IMG_REFRESH_24 = "icons/24/gtk-refresh.png";
	public static final String IMG_OPEN_24 = "icons/24/gtk-open.png";
	public static final String IMG_PLAY_24 = "icons/24/gtk-play.png";
	public static final String IMG_SAVE_AS_24 = "icons/24/gtk-save-as.png";
	
	
	//Images 48x48
	public static final String IMG_DIRECTORY_48 = "icons/48/gtk-directory.png";
	public static final String IMG_CHART_48 = "icons/48/gtk-chart.png";
	public static final String IMG_PLAY_48 = "icons/48/gtk-play.png";
	public static final String IMG_SEARCH_48 = "icons/48/gtk-find.png";
	public static final String IMG_DATA_48 = "icons/48/gtk-removable.png";
	
	//Images BIG
	public static final String IMG_ARROW_DOWN_BIG = "icons/gtk-go-down.png";
}
