package org.codeberg.sebhajek.pcomb4j;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Base class for all errors raised while parsing with pcomb4j.
 *
 * <p>Concrete parser implementations should extend this class to signal
 * specific failure conditions (e.g. unexpected end of input, an unmatched
 * literal, or a failed predicate).
 */
public abstract sealed class ParserError extends Exception {
	public abstract static
	  /* clang-format off */
  non-sealed
	  /* clang-format on */
	  class Leaf extends ParserError {
		public Leaf(final String message) { super(message); }
	}

	public abstract static
	  /* clang-format off */
  non-sealed
	  /* clang-format on */
	  class Branch extends ParserError {
		private final ParserError errorLeft;
		private final ParserError errorRight;

		public Branch(
		  final String      message,
		  final ParserError errorLeft,
		  final ParserError errorRight
		) {
			this.errorLeft  = errorLeft;
			this.errorRight = errorRight;
			super(message);
		}

		public ParserError getErrorLeft() { return errorLeft; }

		public ParserError getErrorRight() { return errorRight; }
	}

	public abstract static
	  /* clang-format off */
  non-sealed
	  /* clang-format on */
	  class Wrapped extends ParserError {
		private final ParserError errorInner;

		public Wrapped(final String message, final ParserError errorInner) {
			super(message);
			this.errorInner = errorInner;
		}

		public ParserError getErrorInner() { return errorInner; }
	}

	public ParserError(final String message, final Throwable cause) {
		super(message, cause);
	}

	private record Frame(ParserError node, boolean visited) {}

	public List<ParserError> traverse() {
		final var result = new ArrayList<ParserError>();
		final var stack  = new ArrayDeque<Frame>();
		traverse(result, stack);
		return result;
	}

	private final void
	traverse(final List<ParserError> result, final Deque<Frame> stack) {
		stack.push(new Frame(this, false));
		while (!stack.isEmpty()) {
			final Frame       frame = stack.pop();
			final ParserError node  = frame.node();
			if (frame.visited()) {
				result.add(node);
				continue;
			}
			stack.push(new Frame(node, true));
			pushNew(stack, node);
		}
	}

	private final void
	pushNew(final Deque<Frame> stack, final ParserError node) {
		switch (node) {
			case ParserError.Leaf _ -> {
			}
			case ParserError.Branch branch -> {
				stack.push(new Frame(branch.getErrorRight(), false));
				stack.push(new Frame(branch.getErrorLeft(), false));
			}
			case ParserError.Wrapped wrapped -> {
				stack.push(new Frame(wrapped.getErrorInner(), false));
			}
		}
	}

	public ParserError(final String message) { super(message); }
}
