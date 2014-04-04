package si.gos.transpiler.core.builder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import si.gos.eclipse.exec.Launcher;
import si.gos.transpiler.core.TranspilerNature;
import si.gos.transpiler.core.model.ConfiguredTranspiler;
import si.gos.transpiler.core.model.PathEntry;
import si.gos.transpiler.core.model.ResourceLocator;

public class TranspilerBuilder extends IncrementalProjectBuilder {

	public static final String ID = "si.gos.transpiler.builder.TranspilerBuilder";
	
	private Map<String, PathEntry> cache;
	
	public TranspilerBuilder() {
		cache = new HashMap<String, PathEntry>();
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
			searchAndTranspile(delta.getAffectedChildren(), locator, launcher);
		}
		
		return null;
	}
	
	private void searchAndTranspile(IResourceDelta[] affectedChildren, ResourceLocator locator, Launcher launcher) {
		for (IResourceDelta affected : affectedChildren) {
			IPath path = affected.getProjectRelativePath();

			PathEntry pathEntry = null;
			String cacheKey = path.toString();
			if (cache.containsKey(cacheKey)) {
				pathEntry = cache.get(cacheKey);
			} else {
				pathEntry = locator.getPath(path);
				cache.put(cacheKey, pathEntry);
			}

			if (pathEntry != null) {
				transpile(pathEntry, launcher);
			}

			searchAndTranspile(affected.getAffectedChildren(), locator, launcher);
		}
	}

	private void transpile(PathEntry pathEntry, Launcher launcher) {
		ConfiguredTranspiler ct = pathEntry.getTranspiler();

		String source = pathEntry.getSource().getProjectRelativePath().toOSString();
		String dest = pathEntry.getDestination().getProjectRelativePath().toOSString();
		String path = ct.getInstalledTranspiler().getPath().toString();
		
		Map<String, String> subs = new HashMap<String, String>();
		subs.put("source", source);
		subs.put("destination", dest);

		CommandLine cmd = ct.getInstalledTranspiler().getTranspiler().getCommand(path, ct.getOptions());
		cmd.setSubstitutionMap(subs);

//		System.out.println("Transpiler: " + ct.getInstalledTranspiler().getTranspiler().getName());
//		System.out.println("From: " + source + ", To: " + dest);
//		System.out.println("Cmd: " + cmd.getExecutable());
		
		try {
			launcher.launch(cmd);
		} catch (ExecuteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
