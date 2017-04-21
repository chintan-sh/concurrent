package Assignment_6.actors;

import akka.actor.UntypedActor;
import Assignment_6.actor.messages.Messages;

/**
 * This actor counts number words in a single line
 * 
 * @author Chintan
 *
 */
public class WordsInLineActor extends UntypedActor {
	@Override
	public void onReceive(Object message) throws Throwable {
		int count = 0;
		Messages.WordCounter line1 = (Messages.WordCounter) message;
		String line = line1.getOneLine();
		String[] result = line.split(" ");
		for(String s : result) {
			if(!s.equals(" "))
				count++;
		}

		getSender().tell(new Messages.LineCounter(count),getSender());
	}
}
