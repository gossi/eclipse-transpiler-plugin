package si.gos.transpiler.ui.controller;

import java.util.ArrayList;
import java.util.List;

import si.gos.transpiler.core.TranspilerPlugin;
import si.gos.transpiler.core.transpiler.ITranspiler;
import si.gos.transpiler.core.transpiler.ITranspilerManager;

public class NonInstalledTranspilerController extends TranspilerController {

	private ITranspilerManager manager = TranspilerPlugin.getDefault().getTranspilerManager();
	
	@Override
	public Object[] getElements(Object inputElement) {
		List<ITranspiler> selection = new ArrayList<ITranspiler>();
		for (ITranspiler transpiler : transpilers.values()) {
			if (transpiler.isGeneric() || !manager.isTranspilerInstalled(transpiler.getId())) {
				selection.add(transpiler);
			}
		}
		return selection.toArray();
	}
}
