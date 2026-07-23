package io.github.sebhajek.pcomb4j;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Base class for all errors raised while parsing with pcomb4j.
 *
 * <p>The hierarchy is sealed into three abstract subclasses:
 *
 * <ul>
 *   <li>{@link Leaf} -- a terminal error with a simple message and no child
 * errors (e.g. "literal not matched", "end of file"). <li>{@link Branch} -- a
 * composite error that records <em>two</em> child errors, typically from the
 * left and right branches of an alternation ({@code or}). <li>{@link Wrapped} --
 * a wrapper error that adds a contextual message around a single inner error
 * (e.g. a label applied via {@code labelError}).
 * </ul>
 *
 * <p>Concrete parser implementations should extend {@link Leaf} for simple
 * terminal failures and
 * {@link Branch} or {@link Wrapped} when the error relates to child parse
 * attempts.
 *
 * <p>The full error tree can be traversed in depth-first order via {@link
 * #traverse()}.
 */
public abstract sealed class ParserError extends Exception {

	/**
	 * Abstract base for terminal errors that carry only a simple message.
	 *
	 * <p>Extend this class to signal specific leaf-level failures such as
	 * "unexpected end of input" or "literal not matched".
	 */
	public abstract static
	  /* clang-format off */
  non-sealed
	  /* clang-format on */
	  class Leaf extends ParserError {

		/**
		 * Creates a new {@code Leaf} error with the given message.
		 *
		 * @param message a human-readable description of the failure
		 */
		public Leaf(final String message) { super(message); }
	}

	/**
	 * Abstract base for composite errors that record two child errors.
	 *
	 * <p>Typically used by alternation combinators ({@code or}) to preserve the
	 * errors from both branches for diagnostic purposes.
	 */
	public abstract static
	  /* clang-format off */
  non-sealed
	  /* clang-format on */
	  class Branch extends ParserError {
		/** Error from the left (first-tried) branch. */
		private final ParserError errorLeft;

		/** Error from the right (fallback) branch. */
		private final ParserError errorRight;

		/**
		 * Creates a new {@code Branch} error.
		 *
		 * @param message a human-readable description of the composite failure
		 * @param errorLeft the error produced by the left (first-tried) branch
		 * @param errorRight the error produced by the right (fallback) branch
		 */
		public Branch(
		  final String      message,
		  final ParserError errorLeft,
		  final ParserError errorRight
		) {
			this.errorLeft  = errorLeft;
			this.errorRight = errorRight;
			super(message);
		}

		/**
		 * Returns the error from the left (first-tried) branch.
		 *
		 * @return the left child error
		 */
		public ParserError getErrorLeft() { return errorLeft; }

		/**
		 * Returns the error from the right (fallback) branch.
		 *
		 * @return the right child error
		 */
		public ParserError getErrorRight() { return errorRight; }
	}

	/**
	 * Abstract base for errors that wrap a single inner error with an
	 * additional contextual message.
	 *
	 * <p>Typically used by labelling combinators to attach a user-friendly
	 * description to an underlying failure.
	 */
	public abstract static
	  /* clang-format off */
  non-sealed
	  /* clang-format on */
	  class Wrapped extends ParserError {
		/** The underlying error being wrapped. */
		private final ParserError errorInner;

		/**
		 * Creates a new {@code Wrapped} error.
		 *
		 * @param message a human-readable description of the context in which
		 *   the inner error occurred
		 * @param errorInner the underlying error being wrapped
		 */
		public Wrapped(final String message, final ParserError errorInner) {
			super(message);
			this.errorInner = errorInner;
		}

		/**
		 * Returns the underlying error that was wrapped.
		 *
		 * @return the inner error
		 */
		public ParserError getErrorInner() { return errorInner; }
	}

	/**
	 * Creates a new {@code ParserError} with the given message and cause.
	 *
	 * @param message a human-readable description of the failure
	 * @param cause the underlying throwable that triggered this error
	 */
	public ParserError(final String message, final Throwable cause) {
		super(message, cause);
	}

	private record Frame(ParserError node, boolean visited) {}

	/**
	 * Returns an in-order (depth-first, left-to-right post-order) flattened
	 * list of every {@code ParserError} node in this error tree.
	 *
	 * <p>This is useful for producing a human-readable summary of all failure
	 * causes, regardless of how deeply nested they are.
	 *
	 * @return an unmodifiable list of all error nodes in the tree
	 */
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

	/**
	 * Creates a new {@code ParserError} with the given message.
	 *
	 * @param message a human-readable description of the failure
	 */
	public ParserError(final String message) { super(message); }
}
