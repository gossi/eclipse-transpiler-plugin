package si.gos.transpiler.core.transpiler;

import java.util.Map;

import org.eclipse.core.resources.IProject;

import si.gos.transpiler.core.model.ConfiguredTranspiler;

public interface ITranspilerManager {

	public Map<String, ITranspiler> getTranspilers();
	
	public ITranspiler getTranspiler(String id);
	
	public boolean isTranspilerInstalled(String id);
	
	public Map<String, InstalledTranspiler> getInstalledTranspilers();
	
	public InstalledTranspiler getInstalledTranspiler(String id);
	
	public void addInstalledTranspiler(InstalledTranspiler transpiler);
	
	public void removeInstalledTranspiler(InstalledTranspiler transpiler);
	
	public void saveInstalledTranspilers();
	
	public Map<String, ConfiguredTranspiler> getConfiguredTranspilers(IProject project);
	
	public void saveConfiguredTranspilers(IProject project, Map<String, ConfiguredTranspiler> configuredTranspilers);
	
}
