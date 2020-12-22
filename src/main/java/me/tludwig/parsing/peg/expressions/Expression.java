package me.tludwig.parsing.peg.expressions;

import me.tludwig.parsing.peg.ExpressionType;
import me.tludwig.parsing.peg.ParseTree;

public abstract class Expression {
	
	/**
	 * @param  input
	 *                  the input String to match
	 * @param  position
	 *                  the position to match at
	 * @return
	 *                  <ul>
	 *                  <li>the {@link ParseTree} Object, the result for this match
	 *                  <li>null, if this expression can't match the input at the
	 *                  specified position
	 *                  </ul>
	 */
	public abstract ParseTree parseTree(String input, int position);
	
	public abstract ExpressionType type();
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
