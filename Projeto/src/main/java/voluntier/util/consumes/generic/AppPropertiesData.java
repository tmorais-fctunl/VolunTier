package voluntier.util.consumes.generic;

import voluntier.util.data.statistics.Variables;

public class AppPropertiesData extends RequestData{
	
	public String variable;
	public String variableValue;
	
	public AppPropertiesData() {
	}
	
	public AppPropertiesData(String email, String token, String property, String propertyValue) {
		super(email, token);
		this.variable = property;
		this.variableValue = propertyValue;
	}
	
	public boolean isValid () {
		boolean matchesVariables = false;
		Variables[] variables = Variables.values();
		for (Variables var: variables)
			if (variable.equals(var.toString())) {
				matchesVariables = true;
				break;
			}
		return super.isValid() && variable != null && !variable.equals("") && variableValue != null && !variableValue.equals("") && matchesVariables;
	}

}
