package si.gos.transpiler.core.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

import si.gos.transpiler.core.TranspilerPlugin;
import si.gos.transpiler.core.transpiler.ITranspilerManager;

public class ResourceLocator {

	private IProject project;
	private ITranspilerManager manager;
	private List<PathEntry> paths;
	private List<IResource> resources;
	
	public ResourceLocator(IProject project) {
		this.project = project;
		manager = TranspilerPlugin.getDefault().getTranspilerManager();
		fetchSources();
	}
	
	public void fetchSources() {
		paths = new ArrayList<PathEntry>();
		resources = new ArrayList<IResource>();
		List<ConfiguredTranspiler> cts = new ArrayList<ConfiguredTranspiler>(manager.getConfiguredTranspilers(project).values());
		
		for (ConfiguredTranspiler ct : cts) {
			for (PathEntry path : ct.getPaths()) {
				paths.add(path);
				resources.add(path.getSource());
			}
		}
	}

	public PathEntry getPath(IPath path) {
		PathEntry found = null;
		for (PathEntry pathEntry : paths) {
			IResource res =  pathEntry.getSource();
			IPath resPath = res.getProjectRelativePath();

			if (resPath.equals(path)) {
				// extension check
				if (res instanceof IFile 
						|| resPath.getFileExtension().equals(pathEntry.getTranspiler().getInstalledTranspiler().getExtension())) {
					return pathEntry;
				}
			}
		}

		return found;
	}
}
