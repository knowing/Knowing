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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import de.lmu.ifi.dbs.knowing.core.events.Status;
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory;
import de.lmu.ifi.dbs.knowing.core.model.INode;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 2011-04-22
 * 
 */
public class DebugUIFactory implements UIFactory<Path> {

    public static final String PROGRESS_FILENAME = ".progress";
    private static final Logger log = LoggerFactory.getLogger(DebugUIFactory.class);

    private ActorContext supervisorContext;
    private final Path executionPath;
    private final Path progressPath;

    public DebugUIFactory(Path executionPath) {
        log.debug("Created DebugUIFactory on path " + executionPath);
        this.executionPath = executionPath;
        this.progressPath = executionPath.resolve(PROGRESS_FILENAME);
        try {
            Files.deleteIfExists(progressPath);
            Files.createFile(progressPath);
        } catch (IOException e) {
            log.error("IOError creating DebugUIFactory", e);
        }

    }

    public DebugUIFactory(String executionPath) {
        this(Paths.get(executionPath));
    }

    @Override
    public Path createContainer(INode node) {
        String nodeId = node.getId().getContent();
        Path targetFile = executionPath.resolve(nodeId + ".arff");
        log.info("Created output file " + targetFile);
        try {
            Files.createDirectories(executionPath);
            Files.deleteIfExists(targetFile);
            Files.createFile(targetFile);
        } catch (IOException e) {
            log.error("IOError creating container targetfile " + targetFile, e);
        }

        return targetFile;
    }

    @Override
    public void update(ActorRef actor, Status status) {
        // open a new writer on every event? This is insane... NO.. THIS IS JAVA
        try (Writer w = Files.newBufferedWriter(progressPath, Charset.defaultCharset(), StandardOpenOption.APPEND);
                ProgressWriter log = new ProgressWriter(w)) {
            log.write(actor, status);
        } catch (IOException e) {
            DebugUIFactory.log.error("IOError on appending to progress file", e);
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

    @Override
    public ActorSystem getSystem() {
        return TypedActor.context().system();
    }

}