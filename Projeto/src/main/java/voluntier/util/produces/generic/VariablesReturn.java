package voluntier.util.produces.generic;

public class VariablesReturn {
	
	public int access_expiration;
	public int max_message_log;
	public int currency_per_minute;
	public double initial_currency;
	public int max_events;
	public int max_routes;
	public int forgot_pass_expiration;
	public int refresh_expiration;
	public int register_code_expiration;
	
	public VariablesReturn () {
	}
	
	public VariablesReturn (int access_expiration, int max_message_log, int currency_per_minute, double initial_currency,
				int max_events, int max_routes, int forgot_pass_expiration, int refresh_expiration, int register_code_expiration) {
		this.access_expiration = access_expiration;
		this.max_message_log = max_message_log;
		this.currency_per_minute = currency_per_minute;
		this.initial_currency = initial_currency;
		this.max_events = max_events;
		this.max_routes = max_routes;
		this.forgot_pass_expiration = forgot_pass_expiration;
		this.refresh_expiration = refresh_expiration;
		this.register_code_expiration = register_code_expiration;
	}

}
