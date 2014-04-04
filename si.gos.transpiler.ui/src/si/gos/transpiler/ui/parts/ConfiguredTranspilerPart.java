package si.gos.transpiler.ui.parts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import si.gos.eclipse.parts.CrudConfig;
import si.gos.eclipse.parts.TableCrudPart;
import si.gos.eclipse.widgets.utils.IWidgetFactory;
import si.gos.transpiler.core.TranspilerPlugin;
import si.gos.transpiler.core.model.ConfiguredTranspiler;
import si.gos.transpiler.core.transpiler.ITranspilerManager;
import si.gos.transpiler.core.transpiler.InstalledTranspiler;
import si.gos.transpiler.ui.controller.ConfiguredTranspilerController;
import si.gos.transpiler.ui.controller.InstalledTranspilerController;

public class ConfiguredTranspilerPart extends TableCrudPart {

	private TableViewer viewer;
	
	private IProject project;
	
	private ITranspilerManager manager = TranspilerPlugin.getDefault().getTranspilerManager();
	private ConfiguredTranspilerController controller = new ConfiguredTranspilerController();
	private Map<String, ConfiguredTranspiler> transpilers = new HashMap<String, ConfiguredTranspiler>();
	
	public ConfiguredTranspilerPart(IProject project) {
		super(new CrudConfig() {
			public boolean getEditVisible() {
				return false;
			}
		});
		
		this.project = project;
		transpilers = manager.getConfiguredTranspilers(project);
	}
	
	public void saveTranspilers() {
		manager.saveConfiguredTranspilers(project, transpilers);
	}
	
	@Override
	protected StructuredViewer createStructuredViewer(Composite parent, IWidgetFactory factory) {
		viewer = (TableViewer)super.createStructuredViewer(parent, factory);
		viewer.setLabelProvider(controller);
		viewer.setContentProvider(controller);
		viewer.setInput(transpilers);
		return viewer;
	}
	
	@Override
	protected void handleAdd(IStructuredSelection selection) {
		ElementListSelectionDialog diag = new ElementListSelectionDialog(getShell(), new InstalledTranspilerController());
		diag.setTitle("Select Installed Transpiler");
		diag.setElements(getInstalledTranspilers());
		if (diag.open() == Dialog.OK) {
			addTranspiler((InstalledTranspiler) diag.getFirstResult());
			viewer.refresh();
		}
	}
	
	private Object[] getInstalledTranspilers() {
		// collect already installed transpilers
		List<InstalledTranspiler> itps = new ArrayList<InstalledTranspiler>(); 
		for (ConfiguredTranspiler ctp : transpilers.values()) {
			itps.add(ctp.getInstalledTranspiler());
		}
		
		List<InstalledTranspiler> elements = new ArrayList<InstalledTranspiler>();
		for (InstalledTranspiler itp : manager.getInstalledTranspilers().values()) {
			if (!itps.contains(itp)) {
				elements.add(itp);
			}
		}
		
		return elements.toArray();
	}

	@Override
	protected void handleRemove(IStructuredSelection selection) {
		removeTranspiler((ConfiguredTranspiler) selection.getFirstElement());
		viewer.refresh();
	}
	
	private void addTranspiler(InstalledTranspiler itp) {
		ConfiguredTranspiler ctp = new ConfiguredTranspiler();
		ctp.setInstalledTranspiler(itp);
		transpilers.put(ctp.getId(), ctp);
	}
	
	private void removeTranspiler(ConfiguredTranspiler ctp) {
		transpilers.remove(ctp.getId());
	}
}
