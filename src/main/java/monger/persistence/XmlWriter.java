package monger.persistence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import monger.model.Iteration;
import monger.model.Task;
import monger.model.TaskDatabase;
import monger.model.TaskIdentifier;
import org.jetbrains.annotations.Nullable;

/**
 * XML serialization.
 *
 * @author Jonathan Lovelace
 */
public class XmlWriter implements ITasksWriter {
	public static final String NAMESPACE = "https://github.com/kingjon3377/TaskMonger";

	private record CloseableXMLStreamWriter(XMLStreamWriter wrapped)
		implements XMLStreamWriter, AutoCloseable {

		@Override
			public void writeStartElement(final String localName) throws XMLStreamException {
				wrapped.writeStartElement(localName);
			}

			@Override
			public void writeStartElement(final String namespaceURI, final String localName)
				throws XMLStreamException {
				wrapped.writeStartElement(namespaceURI, localName);
			}

			@Override
			public void writeStartElement(final String prefix, final String localName,
										  final String namespaceURI)
				throws XMLStreamException {
				wrapped.writeStartElement(prefix, localName, namespaceURI);
			}

			@Override
			public void writeEmptyElement(final String namespaceURI, final String localName)
				throws XMLStreamException {
				wrapped.writeEmptyElement(namespaceURI, localName);
			}

			@Override
			public void writeEmptyElement(final String prefix, final String localName,
										  final String namespaceURI)
				throws XMLStreamException {
				wrapped.writeEmptyElement(prefix, localName, namespaceURI);
			}

			@Override
			public void writeEmptyElement(final String localName) throws XMLStreamException {
				wrapped.writeEmptyElement(localName);
			}

			@Override
			public void writeEndElement() throws XMLStreamException {
				wrapped.writeEndElement();
			}

			@Override
			public void writeEndDocument() throws XMLStreamException {
				wrapped.writeEndDocument();
			}

			@Override
			public void close() throws XMLStreamException {
				wrapped.flush();
				wrapped.close();
			}

			@Override
			public void flush() throws XMLStreamException {
				wrapped.flush();
			}

			@Override
			public void writeAttribute(final String localName, final String value)
				throws XMLStreamException {
				wrapped.writeAttribute(localName, value);
			}

			@Override
			public void writeAttribute(final String prefix, final String namespaceURI,
									   final String localName,
									   final String value) throws XMLStreamException {
				wrapped.writeAttribute(prefix, namespaceURI, localName, value);
			}

			@Override
			public void writeAttribute(final String namespaceURI, final String localName,
									   final String value) throws XMLStreamException {
				wrapped.writeAttribute(namespaceURI, localName, value);
			}

			@Override
			public void writeNamespace(final String prefix, final String namespaceURI)
				throws XMLStreamException {
				wrapped.writeNamespace(prefix, namespaceURI);
			}

			@Override
			public void writeDefaultNamespace(final String namespaceURI)
				throws XMLStreamException {
				wrapped.writeDefaultNamespace(namespaceURI);
			}

			@Override
			public void writeComment(final String data) throws XMLStreamException {
				wrapped.writeComment(data);
			}

			@Override
			public void writeProcessingInstruction(final String target)
				throws XMLStreamException {
				wrapped.writeProcessingInstruction(target);
			}

			@Override
			public void writeProcessingInstruction(final String target, final String data)
				throws XMLStreamException {
				wrapped.writeProcessingInstruction(target, data);
			}

			@Override
			public void writeCData(final String data) throws XMLStreamException {
				wrapped.writeCData(data);
			}

			@Override
			public void writeDTD(final String dtd) throws XMLStreamException {
				wrapped.writeDTD(dtd);
			}

			@Override
			public void writeEntityRef(final String name) throws XMLStreamException {
				wrapped.writeEntityRef(name);
			}

			@Override
			public void writeStartDocument() throws XMLStreamException {
				wrapped.writeStartDocument();
			}

			@Override
			public void writeStartDocument(final String version) throws XMLStreamException {
				wrapped.writeStartDocument(version);
			}

			@Override
			public void writeStartDocument(final String encoding, final String version)
				throws XMLStreamException {
				wrapped.writeStartDocument(encoding, version);
			}

			@Override
			public void writeCharacters(final String text) throws XMLStreamException {
				wrapped.writeCharacters(text);
			}

			@Override
			public void writeCharacters(final char[] text, final int start, final int len)
				throws XMLStreamException {
				wrapped.writeCharacters(text, start, len);
			}

			@Override
			public String getPrefix(final String uri) throws XMLStreamException {
				return wrapped.getPrefix(uri);
			}

			@Override
			public void setPrefix(final String prefix, final String uri)
				throws XMLStreamException {
				wrapped.setPrefix(prefix, uri);
			}

			@Override
			public void setDefaultNamespace(final String uri) throws XMLStreamException {
				wrapped.setDefaultNamespace(uri);
			}

			@Override
			public void setNamespaceContext(final NamespaceContext context)
				throws XMLStreamException {
				wrapped.setNamespaceContext(context);
			}

			@Override
			public NamespaceContext getNamespaceContext() {
				return wrapped.getNamespaceContext();
			}

			@Override
			public Object getProperty(final String name) throws IllegalArgumentException {
				return wrapped.getProperty(name);
			}
		}

	@Override
	public void writeTasks(final Path file, final TaskDatabase tasks) throws PersistenceException {
		final XMLOutputFactory xof = XMLOutputFactory.newInstance();
		try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8);
			 	CloseableXMLStreamWriter xsw = new CloseableXMLStreamWriter(xof.createXMLStreamWriter(writer))) {
			xsw.setDefaultNamespace(NAMESPACE);
			writeTasksImpl(xsw, tasks);
		} catch (final IOException | XMLStreamException except) {
			throw new PersistenceException("Failed to serialize to XML", except);
		}
	}
	@Override
	public String writeTasksToString(TaskDatabase tasks) throws PersistenceException {
		final XMLOutputFactory xof = XMLOutputFactory.newInstance();
		try (StringWriter buffer = new StringWriter();
			 	BufferedWriter writer = new BufferedWriter(buffer);
			 	CloseableXMLStreamWriter xsw = new CloseableXMLStreamWriter(
					 xof.createXMLStreamWriter(writer))) {
			xsw.setDefaultNamespace(NAMESPACE);
			writeTasksImpl(xsw, tasks);
			xsw.flush();
			return buffer.toString();
		} catch (IOException | XMLStreamException except) {
			throw new PersistenceException("Failed to serialize to XML", except);
		}
	}

	private static void writeTag(final XMLStreamWriter writer, final String tag)
			throws XMLStreamException {
		writer.writeStartElement(NAMESPACE, tag);
	}

	private static void writeAttribute(final XMLStreamWriter writer, final String name,
									   final @Nullable String value)
			throws XMLStreamException {
		if (Objects.nonNull(value)) {
			writer.writeAttribute(NAMESPACE, name, value);
		}
	}

	private static void writeDateAttribute(final XMLStreamWriter writer,
										   final String name,
										   final @Nullable LocalDate value)
			throws XMLStreamException {
		if (Objects.nonNull(value)) {
			writer.writeAttribute(NAMESPACE, name,
				value.format(DateTimeFormatter.ISO_DATE));
		}
	}
	private static void writeTagWithContents(final XMLStreamWriter writer,
											 final String tag, final @Nullable String data)
			throws XMLStreamException{
		if (Objects.nonNull(data)) {
			writeTag(writer, tag);
			writer.writeCharacters(data);
			writer.writeEndElement();
		}
	}

	private void writeTasksImpl(final XMLStreamWriter writer, final TaskDatabase taskDatabase)
			throws XMLStreamException {
		writer.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");
		writer.setPrefix("tm", NAMESPACE);
		writer.setDefaultNamespace(NAMESPACE);
		writeTag(writer, "monger");
		writer.writeDefaultNamespace(NAMESPACE);
		writer.writeNamespace("tm", NAMESPACE);
		writeAttribute(writer, "monger-version", "0");
		final Iterable<Iteration> iterations = taskDatabase.getIterations();
		// taskIterations value type is String to simplify null-checks later
		final Map<TaskIdentifier, String> taskIterations = new HashMap<>();
		if (iterations.iterator().hasNext()) {
			writeTag(writer, "iterations");
			for (Iteration iteration : iterations) {
				writeTag(writer, "iteration");
				writeAttribute(writer, "id", iteration.getId().toString());
				writeAttribute(writer, "name", iteration.getName());
				writeDateAttribute(writer, "start", iteration.getStartDate());
				writeDateAttribute(writer, "end", iteration.getEndDate());
				writer.writeEndElement();
				taskDatabase.getTasksInIteration(iteration)
					.forEach(t -> taskIterations.put(t.getIdentifier(),
						iteration.getId().toString()));
			}
			writer.writeEndElement();
		}
		final Iterable<Task> tasks = taskDatabase.getTasks();
		if (tasks.iterator().hasNext()) {
			writeTag(writer, "tasks");
			for (Task task : tasks) {
				writeTag(writer, "task");
				writeTag(writer, "task-identifier");
				final TaskIdentifier id = task.getIdentifier();
				writeAttribute(writer, "provider", id.provider());
				writeAttribute(writer, "identifier", id.identifier());
				writer.writeEndElement();
				writeTagWithContents(writer, "name", task.getName());
				writeTagWithContents(writer, "description", task.getDescription());
				writeTagWithContents(writer, "estimate", task.getEstimate().toString());
				writeTagWithContents(writer, "upstream", task.getUpstreamURL());
				writeTagWithContents(writer, "status", task.getStatus().toString());
				writeTagWithContents(writer, "iteration", taskIterations.get(task.getIdentifier()));
				writeTagWithContents(writer, "assigned-date",
					Optional.ofNullable(taskDatabase.getAssignedDate(task.getIdentifier()))
						.map(d -> d.format(DateTimeFormatter.ISO_DATE)).orElse(null));
				writer.writeEndElement();
			}
			writer.writeEndElement();
		}
		writer.writeEndElement();
		writer.writeEndDocument();
	}
}
