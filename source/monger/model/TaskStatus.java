package monger.model;

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
	private final String description;
	TaskStatus(final String description) {
		this.description = description;
	}
}
