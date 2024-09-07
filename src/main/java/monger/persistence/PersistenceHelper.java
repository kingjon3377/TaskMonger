package monger.persistence;

import java.nio.file.Path;
import monger.model.TaskDatabase;

/**
 * Uses the correct implementation to (de)serialize based on filename.
 *
 * @author Jonathan Lovelace
 */
public final class PersistenceHelper {
	private PersistenceHelper() {
		// Don't instantiate.
	}
	public static TaskDatabase readFromFile(Path file) throws PersistenceException {
		if (file.endsWith(".xml")) {
			return new XmlReader().readTasks(file);
		} else {
			throw new PersistenceException("Unknown file type");
		}
	}

	public static void writeToFile(Path file, TaskDatabase tasks)
			throws PersistenceException {
		if (file.endsWith(".xml")) {
			new XmlWriter().writeTasks(file, tasks);
		} else {
			throw new PersistenceException("Unknown file type");
		}
	}
}
