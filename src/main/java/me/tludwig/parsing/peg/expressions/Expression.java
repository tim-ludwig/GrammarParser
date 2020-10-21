package me.tludwig.parsing.peg.expressions;

import me.tludwig.parsing.peg.Match;

public abstract class Expression {
	/**
	 * @param  input
	 *                  the input String to match
	 * @param  position
	 *                  the position to match at
	 * @return
	 *                  <ul>
	 *                  <li>the {@link Match} Object, the result for this match
	 *                  <li>null, if this expression can't match the input at the specified position
	 *                  </ul>
	 */
	public abstract Match match(String input, int position);
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
