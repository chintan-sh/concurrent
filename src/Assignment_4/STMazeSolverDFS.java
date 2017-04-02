package Assignment_4;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * An efficient single-threaded depth-first solver.
 */
public class STMazeSolverDFS extends SkippingMazeSolver
{
    /*
    * Observations wrt Performance for single thread DFS :
    *
    * 1) 6x6 = Similar for all 3, very lil or no diff
    * 2) 200x100 = BFS is faster (Notice breadth is lesser than height - which means faster traversal for BFS)
    * 3) 200x200 = DFS is faster (Breadth/Height is same)
    * 4) 200x200u = BFS is faster (Solution wasn't found)
    * 5) 500x500 = DFS is faster
    * 6) 1000x1000 = DFS is way faster
    * 7) 4000x400u =  BFS is faster (Solution wasn't found, Recursive fails)
    * 8) 5000x5000 = DFS slam dunks BFS
    * */

    public STMazeSolverDFS(Maze maze)
    {
        super(maze);
    }

    /**
     * Performs a depth-first search for the exit. The solver operates by
     * maintaining a stack of choices. During each iteration, the choice at the
     * top of the stack is examined. If choice.isEmpty() is true, then we have
     * reached a dead-end and must backtrack by popping the stack. If the choice
     * is not empty, then we proceed down the first path in the list of options.
     * If the exit is encountered, then SolutionFound is thrown and we generate
     * the solution path, which we return. At any given point in the execution,
     * the list of first choices yields the current path. That is, if the choice
     * stack is:
     * 
     * <pre>
     * [[E W S] [E W] [S N] [N]]
     * </pre>
     * 
     * Then the current path is given by the list:
     * 
     * <pre>
     * [E E S N]
     * </pre>
     */
    public List<Direction> solve()
    {
        LinkedList<Choice> choiceStack = new LinkedList<Choice>();
        Choice ch;

        try
        {
            choiceStack.push(firstChoice(maze.getStart()));
            while (!choiceStack.isEmpty())
            {
                ch = choiceStack.peek();
                if (ch.isDeadend())
                {
                    // backtrack.
                    choiceStack.pop();
                    if (!choiceStack.isEmpty()) choiceStack.peek().choices.pop();
                    continue;
                }
                choiceStack.push(follow(ch.at, ch.choices.peek()));
            }
            // No solution found.
            return null;
        }
        catch (SolutionFound e)
        {
            Iterator<Choice> iter = choiceStack.iterator();
            LinkedList<Direction> solutionPath = new LinkedList<Direction>();
            while (iter.hasNext())
            {
            	ch = iter.next();
                solutionPath.push(ch.choices.peek());
            }

            if (maze.display != null) maze.display.updateDisplay();
            return pathToFullPath(solutionPath);
        }
    }
}
