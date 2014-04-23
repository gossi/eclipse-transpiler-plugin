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
import org.eclipse.swt.graphics.Point;
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
import si.gos.transpiler.ui.controller.TranspilerController;

public class InstalledTranspilerDialog extends Dialog {
	private Text cmd;
	private Text path;
	private Text name;
	
	private boolean editing = false;
	private boolean fillingFromPreset = false;
	
	private InstalledTranspiler transpiler;
	private Text sourceExtension;
	private Text destinationExtension;

	protected InstalledTranspilerDialog(IShellProvider parentShell, InstalledTranspiler transpiler) {
		super(parentShell);
		this.transpiler = transpiler;
		editing = true;
	}

	public InstalledTranspilerDialog(Shell parentShell, InstalledTranspiler transpiler) {
		super(parentShell);
		this.transpiler = transpiler;
		editing = true;
	}
	
	protected InstalledTranspilerDialog(IShellProvider parentShell) {
		super(parentShell);
		transpiler = new InstalledTranspiler();
	}

	/**
	 * @wbp.parser.constructor
	 */
	public InstalledTranspilerDialog(Shell parentShell) {
		super(parentShell);
		transpiler = new InstalledTranspiler();
		
	}
	
	@Override
	protected Point getInitialSize() {
		return getShell().computeSize(400, SWT.DEFAULT);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite grid = new Composite(parent, SWT.NO_SCROLL);
		grid.setLayout(new GridLayout(2, false));
		grid.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblTranspilers = new Label(grid, SWT.NONE);
		lblTranspilers.setText("Transpiler");
		
		TranspilerController controller;
		if (editing) {
			controller = new TranspilerController();
		} else {
			controller = new NonInstalledTranspilerController();
		}
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
		
		if (editing) {
			combo.select(controller.indexOf(transpiler.getTranspiler()));
		}
		
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
		
		Label lblSourceExtension = new Label(grid, SWT.NONE);
		lblSourceExtension.setText("Extension (Source)");
		
		sourceExtension = new Text(grid, SWT.BORDER);
		sourceExtension.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		sourceExtension.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				transpiler.setSourceExtension(sourceExtension.getText());
			}
		});
		
		Label lblDestinationExtension = new Label(grid, SWT.NONE);
		lblDestinationExtension.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDestinationExtension.setText("Extension (Destination)");
		
		destinationExtension = new Text(grid, SWT.BORDER);
		destinationExtension.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		destinationExtension.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				transpiler.setDestinationExtension(destinationExtension.getText());
			}
		});
		
		Label lblPath = new Label(grid, SWT.NONE);
		lblPath.setText("Executable");
		
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
		lblCmdDesc.setText("Variables for use:\n\n$path - The path to the executable\n$options - Options to the executables\n$source - The source path\n$destination - The destination path");
		
		if (editing) {
			setActiveTranspiler(transpiler.getTranspiler());
		}
		
		return grid;
	}
	
	private void setActiveTranspiler(ITranspiler transpiler) {
		if (name.getData("changed") == null || !(Boolean)name.getData("changed")) {
			fillingFromPreset = true;
			name.setText(transpiler.getName());
			fillingFromPreset = false;
		}
		
		if (this.transpiler != null && this.transpiler.getPath() != null) {
			path.setText(this.transpiler.getPath());
		}
		
		cmd.setText(transpiler.getCmd());
		sourceExtension.setText(transpiler.getSourceExtension());
		destinationExtension.setText(transpiler.getDestinationExtension());
		this.transpiler.setTranspiler(transpiler);
	}

	public InstalledTranspiler getInstalledTranspiler() {
		return transpiler;
	}
	
}
