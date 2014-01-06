package si.gos.transpiler.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import si.gos.transpiler.core.TranspilerPlugin;
import si.gos.transpiler.core.transpiler.ITranspiler;
import si.gos.transpiler.core.transpiler.InstalledTranspiler;
import si.gos.transpiler.ui.controller.NonInstalledTranspilerController;

public class TranspilerDialog extends Dialog {
	private Text cmd;
	private Text path;
	private Text name;
	
	private boolean editing = false;
	private boolean fillingFromPreset = false;
	
	private InstalledTranspiler transpiler;

	protected TranspilerDialog(IShellProvider parentShell, InstalledTranspiler transpiler) {
		super(parentShell);
		this.transpiler = transpiler;
		editing = true;
	}

	public TranspilerDialog(Shell parentShell, InstalledTranspiler transpiler) {
		super(parentShell);
		this.transpiler = transpiler;
		editing = true;
	}
	
	protected TranspilerDialog(IShellProvider parentShell) {
		super(parentShell);
		transpiler = new InstalledTranspiler();
	}

	/**
	 * @wbp.parser.constructor
	 */
	public TranspilerDialog(Shell parentShell) {
		super(parentShell);
		transpiler = new InstalledTranspiler();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite grid = new Composite(parent, SWT.NO_SCROLL);
		grid.setLayout(new GridLayout(2, false));
		
		Label lblTranspilers = new Label(grid, SWT.NONE);
		lblTranspilers.setText("Transpiler");
		
		NonInstalledTranspilerController controller = new NonInstalledTranspilerController();
		ComboViewer comboViewer = new ComboViewer(grid, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		combo.setEnabled(!editing);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboViewer.setLabelProvider(controller);
		comboViewer.setContentProvider(controller);
		comboViewer.setInput(TranspilerPlugin.getDefault().getTranspilerManager().getTranspilers());
		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				Object sel = ((IStructuredSelection)event.getSelection()).getFirstElement();
				setActiveTranspiler((ITranspiler) sel);
			}
		});
		
		
		Label lblName = new Label(grid, SWT.NONE);
		lblName.setText("Name");
		
		name = new Text(grid, SWT.BORDER);
		name.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		name.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				transpiler.setName(name.getText());
				
				if (!fillingFromPreset) {
					name.setData("changed", true);
				}
			}
		});
		
		Label lblPath = new Label(grid, SWT.NONE);
		lblPath.setText("Path");
		
		path = new Text(grid, SWT.BORDER);
		path.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		path.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				transpiler.setPath(path.getText());
			}
		});
		
		Label lblCmd = new Label(grid, SWT.NONE);
		lblCmd.setText("Cmd");
		
		cmd = new Text(grid, SWT.BORDER);
		cmd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		cmd.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				transpiler.setCmd(cmd.getText());
			}
		});
		new Label(grid, SWT.NONE);
		
		Label lblCmdDesc = new Label(grid, SWT.NONE);
		lblCmdDesc.setText("Variables for use:\n\n%path% - The path to the executable\n%options% - Options to the executables\n%source% - The source path\n%destination% - The destination path");
		
		if (editing) {
			setActiveTranspiler(transpiler.getTranspiler());
		}
		
		return super.createDialogArea(parent);
	}
	
	private void setActiveTranspiler(ITranspiler transpiler) {
		if (name.getData("changed") == null || !(Boolean)name.getData("changed")) {
			fillingFromPreset = true;
			name.setText(transpiler.getName());
			fillingFromPreset = false;
		}
		path.setEnabled(!transpiler.isGeneric());
		path.setText(transpiler.getPath());
		cmd.setEnabled(transpiler.isGeneric());
		cmd.setText(transpiler.getCmd());
		this.transpiler.setTranspiler(transpiler);
		this.transpiler.setTranspilerId(transpiler.getId());
	}

	public InstalledTranspiler getTranspiler() {
		return transpiler;
	}
	
}
