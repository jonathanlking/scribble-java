package org.scribble2.model.del.local;

import org.scribble2.fsm.ScribbleFsm;
import org.scribble2.model.ModelNode;
import org.scribble2.model.Module;
import org.scribble2.model.context.LProtocolDeclContext;
import org.scribble2.model.del.ProtocolDeclDel;
import org.scribble2.model.local.LProtocolDecl;
import org.scribble2.model.visit.ContextBuilder;
import org.scribble2.model.visit.FsmConstructor;
import org.scribble2.sesstype.kind.Local;
import org.scribble2.sesstype.name.LProtocolName;
import org.scribble2.sesstype.name.Role;
import org.scribble2.util.ScribbleException;

public class LProtocolDeclDel extends ProtocolDeclDel<Local>
//public class LProtocolDeclDel extends ProtocolDeclDel
{
	public LProtocolDeclDel()
	{

	}

	@Override
	public void enterContextBuilding(ModelNode parent, ModelNode child, ContextBuilder builder) throws ScribbleException
	{
		builder.clearProtocolDependencies();  // collect per protocoldecl all together, do not clear?
		
		Module main = (Module) parent;
		
		LProtocolDecl lpd = (LProtocolDecl) child;
		LProtocolName lpn = lpd.getFullProtocolName(main);
		for (Role role : lpd.header.roledecls.getRoles())
		{
			builder.addProtocolDependency(role, lpn, role);  // FIXME: is it needed to add self protocol decl?
		}
	}
	
	@Override
	public LProtocolDecl leaveContextBuilding(ModelNode parent, ModelNode child, ContextBuilder builder, ModelNode visited) throws ScribbleException
	{
		//System.out.println("c: " + proj.getProtocolDependencies());

		LProtocolDecl gpd = (LProtocolDecl) visited;
		/*LProtocolDeclDel del = copy();  // FIXME: should be a deep clone in principle -- but if any other children are immutable, they can be shared
		del.setProtocolDeclContext(new LProtocolDeclContext(builder.getLocalProtocolDependencies()));*/
		LProtocolDeclContext gcontext = new LProtocolDeclContext(builder.getLocalProtocolDependencyMap());
		LProtocolDeclDel del = (LProtocolDeclDel) setProtocolDeclContext(gcontext);
		return (LProtocolDecl) gpd.del(del);
	}

	@Override
	public void enterFsmConstruction(ModelNode parent, ModelNode child, FsmConstructor conv)// throws ScribbleException
	{
		//pushVisitorEnv(parent, child, conv);
		conv.builder.reset();
	}

	@Override
	public ModelNode leaveFsmConstruction(ModelNode parent, ModelNode child, FsmConstructor conv, ModelNode visited)// throws ScribbleException
	{
		//LProtocolDecl lpd = (LProtocolDecl) visited;
		//ScribbleFsm f = ((FsmBuildingEnv) lpd.def.del().env()).getFsm();

		System.out.println("b: " + new ScribbleFsm(conv.builder.getEntry(), conv.builder.getExit()));
		
		..HERE: store graphs/fsms in jobcontext; bit like projections, but store in jc here directly, don't wait for ModuleDel

		return visited;
	}
	
	@Override
	public LProtocolDeclContext getProtocolDeclContext()
	{
		return (LProtocolDeclContext) super.getProtocolDeclContext();
	}
	
	/*@Override
	//protected DependencyMap<? extends ProtocolName<? extends ProtocolKind>> newDependencyMap()
	protected DependencyMap<LProtocolName> newDependencyMap()
	{
		return new DependencyMap<LProtocolName>();
	}*/

	/*protected LocalProtocolDeclDelegate(Map<Role, Map<ProtocolName, Set<Role>>> dependencies)
	{
		super(dependencies);
	}

	@Override
	protected LocalProtocolDeclDelegate reconstruct(Map<Role, Map<ProtocolName, Set<Role>>> dependencies)
	{
		return new LocalProtocolDeclDelegate(dependencies);
	}*/

	@Override
	protected LProtocolDeclDel copy()
	{
		return new LProtocolDeclDel();
	}

	/*@Override
	public LocalProtocolDeclDelegate setDependencies(Map<Role, Map<ProtocolName, Set<Role>>> dependencies)
	{
		return (LocalProtocolDeclDelegate) super.setDependencies(dependencies);
	}*/

	/*@Override
	protected LProtocolDeclContext newProtocolDeclContext(Map<Role, Map<ProtocolName<? extends ProtocolKind>, Set<Role>>> deps)
	{
		return new LProtocolDeclContext(new DependencyMap<>(cast(deps)));
	}

	private static Map<Role, Map<LProtocolName, Set<Role>>> cast(Map<Role, Map<ProtocolName<? extends ProtocolKind>, Set<Role>>> map)
	{
		return map.keySet().stream().collect(Collectors.toMap((r) -> r, (r) -> castAux(map.get(r))));
	}

	private static Map<LProtocolName, Set<Role>> castAux(Map<ProtocolName<? extends ProtocolKind>, Set<Role>> map)
	{
		return map.keySet().stream().collect(Collectors.toMap((k) -> (LProtocolName) k, (k) -> map.get(k)));
	}*/
}
