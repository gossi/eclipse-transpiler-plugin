package si.gos.transpiler.core.model;

import org.eclipse.core.runtime.IPath;

import si.gos.transpiler.core.transpiler.InstalledTranspiler;

public class TranspileItem {

	private PathEntry pathEntry;
	private InstalledTranspiler itp;
	private ConfiguredTranspiler ctp;
	private IPath source;
	private IPath destination;

	public TranspileItem(PathEntry pathEntry, IPath source, IPath destination) {
		this.pathEntry = pathEntry;
		ctp = pathEntry.getConfiguredTranspiler();
		itp = ctp.getInstalledTranspiler();
		this.source = source;
		this.destination = destination;
	}

	/**
	 * @return the pathEntry
	 */
	public PathEntry getPathEntry() {
		return pathEntry;
	}

	/**
	 * @return the itp
	 */
	public InstalledTranspiler getInstalledTranspiler() {
		return itp;
	}

	/**
	 * @return the ctp
	 */
	public ConfiguredTranspiler getConfiguredTranspiler() {
		return ctp;
	}

	/**
	 * @return the source
	 */
	public IPath getSource() {
		return source;
	}

	/**
	 * @return the destination
	 */
	public IPath getDestination() {
		return destination;
	}
}
