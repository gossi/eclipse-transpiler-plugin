package si.gos.transpiler.core.transpiler;

import org.eclipse.core.runtime.IConfigurationElement;

public class Option {

	private String title;
	private String name;
	private String shortName;
	private String type;
	
	public Option() {
		
	}
	
	public Option(IConfigurationElement config) {
		name = config.getAttribute("name");
		title = config.getAttribute("title");
		shortName = config.getAttribute("short");
		type = config.getAttribute("type");
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getShort() {
		return shortName;
	}
	
	public void setShort(String shortName) {
		this.shortName = shortName;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
}
