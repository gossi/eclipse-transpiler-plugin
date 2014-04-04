package si.gos.transpiler.core.transpiler.sass;

import si.gos.transpiler.core.transpiler.AbstractTranspiler;
import si.gos.transpiler.core.transpiler.InstalledTranspiler;

public class SassTranspiler extends AbstractTranspiler {

	public final static String ID = "si.gos.transpiler.core.transpiler.sass";

	public InstalledTranspiler autoDetect() {
		String sassPath = exec("which sass");
		
		if (!sassPath.isEmpty()) {
			InstalledTranspiler itp = InstalledTranspiler.fromTranspiler(this);
			itp.setPath(sassPath);
			
			return itp;
		}
		return null;
	}	
}
