package si.gos.transpiler.core.builder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import si.gos.eclipse.exec.Launcher;
import si.gos.transpiler.core.TranspilerNature;
import si.gos.transpiler.core.model.ConfiguredTranspiler;
import si.gos.transpiler.core.model.ResourceLocator;
import si.gos.transpiler.core.model.TranspileItem;
import si.gos.transpiler.core.transpiler.InstalledTranspiler;

public class TranspilerBuilder extends IncrementalProjectBuilder {

	public static final String ID = "si.gos.transpiler.builder.TranspilerBuilder";
	
	private Map<String, TranspileItem> cache;
	
	public TranspilerBuilder() {
		cache = new HashMap<String, TranspileItem>();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor)
			throws CoreException {
		
		IProject project = getProject();
		
		if (project.hasNature(TranspilerNature.NATURE_ID) == false) {
			return null;
		}
		
		Launcher launcher = new Launcher(project);
		launcher.addResponseListener(new ConsoleResponseHandler());
		
		IResourceDelta delta = getDelta(project);

		if (delta != null) {
			ResourceLocator locator = new ResourceLocator(project);
			searchAndTranspile(delta.getAffectedChildren(), locator, launcher, monitor);
		}
		
		return null;
	}
	
	private void searchAndTranspile(IResourceDelta[] affectedChildren, ResourceLocator locator, Launcher launcher, IProgressMonitor monitor) {
		for (IResourceDelta affected : affectedChildren) {
			IPath path = affected.getProjectRelativePath();
			
			if (affected.getResource() instanceof IFile) {
				TranspileItem transpileItem = null;
				// TODO: Cache, see: https://stackoverflow.com/questions/22886012/get-the-instance-for-an-eclipse-builder
//				String cacheKey = path.toString();
//				if (cache.containsKey(cacheKey)) {
//					transpileItem = cache.get(cacheKey);
//				} else {
					transpileItem = locator.getTranspileItem(path);
//					cache.put(cacheKey, transpileItem);
//				}

				if (transpileItem != null) {
					transpile(transpileItem, launcher, monitor);
				}
			}

			searchAndTranspile(affected.getAffectedChildren(), locator, launcher, monitor);
		}
	}

	private void transpile(TranspileItem transpileItem, Launcher launcher, IProgressMonitor monitor) {
		ConfiguredTranspiler ct = transpileItem.getConfiguredTranspiler();
		InstalledTranspiler itp = transpileItem.getInstalledTranspiler();

		String source = transpileItem.getSource().toOSString();
		String dest = transpileItem.getDestination().toOSString();
		String path = itp.getPath().toString();
		
		Map<String, String> subs = new HashMap<String, String>();
		subs.put("source", source);
		subs.put("destination", dest);

		CommandLine cmd = itp.getTranspiler().getCommand(path, ct.getOptions());
		cmd.setSubstitutionMap(subs);

//		System.out.println("Transpiler: " + ct.getInstalledTranspiler().getTranspiler().getName());
//		System.out.println("From: " + source + ", To: " + dest);
//		System.out.println("Cmd: " + cmd);
		
		try {
			launcher.launch(cmd);
			
			// refresh workspace
			IProject project = getProject();
			IResource res = project.findMember(dest);
			if (res != null) {
				res.refreshLocal(IResource.DEPTH_ONE, monitor);
			}
		} catch (ExecuteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public void updateCache(ResourceLocator locator) {
		IProject project = locator.getProject();
		for (String cacheKey : cache.keySet()) {
			IPath path = project.findMember(cacheKey).getProjectRelativePath();
			
			TranspileItem item = locator.getTranspileItem(path);
			if (item == null) {
				cache.remove(cacheKey);
			}
		}
	}
	
}
