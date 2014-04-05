package si.gos.transpiler.core.model;

import org.eclipse.core.resources.IResource;

public class PathEntry {

	private IResource source;
	private IResource destination;
	private ConfiguredTranspiler transpiler;
	
	public PathEntry() {
		
	}
	
	public PathEntry(IResource source, IResource destination) {
		this.source = source;
		this.destination = destination;
	}

	/**
	 * @return the source
	 */
	public IResource getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(IResource source) {
		this.source = source;
	}

	/**
	 * @return the destination
	 */
	public IResource getDestination() {
		return destination;
	}

	/**
	 * @param destination the destination to set
	 */
	public void setDestination(IResource destination) {
		this.destination = destination;
	}

	/**
	 * @return the transpiler
	 */
	public ConfiguredTranspiler getConfiguredTranspiler() {
		return transpiler;
	}

	/**
	 * @param transpiler the transpiler to set
	 */
	public void setConfiguredTranspiler(ConfiguredTranspiler transpiler) {
		this.transpiler = transpiler;
	}
	
	
}
