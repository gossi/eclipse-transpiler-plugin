package si.gos.transpiler.ui.controller;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

import si.gos.transpiler.ui.TranspilerUIPluginImages;
import si.gos.transpiler.ui.model.PathEntry;

public class PathEntryController extends LabelProvider implements
		IStructuredContentProvider, ITableLabelProvider {

	protected List<PathEntry> paths;

	private Image file = TranspilerUIPluginImages.FILE.createImage();
	private Image folder = TranspilerUIPluginImages.FOLDER.createImage();
	
	@SuppressWarnings("unchecked")
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		paths = (List<PathEntry>) newInput;
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		return paths.toArray();
	}
	
	public int indexOf(PathEntry entry) {
		return paths.indexOf(entry);
	}
	
	public void add(PathEntry entry) {
		paths.add(entry);
	}
	
	public void remove(PathEntry entry) {
		paths.remove(entry);
	}
	
	public void update(int index, PathEntry entry) {
		paths.set(index, entry);
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof IResource) {
			return ((IResource)element).getProjectRelativePath().toString();
		}
		return super.getText(element);
	}
	
	@Override
	public Image getImage(Object element) {
		if (element instanceof IFolder) {
			return folder;
		} else if (element instanceof IFile) {
			return file;
		}
		return super.getImage(element);
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return getImage(((PathEntry)element).getSource());
			
		case 1:
			return getImage(((PathEntry)element).getDestination());
		}
		
		return super.getImage(element);
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return getText(((PathEntry)element).getSource());

		case 1:
			return getText(((PathEntry)element).getDestination());
		}
		
		return super.getText(element);
	}

}
