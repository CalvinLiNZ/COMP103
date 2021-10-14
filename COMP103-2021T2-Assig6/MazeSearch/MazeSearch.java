// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2021T2, Assignment 6
 * Name:
 * Username:
 * ID:
 */

import ecs100.UI;
import java.awt.*;
import java.util.*;

/**
 * Search for a path to the goal in a maze.
 * The maze consists of a graph of MazeCells:
 *  Each cell has a collection of neighbouring cells.
 *  Each cell can be "visited" and it will remember that it has been visited
 *  A MazeCell is Iterable, so that you can iterate through its neighbour cells with
 *    for(MazeCell neighbour : cell){....
 *
 * The maze has a goal cell (shown in green, two thirds towards the bottom right corner)
 * The maze.getGoal() method returns the goal cell of the maze.
 * The user can click on a cell, and the program will search for a path
 * from that cell to the goal.
 * 
 * Every cell that is looked at during the search is coloured  yellow, and then,
 * if the cell turns out to be on a dead end, it is coloured red.
 */

public class MazeSearch {

    private Maze maze;
    private String search = "first";   // "first", "all", or "shortest"
    private int pathCount = 0;
    private boolean stopNow = false;


    /** CORE
     * Search for a path from a cell to the goal.
     * Return true if we got to the goal via this cell (and don't
     *  search for any more paths).
     * Return false if there is not a path via this cell.
     * 
     * If the cell is the goal, then we have found a path - return true.
     * If the cell is already visited, then abandon this path - return false.
     * Otherwise,
     *  Mark the cell as visited, and colour it yellow [and pause: UI.sleep(delay);]
     *  Recursively try exploring from the cell's neighbouring cells, returning true
     *   if a neighbour leads to the goal
     *  If no neighbour leads to a goal,
     *    colour the cell red (to signal failure)
     *    abandon the path - return false.
     */
    public boolean exploreFromCell(MazeCell cell) {
        if (cell == maze.getGoal()) {
            cell.draw(Color.blue);   // to indicate finding the goal
            return true;
        }
        /*# YOUR CODE HERE */
        else if(cell.isVisited()){ // if cell has already been visited then return false;
            return false;
        }
        else{ // otherwise visit cell and draw as yellow to indicate path.
            cell.visit();
            cell.draw(Color.yellow);
            UI.sleep(delay); // delay
            for(MazeCell cells : cell){ // recursion through every cell
                if(exploreFromCell(cells)){
                    return true;
                }
            }
            cell.draw(Color.RED); // draw as red if true was not returned
            return false;
        }

    }

    /** COMPLETION
     * Search for all paths from a cell,
     * If we reach the goal, then we have found a complete path,
     *  so pause for 1000 milliseconds 
     * Otherwise,
     *  visit the cell, and colour it yellow [and pause: UI.sleep(delay);]
     *  Recursively explore from the cell's neighbours, 
     *  unvisit the cell and colour it white.
     * 
     */
    public void exploreFromCellAll(MazeCell cell) {
        if (stopNow) { return;}    // exit if user clicked the stop now button
        /*# YOUR CODE HERE */
        if(cell == maze.getGoal()){ // if the cell is the end
            pathCount++;
            cell.draw(Color.blue); // draw as blue
            UI.printMessage("Path Count = "+pathCount); // print the path counts
            UI.sleep(1000);
            cell.draw(Color.green); // set back to green to start over.
        }
        else{
            cell.visit(); // otherwise visit the cell
            cell.draw(Color.YELLOW); // draw as yellow
            UI.sleep(delay);
            for(MazeCell current : cell){ // for each cell as long as the cell had not been visited then proceed with recursion
                if(!current.isVisited()){
                    exploreFromCellAll(current);
                }
            }
            cell.unvisit(); // make sure to unvisit so we can go through these cells in the next path
            cell.draw(Color.WHITE); // set back to white.
        }
    }


    /** CHALLENGE
     * Search for shortest path from a cell,
     * Use Breadth first search.
     */
    public void exploreFromCellShortest(MazeCell start) {
        /*# YOUR CODE HERE */
        ArrayDeque<MazeCell> que = new ArrayDeque<MazeCell>(); // create que
        HashMap<MazeCell, MazeCell> path = new HashMap<MazeCell, MazeCell>();
        que.offer(start); // input start into que
        start.draw(Color.yellow);
        MazeCell current = null; // create current maze cell
        while(!que.isEmpty()){ // as long as there is something in the que this will run
            current = que.poll(); // remove and put into current
            if(current == maze.getGoal()){ // if current is the goal then break.
                break;
            }
            current.visit(); // make sure it change current into visited cell so we do not come back to this cell

            if(current == start){ // if this is the first cell
                path.put(current, null); // insert this to the route

            }
            for(MazeCell neighbourCell : current){ // the same for each neighbour
                if(!neighbourCell.isVisited()){ // if the neighbourcell has not been visited then we continue otherwise it stops.
                    que.offer(neighbourCell); // add neighbour cell to que
                    if(!current.equals(start)){ // cant be the first cell
                        path.put(neighbourCell, current); // input next cell
                    }
                }
            }
        }
        while(current != null){ // for every cell
            current.draw(Color.yellow); // draw as yellow
            if(current == maze.getGoal()){
                current.draw(Color.blue);// draw as blue if its the goal
            }
            current = path.get(current); // next cell
        }
    }







    //=================================================

    // fields for gui.
    private int delay = 20;
    private int size = 10;
    /**
     * Set up the interface
     */
    public void setupGui(){
        UI.addButton("New Maze", this::makeMaze);
        UI.addSlider("Maze Size", 4, 40,10, (double v)->{size=(int)v;});
        UI.setMouseListener(this::doMouse);
        UI.addButton("First path",    ()->{search="first";});
        UI.addButton("All paths",     ()->{search="all";});
        UI.addButton("Shortest path", ()->{search="shortest";});
        UI.addButton("Stop",          ()->{stopNow=true;});
        UI.addSlider("Speed", 1, 101,80, (double v)->{delay=(int)(100-v);});
        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.);
    }

    /**
     * Creates a new maze and draws it .
     */
    public void makeMaze(){
        maze = new Maze(size);
        maze.draw();
    }

    /**
     * Clicking the mouse on a cell should make the program
     * search for a path from the clicked cell to the goal.
     */
    public void doMouse(String action, double x, double y){
        if (action.equals("released")){
            maze.reset();
            maze.draw();
            pathCount = 0;
            MazeCell start = maze.getCellAt(x, y);
            if (search=="first"){
                exploreFromCell(start);
            }
            else if (search=="all"){
                stopNow=false;
                exploreFromCellAll(start);
            }
            else if (search=="shortest"){
                exploreFromCellShortest(start);
            }
        }
    }

    public static void main(String[] args) {
        MazeSearch ms = new MazeSearch();
        ms.setupGui();
        ms.makeMaze();
    }
}

