package si.gos.transpiler.core.launch;

import javax.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IExtensionRegistry;

import si.gos.eclipse.exec.Environment;
import si.gos.eclipse.exec.EnvironmentFactory;

public class TranspilerFactory implements EnvironmentFactory {

	@Inject
	public TranspilerFactory(IExtensionRegistry registry) {
//		try {
//			IConfigurationElement[] config = registry.getConfigurationElementsFor(LAUNCHER_ID);
//			for (IConfigurationElement e : config) {
//				final EnvironmentFactory factory = (EnvironmentFactory) e.createExecutableExtension("class");
//				if (factory != null) {
//					factories.put(e.getAttribute("id"), factory);
//				}
//			}
//		} catch (Exception e) {
////			Logger.logException(e);
//		}
	}
	
	@Override
	public Environment getEnvironment(IProject project) {
		
		return null;
	}

}
