package si.gos.transpiler.core.builder;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import si.gos.transpiler.core.TranspilerNature;
import si.gos.transpiler.core.model.ConfiguredTranspiler;
import si.gos.transpiler.core.model.PathEntry;
import si.gos.transpiler.core.model.ResourceLocator;

public class TranspilerBuilder extends IncrementalProjectBuilder {

	public static final String ID = "si.gos.transpiler.builder.TranspilerBuilder";
	
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
		
		IResourceDelta delta = getDelta(project);
		
		
		if (delta != null) {
			// TODO: do some caching here
			ResourceLocator locator = new ResourceLocator(project);
			searchAndTranspile(delta.getAffectedChildren(), locator);
		}
		
		return null;
	}
	
	private void searchAndTranspile(IResourceDelta[] affectedChildren, ResourceLocator locator) {
		for (IResourceDelta affected : affectedChildren) {
			IPath path = affected.getProjectRelativePath();
			
			PathEntry pathEntry = locator.getPath(path);
			
			if (pathEntry != null) {
				ConfiguredTranspiler ct = pathEntry.getTranspiler();
				System.out.println("Transpiler: " + ct.getInstalledTranspiler().getTranspiler().getName());
				System.out.println("From: " + pathEntry.getSource().getProjectRelativePath() + ", To: " + pathEntry.getDestination().getProjectRelativePath());
			}
			
			searchAndTranspile(affected.getAffectedChildren(), locator);
		}
	}

}
