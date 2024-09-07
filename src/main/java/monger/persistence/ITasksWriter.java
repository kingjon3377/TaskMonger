package monger.persistence;

import java.nio.file.Path;
import monger.model.TaskDatabase;

/**
 * An interface for writing the tasks database to wherever it should be stored (an XML or JSON file or a single-file
 * database).
 */
public interface ITasksWriter {
	void writeTasks(Path file, TaskDatabase tasks) throws PersistenceException;
	String writeTasksToString(TaskDatabase tasks) throws PersistenceException;
}
