package org.scribble.del.global;

import java.util.List;

import org.scribble.ast.AstFactoryImpl;
import org.scribble.ast.Continue;
import org.scribble.ast.InteractionNode;
import org.scribble.ast.ScribNode;
import org.scribble.ast.global.GProtocolBlock;
import org.scribble.ast.global.GRecursion;
import org.scribble.ast.local.LProtocolBlock;
import org.scribble.ast.local.LRecursion;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.del.RecursionDel;
import org.scribble.main.ScribbleException;
import org.scribble.sesstype.kind.Local;
import org.scribble.visit.Projector;
import org.scribble.visit.ProtocolDefInliner;
import org.scribble.visit.WFChoiceChecker;
import org.scribble.visit.WFChoicePathChecker;
import org.scribble.visit.env.InlineProtocolEnv;
import org.scribble.visit.env.ProjectionEnv;
import org.scribble.visit.env.WFChoiceEnv;
import org.scribble.visit.env.WFChoicePathEnv;

public class GRecursionDel extends RecursionDel implements GCompoundInteractionNodeDel
{
	@Override
	public ScribNode leaveProtocolInlining(ScribNode parent, ScribNode child, ProtocolDefInliner inl, ScribNode visited) throws ScribbleException
	{
		GRecursion gr = (GRecursion) visited;
		RecVarNode recvar = gr.recvar.clone();
		GProtocolBlock block = (GProtocolBlock) ((InlineProtocolEnv) gr.block.del().env()).getTranslation();	
		GRecursion inlined = AstFactoryImpl.FACTORY.GRecursion(recvar, block);
		inl.pushEnv(inl.popEnv().setTranslation(inlined));
		return (GRecursion) super.leaveProtocolInlining(parent, child, inl, gr);
	}

	@Override
	public GRecursion leaveInlinedWFChoiceCheck(ScribNode parent, ScribNode child, WFChoiceChecker checker, ScribNode visited) throws ScribbleException
	{
		GRecursion rec = (GRecursion) visited;
		WFChoiceEnv merged = checker.popEnv().mergeContext((WFChoiceEnv) rec.block.del().env());
		checker.pushEnv(merged);
		return (GRecursion) super.leaveInlinedWFChoiceCheck(parent, child, checker, rec);
	}

	@Override
	public GRecursion leaveProjection(ScribNode parent, ScribNode child, Projector proj, ScribNode visited) throws ScribbleException
	{
		GRecursion gr = (GRecursion) visited;
		RecVarNode recvar = gr.recvar.clone();
		LProtocolBlock block = (LProtocolBlock) ((ProjectionEnv) gr.block.del().env()).getProjection();	
		LRecursion projection = null;
		if (!block.isEmpty())
		{
			List<? extends InteractionNode<Local>> lis = block.seq.getInteractions();
			if (!(lis.size() == 1 && lis.get(0) instanceof Continue))
			{
				projection = AstFactoryImpl.FACTORY.LRecursion(recvar, block);
			}
		}
		proj.pushEnv(proj.popEnv().setProjection(projection));
		return (GRecursion) GCompoundInteractionNodeDel.super.leaveProjection(parent, child, proj, gr);
	}

	@Override
	public void enterWFChoicePathCheck(ScribNode parent, ScribNode child, WFChoicePathChecker coll) throws ScribbleException
	{
		WFChoicePathEnv env = coll.peekEnv().enterContext();
		env = env.clear();  // Because merge will append the child paths to the parent paths -- without clearing, the child paths already have the parent suffixes
		coll.pushEnv(env);
	}

	@Override
	public GRecursion leaveWFChoicePathCheck(ScribNode parent, ScribNode child, WFChoicePathChecker coll, ScribNode visited) throws ScribbleException
	//public GRecursion leavePathCollection(ScribNode parent, ScribNode child, PathCollectionVisitor coll, ScribNode visited) throws ScribbleException
	{
		GRecursion rec = (GRecursion) visited;
		WFChoicePathEnv merged = coll.popEnv().mergeContext((WFChoicePathEnv) rec.block.del().env());  // Corresponding push is in CompoundInteractionDel
		coll.pushEnv(merged);
		return (GRecursion) super.leaveWFChoicePathCheck(parent, child, coll, rec);
		//return (GRecursion) super.leavePathCollection(parent, child, coll, rec);
	}
}
