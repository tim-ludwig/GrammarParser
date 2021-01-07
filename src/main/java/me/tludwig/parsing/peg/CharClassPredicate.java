package me.tludwig.parsing.peg;

import static me.tludwig.parsing.peg.CharClassPredicateType.BLOCK;
import static me.tludwig.parsing.peg.CharClassPredicateType.CATEGORIES;
import static me.tludwig.parsing.peg.CharClassPredicateType.CATEGORY;
import static me.tludwig.parsing.peg.CharClassPredicateType.CONSTANT;
import static me.tludwig.parsing.peg.CharClassPredicateType.INTERSECTION;
import static me.tludwig.parsing.peg.CharClassPredicateType.LIST;
import static me.tludwig.parsing.peg.CharClassPredicateType.NEGATION;
import static me.tludwig.parsing.peg.CharClassPredicateType.RANGE;
import static me.tludwig.parsing.peg.CharClassPredicateType.UNION;

import java.lang.Character.UnicodeBlock;
import java.util.Arrays;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

import me.tludwig.parsing.UnicodeGeneralCategory;
import me.tludwig.parsing.peg.expressions.primaries.LiteralCharClass;

public abstract class CharClassPredicate implements IntPredicate {
	private final CharClassPredicateType type;
	
	public CharClassPredicate(final CharClassPredicateType type) {
		this.type = type;
	}
	
	public abstract int[] data();
	
	public final boolean test(final char value) {
		return test((int) value);
	}
	
	public final int id() {
		return type.getId();
	}
	
	public static CharClassPredicate constant(final int c) {
		return new CharClassPredicate(CONSTANT) {
			
			@Override
			public boolean test(final int value) {
				return value == c;
			}
			
			@Override
			public int[] data() {
				return new int[] { c };
			}
		};
	}
	
	public static CharClassPredicate range(final int start, final int end) {
		return new CharClassPredicate(RANGE) {
			
			@Override
			public boolean test(final int value) {
				return start <= value && value <= end;
			}
			
			@Override
			public int[] data() {
				return new int[] { start, end };
			}
		};
	}
	
	public static CharClassPredicate list(final char... chars) {
		return list(IntStream.iterate(0, i -> i < chars.length, i -> i + 1).map(i -> chars[i]).toArray());
	}
	
	public static CharClassPredicate list(final int... chars) {
		Arrays.sort(chars);
		
		return new CharClassPredicate(LIST) {
			
			@Override
			public boolean test(final int value) {
				return Arrays.binarySearch(chars, value) >= 0;
			}
			
			@Override
			public int[] data() {
				return chars;
			}
		};
	}
	
	public static CharClassPredicate category(final UnicodeGeneralCategory category) {
		return new CharClassPredicate(CATEGORY) {
			
			@Override
			public boolean test(final int value) {
				return UnicodeGeneralCategory.of(value) == category;
			}
			
			@Override
			public int[] data() {
				return new int[] { category.id() };
			}
		};
	}
	
	public static CharClassPredicate categories(final UnicodeGeneralCategory... categories) {
		Arrays.sort(categories);
		
		return new CharClassPredicate(CATEGORIES) {
			private final int[] data = Arrays.stream(categories).mapToInt(UnicodeGeneralCategory::id).toArray();
			
			@Override
			public boolean test(final int value) {
				return Arrays.binarySearch(categories, UnicodeGeneralCategory.of(value)) >= 0;
			}
			
			@Override
			public int[] data() {
				return data;
			}
		};
	}
	
	public static CharClassPredicate block(final UnicodeBlock block) {
		return new CharClassPredicate(BLOCK) {
			private final int[] data = block.toString().codePoints().toArray();
			
			@Override
			public boolean test(final int value) {
				return UnicodeBlock.of(value).equals(block);
			}
			
			@Override
			public int[] data() {
				return data;
			}
		};
	}
	
	public static CharClassPredicate union(final CharClassPredicate... predicates) {
		return new CharClassPredicate(UNION) {
			private final int[] data = Arrays.stream(predicates).flatMapToInt(p -> Arrays.stream(CharClassPredicate.getData(p))).toArray();
			
			@Override
			public boolean test(final int value) {
				return Arrays.stream(predicates).anyMatch(p -> p.test(value));
			}
			
			@Override
			public int[] data() {
				return data;
			}
		};
	}
	
	public static CharClassPredicate intersection(final CharClassPredicate... predicates) {
		return new CharClassPredicate(INTERSECTION) {
			private final int[] data = Arrays.stream(predicates).flatMapToInt(p -> Arrays.stream(CharClassPredicate.getData(p))).toArray();
			
			@Override
			public boolean test(final int value) {
				return Arrays.stream(predicates).allMatch(p -> p.test(value));
			}
			
			@Override
			public int[] data() {
				return data;
			}
		};
	}
	
	public static CharClassPredicate negation(final CharClassPredicate predicate) {
		return new CharClassPredicate(NEGATION) {
			private final int[] data = CharClassPredicate.getData(predicate);
			
			@Override
			public boolean test(final int value) {
				return !predicate.test(value);
			}
			
			@Override
			public int[] data() {
				return data;
			}
		};
	}
	
	/*
	 * Utility methods
	 */
	public static CharClassPredicate[] convert(final LiteralCharClass... classes) {
		return Arrays.stream(classes).map(LiteralCharClass::getPredicate).toArray(CharClassPredicate[]::new);
	}
	
	public static CharClassPredicate[] convert(final LiteralCharClass charClass, final LiteralCharClass... classes) {
		final LiteralCharClass[] array = new LiteralCharClass[classes.length + 1];
		
		Arrays.setAll(array, i -> i == 0 ? charClass : classes[i - 1]);
		
		return convert(array);
	}
	
	public static int[] getData(final CharClassPredicate predicate) {
		final int[] array = new int[predicate.data().length + 2];
		
		System.arraycopy(predicate.data(), 0, array, 2, array.length);
		
		array[0] = predicate.id();
		array[1] = predicate.data().length;
		
		return array;
	}
	
	public static CharClassPredicate fromData(final int[] data) {
		final int[] realData = Arrays.copyOfRange(data, 2, data.length);
		
		switch(CharClassPredicateType.getById(data[0])) {
			case CONSTANT:
				return constant(realData[0]);
			case RANGE:
				return range(realData[0], realData[1]);
			case LIST:
				return list(realData);
			case CATEGORY:
				return category(UnicodeGeneralCategory.getById(realData[0]));
			case CATEGORIES:
				return categories(Arrays.stream(realData).mapToObj(UnicodeGeneralCategory::getById).toArray(UnicodeGeneralCategory[]::new));
			case BLOCK:
				return block(UnicodeBlock.forName(new String(realData, 0, realData.length)));
			case UNION:
				// TODO
				return union();
			case INTERSECTION:
				// TODO
				return intersection();
			case NEGATION:
				return negation(fromData(realData));
		}
		
		return null;
	}
}
