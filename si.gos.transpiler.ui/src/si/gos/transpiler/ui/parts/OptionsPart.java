package si.gos.transpiler.ui.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import si.gos.eclipse.widgets.utils.WidgetHelper;
import si.gos.transpiler.core.model.ConfiguredTranspiler;
import si.gos.transpiler.core.transpiler.ITranspiler;
import si.gos.transpiler.core.transpiler.Option;

public class OptionsPart {

	private ConfiguredTranspiler confTranspiler;
	private ITranspiler transpiler;
	private Composite area;
	private List<Composite> rows;
	
	public OptionsPart(ConfiguredTranspiler configuredTranspiler) {
		this.transpiler = configuredTranspiler.getInstalledTranspiler().getTranspiler();
		confTranspiler = configuredTranspiler;
	}
	
	public Composite createContents(Composite parent) {
		area = new Composite(parent, SWT.NO_SCROLL);
		RowLayout layout = new RowLayout();
		layout.type = SWT.VERTICAL;
		area.setLayout(layout);
		WidgetHelper.trim(area, 0);
		WidgetHelper.setSpacing(area, 0);
		WidgetHelper.setMargin(area, 0, 0);
		
		// create row for each option
		rows = new ArrayList<Composite>();
		for (Option o : transpiler.getOptions().values()) {
			rows.add(createRow(area, o));
		}
		
		return area;
	}
	
	private Composite createRow(Composite parent, final Option option) {
		boolean hasOption = confTranspiler.hasOption(option.getName());
		final Composite row = new Composite(parent, SWT.NO_SCROLL);
		row.setLayout(new RowLayout());
		RowData rd = new RowData();
		
		row.setLayoutData(rd);
		WidgetHelper.trim(row, 0, 0, 5);
		WidgetHelper.setSpacing(row, 0);
		WidgetHelper.setMargin(row, 0, 0);
		
		final Button enabled = new Button(row, SWT.CHECK);
		row.setData("enabled", enabled);
		if (hasOption) {
			enabled.setSelection(true);
		}
		enabled.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (enabled.getSelection()) {
					if (!confTranspiler.hasOption(option.getName())) {
						confTranspiler.setOption(option.getName());
					}
				} else {
					confTranspiler.removeOption(option.getName());
				}
			}
		});
		
		Composite right = new Composite(row, SWT.NO_SCROLL);
		RowLayout rlayout = new RowLayout();
		rlayout.type = SWT.VERTICAL;
		rlayout.wrap = true;
		right.setLayout(rlayout);
		WidgetHelper.trim(right, 0);
		WidgetHelper.setSpacing(right, 0, 0);
		WidgetHelper.setMargin(right, 0, 0);

		Composite top = new Composite(right, SWT.NO_SCROLL);
		RowLayout tlayout = new RowLayout();
		tlayout.justify = true;
		top.setLayout(tlayout);
		WidgetHelper.trim(top, 2, 0);
		WidgetHelper.setSpacing(top, 0);
		
		StringBuilder sb = new StringBuilder();
		if (option.getShort() != null && !option.getShort().isEmpty()) {
			sb.append("-");
			sb.append(option.getShort());
			sb.append(", ");
		}
		sb.append("--");
		sb.append(option.getName());
		if (option.getType().equals(Option.TYPE_PARAM)) {
			sb.append("=");
		}
		
		final Label name = new Label(top, SWT.NONE); 
		name.setText(sb.toString());
		
		if (option.getType().equals(Option.TYPE_PARAM) || option.getType().equals(Option.TYPE_ENUM)) {
			Control param = null;
			
			if (option.getType().equals(Option.TYPE_PARAM)) {
				param = new Text(top, SWT.SINGLE);
				if (hasOption) {
					((Text)param).setText(confTranspiler.getOption(option.getName()));
				}
				((Text)param).addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent e) {
						confTranspiler.setOption(option.getName(), ((Text)e.getSource()).getText());
					}
				});
			} else if (option.getType().equals(Option.TYPE_ENUM)) {
				param = new Combo(top, SWT.SINGLE | SWT.READ_ONLY);
				((Combo)param).setItems(option.getValues());
				if (hasOption) {
					((Combo)param).setText(confTranspiler.getOption(option.getName()));
				}
				((Combo)param).addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						confTranspiler.setOption(option.getName(), ((Combo)e.getSource()).getText());
					}
				});
			}
			param.setLayoutData(new RowData());
			row.setData("param", param);
		}
		
		
		if (!option.getDescription().isEmpty()) {
			Label description = new Label(right, SWT.WRAP);
			description.setText(option.getDescription());
			description.setLayoutData(new RowData());
			row.setData("description", description);
		}
		
		// automatically resize
		row.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				int width = row.getSize().x;
				int left = enabled.getSize().x;
				
				// description
				Object descObj = row.getData("description");
				if (descObj != null) {
					Label description = (Label)descObj;
					RowData dd = (RowData)description.getLayoutData();
					dd.width = width - left;
				}
				
				// param
				Object paramObj = row.getData("param");
				if (paramObj != null) {
					Control param = (Control)paramObj;
					RowData pd = (RowData)param.getLayoutData();
					pd.width = width - left - name.getSize().x - 10;
				}
			}
		});
		return row;
	}
	
	public void setWidth(int width) {
		for (Composite row : rows) {
			RowData rd = (RowData)row.getLayoutData();
			rd.width = width;
		}
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
