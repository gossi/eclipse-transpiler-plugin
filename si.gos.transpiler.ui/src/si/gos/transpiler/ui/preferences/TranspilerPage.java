package si.gos.transpiler.ui.preferences;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import si.gos.transpiler.core.TranspilerPlugin;
import si.gos.transpiler.core.transpiler.ITranspilerManager;
import si.gos.transpiler.core.transpiler.InstalledTranspiler;
import si.gos.transpiler.ui.controller.InstalledTranspilerController;
import si.gos.transpiler.ui.dialogs.TranspilerDialog;

public class TranspilerPage extends PreferencePage implements IWorkbenchPreferencePage {

	private Button add;
	private Button edit;
	private Button remove;
	
	private TableViewer transpilers;
	
	private ITranspilerManager manager;
	
	@Override
	protected Control createContents(Composite parent) {

		Composite container = new Composite(parent, SWT.NO_SCROLL);
		container.setLayout(new GridLayout(2, false));
		
		manager = TranspilerPlugin.getDefault().getTranspilerManager();
		final InstalledTranspilerController controller = new InstalledTranspilerController();
		transpilers = new TableViewer(container);
		transpilers.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		transpilers.setContentProvider(controller);
		transpilers.setLabelProvider(controller);
		transpilers.setInput(manager.getInstalledTranspilers());
		transpilers.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				edit.setEnabled(!event.getSelection().isEmpty());
				remove.setEnabled(!event.getSelection().isEmpty());
			}
		});
		
		Composite actions = new Composite(container, SWT.NO_SCROLL);
		actions.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		actions.setLayout(new GridLayout());
		
		add = new Button(actions, SWT.PUSH);
		add.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		add.setText("Add");
		add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TranspilerDialog diag = new TranspilerDialog(getShell());
				if (diag.open() == Dialog.OK) {
					manager.addInstalledTranspiler(diag.getTranspiler());
					transpilers.refresh();
				}
			}
		});
		
		edit = new Button(actions, SWT.PUSH);
		edit.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		edit.setText("Edit");
		edit.setEnabled(false);
		edit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				InstalledTranspiler transpiler = getSelectedTranspiler();
				TranspilerDialog diag = new TranspilerDialog(getShell(), transpiler);
				if (diag.open() == Dialog.OK) {
					transpilers.refresh();
				}
			}
		});
		
		remove = new Button(actions, SWT.PUSH);
		remove.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		remove.setText("Remove");
		remove.setEnabled(false);
		remove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				InstalledTranspiler transpiler = getSelectedTranspiler();
				manager.removeInstalledTranspiler(transpiler);
				transpilers.refresh();
			}
		});
		
		return container;
	}

	@Override
	public void init(IWorkbench workbench) {
	}
	
	private InstalledTranspiler getSelectedTranspiler() {
		return (InstalledTranspiler)((IStructuredSelection)transpilers.getSelection()).getFirstElement();
	}
	
	@Override
	public boolean performOk() {
		manager.saveInstalledTranspilers();
		return super.performOk();
	}
}