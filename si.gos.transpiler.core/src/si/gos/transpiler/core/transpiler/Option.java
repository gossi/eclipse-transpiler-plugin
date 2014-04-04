package si.gos.transpiler.core.transpiler;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

public class Option {

	public static final String TYPE_BOOLEAN = "boolean";
	public static final String TYPE_PARAM = "param";
	public static final String TYPE_ENUM = "enum";
	
	private String name;
	private String shortName;
	private String type;
	private String description;
	private List<String> values = new LinkedList<String>();
	
	public Option() {
		
	}
	
	public Option(IConfigurationElement config) {
		name = config.getAttribute("name");
		description = config.getAttribute("description") == null ? "" : config.getAttribute("description");
		shortName = config.getAttribute("short");
		type = config.getAttribute("type");
		
		IConfigurationElement[] valueNodes = config.getChildren("value");
		for (IConfigurationElement v : valueNodes) {
			values.add(v.getAttribute("name"));
		}
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
	
	public boolean isBoolean() {
		return type.equals(TYPE_BOOLEAN);
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String[] getValues() {
		return values.toArray(new String[]{});
	}
	
	
}
