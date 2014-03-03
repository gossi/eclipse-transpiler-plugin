package si.gos.transpiler.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import si.gos.transpiler.ui.parts.OptionsPart;
import si.gos.transpiler.ui.parts.PathEntryPart;

public class ProjectTranspilerPage extends PropertyPage {
	
	public static final String ID = "si.gos.transpiler.ui.preferences.projectPropertyPage";
	
	private TabFolder tabFolder;
	private ConfiguredTranspilerPart configuredTranspilerPart;
	private PathEntryPart pathEntryPart;
	private ScrolledComposite optionContainer;
	private TabItem optionItem;
	private Composite page;
	
	private boolean resizing = false;
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
		page = new Composite(parent, SWT.NO_SCROLL);
		page.setLayout(new GridLayout(1, false));
		WidgetHelper.setMargin(page, 0, 0);
		WidgetHelper.setSpacing(page, 0, 0);

		// left
		Composite left = new Composite(page, SWT.NONE);
		left.setLayout(new GridLayout(2, false));
		GridData gd_left = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_left.minimumWidth = 200;
		gd_left.minimumHeight = 80;
		gd_left.heightHint = 80;
		left.setLayoutData(gd_left);

		IWidgetFactory factory = new WidgetFactory(); 
		configuredTranspilerPart = new ConfiguredTranspilerPart(project);
		configuredTranspilerPart.createControl(left, SWT.DEFAULT, 1, factory);
		configuredTranspilerPart.addSelectionChangedListener(new ISelectionChangedListener() {
			private ConfiguredTranspiler lastSelection = null;
			
			public void selectionChanged(SelectionChangedEvent e) {
				IStructuredSelection sel = (IStructuredSelection) e.getSelection();
				tabFolder.setEnabled(!sel.isEmpty());
				pathEntryPart.setEnabled(!sel.isEmpty());
				
				if (!sel.isEmpty()) {
					ConfiguredTranspiler ct = (ConfiguredTranspiler)sel.getFirstElement();
					if (ct != lastSelection) {
						pathEntryPart.setConfiguredTranspiler(ct);
						updateOptions(ct);
						lastSelection = ct;
					}
				} else {
					pathEntryPart.setConfiguredTranspiler(null);
					lastSelection = null;
				}
			}
		});
		
		// right
		Composite right = new Composite(page, SWT.NONE);
		right.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gd_right = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_right.minimumWidth = 440;
		gd_right.heightHint = 350;
		right.setLayoutData(gd_right);
		
		tabFolder = new TabFolder(right, SWT.NONE);
		tabFolder.setEnabled(false);
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (tabFolder.getSelection()[0] == optionItem) {
					optionContainer.layout(true, true);
					
					resize();
				}
			}
		});
		
		TabItem tbtmPaths = new TabItem(tabFolder, SWT.NONE);
		tbtmPaths.setText("Paths");
		
		Composite pathsContainer = new Composite(tabFolder, SWT.NONE);
		pathsContainer.setLayout(new GridLayout(2, false));
		tbtmPaths.setControl(pathsContainer);

		pathEntryPart = new PathEntryPart(project);
		pathEntryPart.createControl(pathsContainer, SWT.DEFAULT, 1, factory);
		pathEntryPart.setEnabled(false);
		
		optionItem = new TabItem(tabFolder, SWT.NONE);
		optionItem.setText("Options");
		
		optionContainer = new ScrolledComposite(tabFolder, SWT.H_SCROLL | SWT.V_SCROLL);
		optionContainer.setLayout(new GridLayout());
		optionContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		optionContainer.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				resize();
			}
		});
		optionItem.setControl(optionContainer);
		
		return page;
	}
	
	private void resize() {
		if (!resizing) {
			resizing = true;
			Composite composite = (Composite)optionContainer.getContent();
			composite.setSize(composite.computeSize(optionContainer.getClientArea().width, SWT.DEFAULT));
			OptionsPart part = (OptionsPart)optionContainer.getData();
			part.setWidth(optionContainer.getClientArea().width);
			resizing = false;
		}
	}
	
	private void updateOptions(ConfiguredTranspiler ct) {
		// clean up
		for (Control control : optionContainer.getChildren()) {
			control.dispose();
		}

		OptionsPart part = new OptionsPart(ct.getInstalledTranspiler().getTranspiler());
		Composite composite = part.createContents(optionContainer);
		optionContainer.setContent(composite);
		optionContainer.setData(part);
		page.layout(true, true);
	}
	
	@Override
	public boolean performOk() {
		configuredTranspilerPart.saveTranspilers();
		
		return true;
	}
}