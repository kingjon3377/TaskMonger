package monger.model;

import org.jetbrains.annotations.NotNull;

/**
 * An identifier for a Task. TODO: Maybe add validation of "identifier" for different systems?
 * @param provider The provider this task came from, or the empty string if it was added by the user directly
 * @param identifier The identifier for the task. Different systems have different rules, but must not be empty.
 */
public record TaskIdentifier(@NotNull String provider, @NotNull String identifier) {
	public TaskIdentifier {
		if (identifier.isEmpty()) {
			throw new IllegalArgumentException("identifier cannot be empty");
		}
	}
}
