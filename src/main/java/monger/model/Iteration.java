package monger.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import java.util.Comparator;

/**
 * An "iteration" or "sprint"---a span of time to which tasks can be assigned. Iterations in which tasks are actually worked
 * on should have start and end dates, but "iterations" without dates can be used as just task-containing "buckets"
 * for planning purposes.
 */
public class Iteration implements Comparable<Iteration> {
	/**
	 * A unique identifier for the iteration.
	 */
	private final @NotNull UUID id;

	/**
	 * A human-readable name for the iteration. TODO: Nullable, or use the empty string for "none provided"?
	 */
	private @Nullable String name;

	/**
	 * The start point of the iteration. Must not be null if endDate is not null.
	 */
	private @Nullable LocalDate startDate;

	/**
	 * The end point of the iteration. Must be null if startDate is null. If not null, must be after startDate.
	 */
	private @Nullable LocalDate endDate;

	private static void checkInvariant(final @Nullable LocalDate startDate, final @Nullable LocalDate endDate) {
		if (Objects.isNull(startDate)) {
			if (!Objects.isNull(endDate)) {
				throw new IllegalArgumentException("Iteration cannot have end date without start date");
			}
		} else if (!Objects.isNull(endDate) && !endDate.isAfter(startDate)) {
			throw new IllegalArgumentException("Iteration end date must be after start date");
		}
	}

	public Iteration(final @NotNull UUID id, final @Nullable String name, final @Nullable LocalDate startDate,
					 final @Nullable LocalDate endDate) {
		checkInvariant(startDate, endDate);
		this.id = id;
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public Iteration(final @NotNull UUID id) {
		this(id, null, null, null);
	}

	/**
	 * A unique identifier for the iteration.
	 */
	public @NotNull UUID getId() {
		return id;
	}

	/**
	 * A human-readable name for the iteration.
	 */
	public @Nullable String getName() {
		return name;
	}

	/**
	 * @param name A human-readable name for the iteration.
	 */
	public void setName(@Nullable String name) {
		this.name = name;
	}

	/**
	 * The start point of the iteration.
	 */
	public @Nullable LocalDate getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate The start point of the iteration. If endDate not null, must not be null and must be earlier than endDate.
	 */
	public void setStartDate(@Nullable LocalDate startDate) {
		checkInvariant(startDate, endDate);
		this.startDate = startDate;
	}

	/**
	 * The end point of the iteration.
	 */
	public @Nullable LocalDate getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate The end point of the iteration. Must be null if startDate is null. If not null, must be after startDate.
	 */
	public void setEndDate(@Nullable LocalDate endDate) {
		checkInvariant(startDate, endDate);
		this.endDate = endDate;
	}

	public Iteration(final @NotNull UUID id, final @Nullable String name) {
		this(id, name, null, null);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Iteration iteration) {
			return Objects.equals(id, iteration.id) && Objects.equals(name, iteration.name) &&
				Objects.equals(startDate, iteration.startDate) && Objects.equals(endDate, iteration.endDate);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public int compareTo(@NotNull Iteration o) {
		return Comparator.comparing(Iteration::getStartDate, Comparator.nullsLast(Comparator.naturalOrder()))
			.thenComparing(Iteration::getEndDate, Comparator.nullsLast(Comparator.naturalOrder()))
			.thenComparing(Iteration::getName, Comparator.nullsLast(Comparator.naturalOrder()))
			.thenComparing(Iteration::getId).compare(this, o);
	}

	/**
	 * An iteration missing either its start date or its end date does not contain *any* dates for the purposes of this
	 * method.
	 * @param date a date
	 * @return whether this iteration contains that date.
	 */
	public boolean containsDate(final @NotNull LocalDate date) {
		return startDate != null && endDate != null && !date.isBefore(startDate) && !date.isAfter(endDate);
	}
}
