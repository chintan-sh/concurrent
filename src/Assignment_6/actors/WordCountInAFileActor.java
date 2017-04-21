package Assignment_6.actors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import Assignment_6.actor.messages.Messages;

/**
 * this actor reads the file line by line and sends them to
 * {@code WordsInLineActor} to count the words in line. Upon geting the results,
 * It sends the result to it's parent actor {@code WordCount}
 * 
 * @author Chintan
 *
 */

public class WordCountInAFileActor extends UntypedActor {
    ActorSystem system;
	ActorRef sender ;
	File file;
	int i = 0;
	int lineCount = 0;
    int myID;
	int count = 0;

	public WordCountInAFileActor(ActorSystem system, int id) {
		this.system = system;
		this.myID  = id;
	}

	@Override
	public void onReceive(Object msg) throws Throwable {
		if (msg instanceof Messages.LineCounter){
			Messages.LineCounter payload = (Messages.LineCounter)msg;
			count+=payload.getSingleLineCount();
			lineCount++;
			if(lineCount == i) { sender.tell(new Messages.FileCounter(file, count),getSelf()); }
		}else {
			Messages.LineReader readfile = (Messages.LineReader) msg;
			sender = getSender();
			//System.out.println("File found with name => "  + readfile.getFileName());
			file = new File(readfile.getFileName());
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
//				if(readfile.getFileName().trim().equals("/home/chintan/IdeaProjects/Multithreading/multithreading/src/Assignment_6/input_data/big.txt")){
//					//System.out.println(line);
//				}
				Props myProps = Props.create(WordsInLineActor.class);
				ActorRef mpNode1 = system.actorOf(myProps, String.valueOf(myID) + i);
				mpNode1.tell(new Messages.WordCounter(line), getSelf());
				i++;
			}

			fileReader.close();
		}
	}

}
