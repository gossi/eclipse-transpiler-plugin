package si.gos.transpiler.ui.parts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import si.gos.eclipse.widgets.helper.WidgetHelper;
import si.gos.transpiler.core.transpiler.ITranspiler;
import si.gos.transpiler.core.transpiler.Option;

public class OptionsPart {

	private ITranspiler transpiler;
	
	public OptionsPart(ITranspiler transpiler) {
		this.transpiler = transpiler;
	}
	
	public Composite createContents(Composite parent) {
		Composite area = new Composite(parent, SWT.V_SCROLL);
		area.setLayout(new GridLayout());
		WidgetHelper.trimComposite(area, 0);
		WidgetHelper.setSpacing(area, 0, 0);
		WidgetHelper.setMargin(area, 0, 0);
		
		GridData gd = new GridData();
		gd.widthHint = 350;
		area.setLayoutData(gd);
		
		// create row for each option
		for (Option o : transpiler.getOptions()) {
			createRow(area, o);
		}
		
		return null;
	}
	
	private Composite createRow(Composite parent, Option option) {
		Composite row = new Composite(parent, SWT.NO_SCROLL);
		row.setLayout(new GridLayout(2, false));
		WidgetHelper.trimComposite(row, 0, 0, 5);
		WidgetHelper.setSpacing(row, 0, 0);
		WidgetHelper.setMargin(row, 0, 0);
		
		Button enabled = new Button(row, SWT.CHECK);
		enabled.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, false, true));
		
		Composite right = new Composite(row, SWT.NO_SCROLL);
		right.setLayout(new GridLayout(3, false));
		WidgetHelper.trimComposite(right, 0);
		WidgetHelper.setSpacing(right, 0, 0);
		WidgetHelper.setMargin(right, 0, 0);
		
		int span = option.getType().equals(Option.TYPE_BOOLEAN) ? 2 : 1;
		StringBuilder sb = new StringBuilder();
		if (option.getShort() != null && !option.getShort().isEmpty()) {
			sb.append("-");
			sb.append(option.getShort());
			sb.append(", ");
		}
		sb.append("--");
		sb.append(option.getName());
		
		Label name = new Label(right, SWT.NONE); 
		name.setText(sb.toString());
		name.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, span, 1));
		
		if (option.getType().equals(Option.TYPE_PARAM)) {
			Text param = new Text(right, SWT.NONE);
			param.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));
		}
		
		Label dummy = new Label(right, SWT.NONE);
		dummy.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Label description = new Label(right, SWT.WRAP);
		description.setText(option.getDescription());
		description.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		
		return row;
	}

	/**
	 * @return the transpiler
	 */
	public ITranspiler getTranspiler() {
		return transpiler;
	}

	/**
	 * @param transpiler the transpiler to set
	 */
	public void setTranspiler(ITranspiler transpiler) {
		this.transpiler = transpiler;
	}
}
