package si.gos.transpiler.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import si.gos.eclipse.widgets.utils.WidgetFactory;
import si.gos.transpiler.ui.parts.InstallTranspilerPart;

public class TranspilerPage extends PreferencePage implements IWorkbenchPreferencePage {

	private InstallTranspilerPart installerPart;
	
	@Override
	protected Control createContents(Composite parent) {
		
		installerPart = new InstallTranspilerPart();
		Composite container = installerPart.createControl(parent, new WidgetFactory());

		return container;
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	public boolean performOk() {
		installerPart.save();
		return super.performOk();
	}
}