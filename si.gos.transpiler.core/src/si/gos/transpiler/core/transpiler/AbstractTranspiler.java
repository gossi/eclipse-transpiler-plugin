package si.gos.transpiler.core.transpiler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

public abstract class AbstractTranspiler implements ITranspiler {
	
	protected String name;
	protected String id;
	protected String path;
	protected String cmd;
	protected boolean generic;
	
	protected List<Option> options;
	
	public void init(IConfigurationElement config) {
		name = config.getAttribute("name") != null ? config.getAttribute("name") : "";
		id = config.getAttribute("id") != null ? config.getAttribute("id") : "";
		path = config.getAttribute("path") != null ? config.getAttribute("path") : "";
		cmd = config.getAttribute("cmd") != null ? config.getAttribute("cmd") : "";
		generic = config.getAttribute("generic") != null ? config.getAttribute("generic").equalsIgnoreCase("true") : false;
		
		options = new ArrayList<Option>();
		
		IConfigurationElement[] optionNodes = config.getChildren("option");
		for (IConfigurationElement o : optionNodes) {
			options.add(new Option(o));
		}
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return the cmd
	 */
	public String getCmd() {
		return cmd;
	}
	
	public boolean isGeneric() {
		return generic;
	}
	
	public List<Option> getOptions() {
		return options;
	}
}
