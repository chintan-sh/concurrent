package Assignment_6.actor.messages;

import java.io.File;

/**
 * Messages that are passed around the actors are usually immutable classes.
 * Think how you go about creating immutable classes:) Make them all static
 * classes inside the Messages class.
 * 
 * This class should have all the immutable messages that you need to pass
 * around actors. You are free to add more classes(Messages) that you think is
 * necessary
 * 
 * @author Chintan
 *
 */
public class Messages {
    public static final String START = new String("StartProcessingFolder");

    // list of getters and setters
    public static class FileDir{
        final private String directory;
        public FileDir(String directory){
            this.directory = directory;
        }
        public String getDir(){
            return directory;
        }
    }

    public static class FileCounter {
        final private File fileName;
        final private int counter;
        public FileCounter(File fileName, int counter) {this.fileName = fileName;this.counter = counter;}
        public String getFileName() {
            return fileName.getName();
        }
        public int getWordCount() {
            return counter;
        }
    }

    public static class LineReader {
        final private String fileName;
        public LineReader(String fileName) { this.fileName = fileName;}
        public String getFileName()
        {
            return fileName;
        }
    }

    public static class LineCounter {
        final private int lCount;
        public LineCounter(int lCount)
        {
            this.lCount = lCount;
        }
        public int getSingleLineCount()
        {
            return lCount;
        }
    }

    public static class WordCounter {
        final private String oneLine;
        public WordCounter(String line) {this.oneLine = line;}
        public String getOneLine()
        {
            return oneLine;
        }
    }

}