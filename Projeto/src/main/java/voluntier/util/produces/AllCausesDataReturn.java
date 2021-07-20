package voluntier.util.produces;

import java.util.List;

public class AllCausesDataReturn {
	
	List<CauseDataReturn> causes;

	public AllCausesDataReturn () {
	}
	
	public AllCausesDataReturn (List<CauseDataReturn> causes) {
		this.causes = causes;
	}
}
