package si.gos.transpiler.core.transpiler;

public interface ITranspiler {

	public String getName();
	
	public String getPath();
	
	public String getCmd();
	
	public String getId();
	
	public boolean isGeneric();
}
