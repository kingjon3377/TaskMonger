package monger.persistence;

import org.jetbrains.annotations.NotNull;

/**
 * An exception class for problems in the persistence layer, so we don't have to declare both XML and DB related
 * exceptions on the interfaces.
 */
public final class PersistenceException extends Exception {
	public PersistenceException(final @NotNull String message, final @NotNull Throwable wrapped) {
		super(message, wrapped);
	}

	public PersistenceException(final String message) {
		super(message);
	}
}
