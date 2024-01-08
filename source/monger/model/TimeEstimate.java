package monger.model;

import org.jetbrains.annotations.NotNull;

/**
 * The possible values a task's time-estimate field can be set to, to allow time budgeting
 * with some but not *too* much granularity.
 *
 * @author Jonathan Lovelace
 */
public enum TimeEstimate {
	/**
	 * For a task which has not yet been estimated.
	 */
	Unestimated("Unestimated", -1),
	/**
	 * For a "task" which will take no time beyond that already budgeted for in other
	 * tasks.
	 */
	NoTime("No Additional Time", 0),
	/**
	 * One quarter of an hour.
	 */
	QuarterHour("Quarter Hour", 1),
	/**
	 * Half an hour.
	 */
	HalfHour("Half Hour", 2),
	/**
	 * Three-quarters of an hour.
	 */
	ThreeQuartersHour("45 Minutes", 3),
	/**
	 * One hour.
	 */
	Hour("One Hour", 4),
	/**
	 * One and a half hours.
	 */
	HourAndAHalf("1.5 Hours", 6),
	/**
	 * Two hours.
	 */
	TwoHours("Two Hours", 8),
	/**
	 * Three hours.
	 */
	ThreeHours("Three Hours", 12),
	/**
	 * Four hours.
	 */
	FourHours("Four Hours", 16),
	/**
	 * All day (eight hours).
	 */
	AllDay("All Day", 32),
	/**
	 * Two days (sixteen hours).
	 */
	TwoDays("Two Days", 64),
	/**
	 * Longer than two days. Anything of this length should be divided into more granular
	 * tasks, and so is for now marked as being of infinite length.
	 */
	Epic("Unbudgetable", Integer.MAX_VALUE);
	/**
	 * The user-visible description of the estimate.
	 */
	private final @NotNull String description;
	/**
	 * How many quarter-hour intervals this is equivalent to.
	 */
	private final int budget;
	/**
	 * Constructor.
	 */
	TimeEstimate(final @NotNull String description, final int budget) {
		this.description = description;
		this.budget = budget;
	}
	/**
	 * @return a user-visible description of the estimate
	 */
	public @NotNull String getDescription() {
		return description;
	}
	/**
	 * @return how many quarter-hour intervals this is equal to
	 */
	public int getBudget() {
		return budget;
	}
}
