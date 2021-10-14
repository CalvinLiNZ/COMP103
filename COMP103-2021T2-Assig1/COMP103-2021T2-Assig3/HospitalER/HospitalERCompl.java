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
import java.io.*;

/**
 * Simulation of a Hospital ER
 *
 * The hospital has a collection of Departments, including the ER department, each of which has
 *  and a treatment room.
 *
 * When patients arrive at the hospital, they are immediately assessed by the
 *  triage team who determine the priority of the patient and (unrealistically) a sequence of treatments
 *  that the patient will need.
 *
 * The simulation should move patients through the departments for each of the required treatments,
 * finally discharging patients when they have completed their final treatment.
 *
 *  READ THE ASSIGNMENT PAGE!
 */

public class HospitalERCompl{


    // Fields for recording the patients waiting in the waiting room and being treated in the treatment room
    private static final int MAX_PATIENTS = 5;   // max number of patients currently being treated
    private Map<String,Department> department= new HashMap<String,Department>();


    // fields for the statistics
    private int patientNum;
    private int times = 0;
    private int patientNumPri = 0;
    private int timesPri = 0;
    // Fields for the simulation
    private boolean running = false;
    private int time = 0; // The simulated time - the current "tick"
    private int delay = 300;  // milliseconds of real time for each tick

    // fields controlling the probabilities.
    private int arrivalInterval = 5;   // new patient every 5 ticks, on average
    private double probPri1 = 0.1; // 10% priority 1 patients
    private double probPri2 = 0.2; // 20% priority 2 patients
    private Random random = new Random();  // The random number generator.

    /**
     * Construct a new HospitalERCore object, setting up the GUI, and resetting
     */
    public static void main(String[] arguments) {
        HospitalERCompl er = new HospitalERCompl();
        er.setupGUI();
        er.reset(false);   // initialise with an ordinary queue.
    }

    /**
     * Set up the GUI: buttons to control simulation and sliders for setting parameters
     */
    public void setupGUI() {
        UI.addButton("Reset (Queue)", () -> {
            this.reset(false);
        });
        UI.addButton("Reset (Pri Queue)", () -> {
            this.reset(true);
        });
        UI.addButton("Start", () -> {
            if (!running) {
                run();
            }
        });   //don't start if already running!
        UI.addButton("Pause & Report", () -> {
            running = false;
        });
        UI.addSlider("Speed", 1, 400, (401 - delay),
                (double val) -> {
                    delay = (int) (401 - val);
                });
        UI.addSlider("Av arrival interval", 1, 50, arrivalInterval,
                (double val) -> {
                    arrivalInterval = (int) val;
                });
        UI.addSlider("Prob of Pri 1", 1, 100, probPri1 * 100,
                (double val) -> {
                    probPri1 = val / 100;
                });
        UI.addSlider("Prob of Pri 2", 1, 100, probPri2 * 100,
                (double val) -> {
                    probPri2 = Math.min(val / 100, 1 - probPri1);
                });
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1000, 600);
        UI.setDivider(0.5);
    }

    /**
     * Reset the simulation:
     * stop any running simulation,
     * reset the waiting and treatment rooms
     * reset the statistics.
     */
    public void reset(boolean usePriorityQueue) {
        running = false;
        UI.sleep(2 * delay);  // to make sure that any running simulation has stopped
        time = 0;           // set the "tick" to zero.

        // reset the waiting room, the treatment room, and the statistics.

        // create department hashmap containing all of the departments
        department = new HashMap<String,Department>();
        department.put("X-ray",new Department("X-ray",MAX_PATIENTS,usePriorityQueue));
        department.put("ER",new Department("ER",MAX_PATIENTS,usePriorityQueue));
        department.put("MRI",new Department("MRI",MAX_PATIENTS,usePriorityQueue));
        department.put("Surgery",new Department("Surgery",MAX_PATIENTS,usePriorityQueue));
        department.put("Ultrasound",new Department("Ultrasound",MAX_PATIENTS,usePriorityQueue));


        this.patientNum = 0;
        this.times = 0;
        UI.clearGraphics();
        UI.clearText();
    }

    /**
     * Main loop of the simulation
     */
    public void run(){
        if (running) { return; } // don't start simulation if already running one!
        running = true;

        while (running) {         // each time step, check whether the simulation should pause.
            time += delay;

            // create collections with the current waitingroom, treatmentroom and department.
            Queue<Patient> currentWaitingRoom = new PriorityQueue<Patient>();
            Set<Patient> currentTreatmentRoom = new HashSet<Patient>();
            Department currentDepartment;

            for (String index : this.department.keySet()) { // for each department set the waiting room and treatment room to the current one.
                currentDepartment = this.department.get(index);
                currentWaitingRoom = currentDepartment.getWaitingRoom();
                currentTreatmentRoom = currentDepartment.getTreatmentRoom();
                Set<Patient> placeHolder = new HashSet<Patient>();
                for (Patient patient : currentTreatmentRoom) {
                    placeHolder.add(patient);
                }
                for (Patient pat : placeHolder){ // placeholder hashset to check if the patients in the treatment room have finished their treatments.
                    if (pat.completedCurrentTreatment()){
                        pat.incrementTreatmentNumber();
                        currentDepartment.discharge(pat);
                        if (pat.noMoreTreatments()){
                            patientNum++;
                            times += pat.getWaitingTime();
                            if (pat.getPriority()==1){
                                patientNumPri++;
                                timesPri += pat.getWaitingTime();
                            }
                        }
                        else{
                            department.get(pat.getCurrentTreatment()).addPatient(pat);
                            UI.println(time + ":moved: " + pat);
                        }
                    }
                    else {
                        pat.advanceTreatmentByTick();
                    }
                }
            }

            // Get any new patient that has arrived and add them to the waiting room
            if (time==1 || Math.random()<1.0/arrivalInterval){
                Patient newPatient = new Patient(time, randomPriority());
                UI.println(time+ ": Arrived: "+newPatient);
                this.department.get(newPatient.getCurrentTreatment()).addPatient(newPatient);
            }
            redraw();
            UI.sleep(delay);
        }
        // paused, so report current statistics
        reportStatistics();
    }

    // Additional methods used by run() (You can define more of your own)

    /**
     * Report summary statistics about all the patients that have been discharged.
     * (Doesn't include information about the patients currently waiting or being treated)
     * The run method should have been recording various statistics during the simulation.
     */
    public void reportStatistics(){
        /*# YOUR CODE HERE */
        running = false;
        UI.println("Processed " + patientNum + " patients with an average waiting time of " + times/patientNum + " minutes ");
        UI.println("Processed " + patientNumPri + " priority 1 patients with an average waiting time of " + timesPri/patientNumPri + " minutes ");

    }


    // HELPER METHODS FOR THE SIMULATION AND VISUALISATION
    /**
     * Redraws all the departments
     */
    public void redraw(){
        UI.clearGraphics();
        UI.setFontSize(14);
        UI.drawString("Treating Patients", 5, 15);
        UI.drawString("Waiting Queues", 200, 15);
        UI.drawLine(0,32,400, 32);

        // Draw the treatment room and the waiting room:
        double y = 80;
        UI.setFontSize(14);
        double x = 10;
        UI.drawRect(x-5, y-30, MAX_PATIENTS*10, 30);  // box to show max number of patients

        UI.drawLine(0,y+2,400, y+2);


        // draw all departments
        department.get("ER").redraw(y);
        department.get("X-ray").redraw(y*2);
        department.get("Surgery").redraw(y*3);
        department.get("MRI").redraw(y*4);
        department.get("Ultrasound").redraw(y*5);



    }

    /**
     * Returns a random priority 1 - 3
     * Probability of a priority 1 patient should be probPri1
     * Probability of a priority 2 patient should be probPri2
     * Probability of a priority 3 patient should be (1-probPri1-probPri2)
     */
    private int randomPriority(){
        double rnd = random.nextDouble();
        if (rnd < probPri1) {return 1;}
        if (rnd < (probPri1 + probPri2) ) {return 2;}
        return 3;
    }
}
