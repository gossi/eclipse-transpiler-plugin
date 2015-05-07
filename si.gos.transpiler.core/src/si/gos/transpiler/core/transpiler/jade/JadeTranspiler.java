package si.gos.transpiler.core.transpiler.jade;

import si.gos.transpiler.core.transpiler.AbstractTranspiler;
import si.gos.transpiler.core.transpiler.InstalledTranspiler;

public class JadeTranspiler extends AbstractTranspiler {

	public final static String ID = "si.gos.transpiler.core.transpiler.jade";

	public InstalledTranspiler autoDetect() {
		String jadePath = exec("which jade");
		
		if (jadePath.isEmpty()) {
			jadePath = checkLocations("jade");
		}
		
		if (!jadePath.isEmpty()) {
			String nodePath = exec("which node");
			
			if (nodePath.isEmpty()) {
				nodePath = checkLocations("node");
			}
			
			if (!nodePath.isEmpty()) {
				InstalledTranspiler itp = InstalledTranspiler.fromTranspiler(this);
				itp.setPath(nodePath + " " + jadePath);
				
				return itp;
			}
		}
		return null;
	}
}
