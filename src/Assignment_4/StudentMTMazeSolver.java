package Assignment_4;

import java.util.*;
import java.util.concurrent.*;


/**
 * This file needs to hold your solver to be tested. 
 * You can alter the class to extend any class that extends MazeSolver.
 * It must have a constructor that takes in a Maze.
 * It must have a solve() method that returns the datatype List<Direction>
 *   which will either be a reference to a list of steps to take or will
 *   be null if the maze cannot be solved.
 */
public class StudentMTMazeSolver extends SkippingMazeSolver
{

    public StudentMTMazeSolver(Maze maze) {
        // call the super fellow for initializing
        super(maze);
    }

    public List<Direction> solve() {
        try {

            // get first fellow
            Choice startChoice = firstChoice(maze.getStart());

            // init empty callable list
            List<Callable<List<Direction>>> callables = new ArrayList<>(startChoice.choices.size());

            // now, extract direction (ex : SOUTH, WEST) and loop each of em
            for(Direction oneDirection : startChoice.choices){
                // new LL for this direction
                LinkedList<Direction> newDirection = new LinkedList<Direction>();
                newDirection.add(oneDirection);

                // new choice with currdirection as sole element in direction list
                Choice newStartOne =  new Choice(startChoice.at, startChoice.from, newDirection);

                // init as callable
                Callable<List<Direction>> callable = new DFSCallable(newStartOne);

                // add to the list for future
                callables.add(callable);
            }

            // thread pool with no of threads =  num processor + 1
            ExecutorService thrPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+1);

            //Submit all the callables and obtain their Futures
//            for(Callable c : callables) {
//               Future future = thrPool.submit(c);
//            }

            List<Future<List<Direction>>> futures = thrPool.invokeAll(callables);
            thrPool.shutdown();

            //Iterate through the futures
            for ( Future<List<Direction>> future : futures){
                if(future.get() != null){
                    return future.get();
                }
            }

        } catch (SolutionFound solutionFound) {
            solutionFound.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Inner class callable
    class DFSCallable implements Callable<List<Direction>> {
        Choice startingChoice;

        DFSCallable(Choice startingChoice){
            this.startingChoice = startingChoice;
        }

        @Override
        public List<Direction> call() throws Exception {
            LinkedList<Choice> choiceStack = new LinkedList<Choice>();
            Choice ch;

            try {
                choiceStack.push(startingChoice);

                while (!choiceStack.isEmpty()){
                    ch = choiceStack.peek();

                    if (ch.isDeadend()) {
                        // backtrack.
                        choiceStack.pop();

                        if (!choiceStack.isEmpty()){
                            choiceStack.peek().choices.pop();
                        }
                        continue;
                    }
                    choiceStack.push(follow(ch.at, ch.choices.peek()));

                }
                return null;
            }catch (SkippingMazeSolver.SolutionFound e) {
                Iterator<Choice> iter = choiceStack.iterator();
                LinkedList<Direction> solutionPath = new LinkedList<Direction>();
                while (iter.hasNext()) {
                    ch = iter.next();
                    solutionPath.push(ch.choices.peek());
                }
                System.out.println(solutionPath.size() + "," + pathToFullPath(solutionPath).size());
                return pathToFullPath(solutionPath);
            }
        }

    }// end of inner class


}


