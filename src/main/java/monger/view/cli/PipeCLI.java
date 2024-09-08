package monger.view.cli;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import monger.model.Iteration;
import monger.model.Task;
import monger.model.TaskDatabase;
import monger.model.TaskIdentifier;
import monger.persistence.PersistenceException;
import monger.persistence.PersistenceHelper;
import org.jetbrains.annotations.Nullable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * A "pipe" CLI. FIXME: Provide a IExecutionExceptionHandler to improve UI on error
 */
@Command(name = "task-monger",
	description = "Manage a database of tasks programmatically")
public class PipeCLI implements Runnable {
	@CommandLine.Spec
	CommandLine.Model.CommandSpec spec;

	@Override
	public void run() {
		throw new CommandLine.ParameterException(spec.commandLine(),
			"Specify a subcommand");
	}

	public static void main(final String... args) {
		final CommandLine cmd = new CommandLine(new PipeCLI());
		if (args.length == 0) {
			cmd.usage(System.out);
		} else {
			cmd.execute(args);
		}
	}

	@Command(name = "list-tasks", description = "Print a summary of all tasks")
	public String listTasks(@Option(names = {"--database"}, required = true,
		description = "File containing the task database") Path databaseFile,
							@Option(names = {"--quiet", "-q"},
								description = "Omit header row") boolean quiet,
							@Parameters(arity = "0..*", paramLabel = "<task identifier>",
								description =
									"The tasks to be displayed, in the format " +
										"provider:identifier")
							String[] taskIdentifiers)
		throws PersistenceException, IOException {
		List<TaskIdentifier> identifiers = new ArrayList<>();
		for (String string : taskIdentifiers) {
			identifiers.add(new TaskIdentifier("", string.trim()));
			if (string.chars().filter(ch -> ch == ':').count() == 1L) {
				String[] split = string.split(":");
				identifiers.add(new TaskIdentifier(split[0], split[1]));
			}
		}
		TaskDatabase db = PersistenceHelper.readFromFile(databaseFile);
		return listTasks(quiet, db, identifiers);
	}
	private static String iterationForList(@Nullable Iteration iteration) {
		if (Objects.isNull(iteration)) {
			return "<none>";
		}
		String name = iteration.getName();
		if (Objects.isNull(name) || name.isBlank()) {
			LocalDate startDate = iteration.getStartDate();
			LocalDate endDate = iteration.getEndDate();
			if (Objects.isNull(startDate)) {
				return iteration.getId().toString().substring(0, 9);
			} else if (Objects.isNull(endDate)) {
				return startDate.format(DateTimeFormatter.ISO_DATE) + " on";
			} else {
				return "%s to %s".formatted(startDate.format(DateTimeFormatter.ISO_DATE),
					endDate.format(DateTimeFormatter.ISO_DATE));
			}
		} else {
			return name;
		}
	}
	/**
	 * Implementation, split out for automated-test purposes.
	 */
	public String listTasks(boolean quiet, TaskDatabase tasks,
							List<TaskIdentifier> identifiers) throws IOException {
		int[] fieldLengths;
		// FIXME: Need to include "Iteration" and "Scheduled" [date]
		if (quiet) {
			fieldLengths = new int[]{1, 1, 1, 1, 1, 1};
		} else {
			// "ID", "Name", "Status", "Estimate", "Description"
			fieldLengths = new int[]{2, 4, 6, 8, 9, 9};
		}
		final List<List<String>> output = new ArrayList<>();
		for (Task task : tasks.getTasks()) {
			if (identifiers.isEmpty() || identifiers.contains(task.getIdentifier())) {
				final List<String> line =
					List.of(task.getIdentifier().toString(), task.getName(),
						task.getStatus().toString(), task.getEstimate().toString(),
						iterationForList(tasks.getIteration(task.getIdentifier())),
						Optional.ofNullable(tasks.getAssignedDate(task.getIdentifier()))
							.map(d -> d.format(DateTimeFormatter.ISO_DATE))
							.orElse("<none>"));
				for (int i = 0; i < Integer.min(fieldLengths.length, line.size()); i++) {
					int fieldLength = line.get(i).length();
					if (fieldLengths[i] < fieldLength) {
						fieldLengths[i] = fieldLength;
					}
				}
				output.add(line);
			}
		}
		if (output.isEmpty()) {
			return "";
		}
		String format = IntStream.of(fieldLengths)
							.mapToObj("%%-%ds"::formatted)
							.collect(Collectors.joining(" | "));
		try (StringWriter retval = new StringWriter();
			 PrintWriter out = new PrintWriter(retval)) {
			if (!quiet) {
				out.println(
					format.formatted("ID", "Name", "Status", "Estimate", "Iteration", "Scheduled"));
			}
			for (List<String> line : output) {
				out.println(format.formatted(line.toArray()));
			}
			return retval.toString();
		}
	}
}
