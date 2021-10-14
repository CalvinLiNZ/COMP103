// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2021T2, Assignment 5
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;
import java.nio.file.*;

/** 
 * Calculator for Cambridge-Polish Notation expressions
 * (see the description in the assignment page)
 * User can type in an expression (in CPN) and the program
 * will compute and print out the value of the expression.
 * The template provides the method to read an expression and turn it into a tree.
 * You have to write the method to evaluate an expression tree.
 *  and also check and report certain kinds of invalid expressions
 */

public class CPNCalculator{

    /**
     * Setup GUI then run the calculator
     */
    public static void main(String[] args){
        CPNCalculator calc = new CPNCalculator();
        calc.setupGUI();
        calc.runCalculator();
    }

    /** Setup the gui */
    public void setupGUI(){
        UI.addButton("Clear", UI::clearText); 
        UI.addButton("Quit", UI::quit); 
        UI.setDivider(1.0);
    }

    /**
     * Run the calculator:
     * loop forever:  (a REPL - Read Eval Print Loop)
     *  - read an expression,
     *  - evaluate the expression,
     *  - print out the value
     * Invalid expressions could cause errors when reading or evaluating
     * The try-catch prevents these errors from crashing the program - 
     *  the error is caught, and a message printed, then the loop continues.
     */
    public void runCalculator(){
        UI.println("Enter expressions in pre-order format with spaces");
        UI.println("eg   ( * ( + 4 5 8 3 -10 ) 7 ( / 6 4 ) 18 )");
        while (true){
            UI.println();
            try {
                GTNode<ExpElem> expr = readExpr();
                double value = evaluate(expr);
                UI.println(" -> " + value);
            }catch(Exception e){UI.println("Something went wrong! "+e);}
        }
    }

    /**
     * Evaluate an expression and return the value
     * Returns Double.NaN if the expression is invalid in some way.
     * If the node is a number
     *  => just return the value of the number
     * or it is a named constant
     *  => return the appropriate value
     * or it is an operator node with children
     *  => evaluate all the children and then apply the operator.
     */
    public double evaluate(GTNode<ExpElem> expr){ // recursion allows us to continuously call this method until all of the operators have been evaluated and the result is returned.
        if (expr==null){
            return Double.NaN;
        }
        /*# YOUR CODE HERE */
        if(expr.getItem().operator.equalsIgnoreCase("PI")){ // if operator = pi return Math.PI
            return Math.PI;
        }
        if(expr.getItem().operator.equalsIgnoreCase("E")){ // if operator = E return Math.E
            return Math.E;
        }

        if(expr.numberOfChildren() == 0 || expr.getItem().operator.equals("#")){ // if the number of children is not 0  or the items operator is equal to "#" return expr.getItem().value
            return expr.getItem().value;
        }
        else{
            String operator = expr.getItem().operator;
            if(operator.equals("+")){ // if operator is equal to +
                double result =0; // set result to 0 for now
                for(GTNode<ExpElem> child : expr){ // for each child in expr we want to evaluate again using recursion and then return the result when all children nodes have been evaluated
                    result = result + evaluate(child);
                }
                return result;
            }
            else if(operator.equals("-")){ // if operator is equal to -
                double result = evaluate(expr.getChild(0)); // set the result to the first node so we can subtract from that node
                for(int i = 1; i < expr.numberOfChildren(); i++){  // go through every node
                    result = result - evaluate(expr.getChild(i)); // minus the next child from the result and continue to use recursion for the following nodes.
                }
                return result; // return result
            }
            else if(operator.equals("*")){ // if operator is *
                double result = 1; // result is 1 in case of * 0
                for(GTNode<ExpElem> child : expr){
                    result = result * evaluate(child); // recursion through every node and * by the result
                }
                return result;
            }
            else if(operator.equals("/")){ // if operator is *
                double result = evaluate(expr.getChild(0)); // set result as the first child so we can divide by it later
                for(int i = 1; i < expr.numberOfChildren(); i++){
                    result = result / evaluate(expr.getChild(i)); // recursion through every node and / by the result we set earlier
                }
                return result; // return the new result
            }
            else if (operator.equals("^")) { // if operator is ^
                if(expr.numberOfChildren() != 2) { // number of children can not be 2
                    UI.println("Invalid operands for power");
                    return Double.NaN;
                }
                double number = evaluate(expr.getChild(0)); // recursion through every node and set it to number
                return Math.pow(number, evaluate(expr.getChild(1))); // return the result when its been powered
            }

            else if (operator.equalsIgnoreCase("sqrt")) {
                if(expr.numberOfChildren() != 1) {
                    UI.println("Invalid operands for sqrt");
                    return Double.NaN;
                }
                return Math.sqrt(evaluate(expr.getChild(0)));
            }

            else if (operator.equalsIgnoreCase("log")) { // if log is operator
                if(expr.numberOfChildren() == 2) {
                    return Math.log10(evaluate(expr.getChild(0))) / Math.log10(evaluate(expr.getChild(1))); // if there were two child nodes then we must return this
                }
                else if ( expr.numberOfChildren() == 1 ) { // if only one child number then we can return the following
                    return Math.log(evaluate(expr.getChild(0)));
                }
                else {
                    UI.println("Invalid operands for log");
                }
            }
            else if (operator.equalsIgnoreCase("ln")) { // same thing as log except we must use the natural log instead
                if ( expr.numberOfChildren() == 1 ) {
                    return Math.log(evaluate(expr.getChild(0)));
                }
                else {
                    UI.println("Invalid operands for ln");
                }
            }
            else if (operator.equalsIgnoreCase("sin")) {
                if(expr.numberOfChildren() != 1) {
                    UI.println("Error: invalid operands for sin");
                    return Double.NaN;
                }
                return Math.sin(evaluate(expr.getChild(0))); // recursion through the child and return sin of the result
            }
            else if (operator.equals("cos")) {
                if(expr.numberOfChildren() != 1) {
                    UI.println("Invalid operands for cos");
                    return Double.NaN;
                }
                return Math.cos(evaluate(expr.getChild(0)));
            }
            else if (operator.equalsIgnoreCase("tan")) {
                if(expr.numberOfChildren() != 1) {
                    UI.println("Invalid operands for tan");
                    return Double.NaN;
                }
                return Math.tan(evaluate(expr.getChild(0)));
            }

            else if (operator.equalsIgnoreCase("dist")) {
                if(expr.numberOfChildren() == 4) { // if operands = 4
                    double x1 = evaluate(expr.getChild(0)); // set each operand to a variable
                    double y1 = evaluate(expr.getChild(1));
                    double x2 = evaluate(expr.getChild(2));
                    double y2 = evaluate(expr.getChild(3));

                    double x = Math.abs(x1 - x2); // pythagoras setup
                    double y = Math.abs(y1 - y2);

                    double result = Math.pow(x, 2) + Math.pow(y, 2); // pythagoras to solve

                    return Math.sqrt(result); // return sqrt
                }
                else if (expr.numberOfChildren() == 6) { // if operands = 6
                    double x1 = evaluate(expr.getChild(0)); // set each operand to a variable
                    double y1 = evaluate(expr.getChild(1));
                    double z1 = evaluate(expr.getChild(2));
                    double x2 = evaluate(expr.getChild(3));
                    double y2 = evaluate(expr.getChild(4));
                    double z2 = evaluate(expr.getChild(5));

                    double x = Math.abs(x1 - x2); // pythagoras setup
                    double y = Math.abs(y1 - y2);
                    double z = Math.abs(z1 - z2);

                    double result = Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2); // pythagoras to solve
                    return Math.sqrt(result); // return sqrt
                }
                else UI.println("Invalid operands for dist");
            }
            else if (operator.equalsIgnoreCase("avg")) {  // division operator
                if(expr.numberOfChildren() < 1) {
                    UI.println("invalid operands for avg");
                    return Double.NaN;
                }

                double result = 0; // create result variable
                for (int i = 0; i < expr.numberOfChildren(); i++) {
                    result += evaluate(expr.getChild(i)); // recursion through all children to see if any other evaluate other operators and add the results onto the result
                }
                return result / expr.numberOfChildren(); // divide the final result with the number of children in the equation and then return
            }
            else
            {
                UI.println("\"" + expr.getItem() + "\" is not an operator"); // operator was unknown in the code
                return Double.NaN;
            }

        }
        return Double.NaN;

        }


    /** 
     * Reads an expression from the user and constructs the tree.
     */ 
    public GTNode<ExpElem> readExpr(){
        String expr = UI.askString("expr:");
        return readExpr(new Scanner(expr));   // the recursive reading method
    }

    /**
     * Recursive helper method.
     * Uses the hasNext(String pattern) method for the Scanner to peek at next token
     */
    public GTNode<ExpElem> readExpr(Scanner sc){
        if (sc.hasNextDouble()) {                     // next token is a number: return a new node
            return new GTNode<ExpElem>(new ExpElem(sc.nextDouble()));
        }
        else if (sc.hasNext("\\(")) {                 // next token is an opening bracket
            sc.next();                                // read and throw away the opening '('
            ExpElem opElem = new ExpElem(sc.next());  // read the operator
            GTNode<ExpElem> node = new GTNode<ExpElem>(opElem);  // make the node, with the operator in it.
            while (! sc.hasNext("\\)")){              // loop until the closing ')'
                GTNode<ExpElem> child = readExpr(sc); // read each operand/argument
                node.addChild(child);                 // and add as a child of the node
            }
            sc.next();                                // read and throw away the closing ')'
            return node;
        }
        else {                                        // next token must be a named constant (PI or E)
                                                      // make a token with the name as the "operator"
            return new GTNode<ExpElem>(new ExpElem(sc.next()));
        }
    }

}

