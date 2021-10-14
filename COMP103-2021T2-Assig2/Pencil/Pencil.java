// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2021T2, Assignment 2
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;

import javax.swing.*;
import java.util.*;
import java.awt.Color;


/** Pencil   */
public class Pencil{
    private double lastX;
    private double lastY;
    private Stack <List <double[]>> undo = new Stack<>();
    private Stack <List <double[]>> redo = new Stack<>();
    private double strokeWidth = 5;
    List <double[]> list = new ArrayList <>();
    private boolean check = false ;
    private Color lineColour;

    /**
     * Setup the GUI
     */
    public void setupGUI(){
        UI.setMouseMotionListener(this::doMouse);
        UI.addButton("Quit", UI::quit);
        UI.addButton("Undo",this::undo);
        UI.addButton("Redo",this::redo);
        UI.addButton("Choose Colour",this::doLineColour);
        UI.addSlider("Stroke Width",1,10,5,this::setWidth);
        UI.setLineWidth(3);
        UI.setDivider(0.0);
    }

    /**
     * Respond to mouse events
     */
    public void doMouse(String action, double x, double y) {
        if (action.equals("pressed")){
            UI.setLineWidth(strokeWidth);
            UI.setColor(lineColour);
            lastX = x;
            lastY = y;
            list = new ArrayList<>();
            list.add(new double []{ x, y});
        }
        else if (action.equals("dragged")){
            UI.drawLine(lastX, lastY, x, y);
            lastX = x;
            lastY = y;
            list.add(new double []{ x, y});
        }
        else if (action.equals("released")){
            UI.drawLine(lastX, lastY, x, y);
            list.add(new double []{ x, y});
            undo.add(list);
        } if(check){
            redo = new Stack<>();
            check = false;
        }
    }
    public void undo(){
        if(!undo.isEmpty()){
            List <double[]> undo1 = undo.pop();

            for(int i = 1; i < undo1.size(); i++){
                UI.setLineWidth(10);
                UI.eraseLine(undo1.get(i-1)[0],undo1.get(i-1)[1],undo1.get(i)[0],undo1.get(i)[1]);
                UI.setLineWidth(strokeWidth);
            }
            redo.push(undo1);
        }
    }
    public void redo(){
        if(!redo.isEmpty()){
            List<double[]> redo1 = redo.pop();
            for(int i=1;i<redo1.size();i++){
                UI.setLineWidth(strokeWidth);
                UI.drawLine(redo1.get(i-1)[0],redo1.get(i-1)[1],redo1.get(i)[0],redo1.get(i)[1]);
            }
            check = true;
        }
    }
    public void setWidth(double width){
        this.strokeWidth = width;
    }

    public void doLineColour(){
        this.lineColour= JColorChooser.showDialog(null,"Choose Colour",this.lineColour);
    }


    public static void main(String[] arguments){
        new Pencil().setupGUI();
    }

}
