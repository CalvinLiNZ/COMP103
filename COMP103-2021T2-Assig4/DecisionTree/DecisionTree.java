// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2021T2, Assignment 4
 * Name:
 * Username:
 * ID:
 */

/**
 * Implements a decision tree that asks a user yes/no questions to determine a decision.
 * Eg, asks about properties of an animal to determine the type of animal.
 * 
 * A decision tree is a tree in which all the internal nodes have a question, 
 * The answer to the question determines which way the program will
 *  proceed down the tree.  
 * All the leaf nodes have the decision (the kind of animal in the example tree).
 *
 * The decision tree may be a predermined decision tree, or it can be a "growing"
 * decision tree, where the user can add questions and decisions to the tree whenever
 * the tree gives a wrong answer.
 *
 * In the growing version, when the program guesses wrong, it asks the player
 * for another question that would help it in the future, and adds it (with the
 * correct answers) to the decision tree. 
 *
 */

import ecs100.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.awt.Color;

public class DecisionTree {

    public DTNode theTree;    // root of the decision tree;

    /**
     * Setup the GUI and make a sample tree
     */
    public static void main(String[] args){
        DecisionTree dt = new DecisionTree();
        dt.setupGUI();
        dt.loadTree("sample-animal-tree.txt");
    }

    /**
     * Set up the interface
     */
    public void setupGUI(){
        UI.addButton("Load Tree", ()->{loadTree(UIFileChooser.open("File with a Decision Tree"));});
        UI.addButton("Print Tree", this::printTree);
        UI.addButton("Run Tree", this::runTree);
        UI.addButton("Grow Tree", this::growTree);
        UI.addButton("Save Tree", this::saveTree);  // for completion
        UI.addButton("Draw Tree", this::drawTree);  // for challenge
        UI.addButton("Reset", ()->{loadTree("sample-animal-tree.txt");});
        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.5);
    }

    /**  
     * Print out the contents of the decision tree in the text pane.
     * The root node should be at the top, followed by its "yes" subtree,
     * and then its "no" subtree.
     * Needs a recursive "helper method" which is passed a node.
     * 
     * COMPLETION:
     * Each node should be indented by how deep it is in the tree.
     * The recursive "helper method" is passed a node and an indentation string.
     *  (The indentation string will be a string of space characters)
     */
    public void printTree(){
        UI.clearText();
        if(theTree != null){
            UI.println(theTree.getText() + "?");
            printAll(theTree, "   ");
        }

    }

    public void printAll(DTNode node, String blank) {
        if (node.getYes() != null) {
            if (node.getYes().isAnswer()) {
                UI.println(blank + "Y: " + node.getYes().getText());
            } else {
                UI.println(blank + "Y: " + node.getYes().getText() + "?");
            }
            printAll(node.getYes(), blank + "  ");

            if (node.getYes() != null) {
                if (node.getNo().isAnswer()) {
                    UI.println(blank + "N: " + node.getNo().getText());
                } else {
                    UI.println(blank + "N: " + node.getNo().getText() + "?");
                }
                printAll(node.getNo(), blank + "  ");
            }
        }
    }

    /**
     * Run the tree by starting at the top (of theTree), and working
     * down the tree until it gets to a leaf node (a node with no children)
     * If the node is a leaf it prints the answer in the node
     * If the node is not a leaf node, then it asks the question in the node,
     * and depending on the answer, goes to the "yes" child or the "no" child.
     */
    public void runTree(){
        UI.clearText();
        DTNode node = theTree;
        while(node!=null){
            while(node.getYes() != null && node.getNo() != null){ // keep going until no children
                String question = UI.askString("Is it true? " + node.getText() + " Y/N");
                if(question.equalsIgnoreCase("y")){ // if answer is y
                    node = node.getYes(); // change the node to the child node thats yes
                }
                else if (question.equalsIgnoreCase("n")){ // if answer is n
                    node = node.getNo(); // change current node to the no child node
                }
                else{
                    UI.println("Please answer with either 'y' or 'n'");
                }
            }
            UI.println("The answer is " + node.getText());
            break;

        }


    }

    /**
     * Grow the tree by allowing the user to extend the tree.
     * Like runTree, it starts at the top (of theTree), and works its way down the tree
     *  until it finally gets to a leaf node. 
     * If the current node has a question, then it asks the question in the node,
     * and depending on the answer, goes to the "yes" child or the "no" child.
     * If the current node is a leaf it prints the decision, and asks if it is right.
     * If it was wrong, it
     *  - asks the user what the decision should have been,
     *  - asks for a question to distinguish the right decision from the wrong one
     *  - changes the text in the node to be the question
     *  - adds two new children (leaf nodes) to the node with the two decisions.
     */
    public void growTree () {
        UI.clearText();
        DTNode node = theTree;
        if(node!=null){
            while(node.getYes() != null && node.getNo() != null){ // run until no children left
                String question = UI.askString("Is it true? " + node.getText() + " Y/N?");
                if(question.equalsIgnoreCase("y")){
                    node = node.getYes(); // goes down the tree down the yes path
                }else if(question.equalsIgnoreCase("n")){
                    node = node.getNo(); // goes down the tree down the no path
                }else{
                    UI.println("Please type 'y' or 'n'");
                }

            }
            String question = "";
            while(!question.equalsIgnoreCase("n") && !question.equalsIgnoreCase("y")){ // keep running until break as long as answer is not entered
                question = UI.askString("Is it a: " + node.getText() + " ?");
                if(question.equalsIgnoreCase("y")){ // if answer of question == y
                    UI.println("Game Over");
                    break;
                }
                else if(question.equalsIgnoreCase("n")){ // if answer of question == n
                    question = UI.askString("What are you thinking of?");
                    UI.println("I cant tell the difference between a " + node.getText() + " and a " + question);
                    String newChild = UI.askString("What is a question that is true for a "+ question+ " but not a "+ node.getText() + "?");
                    node.setText(newChild); // changes text to new question
                    node.setChildren(new DTNode(question), new DTNode(node.getText())); // created child using the new question and the name of the new node
                    UI.println("Tree Updated");
                    break; // ends the while loop
                }
                else{
                    UI.println("Please type 'y' or 'n'");
                }
            }

        }

    }


    public void drawTree(){
        UI.clearGraphics();
        DTNode node = theTree;
        int width = 50;
        int height = 10;
        int x = 700;
        int y = 50;
        UI.drawRect(x, y, width, height);
        UI.drawString(node.getText(),x,y+height/2);
        if(node.getYes() != null){
            UI.drawLine(x + width/2,y + height,x+100 + width /2,y+100);
            drawTree();
        }
        if(node.getNo() != null){
            UI.drawLine(x + width/2,y + height,x-100 + width/2,y+100);
            drawTree();
        }
    }
    public void saveTree(){
        File file = new File(UIFileChooser.save()); // new file
        BufferedWriter writer; // new writer
        try{
            writer = new BufferedWriter(new FileWriter(file)); // new writer with file
            saveTreeRecursion(theTree, writer); // run the function
            writer.close(); // closes writer

        }catch(IOException e){

        }
    }

    public void saveTreeRecursion(DTNode node, BufferedWriter writer){
        try{
            if(node.isAnswer()){ // is the node is an answer and not a question
                writer.write("Answer: "+node.getText()); // write to the file "answer: " and the node's text
                writer.newLine(); // creates new line in writer
                return;
            }else{
                writer.write("Question: " +node.getText());
                writer.newLine();
                saveTreeRecursion(node.getYes(),writer); // traverse yes
                saveTreeRecursion(node.getNo(),writer); // traverse no
            }

        }catch(IOException e){
        }
    }

    // You will need to define methods for the Completion and Challenge parts.

    // Written for you

    /** 
     * Loads a decision tree from a file.
     * Each line starts with either "Question:" or "Answer:" and is followed by the text
     * Calls a recursive method to load the tree and return the root node,
     *  and assigns this node to theTree.
     */
    public void loadTree (String filename) { 
        if (!Files.exists(Path.of(filename))){
            UI.println("No such file: "+filename);
            return;
        }
        try{theTree = loadSubTree(new ArrayDeque<String>(Files.readAllLines(Path.of(filename))));}
        catch(IOException e){UI.println("File reading failed: " + e);}
    }

    /**
     * Loads a tree (or subtree) from a Scanner and returns the root.
     * The first line has the text for the root node of the tree (or subtree)
     * It should make the node, and 
     *   if the first line starts with "Question:", it loads two subtrees (yes, and no)
     *    from the scanner and add them as the  children of the node,
     * Finally, it should return the  node.
     */
    public DTNode loadSubTree(Queue<String> lines){
        Scanner line = new Scanner(lines.poll());
        String type = line.next();
        String text = line.nextLine().trim();
        DTNode node = new DTNode(text);
        if (type.equals("Question:")){
            DTNode yesCh = loadSubTree(lines);
            DTNode noCh = loadSubTree(lines);
            node.setChildren(yesCh, noCh);
        }
        return node;

    }



}
