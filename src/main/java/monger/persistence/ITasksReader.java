package monger.persistence;

import monger.model.TaskDatabase;

import java.nio.file.Path;

/**
 * An interface for reading the task database from wherever it has been stored (an XML or JSON file or a
 * single-file database)
 */
public interface ITasksReader {
	TaskDatabase readTasks(Path file) throws PersistenceException;
	TaskDatabase readTasksFromString(String xml) throws PersistenceException;
}
