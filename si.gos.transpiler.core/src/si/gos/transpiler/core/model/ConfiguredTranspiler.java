package si.gos.transpiler.core.model;

import si.gos.transpiler.core.transpiler.InstalledTranspiler;

public class ConfiguredTranspiler {

	private InstalledTranspiler transpiler;
	private PathEntry[] paths;
	private ConfiguredOption[] options;
	
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
}
