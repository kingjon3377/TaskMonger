package monger.view.cli;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;
import monger.model.Iteration;
import monger.model.Task;
import monger.model.TaskDatabase;
import monger.model.TaskIdentifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test the pipe CLI.
 *
 * @author Jonathan Lovelace
 */
public class TestPipeCLI {
	@Test
	public void testListAllTasksQuiet() throws IOException {
		final TaskDatabase tasks = new TaskDatabase();
		final Task task = new Task(new TaskIdentifier("testing", "id"), "name for testing");
		final Iteration iteration =
			new Iteration(UUID.fromString("b8c6d3e3-d5a8-4d99-b17e-d5f0fd2fe8dd"),
				"test iteration", LocalDate.parse("2024-09-08"),
				LocalDate.parse("2024-09-22"));
		tasks.addTask(task);
		tasks.addIteration(iteration);
		tasks.assignToIteration(task.getIdentifier(), iteration);
		tasks.assignToDate(task.getIdentifier(), LocalDate.parse("2024-09-13"));
		final PipeCLI cli = new PipeCLI();
		final String output = cli.listTasks(true, tasks, Collections.emptyList());
		final String expected = "testing:id | name for testing | Unscheduled | Unestimated | test iteration | 2024-09-13\n";
		assertEquals(expected, output, "Quiet list-all should produce expected output");
	}
	@Test
	public void testListAllTasksVerbose() throws IOException {
		final TaskDatabase tasks = new TaskDatabase();
		final Task task = new Task(new TaskIdentifier("testing", "id"), "name for testing");
		final Iteration iteration =
			new Iteration(UUID.fromString("b8c6d3e3-d5a8-4d99-b17e-d5f0fd2fe8dd"),
				"test iteration", LocalDate.parse("2024-09-08"),
				LocalDate.parse("2024-09-22"));
		tasks.addTask(task);
		tasks.addIteration(iteration);
		tasks.assignToIteration(task.getIdentifier(), iteration);
		tasks.assignToDate(task.getIdentifier(), LocalDate.parse("2024-09-13"));
		final PipeCLI cli = new PipeCLI();
		final String output = cli.listTasks(false, tasks, Collections.emptyList());
		final String expected = """
			ID         | Name             | Status      | Estimate    | Iteration      | Scheduled\s
			testing:id | name for testing | Unscheduled | Unestimated | test iteration | 2024-09-13
			""";
		assertEquals(expected, output, "Verbose list-all should produce expected output");
	}
}
