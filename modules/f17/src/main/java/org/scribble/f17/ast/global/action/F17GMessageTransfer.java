package org.scribble.f17.ast.global.action;

import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.Op;
import org.scribble.sesstype.name.Role;


public class F17GMessageTransfer extends F17GAction
{
	public final Role dest;
	public final Op op;
	public final Payload pay;
	
	public F17GMessageTransfer(Role src, Role dest, Op op, Payload pay)
	{
		super(src);
		this.op = op;
		this.dest = dest;
		this.pay = pay;
	}
	
	@Override
	public String toString()
	{
		return super.toString() + "->" + this.dest + ":" + this.op + this.pay;  // Payload.toString includes parentheses
	} 

	@Override
	public int hashCode()
	{
		int hash = 31;
		hash = 31 * hash + dest.hashCode();
		hash = 31 * hash + this.op.hashCode();
		hash = 31 * hash + this.pay.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!(obj instanceof F17GMessageTransfer))
		{
			return false;
		}
		F17GMessageTransfer them = (F17GMessageTransfer) obj;
		return super.equals(obj)  // super does canEquals
				&& this.dest.equals(them.dest) && this.op.equals(them.op) && this.pay.equals(them.pay);
	}
	
	@Override
	protected boolean canEquals(Object o)
	{
		return o instanceof F17GMessageTransfer;
	}
}
