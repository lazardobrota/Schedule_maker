import exceptions.InvalidDateException;
import implementation.DaySchedule;
import specification.Appointment;
import specification.Room;
import specification.Schedule;
import specification.Time;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //todo odmah prvo unese neki fajl
        //todo treba da sami izaberu range datuma
        Schedule schedule = new DaySchedule(LocalDate.of(2023, 8, 1), LocalDate.of(2024, 1, 1));
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter number 0-11");
            System.out.println("1) Add Room");
            System.out.println("2) Add Appointment");
            System.out.println("3) Remove Appointment");
            System.out.println("4) Change Appointment");
            System.out.println("5) Search Appointment");
            System.out.println("6) Search Available Appointment");
            System.out.println("7) Import CSV file");
            System.out.println("8) Import JSON file");
            System.out.println("9) Export CSV file");
            System.out.println("10) Export JSON file");
            System.out.println("11) Export PDF file");
            System.out.println("0) Exist application");

            System.out.println("\n\n\n");
            int choosen = Integer.parseInt(scanner.nextLine());
            switch (choosen) {
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
                        schedule.addAppointment(appointment, appointment.getTime().getDay());
                    } catch (InvalidDateException e) {
                        System.out.println(e.getMessage());
                    }
                    System.out.println(schedule.getAppointments());
                }
                break;
                //Remove Appointment
                case 3: {
                    Appointment appointment = createAppointment(scanner);
                    if (appointment == null)
                        break;

                    //Correct amount
                    try {
                        schedule.removeAppointment(appointment, appointment.getTime().getDay());
                    } catch (InvalidDateException e) {
                        System.out.println(e.getMessage());
                    }
                    System.out.println(schedule.getAppointments());
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
                    LocalDate start = LocalDate.parse(splitDate[0], formatter);
                    LocalDate end = LocalDate.parse(splitDate[1], formatter);

                    //Correct amount
                    try {
                        schedule.changeAppointment(appointment, appointment.getTime().getDay(), start, end);
                    } catch (InvalidDateException e) {
                        System.out.println(e.getMessage());
                    }
                    System.out.println(schedule.getAppointments());
                }
                break;
                //Search appointment
                case 5: {
                    //TODO
                }
                break;
                //Search Available Appointment
                case 6: {
                    //TODO
                }
                break;
                //Import CSV
                case 7: {
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
                case 8: {
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
                case 9: {
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
                case 10: {
                    //TODO Iz nekog razloga ne napravi fajl iako kaze da je uspeo
                    System.out.println("Enter file path and config path: filePath");
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
                case 11: {
                    System.out.println("Enter file path and config path: filePath");
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
        System.out.println("Write data in this format using dd/mm/yyyy hh:mm: startDateTime,endDateTime,day,roomName");
        String[] split = scanner.nextLine().split(",");
        if (split.length != 4) {
            System.out.println("Incorrect amount of data");
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime start = LocalDateTime.parse(split[0], formatter);
        LocalDateTime end = LocalDateTime.parse(split[1], formatter);

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
