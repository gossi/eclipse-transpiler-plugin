package si.gos.transpiler.core.transpiler.generic;

import java.util.Map;

import org.apache.commons.exec.CommandLine;

import si.gos.transpiler.core.transpiler.AbstractTranspiler;
import si.gos.transpiler.core.transpiler.InstalledTranspiler;

public class GenericTranspiler extends AbstractTranspiler {

	public final static String ID = "si.gos.transpiler.core.transpiler.generic";

	public CommandLine getCommand(String path, Map<String, String> options) {
		String cmdLine = cmd;
		cmdLine.replace("$path", path);
		cmdLine.replace("$source", "${source}");
		cmdLine.replace("$destination", "${destination}");
		return CommandLine.parse(cmdLine);
	}

	public boolean isGeneric() {
		return true;
	}

	@Override
	public InstalledTranspiler autoDetect() {
		return null;
	}
}
