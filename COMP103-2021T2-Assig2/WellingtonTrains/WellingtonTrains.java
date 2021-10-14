// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2021T2, Assignment 2
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.util.*;
import java.util.Map.Entry;
import java.io.*;
import java.nio.file.*;

/**
 * WellingtonTrains
 * A program to answer queries about Wellington train lines and timetables for
 *  the train services on those train lines.
 *
 * See the assignment page for a description of the program and what you have to do.
 */

public class WellingtonTrains {
    //Fields to store the collections of Stations and Lines
    private Map <String, TrainLine> allTrainLines = new HashMap<String, TrainLine>();
    private Map <String, Station> allStations = new HashMap<String, Station>();;

    // Fields for the suggested GUI.
    private String stationName;        // station to get info about, or to start journey from
    private String lineName;           // train line to get info about.
    private String destinationName;
    private int startTime = 0;         // time for enquiring about

    /**
     * main method:  load the data and set up the user interface
     */
    public static void main(String[] args) {
        WellingtonTrains wel = new WellingtonTrains();
        wel.loadData();   // load all the data
        wel.setupGUI();   // set up the interface
    }

    /**
     * Load data files
     */
    public void loadData() {
        loadStationData();
        UI.println("Loaded Stations");
        loadTrainLineData();
        UI.println("Loaded Train Lines");
        // The following is only needed for the Completion and Challenge
        loadTrainServicesData();
        UI.println("Loaded Train Services");
    }

    /**
     * User interface has buttons for the queries and text fields to enter stations and train line
     * You will need to implement the methods here.
     */
    public void setupGUI() {
        UI.addButton("All Stations", this::listAllStations);
        UI.addButton("Stations by name", this::listStationsByName);
        UI.addButton("All Lines", this::listAllTrainLines);
        UI.addTextField("Station", (String name) -> {
            this.stationName = name;
        });
        UI.addTextField("Train Line", (String name) -> {
            this.lineName = name;
        });
        UI.addTextField("Destination", (String name) -> {
            this.destinationName = name;
        });
        UI.addTextField("Time (24hr)", (String time) ->
        {
            try {
                this.startTime = Integer.parseInt(time);
            } catch (Exception e) {
                UI.println("Enter four digits");
            }
        });
        UI.addButton("Lines of Station", () -> {
            listLinesOfStation(this.stationName);
        });
        UI.addButton("Stations on Line", () -> {
            listStationsOnLine(this.lineName);
        });
        UI.addButton("Stations connected?", () -> {
            checkConnected(this.stationName, this.destinationName);
        });
        UI.addButton("Next Services", () -> {
            findNextServices(this.stationName, this.startTime);
        });
        UI.addButton("Find Trip", () -> {
            findTrip(this.stationName, this.destinationName, this.startTime);
        });

        UI.addButton("Quit", UI::quit);
        UI.setMouseListener(this::doMouse);

        UI.setWindowSize(900, 400);
        UI.setDivider(0.2);
        // this is just to remind you to start the program using main!
        if (allStations.isEmpty()) {
            UI.setFontSize(36);
            UI.drawString("Start the program from main", 2, 36);
            UI.drawString("in order to load the data", 2, 80);
            UI.sleep(2000);
            UI.quit();
        } else {
            UI.drawImage("data/geographic-map.png", 0, 0);
            UI.drawString("Click to list closest stations", 2, 12);
        }
    }

    public void doMouse(String action, double x, double y) {
        if (action.equals("released")){
            int i = 0;
            Map<Double,Station> stations = new TreeMap<Double,Station>();
            UI.clearText();
            for(String key:allStations.keySet()){
                double distance = Math.sqrt((x-allStations.get(key).getXCoord())*(x-allStations.get(key).getXCoord())+(y-allStations.get(key).getYCoord())*(y-allStations.get(key).getYCoord()));
                stations.put(distance,allStations.get(key));
            }
            UI.println("10 stations closest (km) to (x,y) are: ");
            UI.println("---------");
            for(Double key:stations.keySet()){
                i++;
                if(i<11){
                    UI.printf("Station: "+stations.get(key).getName()+" is %.1f\n",key);

                }
            }
        }

    }

    // Methods for loading data and answering queries

    public void loadStationData() {
        String file = "data/stations.data";
        File stationData = new File(file);
        try {
            Scanner sc = new Scanner(stationData);
            while (sc.hasNext()) {
                String name = sc.next();
                allStations.put(name, new Station(name, sc.nextInt(), sc.nextDouble(), sc.nextDouble()));
            }
        } catch (IOException e) {
        }
    }

    public void loadTrainLineData(){
        try{
            List<String>trainLineList = Files.readAllLines(Path.of("data/train-lines.data"));
            if(trainLineList == null){
                return;
            }
            for(String thisLine : trainLineList){
                Scanner sc = new Scanner(thisLine);
                String name = sc.next();
                TrainLine trainLine = new TrainLine(name);
                allTrainLines.put(name, trainLine);
                File file = new File("data/"+name+"-stations.data");
                try{
                    Scanner scan = new Scanner(file);
                    while (scan.hasNext()){
                        String station = scan.next();
                        Station sta = allStations.get(station);
                        TrainLine trainLine1 = allTrainLines.get(name);
                        trainLine1.addStation(sta);
                        sta.addTrainLine(trainLine1);
                    }
                }
                catch(Exception e){
                }
            }
        }catch(Exception e){
        }
    }

        public void loadTrainServicesData(){
            try{
                List<String> trainLineList = Files.readAllLines(Path.of("data/train-lines.data"));
                if(trainLineList ==null){
                    UI.println("No train line data found");
                    return;
                }
                for(String lines : trainLineList){
                    Scanner sc = new Scanner(lines);
                    String name = sc.next();
                    TrainLine thisTrainLine = allTrainLines.get(name);

                    List<String> trainLineServices = Files.readAllLines(Path.of("data/"+name+"-services.data"));
                    for(String services : trainLineServices){
                        Scanner sca = new Scanner(services);
                        TrainService trainService = new TrainService(thisTrainLine);
                        thisTrainLine.addTrainService(trainService);
                        while(sca.hasNextInt()){
                            int time = sca.nextInt();
                            trainService.addTime(time);
                        }

                    }
                }
            }catch(Exception e){
            }
        }


        public void listAllStations(){
            UI.clearText();
            Iterator it = allStations.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry entry = (Map.Entry)it.next();
                UI.println("Station: "+  entry.getValue());
            }
        }


    public void listAllTrainLines(){
        UI.clearText();
        Iterator it = allTrainLines.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry)it.next();
            UI.println("Train Line: "+  entry.getValue());
        }

    }
    public void listLinesOfStation(String stationName){
        UI.clearText();
        int count = 0;
        for(String test : allTrainLines.keySet()){
            TrainLine testLine = allTrainLines.get(test);
            List <Station> stations = testLine.getStations();
            for(Station station : stations){
                if(station.getName().equals(stationName)){
                    UI.println(testLine);
                    count +=1;
                }
            }
        }
        if(count == 0){
            UI.println("No matches");
        }
    }

    public void listStationsOnLine(String lineName){
        UI.clearText();
        int count = 0;
        List<Station>lineStations= allTrainLines.get(lineName).getStations();
        for(Station eachstation: lineStations){
            UI.println(eachstation);
            count+=1;
        }
        if(count ==0){
            UI.println("No matches");
        }
    }


    public void checkConnected(String stationName, String destinationName){
        UI.clearText();
        boolean check = false;
        for(String test : allTrainLines.keySet()){
            TrainLine testLine = allTrainLines.get(test);
            List <Station> stations = testLine.getStations();
            for(Station station : stations){
                if(station.getName().equals(stationName)){
                    int zone1 = station.getZone();
                    if(check == false){
                    for(Station station2 : stations) {
                        if (station2.getName().equals(destinationName)) {
                            UI.println("The " + testLine.getName() + " train line goes from " + stationName + " to " + destinationName + ".");
                            int zone2 = station2.getZone();
                            if(zone2> zone1) {
                                zone2 = zone2 - zone1 + 1;
                                UI.println("the trip goes through " + zone2 + " zones");
                            }else if (zone1 > zone2){
                                zone1 = zone1 - zone2 +1;
                                UI.println("the trip goes through " + zone1 + " zones");
                            }
                            check = true;
                        }
                    }
                    }
                }
            }
        }
        if(check==false){
            UI.println("no train line found from  "+stationName+" to "+destinationName);
        }
    }


    public void findNextServices(String stationName, int startTime){
        UI.clearText();
        Station station = allStations.get(stationName);
        boolean found = false;
        if(station == null){
            UI.println("no such station");
            return;
        }
        Set<TrainLine> trainLineSet = station.getTrainLines();
        for(TrainLine thisTrainLine : trainLineSet){
            List<TrainService> servicesList = thisTrainLine.getTrainServices();
            List<Station> stations = thisTrainLine.getStations();
            int location = stations.indexOf(station);
            for(TrainService thisTime : servicesList){
                List<Integer> services = thisTime.getTimes();
                int time = services.get(location);
                if(time>startTime && time != -1){
                    found = true;
                    UI.println("next service is on "+ thisTrainLine.getName()+" from "+stationName+" is at "+time);
                    break;
                }
            }
        }
        if (!(found)){
            UI.println("no services found");
        }
    }





    public void findTrip(String stationName, String destinationName, int startTime){
        UI.clearText();
        Station station = allStations.get(stationName);
        Station destination = allStations.get(destinationName);
        for (TrainLine trainLine : station.getTrainLines()) {
            if (destination.getTrainLines().contains(trainLine)) {
                    for (TrainService trainservice : trainLine.getTrainServices()) {
                        int service = trainservice.getTimes().get(trainLine.getStations().indexOf(station));
                        if (service >= startTime) {
                            int finalTime = trainservice.getTimes().get(trainLine.getStations().indexOf(destination));
                            UI.println("leaves " + stationName + " at " + service + " and arrives at " + finalTime);
                        }
                    }
                }

            }
        }

    public void listStationsByName(){
        UI.clearText();
        for(String name : allStations.keySet()){
            UI.println("Station: " + name);
        }
    }

}
