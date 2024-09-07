package monger.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import monger.model.Iteration;
import monger.model.Task;
import monger.model.TaskDatabase;
import monger.model.TaskIdentifier;
import monger.model.TaskStatus;
import monger.model.TimeEstimate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * XML deserialization
 *
 * @author Jonathan Lovelace
 */
public class XmlReader implements ITasksReader {
	@Override
	public TaskDatabase readTasks(final Path file) throws PersistenceException {
		try (final BufferedReader istream = Files.newBufferedReader(file, StandardCharsets.UTF_8);
			 	final TypesafeXMLEventReader reader = new TypesafeXMLEventReader(istream)) {
			return readTasksImpl(new IteratorWrapper<>(reader));
		} catch (IOException | XMLStreamException except) {
			throw new PersistenceException("Failed to deserialize from XML", except);
		}
	}

	@Override
	public TaskDatabase readTasksFromString(final String xml)
			throws PersistenceException {
		try (final BufferedReader istream = new BufferedReader(new StringReader(xml));
				final TypesafeXMLEventReader reader = new TypesafeXMLEventReader(istream)) {
			return readTasksImpl(new IteratorWrapper<>(reader));
		} catch (IOException | XMLStreamException except) {
			throw new PersistenceException("Failed to deserialize from XML", except);
		} catch (RuntimeException except) {
			System.err.println(xml);
			throw new PersistenceException("Failed to deserialize",
				Optional.ofNullable(except.getCause()).orElse(except));
		}
	}

	private TaskDatabase readTasksImpl(final Iterable<XMLEvent> input)
			throws PersistenceException {
		StartElement root = null;
		for (XMLEvent event : input) {
			if (event instanceof StartElement element && isInNamespace(element)) {
				expectTag(element, "monger");
				expectAttribute(element, "monger-version", "0");
				root = element;
				break;
			}
		}
		if (Objects.isNull(root)) {
			throw new PersistenceException("Root tag not found");
		}
		final TaskDatabase retval = new TaskDatabase();
		for (XMLEvent event : input) {
			if (event instanceof StartElement element &&
					isInNamespace(element)
					&& "iterations".equals(element.getName().getLocalPart())) {
				readIterations(element, input).forEach(retval::addIteration);
			} else if (event instanceof StartElement element && isInNamespace(element)
						   && "tasks".equals(element.getName().getLocalPart())) {
				readIndivTasks(element, input, retval);
			} else if (event instanceof StartElement element && isInNamespace(element)) {
				throw new PersistenceException(
					"Unexpected tag '%s'; expected 'iterations' or 'tasks'"
						.formatted(element.getName().getLocalPart()));
			} else if (event instanceof EndElement element &&
						   Objects.equals(element.getName(), root.getName())) {
				return retval;
			}
		}
		throw new PersistenceException("Root tag not properly closed");
	}

	private void readIndivTasks(StartElement element, Iterable<XMLEvent> input, TaskDatabase database) throws PersistenceException {
		for (XMLEvent event : input) {
			if (event instanceof StartElement child && isInNamespace(child)) {
				expectTag(child, "task");
				readSingleTask(child, input, database);
			} else if (event instanceof EndElement child && Objects.equals(element.getName(), child.getName())) {
				return;
			}
		}
		throw new PersistenceException("<tasks> not properly closed");
	}

	private void readSingleTask(StartElement element, Iterable<XMLEvent> input, TaskDatabase database)
		throws PersistenceException {
		boolean properlyClosed = false;
		TaskIdentifier id = null;
		String name = null;
		String description = null;
		String estimate = null;
		String upstream = null;
		String status = null;
		String iterationStr = null;
		String assignedDateStr = null;
		for (XMLEvent event : input) {
			if (event instanceof StartElement child && isInNamespace(child)) {
				switch (child.getName().getLocalPart()) {
					case "task-identifier" -> {
						if (Objects.nonNull(id)) {
							throw new PersistenceException(
								"Multiple <task-identifier> in a single <task>");
						} else {
							id = new TaskIdentifier(
								getRequiredAttribute(child, "provider"),
								getRequiredAttribute(child, "identifier"));
						}
					}
					case "name" -> {
						if (Objects.nonNull(name)) {
							throw new PersistenceException(
								"Multiple <name> in a single <task>");
						} else {
							name = readTagContents(child, input);
						}
					}
					case "description" -> {
						if (Objects.nonNull(description)) {
							throw new PersistenceException(
								"Multiple <description> in a single <task>");
						} else {
							description = readTagContents(child, input);
						}
					}
					case "estimate" -> {
						if (Objects.nonNull(estimate)) {
							throw new PersistenceException(
								"Multiple <estimate> in a single <task>");
						} else {
							estimate = readTagContents(child, input);
						}
					}
					case "upstream" -> {
						if (Objects.nonNull(upstream)) {
							throw new PersistenceException(
								"Multiple <upstream> in a single <task>");
						} else {
							upstream = readTagContents(child, input);
						}
					}
					case "status" -> {
						if (Objects.nonNull(status)) {
							throw new PersistenceException(
								"Multiple <status> in a single <task>");
						} else {
							status = readTagContents(child, input);
						}
					}
					case "iteration" -> {
						if (Objects.nonNull(iterationStr)) {
							throw new PersistenceException(
								"Multiple <iteration> in a single <task>");
						} else {
							iterationStr = readTagContents(child, input);
						}
					}
					case "assigned-date" -> {
						if (Objects.nonNull(assignedDateStr)) {
							throw new PersistenceException(
								"Multiple <assigned-date> in a single <task>");
						} else {
							assignedDateStr = readTagContents(child, input);
						}
					}
				}
			} else if (event instanceof EndElement child && Objects.equals(
					element.getName(), child.getName())) {
				properlyClosed = true;
				break;
			}
		}
		if (!properlyClosed) {
			throw new PersistenceException("<task> not properly closed");
		}
		if (Objects.isNull(name) || name.isBlank()) {
			throw new PersistenceException("Task with missing or empty name");
		}
		if (Objects.isNull(id)) {
			throw new PersistenceException("<task> without <task-identifier>");
		}
		final Task task = new Task(id, name);
		Optional.ofNullable(description).ifPresent(task::setDescription);
		Optional.ofNullable(upstream).ifPresent(task::setUpstreamURL);
		UUID iteration;
		LocalDate assignedDate;
		try {
			Optional.ofNullable(estimate).map(TimeEstimate::valueOf)
				.ifPresent(task::setEstimate);
			Optional.ofNullable(status).map(TaskStatus::valueOf)
				.ifPresent(task::setStatus);
			iteration = Optional.ofNullable(iterationStr)
							.map(UUID::fromString).orElse(null);
			assignedDate = Optional.ofNullable(assignedDateStr).map(LocalDate::parse)
							   .orElse(null);
		} catch (IllegalArgumentException except) {
			throw new PersistenceException("Task field failed to parse", except);
		}
		database.addTask(task);
		if (Objects.nonNull(iteration)) {
			database.assignToIteration(id, iteration);
		}
		if (Objects.nonNull(assignedDate)) {
			database.assignToDate(id, assignedDate);
		}
	}

	private String readTagContents(StartElement element, Iterable<XMLEvent> input)
			throws PersistenceException {
		final StringBuilder sb = new StringBuilder();
		for (XMLEvent event : input) {
			switch (event) {
				case Characters child -> sb.append(child.getData());
				case EndElement child
					when Objects.equals(element.getName(), child.getName()) -> { return sb.toString(); }
				default -> {}
			}
		}
		throw new PersistenceException("Unclosed tag <%s>".formatted(element.getName().getLocalPart()));
	}

	private Iterable<Iteration> readIterations(StartElement element, Iterable<XMLEvent> input)
			throws PersistenceException {
		final List<Iteration> retval = new ArrayList<>();
		for (XMLEvent event : input) {
			if (event instanceof StartElement child && isInNamespace(child)) {
				expectTag(child, "iteration");
				retval.add(readSingleIteration(child));
			} else if (event instanceof EndElement child && Objects.equals(element.getName(), child.getName())) {
				return retval;
			}
		}
		throw new PersistenceException("Didn't get </iterations>");
	}

	private static String getRequiredAttribute(StartElement element, String attribute)
			throws PersistenceException {
		final Optional<String> value = Optional.ofNullable(element.getAttributeByName(
			new QName(XmlWriter.NAMESPACE, attribute))).map(Attribute::getValue).filter(s -> !s.isBlank());
		if (value.isPresent()) {
			return value.get();
		} else {
			throw new PersistenceException(
				"In <%s>, expected attribute '%s'"
					.formatted(element.getName().getLocalPart(), attribute));
		}
	}

	private static @Nullable String getOptionalAttribute(StartElement element, String attribute) {
		return Optional.ofNullable(element.getAttributeByName(
			new QName(XmlWriter.NAMESPACE, attribute))).map(Attribute::getValue)
				   .filter(s -> !s.isBlank()).orElse(null);
	}

	private Iteration readSingleIteration(StartElement element)
			throws PersistenceException {
		final String idStr = getRequiredAttribute(element, "id");
		UUID id;
		try {
			id = UUID.fromString(idStr);
		} catch (IllegalArgumentException except) {
			throw new PersistenceException("Invalid id attribute in iteration", except);
		}
		String name = getOptionalAttribute(element, "name");
		String startDateStr = getOptionalAttribute(element, "start");
		String endDateStr = getOptionalAttribute(element, "end");
		try {
			LocalDate startDate =
				Optional.ofNullable(startDateStr).map(LocalDate::parse).orElse(null);
			LocalDate endDate =
				Optional.ofNullable(endDateStr).map(LocalDate::parse).orElse(null);
			return new Iteration(id, name, startDate, endDate);
		} catch (DateTimeParseException except) {
			throw new PersistenceException("Unparseable date attribute in iteration", except);
		}
	}

	private static void expectTag(final @NotNull XMLEvent event, String tag)
		throws PersistenceException {
		switch (event) {
		case StartElement element when isInNamespace(element) &&
				tag.equals(element.getName().getLocalPart()) -> {
		}
		case StartElement element when isInNamespace(element) ->
			throw new PersistenceException("Expected tag '%s', got '%s'".formatted(tag,
				element.getName().getLocalPart()));
		case StartElement ignored -> throw new PersistenceException(
			"Got unexpected tag outside our namespace");
		default -> throw new PersistenceException(
			"Expected tag '%s', got non-tag".formatted(tag));
		}
	}

	private static boolean isInNamespace(final StartElement element) {
		return XmlWriter.NAMESPACE.equals(element.getName().getNamespaceURI());
	}

	@SuppressWarnings("SameParameterValue")
	private static void expectAttribute(final @NotNull StartElement element,
										String attribute, String value)
			throws PersistenceException {
		final String actual =
			element.getAttributeByName(new QName(XmlWriter.NAMESPACE, attribute))
				.getValue();
		if (!value.equals(actual)) {
			throw new PersistenceException(
				"Expected value '%s' for attribute %s, got '%s'".formatted(value,
					attribute, actual));
		}
	}
}
