package me.tludwig.parsing;

public class Util {
	
	public static boolean checkNonNull(final Object... objects) {
		for(final Object obj : objects)
			if(obj == null) return false;
		
		return true;
	}
	
	public static void requireNonNull(final String msg, final Object... objects) {
		if(!checkNonNull(objects)) throw new NullPointerException(msg);
	}
}
