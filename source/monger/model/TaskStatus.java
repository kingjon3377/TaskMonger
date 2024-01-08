package monger.model;

import org.jetbrains.annotations.NotNull;

/**
 * The possible states a task might be in.
 *
 * @author Jonathan Lovelace
 */
public enum TaskStatus {
	Unscheduled("unscheduled"),
	Unstarted("unstarted"),
	InProgress("started"),
	Completed("completed");
	/**
	 * A description of the status.
	 */
	private final @NotNull String description;
	TaskStatus(final @NotNull String description) {
		this.description = description;
	}
}
