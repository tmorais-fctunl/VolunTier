package voluntier.filters;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

@Provider
public class AuthRequestFilter implements ReaderInterceptor {

	/*List<String> unregistered_requests = Arrays.asList("register", "login", "forgotpassword", "forgotpassword/change",
			"forgotpassword/confirm", "refresh", "logout");
	private static final String exception_regex = "register/.+/confirm";
	 */
	
	@Override
	public Object aroundReadFrom(ReaderInterceptorContext ctx) throws IOException, WebApplicationException {
		return ctx.proceed();
	}
}