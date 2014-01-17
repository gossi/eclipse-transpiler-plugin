package si.gos.transpiler.core.model;

import java.util.ArrayList;
import java.util.List;

import si.gos.transpiler.core.transpiler.InstalledTranspiler;

public class ConfiguredTranspiler {

	private InstalledTranspiler transpiler;
	private List<PathEntry> paths = new ArrayList<PathEntry>();
	private List<ConfiguredOption> options = new ArrayList<ConfiguredOption>();
	
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
		paths.add(path);
	}
	
	public void removePath(PathEntry path) {
		paths.remove(path);
	}
}
