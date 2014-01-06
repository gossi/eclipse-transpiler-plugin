package si.gos.transpiler.ui.controller;

import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;

import si.gos.transpiler.core.transpiler.InstalledTranspiler;

public class InstalledTranspilerController extends LabelProvider implements
		IStructuredContentProvider {

	private Map<String, InstalledTranspiler> transpilers;
	
	@SuppressWarnings("unchecked")
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		transpilers = (Map<String, InstalledTranspiler>) newInput;
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		return transpilers.values().toArray();
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof InstalledTranspiler) {
			InstalledTranspiler itp = (InstalledTranspiler)element;
			return itp.getName() + " [" + itp.getTranspiler().getName() + "]";
		}
		return super.getText(element);
	}

}
