// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2021T2, Assignment 3
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.util.*;

/**
 * A treatment Department (Surgery, X-ray room,  ER, Ultrasound, etc)
 * Each department will need
 * - A name,
 * - A maximum number of patients that can be treated at the same time
 * - A Set of Patients that are currently being treated
 * - A Queue of Patients waiting to be treated.
 *    (ordinary queue, or priority queue, depending on argument to constructor)
 */

public class Department{

    private String name;
    private int maxPatients;

    /*# YOUR CODE HERE */
    private Queue<Patient> waitingRoom = new ArrayDeque<Patient>();

    private Set<Patient> treatmentRoom = new HashSet<Patient>();

    public Department(String name, int max, boolean priority ){ // create constructor
        this.name = name;
        this.maxPatients =  max;
        if(priority){
            waitingRoom = new PriorityQueue<Patient>();
        }else{
            waitingRoom = new ArrayDeque<Patient>();

        }

    }

    // getters
    public int getMaxPatients(){
        return this.maxPatients;
    }

    public Queue <Patient> getWaitingRoom (){
        return this.waitingRoom;
    }
    public Set<Patient> getTreatmentRoom (){
        return this.treatmentRoom;
    }
    public void addPatient(Patient p){
        if(this.treatmentRoom.size()<this.maxPatients){
            this.treatmentRoom.add(p);
        }else{
            this.waitingRoom.offer(p);
        }
    }
    public void discharge(Patient p){
        this.treatmentRoom.remove(p);
    }

    public void removeFromWaitingRoom(){
        if(!this.waitingRoom.isEmpty()){
            Patient p = this.waitingRoom.poll();
            this.treatmentRoom.add(p);
        }

    }

    /**
     * Draw the department: the patients being treated and the patients waiting
     * You may need to change the names if your fields had different names
     */
    public void redraw(double y){
        UI.setFontSize(14);
        UI.drawString(name, 0, y-35);
        double x = 10;
        UI.drawRect(x-5, y-30, maxPatients*10, 30);  // box to show max number of patients
        for(Patient p : treatmentRoom){
            p.redraw(x, y);
            x += 10;
        }
        x = 200;
        List <Patient> waiting = new ArrayList<Patient>();
        Queue<Patient> temp = new ArrayDeque<Patient>();
        for(int i =0; i < this.waitingRoom.size(); i++){
            Patient p = waitingRoom.poll();
            waiting.add(p);
            temp.offer(p);
        }
        for(Patient p : temp){
            waitingRoom.offer(p);
        }
        for(Patient p : waiting){
            p.redraw(x, y);
            x += 10;
        }

        UI.drawLine(0,y+2,400, y+2);
    }

}
