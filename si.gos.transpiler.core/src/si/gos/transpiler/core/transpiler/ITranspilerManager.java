package si.gos.transpiler.core.transpiler;

import java.util.Map;

public interface ITranspilerManager {

	public Map<String, ITranspiler> getTranspilers();
	
	public ITranspiler getTranspiler(String id);
	
	public boolean isTranspilerInstalled(String id);
	
	public Map<String, InstalledTranspiler> getInstalledTranspilers();
	
	public void addInstalledTranspiler(InstalledTranspiler transpiler);
	
	public void removeInstalledTranspiler(InstalledTranspiler transpiler);
	
	public void saveInstalledTranspilers();
	
	
}
