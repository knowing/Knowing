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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import de.lmu.ifi.dbs.knowing.core.events.Status;
import de.lmu.ifi.dbs.knowing.core.model.INode;
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory;
import akka.actor.ActorRef;
import akka.actor.TypedActor;

/**
 *
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 2011-04-22
 *
 */
public class DebugUIFactory extends TypedActor implements UIFactory<Path> {

	private ActorRef supervisor;
	private final Path executionPath;
	

	public DebugUIFactory(Path executionPath) {
		this.executionPath = executionPath;
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
		//TODO write to log
		System.out.println("Update " + status);
	}

	@Override
	public void setSupervisor(ActorRef supervisor) {
		this.supervisor = supervisor;
	}

	@Override
	public String getId() {
		return getClass().getName();
	}

}