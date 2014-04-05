package si.gos.transpiler.core.transpiler;


public class InstalledTranspiler {

	private String name;
	private String id;
	private String sourceExtension;
	private String destinationExtension;
	private String path;
	private String cmd;
	private String transpilerId;
	
	private ITranspiler transpiler;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
		buildId();
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * @return the cmd
	 */
	public String getCmd() {
		return cmd;
	}
	
	/**
	 * @param cmd the cmd to set
	 */
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	/**
	 * @return the transpiler
	 */
	public ITranspiler getTranspiler() {
		return transpiler;
	}

	/**
	 * @param transpiler the transpiler to set
	 */
	public void setTranspiler(ITranspiler transpiler) {
		this.transpiler = transpiler;
		setTranspilerId(transpiler.getId());
	}

	/**
	 * @return the transpilerId
	 */
	public String getTranspilerId() {
		return transpilerId;
	}

	/**
	 * @param transpilerId the transpilerId to set
	 */
	public void setTranspilerId(String transpilerId) {
		this.transpilerId = transpilerId;
		buildId();
	}
	
	private void buildId() {
		if (transpiler != null) {
			if (transpiler.isGeneric()) {
				setId(transpilerId + "_" + name.toLowerCase());
			} else {
				setId(transpilerId);
			}
		}
	}

	/**
	 * @return the extension
	 */
	public String getSourceExtension() {
		return sourceExtension;
	}

	/**
	 * @param extension the extension to set
	 */
	public void setSourceExtension(String extension) {
		this.sourceExtension = extension;
	}

	/**
	 * @return the destinationExtension
	 */
	public String getDestinationExtension() {
		return destinationExtension;
	}

	/**
	 * @param destinationExtension the destinationExtension to set
	 */
	public void setDestinationExtension(String destinationExtension) {
		this.destinationExtension = destinationExtension;
	}
	
	public static InstalledTranspiler fromTranspiler(ITranspiler transpiler) {
		InstalledTranspiler itp = new InstalledTranspiler();
		itp.setCmd(transpiler.getCmd());
		itp.setSourceExtension(transpiler.getSourceExtension());
		itp.setDestinationExtension(transpiler.getDestinationExtension());
		itp.setName(transpiler.getName());
		itp.setTranspiler(transpiler);

		return itp;
	}
	
}
