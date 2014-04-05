package si.gos.transpiler.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import si.gos.transpiler.core.transpiler.InstalledTranspiler;

public class ConfiguredTranspiler {

	private InstalledTranspiler transpiler;
	private List<PathEntry> paths = new ArrayList<PathEntry>();
//	private List<ConfiguredOption> options = new ArrayList<ConfiguredOption>();
	private Map<String, String> options = new HashMap<String, String>();
	
	
	/**
	 * @return the transpiler
	 */
	public InstalledTranspiler getInstalledTranspiler() {
		return transpiler;
	}
	
	/**
	 * @param transpiler the transpiler to set
	 */
	public void setInstalledTranspiler(InstalledTranspiler transpiler) {
		this.transpiler = transpiler;
	}
	
	/**
	 * Returns the id for this configured transpiler or null, if no 
	 * installed transpiler is set.
	 * 
	 * @return id
	 */
	public String getId() {
		if (transpiler != null) {
			return transpiler.getId();
		}
		return null;
	}
	
	public List<PathEntry> getPaths() {
		return paths;
	}
	
	public void setPaths(List<PathEntry> paths) {
		this.paths = paths;
	}
	
	public void addPath(PathEntry path) {
		path.setConfiguredTranspiler(this);
		paths.add(path);
	}
	
	public void removePath(PathEntry path) {
		path.setConfiguredTranspiler(null);
		paths.remove(path);
	}
	
	public void setOption(String name) {
		setOption(name, "");
	}
	
	public void setOption(String name, String value) {
		options.put(name, value);
	}
	
	public boolean hasOption(String name) {
		return options.containsKey(name);
	}

	public void removeOption(String name) {
		options.remove(name);
	}
	
	public String getOption(String name) {
		return options.get(name);
	}
	
	public Map<String, String> getOptions() {
		return options;
	}
}
