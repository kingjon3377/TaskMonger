package monger.model;

import java.util.Objects;

/**
 * A task in the system.
 *
 * TODO: We need to have an ID field, and a way (probably injected) of generating them
 *
 * TODO: Convert to interface?
 *
 * @author Jonathan Lovelace
 */
public class Task {
	/**
	 * A brief description of the task.
	 */
	private String name;
	/**
	 * A longer description of the task.
	 */
	private String description = "";

	/**
	 * Main constructor.
	 *
	 * @param name the brief description of the task.
	 */
	public Task(final String name) {
		this.name = name;
	}

	/**
	 * Fuller constructor.
	 *
	 * @param name        the brief description of the task
	 * @param description a longer description of the task
	 */
	public Task(final String name, final String description) {
		this.name = name;
		this.description = description;
	}

	/**
	 * @return a brief description of the task
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return a fuller description of the task
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param name the new brief description for the task
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @param description the new full description of the task
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @param obj another object
	 * @return whether it is an identical Task
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Task) {
			return Objects.equals(name, ((Task) obj).name) &&
					   Objects.equals(description, ((Task) obj).getDescription());
		} else {
			return false;
		}
	}

	/**
	 * @return a hash value for this object.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(name, description);
	}
}
