package monger.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.*;

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
	 * Dates within an iteration that tasks are scheduled.
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
}
