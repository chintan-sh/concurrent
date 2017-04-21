package Assignment_6.actors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import Assignment_6.actor.messages.Messages;


/**
 * This is the main actor and the only actor that is created directly under the
 * {@code ActorSystem} This actor creates more child actors
 * {@code WordCountInAFileActor} depending upon the number of files in the given
 * directory structure
 * 
 * @author Chintan
 *
 */
public class WordCountActor extends UntypedActor {
	ActorSystem system;
	ActorRef sender ;

	List<Messages.FileCounter> result = new ArrayList<>();
	int directorySize = 0;
	int sizeCounter = 0;
	int randomStartID = 1;

	public WordCountActor(ActorSystem system) {
		this.system = system;
	}

	@Override
	public void onReceive(Object msg) throws Throwable {
		if(msg instanceof Messages.FileDir) {
			// sender reference
			sender = getSender();

			// what is message going to be?
			Messages.FileDir payload = (Messages.FileDir) msg;

			// load files form path given
			File file = new File(payload.getDir());

			// send for reading and forwarding
			readFileAndForward(file);

		}else if(msg instanceof Messages.FileCounter) {
			 Messages.FileCounter totalWordCount = (Messages.FileCounter) msg;
			 result.add(totalWordCount);
			 //System.out.println("Current size" +  sizeCounter);
			 sizeCounter++;

			// check if size is similar
			if(sizeCounter == directorySize) {
				 sender.tell(result, getSelf()); 	
			 }

		}else {
			System.out.println("Guise, you have sent something I wasnt expecting. Please check your input, man!");
		}
	}

	public void readFileAndForward(File file){
		File[] listing= file.listFiles();

		// find directory listing size
		directorySize = listing.length;
		//System.out.println("LIsting length found " + listing.length);

		// loop that many times
		for(int i=0 ; i < listing.length; i++){
			if(listing[i].getName().endsWith(".txt")){
				// set up reference
				Props FileActorProps = Props.create(WordCountInAFileActor.class, system, randomStartID);
				ActorRef mpNode = system.actorOf(FileActorProps, "file"+i);
				mpNode.tell(new Messages.LineReader( listing[i].toString()), getSelf() );
				randomStartID++;
			}
		}
	}

}
