package si.gos.transpiler.ui.parts;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;

import si.gos.eclipse.actions.PartAction;
import si.gos.eclipse.parts.CrudConfig;
import si.gos.eclipse.parts.TableCrudPart;
import si.gos.eclipse.widgets.utils.IWidgetFactory;
import si.gos.transpiler.core.TranspilerPlugin;
import si.gos.transpiler.core.transpiler.ITranspiler;
import si.gos.transpiler.core.transpiler.ITranspilerManager;
import si.gos.transpiler.core.transpiler.InstalledTranspiler;
import si.gos.transpiler.ui.controller.InstalledTranspilerController;
import si.gos.transpiler.ui.dialogs.InstalledTranspilerDialog;

public class InstallTranspilerPart extends TableCrudPart {

	private TableViewer viewer;

	private ITranspilerManager manager = TranspilerPlugin.getDefault().getTranspilerManager();
	private InstalledTranspilerController controller = new InstalledTranspilerController();
	
	public InstallTranspilerPart() {
		super(new CrudConfig() {
			@Override
			public String[] getActionLabels() {
				String[] labels = super.getActionLabels(); 
				
				String[] newLabels = new String[labels.length + 1];
				for (int i = 0; i < labels.length; i++) {
					newLabels[i] = labels[i];
				}
				newLabels[3] = "Auto-Detect";
				
				return newLabels;
			}
		});
	}
	
	@Override
	protected StructuredViewer createStructuredViewer(Composite parent, IWidgetFactory factory) {
		viewer = (TableViewer)super.createStructuredViewer(parent, factory);
		viewer.setLabelProvider(controller);
		viewer.setContentProvider(controller);
		viewer.setInput(manager.getInstalledTranspilers());
		return viewer;
	}
	
	@Override
	protected void handleAdd(IStructuredSelection selection) {
		InstalledTranspilerDialog diag = new InstalledTranspilerDialog(getShell());
		if (diag.open() == Dialog.OK) {
			manager.addInstalledTranspiler(diag.getInstalledTranspiler());
			viewer.refresh();
		}
	}

	@Override
	protected void handleEdit(IStructuredSelection selection) {
		InstalledTranspiler transpiler = (InstalledTranspiler)selection.getFirstElement();
		InstalledTranspilerDialog diag = new InstalledTranspilerDialog(getShell(), transpiler);
		if (diag.open() == Dialog.OK) {
			viewer.refresh();
		}
	}

	@Override
	protected void handleRemove(IStructuredSelection selection) {
		InstalledTranspiler transpiler = (InstalledTranspiler)selection.getFirstElement();
		manager.removeInstalledTranspiler(transpiler);
		viewer.refresh();
	}
	
	private void autoDetect() {
		for (ITranspiler transpiler : manager.getTranspilers().values()) {
			InstalledTranspiler itp = transpiler.autoDetect();
			
			if (itp != null) {
				manager.addInstalledTranspiler(itp);
			}
		}
		viewer.refresh();
	}
	
	@Override
	protected void handleAction(PartAction action, int index) {
		super.handleAction(action, index);
		
		// auto-detect
		if (index == 3) {
			autoDetect();
		}
	}
	
	public void save() {
		manager.saveInstalledTranspilers();
	}
	
}
