package si.gos.transpiler.ui.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.PropertyPage;

import si.gos.transpiler.core.TranspilerPlugin;
import si.gos.transpiler.core.transpiler.ITranspilerManager;
import si.gos.transpiler.core.transpiler.InstalledTranspiler;
import si.gos.transpiler.ui.TranspilerUIPlugin;
import si.gos.transpiler.ui.controller.InstalledTranspilerController;
import si.gos.transpiler.ui.controller.PathEntryController;
import si.gos.transpiler.ui.dialogs.PathDialog;
import si.gos.transpiler.ui.model.PathEntry;

public class ProjectTranspilerPage extends PropertyPage {
	
	public static final String ID = "si.gos.transpiler.ui.preferences.projectPropertyPage";
	
	private Table transpilerTable;
	private Table pathTable;
	private TableViewer transpilerViewer;
	private TableViewer paths;
	
	private Button removeTranspilerButton;
	private Button editPathButton;
	private Button removePathButton;
	
	private PathEntryController pathController;
	
	private Map<String, InstalledTranspiler> transpilers = new HashMap<String, InstalledTranspiler>();
	private ITranspilerManager manager = TranspilerPlugin.getDefault().getTranspilerManager();
	private IProject project;

	public ProjectTranspilerPage() {
		super();

		// get project
		ISelectionService ss = TranspilerUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		ISelection sel = ss.getSelection();
		Object selectedObject = sel;
        
		if (sel instanceof IStructuredSelection) {
        	selectedObject = ((IStructuredSelection)sel).getFirstElement();
        }

        if (selectedObject instanceof IAdaptable) {
        	IResource res = (IResource) ((IAdaptable) selectedObject).getAdapter(IResource.class);
        	project = res.getProject();
        }
	}

	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NO_SCROLL);
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.verticalSpacing = 0;
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		gl_composite.horizontalSpacing = 0;
		composite.setLayout(gl_composite);
		
		Composite left = new Composite(composite, SWT.NONE);
		left.setLayout(new GridLayout(2, false));
		GridData gd_left = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_left.minimumWidth = 200;
		left.setLayoutData(gd_left);
		
		final InstalledTranspilerController itpController = new InstalledTranspilerController();
		transpilerViewer = new TableViewer(left, SWT.BORDER | SWT.FULL_SELECTION);
		transpilerTable = transpilerViewer.getTable();
		transpilerTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		transpilerViewer.setContentProvider(itpController);
		transpilerViewer.setLabelProvider(itpController);
		transpilerViewer.setInput(getInstalledTranspiler());
		transpilerViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				removeTranspilerButton.setEnabled(!event.getSelection().isEmpty());
			}
		});
		
		Composite transpilerActions = new Composite(left, SWT.NONE);
		transpilerActions.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		GridLayout gl_transpilerActions = new GridLayout(1, false);
		gl_transpilerActions.marginWidth = 0;
		gl_transpilerActions.marginHeight = 0;
		gl_transpilerActions.verticalSpacing = 0;
		gl_transpilerActions.horizontalSpacing = 0;
		transpilerActions.setLayout(gl_transpilerActions);
		
		Button addTranspilerButton = new Button(transpilerActions, SWT.NONE);
		addTranspilerButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		addTranspilerButton.setText("Add");
		addTranspilerButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ElementListSelectionDialog diag = new ElementListSelectionDialog(getShell(), itpController);
				diag.setTitle("Select Installed Transpiler");
				diag.setElements(manager.getInstalledTranspilers().values().toArray());
				if (diag.open() == Dialog.OK) {
					addTranspiler((InstalledTranspiler) diag.getFirstResult());
					transpilerViewer.refresh();
				}
			}
		});
		
		removeTranspilerButton = new Button(transpilerActions, SWT.NONE);
		removeTranspilerButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		removeTranspilerButton.setText("Remove");
		removeTranspilerButton.setEnabled(false);
		removeTranspilerButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel = (IStructuredSelection)transpilerViewer.getSelection();
				removeTranspiler((InstalledTranspiler) sel.getFirstElement());
				transpilerViewer.refresh();
			}
		});
		
		
		// right
		Composite right = new Composite(composite, SWT.NONE);
		right.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gd_right = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_right.minimumWidth = 300;
		right.setLayoutData(gd_right);
		
		TabFolder tabFolder = new TabFolder(right, SWT.NONE);
		
		TabItem tbtmPaths = new TabItem(tabFolder, SWT.NONE);
		tbtmPaths.setText("Paths");
		
		Composite pathsContainer = new Composite(tabFolder, SWT.NONE);
		tbtmPaths.setControl(pathsContainer);
		pathsContainer.setLayout(new GridLayout(2, false));
		
		pathController = new PathEntryController();
		paths = new TableViewer(pathsContainer, SWT.BORDER | SWT.FULL_SELECTION);
		
		pathTable = paths.getTable();
		pathTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		pathTable.setHeaderVisible(true);
		pathTable.setLinesVisible(true);
		TableColumn srcCol = new TableColumn(pathTable, SWT.DEFAULT);
		srcCol.setText("Source");
		srcCol.setWidth(155);
		TableColumn destCol = new TableColumn(pathTable, SWT.DEFAULT);
		destCol.setText("Destination");
		destCol.setWidth(155);
		
		paths.setLabelProvider(pathController);
		paths.setContentProvider(pathController);
		paths.setInput(new ArrayList<PathEntry>());
		paths.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				editPathButton.setEnabled(!event.getSelection().isEmpty());
				removePathButton.setEnabled(!event.getSelection().isEmpty());
			}
		});
		 
		Composite pathsActions = new Composite(pathsContainer, SWT.NONE);
		GridLayout gl_pathsActions = new GridLayout(1, false);
		gl_pathsActions.verticalSpacing = 0;
		gl_pathsActions.marginWidth = 0;
		gl_pathsActions.marginHeight = 0;
		gl_pathsActions.horizontalSpacing = 0;
		pathsActions.setLayout(gl_pathsActions);
		pathsActions.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		
		Button addPathButton = new Button(pathsActions, SWT.NONE);
		addPathButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		addPathButton.setText("Add");
		addPathButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PathDialog diag = new PathDialog(getShell(), project);
				if (diag.open() == Dialog.OK) {
					pathController.add(diag.getEntry());
					paths.refresh();
				}
			}
		});
		
		editPathButton = new Button(pathsActions, SWT.NONE);
		editPathButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		editPathButton.setText("Edit");
		editPathButton.setEnabled(false);
		editPathButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PathDialog diag = new PathDialog(getShell(), project);
				PathEntry entry = getSelectedPathEntry();
				int index = pathController.indexOf(entry);
				diag.setEntry(entry);
				if (diag.open() == Dialog.OK) {
					pathController.update(index, diag.getEntry());
					paths.refresh();
				}
			}
		});
		
		removePathButton = new Button(pathsActions, SWT.NONE);
		removePathButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		removePathButton.setText("Remove");
		removePathButton.setEnabled(false);
		removePathButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				pathController.remove(getSelectedPathEntry());
			}
		});
		
		TabItem tbtmOptions = new TabItem(tabFolder, SWT.NONE);
		tbtmOptions.setText("Options");
		return composite;
	}
	
	private PathEntry getSelectedPathEntry() {
		IStructuredSelection sel = (IStructuredSelection)paths.getSelection();
		if (!sel.isEmpty()) {
			return (PathEntry)sel.getFirstElement();
		}
		return null;
	}
	
	private Map<String, InstalledTranspiler> getInstalledTranspiler() {
		return transpilers;
	}
	
	private void addTranspiler(InstalledTranspiler transpiler) {
		transpilers.put(transpiler.getId(), transpiler);
	}
	
	private void removeTranspiler(InstalledTranspiler transpiler) {
		transpilers.remove(transpiler.getId());
	}
}