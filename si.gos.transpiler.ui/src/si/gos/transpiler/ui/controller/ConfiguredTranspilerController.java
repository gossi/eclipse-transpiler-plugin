package si.gos.transpiler.ui.controller;

import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;

import si.gos.transpiler.core.model.ConfiguredTranspiler;

public class ConfiguredTranspilerController extends LabelProvider implements
		IStructuredContentProvider {

	private Map<String, ConfiguredTranspiler> transpilers;
	
	@SuppressWarnings("unchecked")
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		transpilers = (Map<String, ConfiguredTranspiler>) newInput;
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		return transpilers.values().toArray();
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof ConfiguredTranspiler) {
			ConfiguredTranspiler itp = (ConfiguredTranspiler)element;
			return itp.getInstalledTranspiler().getName() + " [" + itp.getInstalledTranspiler().getTranspiler().getName() + "]";
		}
		return super.getText(element);
	}

}
