package si.gos.transpiler.core.transpiler.traceur;

import si.gos.transpiler.core.transpiler.AbstractTranspiler;
import si.gos.transpiler.core.transpiler.InstalledTranspiler;

public class TraceurTranspiler extends AbstractTranspiler {

	public final static String ID = "si.gos.transpiler.core.transpiler.traceur";

	public InstalledTranspiler autoDetect() {
		String traceurPath = exec("which traceur");
		
		if (traceurPath.isEmpty()) {
			traceurPath = checkLocations("traceur");
		}
		
		if (!traceurPath.isEmpty()) {
			String nodePath = exec("which node");
			
			if (nodePath.isEmpty()) {
				nodePath = checkLocations("node");
			}
			
			if (!nodePath.isEmpty()) {
				InstalledTranspiler itp = InstalledTranspiler.fromTranspiler(this);
				itp.setPath(nodePath + " " + traceurPath);
				
				return itp;
			}
		}
		return null;
	}
}
