package si.gos.transpiler.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.dialogs.PropertyPage;

import si.gos.eclipse.widgets.helper.IWidgetFactory;
import si.gos.eclipse.widgets.helper.WidgetFactory;
import si.gos.eclipse.widgets.helper.WidgetHelper;
import si.gos.transpiler.core.model.ConfiguredTranspiler;
import si.gos.transpiler.ui.TranspilerUIPlugin;
import si.gos.transpiler.ui.parts.ConfiguredTranspilerPart;
import si.gos.transpiler.ui.parts.PathEntryPart;

public class ProjectTranspilerPage extends PropertyPage {
	
	public static final String ID = "si.gos.transpiler.ui.preferences.projectPropertyPage";
	
	private TabFolder tabFolder;
	private ConfiguredTranspilerPart configuredTranspilerPart;
	private PathEntryPart pathEntryPart;
	
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
		composite.setLayout(new GridLayout(2, false));
		WidgetHelper.setMargin(composite, 0, 0);
		WidgetHelper.setSpacing(composite, 0, 0);

		// left
		Composite left = new Composite(composite, SWT.NONE);
		left.setLayout(new GridLayout(2, false));
		GridData gd_left = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_left.minimumWidth = 200;
		left.setLayoutData(gd_left);

		IWidgetFactory factory = new WidgetFactory(); 
		configuredTranspilerPart = new ConfiguredTranspilerPart(project);
		configuredTranspilerPart.createControl(left, SWT.DEFAULT, 1, factory);
		configuredTranspilerPart.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				IStructuredSelection sel = (IStructuredSelection) e.getSelection();
				tabFolder.setEnabled(!sel.isEmpty());
				pathEntryPart.setEnabled(!sel.isEmpty());
				if (!sel.isEmpty()) {
					pathEntryPart.setConfiguredTranspiler((ConfiguredTranspiler)sel.getFirstElement());
				} else {
					pathEntryPart.setConfiguredTranspiler(null);
				}
			}
		});
		
		// right
		Composite right = new Composite(composite, SWT.NONE);
		right.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gd_right = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_right.minimumWidth = 400;
		right.setLayoutData(gd_right);
		
		tabFolder = new TabFolder(right, SWT.NONE);
		tabFolder.setEnabled(false);
		
		TabItem tbtmPaths = new TabItem(tabFolder, SWT.NONE);
		tbtmPaths.setText("Paths");
		
		Composite pathsContainer = new Composite(tabFolder, SWT.NONE);
		pathsContainer.setLayout(new GridLayout(2, false));
		tbtmPaths.setControl(pathsContainer);
		
		pathEntryPart = new PathEntryPart(project);
		pathEntryPart.createControl(pathsContainer, SWT.DEFAULT, 1, factory);
		pathEntryPart.setEnabled(false);
		
		TabItem tbtmOptions = new TabItem(tabFolder, SWT.NONE);
		tbtmOptions.setText("Options");
		return composite;
	}
	
	@Override
	public boolean performOk() {
		configuredTranspilerPart.saveTranspilers();
		
		return true;
	}
}