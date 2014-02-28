package si.gos.transpiler.core.transpiler;

import java.util.List;

public interface ITranspiler {

	public String getName();
	
	public String getPath();
	
	public String getCmd();
	
	public String getId();
	
	public boolean isGeneric();
	
	public List<Option> getOptions();
}
