package si.gos.transpiler.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import si.gos.transpiler.core.internal.transpiler.TranspilerManager;
import si.gos.transpiler.core.transpiler.ITranspilerManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class TranspilerPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "si.gos.transpiler.core"; //$NON-NLS-1$

	// The shared instance
	private static TranspilerPlugin plugin;
	
	private TranspilerManager transpilerManager;
	
	/**
	 * The constructor
	 */
	public TranspilerPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		transpilerManager = new TranspilerManager();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static TranspilerPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	public ITranspilerManager getTranspilerManager() {
		return transpilerManager;
	}
	
	public IEclipsePreferences getProjectPreferences(IProject project) {
		ProjectScope ps = new ProjectScope(project);
		return ps.getNode(PLUGIN_ID);
	}
	
	public IEclipsePreferences getPreferences() {
		return ConfigurationScope.INSTANCE.getNode(PLUGIN_ID);
	}
	
	public IEclipsePreferences getPreferences(String path) {
		return ConfigurationScope.INSTANCE.getNode(PLUGIN_ID + "/" + path);
	}
}
