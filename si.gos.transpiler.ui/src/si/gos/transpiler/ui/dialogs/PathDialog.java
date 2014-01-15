package si.gos.transpiler.ui.dialogs;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import si.gos.eclipse.dialogs.ResourceFileSelectionDialog;
import si.gos.transpiler.core.model.PathEntry;
import si.gos.transpiler.ui.TranspilerUIPluginImages;
import si.gos.transpiler.ui.internal.filter.TypedViewerFilter;

public class PathDialog extends Dialog {
	private Text source;
	private Text destFile;
	private Text destFolder;

	private Composite destRow;
	private Composite root;

	private IProject project;
	private PathEntry entry;
	
	private Image file = TranspilerUIPluginImages.FILE.createImage();
	private Image folder = TranspilerUIPluginImages.FOLDER.createImage();

	public PathDialog(Shell parentShell, IProject project) {
		super(parentShell);
		
		this.project = project;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		root = new Composite(parent, SWT.NO_SCROLL);
		root.setLayout(new GridLayout(2, false));
		
		Label lblSource = new Label(root, SWT.NONE);
		lblSource.setText("Source");
		
		Composite sourceRow = new Composite(root, SWT.NONE);
		GridData gd_sourceRow = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_sourceRow.widthHint = 350;
		sourceRow.setLayoutData(gd_sourceRow);
		GridLayout gl_sourceRow = new GridLayout(3, false);
		gl_sourceRow.marginRight = -5;
		gl_sourceRow.marginBottom = -5;
		gl_sourceRow.marginTop = -4;
		gl_sourceRow.verticalSpacing = 0;
		gl_sourceRow.marginWidth = 0;
		gl_sourceRow.horizontalSpacing = 0;
		gl_sourceRow.marginHeight = 0;
		sourceRow.setLayout(gl_sourceRow);
		
		source = new Text(sourceRow, SWT.BORDER | SWT.READ_ONLY);
		source.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button fileButton = new Button(sourceRow, SWT.NONE);
		fileButton.setImage(file);
		fileButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		fileButton.setText("File");
		fileButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ResourceFileSelectionDialog diag = createFileDialog("Source File Selection", entry.getSource());
				if (diag.open() == Dialog.OK) {
					setSource((IFile)diag.getFirstResult());
				}
			}
		});
		
		Button folderButton = new Button(sourceRow, SWT.NONE);
		folderButton.setImage(folder);
		folderButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		folderButton.setText("Folder");
		folderButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ElementTreeSelectionDialog diag = createFolderDialog("Source Folder Selection", entry.getSource());
				if (diag.open() == Dialog.OK) {
					setSource((IFolder)diag.getResult()[0]);
				}
			}
		});
		
		
		
		Label lblDestination = new Label(root, SWT.NONE);
		lblDestination.setText("Destination");
		
		destRow = new Composite(root, SWT.NONE);
		GridLayout gl_destRow = new GridLayout(1, false);
		gl_destRow.horizontalSpacing = 0;
		gl_destRow.marginRight = -5;
		gl_destRow.marginHeight = 0;
		gl_destRow.marginWidth = 0;
		gl_destRow.marginTop = -5;
		gl_destRow.verticalSpacing = 0;
		destRow.setLayout(gl_destRow);
		destRow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblPleaseSelectSource = new Label(destRow, SWT.NONE);
		GridData gd_lblPleaseSelectSource = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_lblPleaseSelectSource.verticalIndent = 8;
		gd_lblPleaseSelectSource.heightHint = 17;
		lblPleaseSelectSource.setLayoutData(gd_lblPleaseSelectSource);
		lblPleaseSelectSource.setText("Please select source first");
		
		if (entry != null) {
			setSource(entry.getSource());
			setDestination(entry.getDestination());
		} else {
			entry = new PathEntry();
		}
		
		return root;
	}

	private void setSource(IResource source) {
		if (source == null) {
			return;
		}
		entry.setSource(source);
		this.source.setText(source.getProjectRelativePath().toString());
		
		// remove destRow contents
		for (Control c : destRow.getChildren()) {
			c.dispose();
		}
		
		// show new content
		if (source instanceof IFolder) {
			createFolderDestination();
		} else if (source instanceof IFile) {
			createFileDestination();
		}
		
		root.layout(true, true);
	}
	
	private void setDestination(IResource destination) {
		if (destination == null) {
			return;
		}
		entry.setDestination(destination);
		
		if (entry.getSource() instanceof IFolder) {
			destFolder.setText(destination.getProjectRelativePath().toString());
		} else {
			destFile.setText(destination.getProjectRelativePath().toString());
		}
	}
	
	private void createFileDestination() {
		Composite row = new Composite(destRow, SWT.NONE);
		row.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_fileSource = new GridLayout(2, false);
		gl_fileSource.marginRight = -5;
		gl_fileSource.marginLeft = -5;
		row.setLayout(gl_fileSource);
		
		destFile = new Text(row, SWT.BORDER | SWT.READ_ONLY);
		destFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button destFileButon = new Button(row, SWT.NONE);
		destFileButon.setImage(file);
		destFileButon.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		destFileButon.setText("File");
		destFileButon.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ResourceFileSelectionDialog diag = createFileDialog("Destination File Selection", entry.getDestination());
				if (diag.open() == Dialog.OK) {
					setDestination((IFile)diag.getFirstResult());
				}
			}
		});
	}
	
	private void createFolderDestination() {
		Composite row = new Composite(destRow, SWT.NONE);
		row.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_folderSource = new GridLayout(2, false);
		gl_folderSource.marginRight = -5;
		gl_folderSource.marginLeft = -5;
		row.setLayout(gl_folderSource);
		
		destFolder = new Text(row, SWT.BORDER | SWT.READ_ONLY);
		destFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button destFolderButton = new Button(row, SWT.NONE);
		destFolderButton.setImage(folder);
		destFolderButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		destFolderButton.setText("Folder");
		destFolderButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ElementTreeSelectionDialog diag = createFolderDialog("Destination Folder Selection", entry.getDestination());
				if (diag.open() == Dialog.OK) {
					setDestination((IFolder)diag.getFirstResult());
				}
			}
		});
	}
	
	private ResourceFileSelectionDialog createFileDialog(String title, Object selection) {
		ResourceFileSelectionDialog diag = new ResourceFileSelectionDialog(getShell(), 
				new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
		
		configureDialog(diag, title, "Select Folder", selection);
		
		return diag;
	}
	
	private ElementTreeSelectionDialog createFolderDialog(String title, Object selection) {
		ElementTreeSelectionDialog diag = new ElementTreeSelectionDialog(getShell(), 
				new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
		
		diag.addFilter(new TypedViewerFilter(new Class[]{IFolder.class}));
		configureDialog(diag, title, "Select Folder", selection);
		
		return diag;
	}
	
	private void configureDialog(ElementTreeSelectionDialog diag, String title, String message, Object selection) {
		diag.setTitle(title);
		diag.setMessage("Select Folder");
		diag.setAllowMultiple(false);
		diag.setHelpAvailable(false);
		diag.setInput(project);
		
		if (selection != null) {
			diag.setInitialSelection(selection);
		}
	}
	
	public void setEntry(PathEntry entry) {
		this.entry = entry;
	}
	
	public PathEntry getEntry() {
		return entry;
	}
}
