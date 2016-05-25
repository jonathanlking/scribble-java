package org.scribble.model.local;

import java.util.List;
import java.util.Set;

import org.scribble.model.ModelState;
import org.scribble.sesstype.kind.Local;
import org.scribble.sesstype.name.RecVar;

// http://sandbox.kidstrythisathome.com/erdos/
public class EndpointState extends ModelState<IOAction, EndpointState, Local>
{
	public static enum Kind { OUTPUT, UNARY_INPUT, POLY_INPUT, TERMINAL }
			// FIXME: distinguish connection and message transfer
	
	/*private static int count = 0;
	
	public final int id;

	private final Set<RecVar> labs;  // Was RecVar and SubprotocolSigs, now using inlined protocol for FSM building so just RecVar
	private final LinkedHashMap<IOAction, EndpointState> edges;  // Want predictable ordering of entries for e.g. API generation (state enumeration)*/
	
	protected EndpointState(Set<RecVar> labs)
	{
		/*this.id = EndpointState.count++;
		this.labs = new HashSet<>(labs);
		this.edges = new LinkedHashMap<>();*/
		super(labs);
	}
	
	public Kind getStateKind()
	{
		List<IOAction> as = this.getAllAcceptable();
		if (as.size() == 0)
		{
			return Kind.TERMINAL;
		}
		else
		{
			IOAction a = as.iterator().next();
			return (a.isSend() || a.isConnect() || a.isDisconnect())
						? Kind.OUTPUT
						: (as.size() > 1) ? Kind.POLY_INPUT : Kind.UNARY_INPUT;
		}
	}
}
