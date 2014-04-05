package si.gos.transpiler.core.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

import si.gos.transpiler.core.TranspilerPlugin;
import si.gos.transpiler.core.transpiler.ITranspiler;
import si.gos.transpiler.core.transpiler.ITranspilerManager;
import si.gos.transpiler.core.transpiler.InstalledTranspiler;

public class ResourceLocator {

	private IProject project;
	private ITranspilerManager manager;
	private List<PathEntry> paths;
	
	public ResourceLocator(IProject project) {
		this.project = project;
		manager = TranspilerPlugin.getDefault().getTranspilerManager();
		fetchSources();
	}
	
	public void fetchSources() {
		paths = new ArrayList<PathEntry>();
		List<ConfiguredTranspiler> cts = new ArrayList<ConfiguredTranspiler>(manager.getConfiguredTranspilers(project).values());
		
		for (ConfiguredTranspiler ct : cts) {
			for (PathEntry path : ct.getPaths()) {
				paths.add(path);
			}
		}
		
		StringBuilder sb = new StringBuilder();
		for (PathEntry path : paths) {
			sb.append(path.getSource().getProjectRelativePath() + ", ");
		}
		System.out.println("Paths: " + sb);
	}

	public TranspileItem getTranspileItem(IPath path) {

		for (PathEntry pathEntry : paths) {
			IResource sourceResource = pathEntry.getSource();
			
			if (sourceResource != null && sourceResource.exists()) {
				IPath sourcePath = sourceResource.getProjectRelativePath();
				InstalledTranspiler itp = pathEntry.getConfiguredTranspiler().getInstalledTranspiler();
				ITranspiler transpiler = itp.getTranspiler();
	
				if (sourceResource instanceof IFile && sourcePath.equals(path)) {
					IPath dest = transpiler.getOutputOption(path, pathEntry.getDestination().getProjectRelativePath());
					return new TranspileItem(pathEntry, path, dest);
				} else if (sourceResource instanceof IFolder) {
					sourcePath = sourcePath.addTrailingSeparator();
					IPath folder = path.removeLastSegments(1).addTrailingSeparator();
					String sourceExt = itp.getSourceExtension();

					if (sourcePath.equals(folder) && path.getFileExtension().equals(sourceExt)) {
						String filename = path.lastSegment();
						filename = filename.replaceFirst("(.*)" + sourceExt + "$", "$1" + itp.getDestinationExtension()); 
						IPath destFolder = pathEntry.getDestination().getProjectRelativePath();
						IPath dest = transpiler.getOutputOption(path, destFolder.append(filename));
						
						return new TranspileItem(pathEntry, path, dest);
					}
				}
				
			}
		}

		return null;
	}
	
	public IProject getProject() {
		return project;
	}
}
