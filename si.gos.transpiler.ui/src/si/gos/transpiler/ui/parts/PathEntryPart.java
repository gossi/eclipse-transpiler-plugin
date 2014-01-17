package si.gos.transpiler.ui.parts;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import si.gos.eclipse.parts.TableCRUDPart;
import si.gos.eclipse.widgets.helper.IWidgetFactory;
import si.gos.transpiler.core.model.ConfiguredTranspiler;
import si.gos.transpiler.core.model.PathEntry;
import si.gos.transpiler.ui.controller.PathEntryController;
import si.gos.transpiler.ui.dialogs.PathDialog;

public class PathEntryPart extends TableCRUDPart {

	private TableViewer viewer;
	
	private IProject project;
	private PathEntryController controller = new PathEntryController();
	private ConfiguredTranspiler transpiler;
	
	public PathEntryPart(IProject project) {
		super();
		this.project = project;
	}
	
	@Override
	protected StructuredViewer createStructuredViewer(Composite parent,
			int style, IWidgetFactory factory) {
		viewer = (TableViewer)super.createStructuredViewer(parent, style, factory);
		
		// configure viewer
		viewer.setLabelProvider(controller);
		viewer.setContentProvider(controller);
		
		if (transpiler != null) {
			viewer.setInput(transpiler.getPaths());
		}
		
		// configure table
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn srcCol = new TableColumn(table, SWT.DEFAULT);
		srcCol.setText("Source");
		srcCol.setWidth(155);
		TableColumn destCol = new TableColumn(table, SWT.DEFAULT);
		destCol.setText("Destination");
		destCol.setWidth(155);
		
		return viewer;
	}
	
	public void setConfiguredTranspiler(ConfiguredTranspiler transpiler) {
		this.transpiler = transpiler;
		
		if (viewer != null) {
			if (transpiler == null) {
				viewer.setInput(null);
			} else {
				viewer.setInput(transpiler.getPaths());
			}
		}
	}
	
	@Override
	protected void handleAdd(IStructuredSelection selection) {
		PathDialog diag = new PathDialog(getShell(), project);
		if (diag.open() == Dialog.OK) {
			controller.add(diag.getEntry());
			viewer.refresh();
		}
	}
	
	@Override
	protected void handleEdit(IStructuredSelection selection) {
		PathDialog diag = new PathDialog(getShell(), project);
		PathEntry entry = (PathEntry)selection.getFirstElement();
		int index = controller.indexOf(entry);
		diag.setEntry(entry);
		if (diag.open() == Dialog.OK) {
			controller.update(index, diag.getEntry());
			viewer.refresh();
		}
	}
	
	@Override
	protected void handleRemove(IStructuredSelection selection) {
		controller.remove((PathEntry)selection.getFirstElement());
		viewer.refresh();
	}
}
