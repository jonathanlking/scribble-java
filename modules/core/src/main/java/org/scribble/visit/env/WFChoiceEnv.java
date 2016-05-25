package org.scribble.visit.env;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.scribble.sesstype.Message;
import org.scribble.sesstype.MessageSig;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.MessageId;
import org.scribble.sesstype.name.Op;
import org.scribble.sesstype.name.Role;
import org.scribble.util.ConnectedMap;
import org.scribble.util.MessageIdMap;

public class WFChoiceEnv extends Env<WFChoiceEnv>
{
	private static final Role DUMMY_ROLE = new Role("__ROLE");
	private static final Op ROOT_OPERATOR = new Op("__ROOT");
	private static final Op SUBJECT_OPERATOR = new Op("__SUBJECT");

	private static final MessageSig ROOT_MESSAGESIGNATURE = new MessageSig(ROOT_OPERATOR, Payload.EMPTY_PAYLOAD);
	private static final MessageSig SUBJECT_MESSAGESIGNATURE = new MessageSig(SUBJECT_OPERATOR, Payload.EMPTY_PAYLOAD);
	
	// dest -> src -> msg
	private MessageIdMap initial;  // message transfers recorded here in block envs
	//private MessageIdMap initialInterrupts;  //  interrupts recorded here in interruptible env
	
	private ConnectedMap connected;
	
	// FIXME: use roles to initialise MessageIdMap properly, cf. WFBuffers
	public WFChoiceEnv(Set<Role> roles, boolean implicit)
	{
		//this(new MessageIdMap(), new MessageIdMap());
		this(new MessageIdMap(), new ConnectedMap(roles, implicit));
	}
	
	//protected InlinedWFChoiceEnv(MessageIdMap initial, MessageIdMap initialInterrupts)
	protected WFChoiceEnv(MessageIdMap initial, ConnectedMap connected)
	{
		this.initial = new MessageIdMap(initial);
		//this.initialInterrupts = new MessageIdMap(initialInterrupts);
		this.connected = new ConnectedMap(connected);
	}

	@Override
	protected WFChoiceEnv copy()
	{
		//return new InlinedWFChoiceEnv(this.initial, this.initialInterrupts);
		return new WFChoiceEnv(this.initial, this.connected);
	}
	
	public WFChoiceEnv clear()
	{
		WFChoiceEnv copy = copy();
		copy.initial.clear();
		//copy.initialInterrupts.clear();
		return copy;
	}

	@Override
	public WFChoiceEnv enterContext()
	{
		//return new InlinedWFChoiceEnv(this.initial, this.initialInterrupts);
		return new WFChoiceEnv(this.initial, this.connected);
	}
	
	@Override
	public WFChoiceEnv mergeContext(WFChoiceEnv child)
	{
		return mergeContexts(Arrays.asList(child));
	}

	@Override
	public WFChoiceEnv mergeContexts(List<WFChoiceEnv> children)
	{
		WFChoiceEnv copy = copy();
		for (WFChoiceEnv child : children)
		{
			merge(this, copy.initial, child.initial);
			//merge(this, copy.initialInterrupts, child.initialInterrupts);
			merge(this, copy.connected, child.connected);
		}
		return copy;
	}

	// Pre: foo is parent.copy().initial
	// updates foo according to state of parent and child
	private static void merge(WFChoiceEnv parent, MessageIdMap foo, MessageIdMap child)
	{
		for (Role dest : child.getDestinations())
		{
			for (Role src : child.getSources(dest))
			{
				if (!parent.isEnabled(dest))
				{
					foo.putMessages(dest, src, child.getMessages(dest, src));
				}
			}
		}
	}

	// FIXME: factor out with MessageIdMap
	private static void merge(WFChoiceEnv parent, ConnectedMap foo, ConnectedMap child)
	{
		for (Role dest : child.getDestinations())
		{
			for (Role src : child.getSources(dest))
			{
				if (!parent.isConnected(src, dest))
				{
					// If not previously connected, update connected according to latest state
					foo.setConnected(dest, src, child.isConnected(dest, src));
				}
			}
		}
	}
	
	public WFChoiceEnv enableRoleForRootProtocolDecl(Role role)
	{
		return addMessage(WFChoiceEnv.DUMMY_ROLE, role, WFChoiceEnv.ROOT_MESSAGESIGNATURE);
	}

	public WFChoiceEnv enableChoiceSubject(Role role)
	{
		return addMessage(WFChoiceEnv.DUMMY_ROLE, role, WFChoiceEnv.SUBJECT_MESSAGESIGNATURE);
	}
	
	public WFChoiceEnv connect(Role src, Role dest)
	{
		WFChoiceEnv copy = copy();
		copy.connected.connect(src, dest);
		return copy;
	}
	
	public WFChoiceEnv disconnect(Role src, Role dest)
	{
		WFChoiceEnv copy = copy();
		copy.connected.disconnect(src, dest);
		return copy;
	}

	// The "main" public routine
	// Rename: more like enable-if-not-already
	public WFChoiceEnv addMessage(Role src, Role dest, Message msg)
	{
		WFChoiceEnv copy = copy();
		addMessages(copy.initial, src, dest, Arrays.asList(msg.getId()));
		return copy;
	}

	public WFChoiceEnv removeMessage(Role src, Role dest, Message msg)
	{
		WFChoiceEnv copy = copy();
		copy.initial.removeMessage(dest, src, msg.getId());
		return copy;
	}

	// FIXME: List/Set argument
	// Means: record message as initial enabling message if dest not already enabled
	private static void addMessages(MessageIdMap map, Role src, Role dest, List<MessageId<?>> msgs)
	{
		if (!map.containsDestination(dest))  // FIXME: factor out isEnabled
		{
			map.putMessages(dest, src, new HashSet<>(msgs));
		}
	}

	/*public InlinedWFChoiceEnv addInterrupt(Role src, Role dest, Message msg)
	{
		InlinedWFChoiceEnv copy = copy();
		if (!copy.initial.containsLeftKey(dest))
		{
			copy.initialInterrupts.putMessage(dest, src, msg.getId());
		}
		return copy;
	}*/
	
	public boolean isEnabled(Role role)
	{
		return this.initial.containsDestination(role);
	}

	public MessageIdMap getEnabled()
	{
		MessageIdMap tmp = new MessageIdMap(this.initial);
		//tmp.merge(this.initialInterrupts);
		return tmp;
	}
	
	public boolean isConnected(Role r1, Role r2)
	{
		return this.connected.isConnected(r1, r2);
	}

	@Override
	public String toString()
	{
		//return "initial=" + this.initial.toString() + ", initialInterrupts=" + this.initialInterrupts.toString();
		return "initial=" + this.initial.toString();
	}
}
