import exceptions.InvalidDateException;
import specification.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            Class.forName("implementation.DaySchedule"); //makes instance so it calls static block
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);

        //Makes schedule
        Schedule schedule = ScheduleManager.getSchedule();
        try {
            System.out.println("Enter meta data file: ");
            schedule.initialization(scanner.nextLine());
        } catch (IOException | InvalidDateException e) {
            System.out.println(e.getMessage() + " Wrong input");
            return;
        }

        while (true) {
            System.out.println("Enter number 0-10");
            System.out.println("1) Add Room");
            System.out.println("2) Add Appointment");
            System.out.println("3) Remove Appointment");
            System.out.println("4) Change Appointment");
            System.out.println("5) Search Appointment");
            System.out.println("6) Import CSV file");
            System.out.println("7) Import JSON file");
            System.out.println("8) Export CSV file");
            System.out.println("9) Export JSON file");
            System.out.println("10) Export PDF file");
            System.out.println("0) Exit application");

            System.out.println("\n\n\n");
            int chosen;
            try {
                chosen = Integer.parseInt(scanner.nextLine());
            }
            catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                continue;
            }
            switch (chosen) {
                case 0:
                    scanner.close();
                    return; //leave application
                //Add Room
                case 1: { //adds room
                    System.out.println("Enter room name to add: ");
                    boolean added = schedule.addRooms(new Room(scanner.nextLine()));
                    if (added)
                        System.out.println("Room added");
                    else
                        System.out.println("This room already exists");

                    System.out.println(schedule.getRooms());
                }
                break;
                //Add Appointment
                case 2: {
                    Appointment appointment = createAppointment(scanner); //creates appointment if possible
                    if (appointment == null)
                        break;
                    //Correct amount
                    try {
                        if (!schedule.addAppointment(appointment, appointment.getTime().getDay()))
                            System.out.println("Couldn't add appointment");
                    } catch (InvalidDateException e) {
                        System.out.println(e.getMessage());
                    }
                    println(schedule.getAppointments());
                }
                break;
                //Remove Appointment
                case 3: {
                    Appointment appointment = createAppointment(scanner);
                    if (appointment == null)
                        break;

                    //Correct amount
                    try {
                        if (!schedule.removeAppointment(appointment, appointment.getTime().getDay()))
                            System.out.println("Couldn't remove appointment");
                    } catch (InvalidDateException e) {
                        System.out.println(e.getMessage());
                    }
                    println(schedule.getAppointments());
                }
                break;
                //Change Appointment
                case 4: {
                    Appointment appointment = createAppointment(scanner);
                    if (appointment == null)
                        break;

                    System.out.println("Enter start and end date using format \"dd/MM/yyyy\": startDate,endDate");
                    String[] splitDate = scanner.nextLine().split(",");

                    if (splitDate.length != 2) {
                        System.out.println("Wrong amount of argument, why you troll :)");
                        break;
                    }

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate start = null;
                    LocalDate end = null;

                    try {
                        start = LocalDate.parse(splitDate[0], formatter);
                        end = LocalDate.parse(splitDate[1], formatter);
                    }
                    catch (Exception e) {
                        System.out.println("Wrong date or time format");
                        System.out.println(e.getMessage());
                        continue;
                    }

                    //Correct amount
                    try {
                        if (!schedule.changeAppointment(appointment, appointment.getTime().getDay(), start, end))
                            System.out.println("Couldn't change appointment");
                    } catch (InvalidDateException e) {
                        System.out.println(e.getMessage());
                    }
                    println(schedule.getAppointments());
                }
                break;
                //Search appointment
                case 5: {

                    //Is available
                    System.out.println("Say \"yes\" if searching for available appointments, else say \"0\": ");
                    boolean isAvailable = false;
                    if (scanner.nextLine().toLowerCase().equals("yes"))
                        isAvailable = true;

                    //Room additional
                    System.out.println("Add additional information about room \"key,value\" or write 0 if you want to stop:");
                    Map<String, String> roomAdditional = new HashMap<>();
                    //hashmap room
                    while (true) {
                        String[] additional = scanner.nextLine().split(",");
                        if (additional.length == 1 && additional[0].equals("0"))
                            break;

                        //If has key and value, has items so it means it has config, that key exists
                        if (additional.length == 2) {
                            roomAdditional.put(additional[0], additional[1]);
                        }
                        else
                            System.out.println("Wrong input");
                    }


                    //Room class
                    System.out.println("Enter room name or say \"0\": ");
                    String roomName = scanner.nextLine();
                    Room room = null;
                    if (!roomName.equals("0")) {
                        room = new Room(roomAdditional, roomName);
                    }

                    //Time class
                    System.out.println("Do you want to have startDateTime, endDateTime, day, timeAdditonal. Say \"yes\" or \"0\": ");
                    Time time = null;
                    LocalDate date = null;
                    if (scanner.nextLine().equalsIgnoreCase("yes")) {
                        System.out.println("Write data in this format using dd/mm/yyyy hh:mm: startDateTime,endDateTime,day");
                        String[] split = scanner.nextLine().split(",");
                        if (split.length != 3) {
                            System.out.println("Incorrect amount of data");
                            break;
                        }
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                        LocalDateTime start = LocalDateTime.parse(split[0], formatter);
                        LocalDateTime end = LocalDateTime.parse(split[1], formatter);

                        time = new Time(LocalDate.of(start.getYear(), start.getMonth(), start.getDayOfMonth()), LocalDate.of(end.getYear(), end.getMonth(), end.getDayOfMonth()),
                                LocalTime.of(start.getHour(), start.getMinute()), LocalTime.of(end.getHour(), end.getMinute()));
                        System.out.println("Add additional information about time \"key,value\" or write 0 if you want to stop:");
                        Map<String, String> timeAdditional = new HashMap<>();
                        //hashmap time
                        while (true) {
                            String[] additional = scanner.nextLine().split(",");
                            if (additional.length == 1 && additional[0].equals("0"))
                                break;

                            //If has key and value, has items so it means it has config, that key exists
                            if (additional.length == 2) {
                                timeAdditional.put(additional[0], additional[1]);
                            }
                            else
                                System.out.println("Wrong input");
                        }

                        time.setAdditionally(timeAdditional);
                    }
                    else {
                        System.out.println("Enter specific date, format \"dd/MM/yyyy\": ");

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        try {
                            date = LocalDate.parse(scanner.nextLine(), formatter);
                        }
                        catch (Exception e) {
                            System.out.println(e.getMessage());
                            break;
                        }
                    }


                    List<Appointment> appointmentList = new ArrayList<>();
                    if (date != null && room != null) {
                        try {
                            appointmentList = schedule.search(date, room, isAvailable);
                        } catch (InvalidDateException e) {
                            System.out.println(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    else if (date != null && !roomAdditional.isEmpty()) {
                        try {
                            appointmentList = schedule.search(date, roomAdditional, isAvailable);
                        } catch (InvalidDateException e) {
                            System.out.println(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    else if (date != null) {
                        try {
                            appointmentList = schedule.search(date, isAvailable);
                        } catch (InvalidDateException e) {
                            System.out.println(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    else if (time != null && room != null) {
                        try {
                            appointmentList = schedule.search(time, time.getDay(), room, isAvailable);
                        } catch (InvalidDateException e) {
                            System.out.println(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    else if (time != null && !roomAdditional.isEmpty()) {
                        try {
                            appointmentList = schedule.search(time, time.getDay(), roomAdditional, isAvailable);
                        } catch (InvalidDateException e) {
                            System.out.println(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    else if (time != null) {
                        try {
                            appointmentList = schedule.search(time, time.getDay(), isAvailable);
                        } catch (InvalidDateException e) {
                            System.out.println(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    println(appointmentList);
                }
                break;
                //Import CSV
                case 6: {
                    System.out.println("Enter file path and config path: filePath,configPath");
                    String[] splitFile = scanner.nextLine().split(",");

                    if (splitFile.length != 2) {
                        System.out.println("Please enter TWO files");
                        break;
                    }

                    try {
                        schedule.importCSV(splitFile[0], splitFile[1]);
                    } catch (IOException | InvalidDateException e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }

                    println(schedule.getAppointments());
                }
                break;
                //Import JSON
                case 7: {
                    System.out.println("Enter file path and config path: filePath,configPath");
                    String[] splitFile = scanner.nextLine().split(",");

                    if (splitFile.length != 2) {
                        System.out.println("Please enter TWO files");
                        break;
                    }

                    try {
                        schedule.importJson(splitFile[0], splitFile[1]);
                    } catch (IOException | InvalidDateException e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }

                    println(schedule.getAppointments());
                }
                break;
                //Export CSV
                case 8: {
                    System.out.println("Enter file path and config path: filePath,configPath");
                    String[] splitFile = scanner.nextLine().split(",");

                    if (splitFile.length != 2) {
                        System.out.println("Please enter TWO files");
                        break;
                    }

                    try {
                        schedule.exportCSV(splitFile[0], splitFile[1]);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }

                }
                break;
                //Export JSON
                case 9: {
                    System.out.println("Enter file path: filePath");
                    String[] splitFile = scanner.nextLine().split(",");

                    if (splitFile.length != 1) {
                        System.out.println("Please enter ONE files");
                        break;
                    }

                    try {
                        schedule.exportJson(splitFile[0]);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }

                }
                break;
                //Export PDF
                case 10: {
                    System.out.println("Enter file path and config path: filePath,configPath");
                    String[] splitFile = scanner.nextLine().split(",");

                    if (splitFile.length != 2) {
                        System.out.println("Please enter TWO files");
                        break;
                    }

                    try {
                        schedule.exportPDF(splitFile[0], splitFile[1]);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                }
                break;
                default:
                    System.out.println("Wrong command");
                    break;
            }
            System.out.println("\n\n\n");
        }

    }

    private static void println(List<Appointment> appointmentList) {
        for (Appointment a: appointmentList) {
            System.out.println(a);
        }

        System.out.println("\n\n\n");
    }


    private static Appointment createAppointment(Scanner scanner) {
        System.out.println("Write data in this format using \"dd/mm/yyyy hh:mm\": startDateTime,endDateTime,day,roomName");
        String[] split = scanner.nextLine().split(",");
        if (split.length != 4) {
            System.out.println("Incorrect amount of data");
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime start = null;
        LocalDateTime end = null;

        try {
            start = LocalDateTime.parse(split[0], formatter);
            end = LocalDateTime.parse(split[1], formatter);
        }
        catch (Exception e) {
            System.out.println("Wrong date or time format");
            System.out.println(e.getMessage());
            return null;
        }

        Time time = new Time(LocalDate.of(start.getYear(), start.getMonth(), start.getDayOfMonth()), LocalDate.of(end.getYear(), end.getMonth(), end.getDayOfMonth()),
                LocalTime.of(start.getHour(), start.getMinute()), LocalTime.of(end.getHour(), end.getMinute()));
        Appointment appointment = new Appointment(new Room(split[3]), time);
        System.out.println("Add additional information about time \"key,value\" or write 0 if you want to stop:");
        Map<String, String> map = new HashMap<>();
        //hashmap time
        while (true) {
            String[] additional = scanner.nextLine().split(",");
            if (additional.length == 1 && additional[0].equals("0"))
                break;

            //If has key and value, has items so it means it has config, that key exists
            if (additional.length == 2) {
                map.put(additional[0], additional[1]);
            }
            else
                System.out.println("Wrong input");
        }

        appointment.getTime().setAdditionally(map);

        System.out.println("Add additional information about room \"key,value\" or write 0 if you want to stop:");
        map = new HashMap<>();
        //hashmap room
        while (true) {
            String[] additional = scanner.nextLine().split(",");
            if (additional.length == 1 && additional[0].equals("0"))
                break;

            //If has key and value, has items so it means it has config, that key exists
            if (additional.length == 2) {
                map.put(additional[0], additional[1]);
            }
            else
                System.out.println("Wrong input");
        }
        appointment.getRoom().setAdditionally(map);
        appointment.getTime().setDay(Integer.parseInt(split[2]));
        return appointment;
    }


}
