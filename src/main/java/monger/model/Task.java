package monger.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A task in the system. TODO: Convert to interface?
 *
 * @author Jonathan Lovelace
 */
public class Task {
	/**
	 * A brief description of the task.
	 */
	private @NotNull String name;
	/**
	 * A longer description of the task.
	 */
	private @NotNull String description = "";
	/**
	 * How much time this task is estimated to take.
	 */
	private @NotNull TimeEstimate estimate = TimeEstimate.Unestimated;
	/**
	 * If this task is a proxy for one in another service, the URL where it can be found.
	 */
	private @NotNull String upstreamURL = "";
	/**
	 * The current status of the task.
	 */
	private @NotNull TaskStatus status = TaskStatus.Unscheduled;

	/**
	 * An identifier for the task. TODO: Need some way of generating IDs for purely-local tasks
	 */
	private final @NotNull TaskIdentifier identifier;

	/**
	 * Main constructor.
	 *
	 * @param identifier the identifier for the task
	 * @param name the brief description of the task.
	 */
	public Task(final @NotNull TaskIdentifier identifier, final @NotNull String name) {
		this.identifier = identifier;
		this.name = name;
	}

	/**
	 * Fuller constructor.
	 *
	 * @param identifier the identifier for the task
	 * @param name        the brief description of the task
	 * @param description a longer description of the task
	 */
	public Task(final @NotNull TaskIdentifier identifier, final @NotNull String name, final @NotNull String description) {
		this.identifier = identifier;
		this.name = name;
		this.description = description;
	}

	/**
	 * @return the identifier for the task
	 */
	public @NotNull TaskIdentifier getIdentifier() {
		return identifier;
	}

	/**
	 * @return a brief description of the task
	 */
	public @NotNull String getName() {
		return name;
	}

	/**
	 * @return a fuller description of the task
	 */
	public @NotNull String getDescription() {
		return description;
	}

	/**
	 * @return how long this task is expected to take.
	 */
	public @NotNull TimeEstimate getEstimate() {
		return estimate;
	}

	/**
	 * @return the URL of the master location of this task, if any
	 */
	public @NotNull String getUpstreamURL() {
		return upstreamURL;
	}

	/**
	 * @return the current status of this task
	 */
	public @NotNull TaskStatus getStatus() {
		return status;
	}

	/**
	 * @param name the new brief description for the task
	 */
	public void setName(final @NotNull String name) {
		this.name = name;
	}

	/**
	 * @param description the new full description of the task
	 */
	public void setDescription(final @NotNull String description) {
		this.description = description;
	}

	/**
	 * @param estimate the new time estimate for the task
	 */
	public void setEstimate(final @NotNull TimeEstimate estimate) {
		this.estimate = estimate;
	}

	/**
	 * @param upstream the new upstream URL for the task
	 */
	public void setUpstreamURL(final @NotNull String upstream) {
		upstreamURL = upstream;
	}

	/**
	 * @param status the new status for the task
	 */
	public void setStatus(final @NotNull TaskStatus status) {
		this.status = status;
	}

	/**
	 * @param obj another object
	 * @return whether it is an identical Task
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Task) {
			return Objects.equals(identifier, ((Task) obj).identifier) && Objects.equals(name, ((Task) obj).name) &&
					   Objects.equals(description, ((Task) obj).getDescription()) &&
					   Objects.equals(estimate, ((Task) obj).estimate) &&
					   Objects.equals(upstreamURL, ((Task) obj).upstreamURL) &&
					   Objects.equals(status, ((Task) obj).status);
		} else {
			return false;
		}
	}

	/**
	 * TODO: Maybe just use identifier.hashCode()? Especially since many of these are mutable.
	 * @return a hash value for this object.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(identifier, name, description, estimate, upstreamURL, status);
	}
}
