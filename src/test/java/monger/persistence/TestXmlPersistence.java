package monger.persistence;

import java.time.LocalDate;
import java.util.UUID;
import monger.model.Iteration;
import monger.model.Task;
import monger.model.TaskDatabase;
import monger.model.TaskIdentifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Test XML persistence
 *
 * @author Jonathan Lovelace
 */
public class TestXmlPersistence {
	private final XmlWriter writer = new XmlWriter();
	private final XmlReader reader = new XmlReader();
	@Test
	public void testPersistenceTrivial() throws PersistenceException {
		final TaskDatabase original = new TaskDatabase();
		final String xml = writer.writeTasksToString(original);
		final TaskDatabase copy = reader.readTasksFromString(xml);
		assertEquals(original, copy, "Empty database serializes");
	}
	@Test
	public void testPersistenceNegative() throws PersistenceException {
		final TaskDatabase original = new TaskDatabase();
		final String xml = writer.writeTasksToString(original);
		original.addTask(new Task(new TaskIdentifier("testing", "id"), "name for testing"));
		final TaskDatabase copy = reader.readTasksFromString(xml);
		assertNotEquals(original, copy,
			"Deserialized database differs when original modified after serialization");
	}

	@Test
	public void testPersistenceComplex() throws PersistenceException {
		final TaskDatabase original = new TaskDatabase();
		final Task task = new Task(new TaskIdentifier("testing", "id"), "name for testing");
		final Iteration iteration = new Iteration(UUID.randomUUID(), "test iteration",
			LocalDate.now(), LocalDate.now().plusDays(14));
		original.addTask(task);
		original.addIteration(iteration);
		original.assignToIteration(task.getIdentifier(), iteration);
		original.assignToDate(task.getIdentifier(), LocalDate.now().plusDays(5));
		final String xml = writer.writeTasksToString(original);
		System.err.println(xml);
		final TaskDatabase copy = reader.readTasksFromString(xml);
		assertEquals(original, copy, "Slightly-complex database serializes");
	}
}
