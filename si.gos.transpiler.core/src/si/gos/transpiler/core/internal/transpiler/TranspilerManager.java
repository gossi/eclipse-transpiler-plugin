package si.gos.transpiler.core.internal.transpiler;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;

import si.gos.transpiler.core.TranspilerPlugin;
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

	private IEclipsePreferences prefs = TranspilerPlugin.getDefault().getPreferences();
	private IPreferenceStore store = TranspilerPlugin.getDefault().getPreferenceStore();;
	
	private Map<String, ITranspiler> transpilers;
	private Map<String, InstalledTranspiler> installedTranspilers;
	
	public TranspilerManager() {
		transpilers = loadTranspilers();
		installedTranspilers = loadInstalledTranspilers();
	}

	public Map<String, ITranspiler> getTranspilers() {
		return transpilers;
	}
	
	private Map<String, ITranspiler> loadTranspilers() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(TRANSPILER_EXTENSION_POINT);
		
		Map<String, ITranspiler> transpilers = new HashMap<String, ITranspiler>();
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

	public void addInstalledTranspiler(InstalledTranspiler transpiler) {
		installedTranspilers.put(transpiler.getId(), transpiler);
	}
	
	public void removeInstalledTranspiler(InstalledTranspiler transpiler) {
		installedTranspilers.remove(transpiler.getId());
	}
	
	private Map<String, InstalledTranspiler> loadInstalledTranspilers() {
		Map<String, InstalledTranspiler> transpilers = new HashMap<String, InstalledTranspiler>();
		for (String id : getInstalledTranspilerIds()) {
			transpilers.put(id, loadInstalledTranspiler(id));
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
				node.flush();
			}

			// save list
			String list = StringUtils.join(installedTranspilers.keySet().toArray(new String[]{}));
			store.putValue(PreferenceConstants.TRANSPILERS, list);
		
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
}
