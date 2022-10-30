package monger.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A set of tasks that make up a single schedule.
 */
public class Project {
	private static final @NotNull Logger LOGGER = Logger.getLogger(Project.class.getName());

	@NotNull
	private IDGenerator idg;

	@NotNull
	private final Map<@NotNull Long, @NotNull Task> tasks = new HashMap<>();

	public Project(final @NotNull IDGenerator idg) {
		this.idg = idg;
	}

	public Project() {
		idg = new IDGenerator();
	}

	/**
	 * This method should RARELY be called, but it may sometimes be necessary.
	 */
	public final void setIDGenerator(final @NotNull IDGenerator idg) {
		LOGGER.log(Level.WARNING, "ID generator set from this context", new Exception());
		this.idg = idg;
	}

	/**
	 * Get a task by ID.
	 * @param id the ID of the task to get
	 * @return the task with that ID, or null if none present
	 */
	public final @Nullable Task getTask(final long id) {
		return tasks.get(id);
	}

	/**
	 * Generate an ID for a task.
	 * @return the next suitable ID number.
	 */
	public final long generateID() {
		return idg.getId();
	}

	/**
	 * Add a task to the project.
	 *
	 * TODO: How to handle ID collisions? Overwrite, throw, or what?
	 *
	 * @param task the task to add
	 */
	public final void addTask(final @NotNull Task task) {
		tasks.put(task.getId(), task);
	}

	/**
	 * @return all tasks in the project
	 */
	public @NotNull Map<@NotNull Long, @NotNull Task> getTasks() {
		return Collections.unmodifiableMap(tasks);
	}

	/**
	 * @return a stream of all tasks in the project.
	 */
	public @NotNull Stream<@NotNull Task> streamTasks() {
		return tasks.values().stream();
	}
}
