package Assignment_6.actors;

import java.util.ArrayList;

import Assignment_6.actor.messages.Messages;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

/**
 * Main class for your wordcount actor system.
 * 
 * @author Chintan
 *
 */
public class WordCount {
	public static int finalCount = 0;

	public static void main(String[] args) throws Exception {
		/*
		 * Create the WordCountActor and send it the StartProcessingFolder
		 * message. Once you get back the response, use it to print the result.
		 * Remember, there is only one actor directly under the ActorSystem.
		 * Also, do not forget to shutdown the actorsystem
		 */
		ActorSystem system = ActorSystem.create("wordcounter");

		// set home dir
		String localDir = "/home/chintan/IdeaProjects/Multithreading/multithreading/src/Assignment_6/input_data/";

		// load actor class
		Props startProp = Props.create(WordCountActor.class, system);

		// send that chap a start message now
		ActorRef mainFellowNode = system.actorOf(startProp, Messages.START);

		// timeout for future
		Timeout timeout = new Timeout(Duration.create(1500, "seconds"));
		Future<Object> futureTask = Patterns.ask(mainFellowNode, new Messages.FileDir(localDir), timeout);

		//System.out.println("Timeout scenes " + timeout.duration());
		// Await for final outcome
		ArrayList<Messages.FileCounter> results = (ArrayList<Messages.FileCounter>) Await.result(futureTask, timeout.duration());

		//System.out.println("Total returned vount " + results.size());

		// Check what happened
		for(Messages.FileCounter oneResult: results) {
			System.out.println("Count for " + oneResult.getFileName() + " found as :- "  + oneResult.getWordCount());
			finalCount+= oneResult.getWordCount();
		}

		// print final count man
		System.out.println("Sum count came as :- " + finalCount);

		// shutdown time
		system.terminate();
	}

}
