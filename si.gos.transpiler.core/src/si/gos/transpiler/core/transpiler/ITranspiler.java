package si.gos.transpiler.core.transpiler;

import java.util.Map;

import org.apache.commons.exec.CommandLine;

public interface ITranspiler {

	public String getName();
	
	public String getCmd();
	
	public String getId();
	
	public String getExtension();
	
	public boolean isGeneric();
	
	public Map<String, Option> getOptions();
	
	public Option getOption(String name);
	
	public CommandLine getCommand(String path, Map<String, String> options);
	
	public InstalledTranspiler autoDetect();
}
