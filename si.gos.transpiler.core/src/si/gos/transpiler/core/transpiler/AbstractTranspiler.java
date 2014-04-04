package si.gos.transpiler.core.transpiler;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.eclipse.core.runtime.IConfigurationElement;

import si.gos.eclipse.exec.ExecutionResponseAdapter;
import si.gos.eclipse.exec.Launcher;

public abstract class AbstractTranspiler implements ITranspiler {
	
	protected String name;
	protected String id;
	protected String cmd;
	protected String extension;
	private String lastExecMessage;
	
	protected Map<String, Option> options;
	
	public void init(IConfigurationElement config) {
		name = config.getAttribute("name") != null ? config.getAttribute("name") : "";
		id = config.getAttribute("id") != null ? config.getAttribute("id") : "";
		extension =  config.getAttribute("extension") != null ? config.getAttribute("extension") : "";
		cmd = config.getAttribute("cmd") != null ? config.getAttribute("cmd") : "";

		options = new LinkedHashMap<String, Option>();

		IConfigurationElement[] optionNodes = config.getChildren("option");
		for (IConfigurationElement o : optionNodes) {
			options.put(o.getAttribute("name"), new Option(o));
		}
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	

	/**
	 * @return the extension
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * @return the cmd
	 */
	public String getCmd() {
		return cmd;
	}
	
	public boolean isGeneric() {
		return false;
	}
	
	public Map<String, Option> getOptions() {
		return options;
	}
	
	public Option getOption(String name) {
		return options.get(name);
	}
	
	protected String buildOptionArgs(Map<String, String>options) {
		StringBuilder ob = new StringBuilder();
		
		if (options != null && options.size() > 0) {
			for (Map.Entry<String, String> entry : options.entrySet()) {
				Option option = getOption(entry.getKey());
				
				ob.append("--" + entry.getKey());
				if (!option.isBoolean()) {
					ob.append("=" + entry.getValue());
				}
				
				ob.append(" ");
			}
		}
		
		return ob.toString().trim();
	}

	public CommandLine getCommand(String path, Map<String, String> options) {
		String cmd = this.cmd;
		cmd = cmd.replace("$path", path);
		cmd = cmd.replace("$source", "\"${source}\"");
		cmd = cmd.replace("$destination", "\"${destination}\"");
		cmd = cmd.replace("$options", buildOptionArgs(options));
		return CommandLine.parse(cmd);
	}
	
	protected String checkLocations(String bin) {
		String[] paths = new String[]{"/usr/local/bin"};
		for (String path : paths) {
			path = path + "/";
			String binPath = path.replace("//", "/") + bin;
			File f = new File(binPath);
			if (f.exists()) {
				return binPath;
			}
		}
		
		return "";
	}
	
	protected String exec(String command) {
		lastExecMessage = "";
		Launcher launcher = new Launcher();
		launcher.addResponseListener(new ExecutionResponseAdapter() {
			public void executionMessage(String message) {
				lastExecMessage = message;
			}
		});
		try {
			launcher.launch(CommandLine.parse(command));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lastExecMessage;
	}
}
