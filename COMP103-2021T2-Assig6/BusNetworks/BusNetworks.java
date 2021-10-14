// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2021T2, Assignment 6
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;

public class BusNetworks {

    /** Map of towns, indexed by their names */
    private Map<String,Town> busNetwork = new HashMap<String,Town>();

    /** CORE
     * Loads a network of towns from a file.
     * Constructs a Set of Town objects in the busNetwork field
     * Each town has a name and a set of neighbouring towns
     * First line of file contains the names of all the towns.
     * Remaining lines have pairs of names of towns that are connected.
     */
    public void loadNetwork(String filename) {
        try {
            busNetwork.clear();
            UI.clearText();
            List<String> lines = Files.readAllLines(Path.of(filename));
            String firstLine = lines.remove(0);
            /*# YOUR CODE HERE */
            String[] towns = firstLine.split(" "); // splits the first line of file by white spaces and inserts each string into the network
            for(int i =0; i <towns.length; i++){
                this.busNetwork.put(towns[i],new Town(towns[i]));
            }
            for(String temp : lines){ // for each line in the file we get the town and its destination and set them as two seperate strings.
                Scanner sc = new Scanner(temp);
                String town1 = sc.next();
                String town2 = sc.next();
                this.busNetwork.get(town1).addNeighbour(this.busNetwork.get(town2)); // using the two neighbouring strings we are able to set town2 as the neighbour of town 1
                this.busNetwork.get(town2).addNeighbour(this.busNetwork.get(town1)); // vice versa
            }

            UI.println("Loaded " + busNetwork.size() + " towns:");

        } catch (IOException e) {throw new RuntimeException("Loading data.txt failed" + e);}
    }

    /**  CORE
     * Print all the towns and their neighbours:
     * Each line starts with the name of the town, followed by
     *  the names of all its immediate neighbours,
     */
    public void printNetwork() {
        UI.println("The current network: \n====================");
        /*# YOUR CODE HERE */
        for(String town : this.busNetwork.keySet()){ // for each town in the bus network
            Set<Town> neighbours = this.busNetwork.get(town).getNeighbours(); // create a neighbours set using the Town.getNeighbour function although this is not neccesary
            String neighbourStrings = ""; // create new string for the output and set it as empty for now.
            for(Town towns : neighbours){ // for each neighbouring town.
                neighbourStrings += towns.getName() + " "; // get the name and pass add it onto the string
            }
            UI.println(this.busNetwork.get(town).getName() + " -> " + neighbourStrings); // prints the line
        }

    }

    /** COMPLETION
     * Return a set of all the nodes that are connected to the given node.
     * Traverse the network from this node in the standard way, using a
     * visited set, and then return the visited set
     */
    public Set<Town> findAllConnected(Town town) {
        /*# YOUR CODE HERE */
        Set<Town> visited = new HashSet<Town>(); // create a visited set
        findAllConnectedRecursive(town,visited);  // call the helper method
        return visited; // return the set
    }
    public void findAllConnectedRecursive(Town town, Set<Town> visited){
        for(Town neighbour : town.getNeighbours()){ // for each neighbour of the town
            if(!visited.contains(neighbour)){ // if the visited set does not contain said neighbour already, add it and use recursion on the neighbour to find the next town thats connected.
                visited.add(neighbour);
                findAllConnectedRecursive(neighbour,visited);
            }
        }
    }

    /**  COMPLETION
     * Print all the towns that are reachable through the network from
     * the town with the given name.
     * Note, do not include the town itself in the list.
     */
    public void printReachable(String name) {
        Town town = busNetwork.get(name);
        if (town == null) {
            UI.println(name + " is not a recognised town");
        } else {
            UI.println("\nFrom " + town.getName() + " you can get to:");
            /*# YOUR CODE HERE */
            Set<Town> connected = findAllConnected(town); // create a connected set using the findAllConnected method which will return a set that contains all of the towns that are connected to the town specified
            for (Town reachableTown : connected) { // now for each town in this new connected set
                if (!reachableTown.equals(town)) { // we don't want to print the town in its own list
                    UI.println(reachableTown); // print the town
                }

            }

        }
    }

    /**  COMPLETION
     * Print all the connected sets of towns in the busNetwork
     * Each line of the output should be the names of the towns in a connected set
     * Works through busNetwork, using findAllConnected on each town that hasn't
     * yet been printed out.
     */
    public void printConnectedGroups() {
        UI.println("Groups of Connected Towns: \n================");
        int groupNum = 1;
        /*# YOUR CODE HERE */
        Set<Town> printedTowns = new HashSet<Town>(); // create a new hash set that will contain the printed towns
        for(String town : this.busNetwork.keySet()){ // for each town in our network
            if(!printedTowns.contains(this.busNetwork.get(town))){ // as long as the printedTowns set does not contain the town we are going through now otherwise we get duplicate groups
                printedTowns.add(this.busNetwork.get(town)); // add the town to the printedTowns set
                String print = ""; // create a String and set it as empty
                for(Town town1 : findAllConnected(this.busNetwork.get(town))){ // now for each neighbour of this town
                        printedTowns.add(town1);
                        String name = town1.getName(); // creat a string to contain town1 although not neccessary
                        print += name + " "; // add name to the print string and a whitespace

                }
                UI.println("Group " + groupNum + ": " + print ); // print the group number as well as the connected towns
                groupNum++; // increase the group number by one
            }

        }

    }

    /**
     * Set up the GUI (buttons and mouse)
     */
    public void setupGUI() {
        UI.addButton("Load", ()->{loadNetwork(UIFileChooser.open());});
        UI.addButton("Print Network", this::printNetwork);
        UI.addTextField("Reachable from", this::printReachable);
        UI.addButton("All Connected Groups", this::printConnectedGroups);
        UI.addButton("Clear", UI::clearText);
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1100, 500);
        UI.setDivider(1.0);
        loadNetwork("data-small.txt");
    }

    // Main
    public static void main(String[] arguments) {
        BusNetworks bnw = new BusNetworks();
        bnw.setupGUI();
    }

}
