package si.gos.transpiler.ui.controller;

import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;

import si.gos.transpiler.core.transpiler.ITranspiler;

public class TranspilerController extends LabelProvider implements
		IStructuredContentProvider {

	protected Map<String, ITranspiler> transpilers;
	
	@SuppressWarnings("unchecked")
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		transpilers = (Map<String, ITranspiler>) newInput;
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		return transpilers.values().toArray();
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof ITranspiler) {
			return ((ITranspiler)element).getName();
		}
		return super.getText(element);
	}

	

}
