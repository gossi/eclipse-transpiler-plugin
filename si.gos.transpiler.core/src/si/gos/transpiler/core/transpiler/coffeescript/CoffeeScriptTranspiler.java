package si.gos.transpiler.core.transpiler.coffeescript;

import org.eclipse.core.runtime.IPath;

import si.gos.transpiler.core.transpiler.AbstractTranspiler;
import si.gos.transpiler.core.transpiler.InstalledTranspiler;

public class CoffeeScriptTranspiler extends AbstractTranspiler {

	public final static String ID = "si.gos.transpiler.core.transpiler.coffeescript";

	public InstalledTranspiler autoDetect() {
		String coffeePath = exec("which coffee");
		
		if (coffeePath.isEmpty()) {
			coffeePath = checkLocations("coffee");
		}
		
		if (!coffeePath.isEmpty()) {
			String nodePath = exec("which node");
			
			if (nodePath.isEmpty()) {
				nodePath = checkLocations("node");
			}
			
			if (!nodePath.isEmpty()) {
				InstalledTranspiler itp = InstalledTranspiler.fromTranspiler(this);
				itp.setPath(nodePath + " " + coffeePath);
				
				return itp;
			}
		}
		return null;
	}
	
	public IPath getOutputOption(IPath from, IPath to) {
		return to.removeLastSegments(1).addTrailingSeparator();
	}
}
