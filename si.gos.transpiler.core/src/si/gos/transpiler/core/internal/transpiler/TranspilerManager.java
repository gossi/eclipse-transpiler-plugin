package si.gos.transpiler.core.internal.transpiler;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;

import si.gos.transpiler.core.TranspilerPlugin;
import si.gos.transpiler.core.model.ConfiguredTranspiler;
import si.gos.transpiler.core.model.PathEntry;
import si.gos.transpiler.core.preferences.PreferenceConstants;
import si.gos.transpiler.core.transpiler.AbstractTranspiler;
import si.gos.transpiler.core.transpiler.ITranspiler;
import si.gos.transpiler.core.transpiler.ITranspilerManager;
import si.gos.transpiler.core.transpiler.InstalledTranspiler;

public class TranspilerManager implements ITranspilerManager {

	public static final String TRANSPILER_EXTENSION_POINT = "si.gos.transpiler.core.transpiler";
	
	private final static String TRANSPILER = "transpiler";
	private final static String NAME = "name";
	private final static String PATH = "path";
	private final static String CMD = "cmd";
	private final static String SOURCE_EXTENSION = "source-extension";
	private final static String DESTINATION_EXTENSION = "destination-extension";
	
	private final static String OPTIONS = "options";
	private final static String PATHS = "paths";
	private final static String PATH_SEPARATOR = ":";
	private final static String RESOURCE_SEPARATOR = "::";

	private IEclipsePreferences prefs = TranspilerPlugin.getDefault().getPreferences();
	private IPreferenceStore store = TranspilerPlugin.getDefault().getPreferenceStore();;
	
	private Map<String, ITranspiler> transpilers;
	private Map<String, InstalledTranspiler> installedTranspilers;
	private Map<IProject, Map<String, ConfiguredTranspiler>> configuredTranspilers;

	public TranspilerManager() {
		transpilers = loadTranspilers();
		installedTranspilers = loadInstalledTranspilers();
		configuredTranspilers = new LinkedHashMap<IProject, Map<String, ConfiguredTranspiler>>();
	}

	public Map<String, ITranspiler> getTranspilers() {
		return transpilers;
	}
	
	private Map<String, ITranspiler> loadTranspilers() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(TRANSPILER_EXTENSION_POINT);
		
		Map<String, ITranspiler> transpilers = new LinkedHashMap<String, ITranspiler>();
		for (IConfigurationElement element : elements) {
			try {
				Object transpiler = element.createExecutableExtension("class");
				if (transpiler instanceof AbstractTranspiler) {
					((AbstractTranspiler)transpiler).init(element);
					transpilers.put(element.getAttribute("id"), (ITranspiler) transpiler);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		
		return transpilers;
	}

	@Override
	public ITranspiler getTranspiler(String id) {
		if (transpilers.containsKey(id)) {
			return transpilers.get(id);
		}
		
		return null;
	}
	
	@Override
	public boolean isTranspilerInstalled(String id) {
		getInstalledTranspilers();
		for (InstalledTranspiler transpiler : installedTranspilers.values()) {
			if (transpiler.getTranspiler().getId().equals(id)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Map<String, InstalledTranspiler> getInstalledTranspilers() {
		return installedTranspilers;
	}
	
	public InstalledTranspiler getInstalledTranspiler(String id) {
		if (installedTranspilers.containsKey(id)) {
			return installedTranspilers.get(id);
		}
		
		return null;
	}

	public void addInstalledTranspiler(InstalledTranspiler transpiler) {
		installedTranspilers.put(transpiler.getId(), transpiler);
	}
	
	public void removeInstalledTranspiler(InstalledTranspiler transpiler) {
		installedTranspilers.remove(transpiler.getId());
	}

	private Map<String, InstalledTranspiler> loadInstalledTranspilers() {
		Map<String, InstalledTranspiler> transpilers = new LinkedHashMap<String, InstalledTranspiler>();
		for (String id : getInstalledTranspilerIds()) {
			InstalledTranspiler itp = loadInstalledTranspiler(id);
			if (itp != null) {
				transpilers.put(id, itp);
			}
		}
		
		return transpilers;
	}
	
	private InstalledTranspiler loadInstalledTranspiler(String id) {
		String root = PreferenceConstants.TRANSPILERS + "/" + id;
		
		try {
			if (prefs.nodeExists(root)) {
				IEclipsePreferences node = (IEclipsePreferences)prefs.node(root);
				
				String transpilerId = node.get(TRANSPILER, "");
				InstalledTranspiler transpiler = new InstalledTranspiler();
				transpiler.setId(id);
				transpiler.setName(node.get(NAME, ""));
				transpiler.setPath(node.get(PATH, ""));
				transpiler.setCmd(node.get(CMD, ""));
				transpiler.setSourceExtension(node.get(SOURCE_EXTENSION, ""));
				transpiler.setDestinationExtension(node.get(DESTINATION_EXTENSION, ""));
				transpiler.setTranspilerId(transpilerId);
				transpiler.setTranspiler(getTranspiler(transpilerId));
				
				return transpiler;
			}
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private String[] getInstalledTranspilerIds() {
		String list = store.getString(PreferenceConstants.TRANSPILERS);
		if (list.equals("")) {
			return new String[]{};
		}
		return list.split(",");
	}
	
	public void saveInstalledTranspilers() {
		
		try {
			// clear first
			for (String id : getInstalledTranspilerIds()) {
				String root = PreferenceConstants.TRANSPILERS + "/" + id;	
				prefs.node(root).removeNode();
			}

			// save transpilers
			for (InstalledTranspiler transpiler : installedTranspilers.values()) {
				String root = PreferenceConstants.TRANSPILERS + "/" + transpiler.getId();
				IEclipsePreferences node = (IEclipsePreferences)prefs.node(root);
				
				node.put(TRANSPILER, transpiler.getTranspilerId());
				node.put(CMD, transpiler.getCmd());
				node.put(PATH, transpiler.getPath());
				node.put(NAME, transpiler.getName());
				node.put(SOURCE_EXTENSION, transpiler.getSourceExtension());
				node.put(DESTINATION_EXTENSION, transpiler.getDestinationExtension());
				node.flush();
			}

			// save list
			String list = StringUtils.join(installedTranspilers.keySet().toArray(new String[]{}), ',');
			store.putValue(PreferenceConstants.TRANSPILERS, list);
		
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, ConfiguredTranspiler> getConfiguredTranspilers(IProject project) {
		if (configuredTranspilers.containsKey(project)) {
			return configuredTranspilers.get(project);
		}

		IEclipsePreferences prefs = TranspilerPlugin.getDefault().getProjectPreferences(project);
		String list = prefs.get(PreferenceConstants.TRANSPILERS, "");
		String[] ids = new String[]{};
		if (!list.equals("")) {
			ids = list.split(","); 
		}
		
		Map<String, ConfiguredTranspiler> ctps = new LinkedHashMap<String, ConfiguredTranspiler>();
		for (String id : ids) {
			ConfiguredTranspiler ctp = new ConfiguredTranspiler();
			ctp.setInstalledTranspiler(getInstalledTranspiler(id));
			String root = PreferenceConstants.TRANSPILERS + "/" + id;
			IEclipsePreferences tp = (IEclipsePreferences)prefs.node(root);
			
			// load paths
			String paths = tp.get(PATHS, "");

			if (paths.length() > 0) {
				String resources[] = paths.split(RESOURCE_SEPARATOR);

				for (String resource : resources) {
					String parts[] = resource.split(PATH_SEPARATOR);
					ctp.addPath(new PathEntry(project.findMember(parts[0]), project.findMember(parts[1])));
				}
			}
			
			// load options
			String optionsString = tp.get(OPTIONS, "");
			
			if (optionsString.length() > 0) {
				String options[] = optionsString.split(RESOURCE_SEPARATOR);
			
				for (String option : options) {
					String parts[] = option.split("=");
					ctp.setOption(parts[0]);
					if (parts.length > 1) {
						ctp.setOption(parts[0], parts[1]);	
					}
				}
			}
			
			// add to collection
			ctps.put(ctp.getId(), ctp);
		}
		configuredTranspilers.put(project, ctps);
		
		return ctps;
	}

	@Override
	public void saveConfiguredTranspilers(IProject project, Map<String, ConfiguredTranspiler> configuredTranspilers) {
		IEclipsePreferences prefs = TranspilerPlugin.getDefault().getProjectPreferences(project);
		prefs.put(PreferenceConstants.TRANSPILERS, StringUtils.join(configuredTranspilers.keySet().toArray(new String[]{}), ','));
		
		// save
		try {
			for (ConfiguredTranspiler ctp : configuredTranspilers.values()) {
				String root = PreferenceConstants.TRANSPILERS + "/" + ctp.getId();
				IEclipsePreferences tp = (IEclipsePreferences)prefs.node(root);
				
				// paths
				List<String> resources = new LinkedList<String>();
				
				for (PathEntry path : ctp.getPaths()) {
					resources.add(path.getSource().getProjectRelativePath() + PATH_SEPARATOR + path.getDestination().getProjectRelativePath());
				}
				
				tp.put(PATHS, StringUtils.join(resources, RESOURCE_SEPARATOR));
				
				// options
				List<String> options = new LinkedList<String>();
				
				for (Map.Entry<String, String> option : ctp.getOptions().entrySet()) {
					StringBuilder sb = new StringBuilder(option.getKey());
					
					if (!option.getValue().isEmpty()) {
						sb.append("=" + option.getValue());
					}
					
					options.add(sb.toString());
				}
				
				tp.put(OPTIONS, StringUtils.join(options, RESOURCE_SEPARATOR));
			}

			this.configuredTranspilers.put(project, configuredTranspilers);
			prefs.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
}
