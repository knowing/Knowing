/*                                                               *\
 ** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|  **
 ** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---, **
 ** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|  **
 **                                                              **
 ** Knowing Framework                                            **
 ** Apache License - http://www.apache.org/licenses/             **
 ** LMU Munich - Database Systems Group                          **
 ** http://www.dbs.ifi.lmu.de/                                   **
\*                                                               */
package de.lmu.ifi.dbs.knowing.debug.presenter;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import de.lmu.ifi.dbs.knowing.core.events.Status;
import de.lmu.ifi.dbs.knowing.core.model.INode;
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory;
import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.TypedActor;

/**
 *
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 2011-04-22
 *
 */
public class DebugUIFactory implements UIFactory<Path> {

	public static final String PROGRESS_FILENAME = ".progress";
	
	private ActorContext supervisorContext;
	private final Path executionPath;
	private final Path progressPath;
	

	public DebugUIFactory(Path executionPath) {
		this.executionPath = executionPath;
		this.progressPath = executionPath.resolve(PROGRESS_FILENAME); 
		try {
			Files.deleteIfExists(progressPath);
			Files.createFile(progressPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public DebugUIFactory(String executionPath) {
		this(Paths.get(executionPath));
	}

	@Override
	public Path createContainer(INode node) {
		String nodeId = node.getId().getContent();
		Path targetFile = executionPath.resolve(nodeId + ".arff");
		
		try {
			Files.createDirectories(executionPath);
			Files.deleteIfExists(targetFile);
			Files.createFile(targetFile);
		} catch (IOException e) {
			e.printStackTrace();
			//TODO handle exception - write log or something
		}
		
		return targetFile;
	}

	@Override
	public void update(ActorRef actor , Status status) {
		//TODO open a new writer on every event? This is insane... NO.. THIS IS JAVA
		try(Writer w = Files.newBufferedWriter(progressPath, Charset.defaultCharset(), StandardOpenOption.APPEND);
				ProgressWriter log = new ProgressWriter(w) ) {
			log.write(actor, status);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void setSupervisorContext(ActorContext supervisorContext) {
		this.supervisorContext = supervisorContext;
	}

	@Override
	public String getId() {
		return getClass().getName();
	}

}