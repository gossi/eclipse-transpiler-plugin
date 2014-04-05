package si.gos.transpiler.core.transpiler.less;

import si.gos.transpiler.core.transpiler.AbstractTranspiler;
import si.gos.transpiler.core.transpiler.InstalledTranspiler;

public class LessTranspiler extends AbstractTranspiler {

	public final static String ID = "si.gos.transpiler.core.transpiler.less";

	public InstalledTranspiler autoDetect() {
		String lessPath = exec("which lessc");
		
		if (lessPath.isEmpty()) {
			lessPath = checkLocations("lessc");
		}
		
		if (!lessPath.isEmpty()) {
			String nodePath = exec("which node");
			
			if (nodePath.isEmpty()) {
				nodePath = checkLocations("node");
			}
			
			if (!nodePath.isEmpty()) {
				InstalledTranspiler itp = InstalledTranspiler.fromTranspiler(this);
				itp.setPath(nodePath + " " + lessPath);
				
				return itp;
			}
		}
		return null;
	}
}
