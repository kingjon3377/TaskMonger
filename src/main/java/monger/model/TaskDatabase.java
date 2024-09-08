package monger.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;

/**
 * The single class that contains the entire data model.
 */
public class TaskDatabase {
	/**
	 * All iterations known.
	 */
	private final Set<Iteration> iterations = new TreeSet<>();
	/**
	 * All tasks known.
	 */
	private final Map<TaskIdentifier, Task> tasks = new HashMap<>();
	/**
	 * Mappings from tasks to iterations. TODO: We want a MultiMap from iterations to tasks!
	 */
	private final Map<TaskIdentifier, Iteration> taskIterations = new HashMap<>();

	/**
	 * Dates within an iteration that tasks are scheduled. TODO: What about multi-day tasks?
	 */
	private final Map<TaskIdentifier, LocalDate> taskDates = new HashMap<>();

	/**
	 * If multiple iterations overlap, we pick the one with the earliest start date; if there's a tie there, we
	 * pick the one with the earliest end date; if there's a tie there, we pick the one with the alphabetically first
	 * name, and if there's multiple otherwise-identical iterations we pick the first ID.
	 * @param date a date
	 * @return the iteration, if any, containing that date
	 */
	public @Nullable Iteration getIterationByDate(final @NotNull LocalDate date) {
		return iterations.stream().filter(iteration -> iteration.containsDate(date)).findFirst().orElse(null);
	}

	/**
	 * @return a stream of all the iterations
	 */
	public Iterable<Iteration> getIterations() {
		return Collections.unmodifiableCollection(iterations);
	}

	/**
	 * @return a stream of all tasks
	 */
	public Iterable<Task> getTasks() {
		return Collections.unmodifiableCollection(tasks.values());
	}

	protected void setTasks(List<Task> tasks) {
		for (Task task : tasks) {
			this.tasks.put(task.getIdentifier(), task);
		}
	}

	/**
	 * TODO: Should we just return task IDs?
	 * @param iteration an iteration
	 * @return all the tasks it contains, sorted by their scheduled date (unscheduled tasks first)
	 */
	public Iterable<Task> getTasksInIteration(final @NotNull Iteration iteration) {
		return taskIterations.entrySet().stream()
			.filter(e -> Objects.equals(iteration.getId(), e.getValue().getId()))
			.map(Map.Entry::getKey).sorted(Comparator.nullsFirst(Comparator.comparing(taskDates::get,
				Comparator.naturalOrder())))
			.map(tasks::get)
			.filter(Objects::nonNull)
			.toList();
	}

	public void addTask(final Task task) {
		tasks.put(task.getIdentifier(), task);
	}

	public void assignToIteration(final TaskIdentifier task, final UUID iterationId) {
		final Optional<Iteration> iteration =
			iterations.stream().filter(i -> Objects.equals(iterationId, i.getId()))
				.findAny();
		if (iteration.isPresent()) {
			assignToIteration(task, iteration.get());
		} else {
			throw new IllegalArgumentException("Unknown iteration");
		}
	}

	public void assignToIteration(final TaskIdentifier task, final Iteration iteration) {
		if (!tasks.containsKey(task)) {
			throw new IllegalArgumentException("Unknown task");
		} else if (iterations.contains(iteration)) {
			taskIterations.put(task, iteration);
		} else {
			throw new IllegalArgumentException("Unknown iteration");
		}
	}

	public void assignToDate(final TaskIdentifier task, final LocalDate date) {
		Iteration iteration = taskIterations.get(task);
		if (!tasks.containsKey(task)) {
			throw new IllegalArgumentException("Unknown task");
		} else if (iteration == null) {
			throw new IllegalStateException("Task not assigned to an iteration");
		} else if (iteration.containsDate(date)) {
			taskDates.put(task, date);
		} else {
			throw new IllegalArgumentException("Date not within task's iteration");
		}
	}

	public @Nullable LocalDate getAssignedDate(final TaskIdentifier task) {
		return taskDates.get(task);
	}

	public void addIteration(final Iteration iteration) {
		iterations.add(iteration);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		} else if (o instanceof TaskDatabase that) {
			return Objects.equals(iterations, that.iterations) &&
					   Objects.equals(tasks, that.tasks) &&
					   Objects.equals(taskIterations, that.taskIterations) &&
					   Objects.equals(taskDates, that.taskDates);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(iterations, tasks, taskIterations, taskDates);
	}

	@Override
	public String toString() {
		return "TaskDatabase:\n" +
				   "\titerations=" + iterations +
				   "\n\ttasks=" + tasks +
				   "\n\ttaskIterations=" + taskIterations +
				   "\n\ttaskDates=" + taskDates;
	}

	/**
	 * @param identifier The identifier for a task
	 * @return The iteration, if any, to which that task is assigned.
	 */
	public @Nullable Iteration getIteration(final @NotNull TaskIdentifier identifier) {
		return taskIterations.get(identifier);
	}
}
