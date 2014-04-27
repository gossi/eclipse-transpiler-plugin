package si.gos.transpiler.ui.handler;

import java.util.Iterator;

import org.eclipse.core.commands.*;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import si.gos.transpiler.core.TranspilerNature;

public class AddTranspilerNatureHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection instanceof IStructuredSelection) {
			for (Iterator<?> it = ((IStructuredSelection) selection).iterator(); it
					.hasNext();) {
				Object element = it.next();
				IProject project = null;
				if (element instanceof IProject) {
					project = (IProject) element;
				} else if (element instanceof IAdaptable) {
					project = (IProject) ((IAdaptable) element)
							.getAdapter(IProject.class);
				}
				if (project != null) {
					try {
						addTranspilerNature(project);
					} catch (CoreException e) {
						throw new ExecutionException("Failed to toggle nature", e);
					}
				}
			}
		}

		return null;
	}

	/**
	 * Adds transpiler nature on a project
	 *
	 * @param project
	 *            to have transpiler nature added
	 */
	private void addTranspilerNature(IProject project) throws CoreException {
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();

		// Add the nature
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = TranspilerNature.NATURE_ID;
		description.setNatureIds(newNatures);
		project.setDescription(description, null);
	}

}