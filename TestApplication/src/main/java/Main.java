import exceptions.InvalidDateException;
import implementation.DaySchedule;
import specification.Appointment;
import specification.Room;
import specification.Schedule;
import specification.Time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //todo treba da sami izaberu range datuma
        Schedule schedule = new DaySchedule(LocalDate.of(2023, 8, 1), LocalDate.of(2024, 1, 1));
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter number 0-12");
            System.out.println("1) Add Room");
            System.out.println("2) Add Appointment");
            System.out.println("3) Remove Appointment");
            System.out.println("4) Change Appointment");
            System.out.println("5) Change Appointment");
            System.out.println("6) Search Appointment");
            System.out.println("7) Search Available Appointment");
            System.out.println("8) Import CSV file");
            System.out.println("9) Import JSON file");
            System.out.println("10) Export CSV file");
            System.out.println("11) Export JSON file");
            System.out.println("12) Export PDF file");

            System.out.println("\n0) Exist application");

            System.out.println("\n\n\n");
            int choosen = Integer.parseInt(scanner.nextLine());
            switch (choosen) {
                case 0:
                    return; //leave application
                case 1: { //adds room
                    System.out.println("Enter room name to add: ");
                    boolean added = schedule.addRooms(new Room(scanner.nextLine()));
                    if (added)
                        System.out.println("Room added");
                    else
                        System.out.println("This room already exists");
                }
                break;
                case 2: {
                    System.out.println("Write data in this format using dd/mm/yyyy hh:mm: startDateTime,endDateTime,day,roomName");
                    String[] split = scanner.nextLine().split(",");
                    if (split.length != 5) {
                        System.out.println("Incorrect amount of data");
                        break;
                    }
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(split[0]);
                    LocalDateTime start = LocalDateTime.parse(split[1], formatter);
                    LocalDateTime end = LocalDateTime.parse(split[2], formatter);

                    Time time = new Time(LocalDate.of(start.getYear(), start.getMonth(), start.getDayOfMonth()), LocalDate.of(end.getYear(), end.getMonth(), end.getDayOfMonth()),
                            LocalTime.of(start.getHour(), start.getMinute()), LocalTime.of(end.getHour(), end.getMinute()));
                    Appointment appointment = new Appointment(new Room(split[6]), time);
                    System.out.println("Add additional information about time \"key,value\" or write 0 if you want to stop:");
                    Map<String, String> map = new HashMap<>();
                    //hashmap time
                    while (true) {
                        String[] additional = scanner.nextLine().split(",");
                        if (additional.length == 1 && additional[0].equals("0"))
                            break;

                        //If has key and value, has items so it means it has config, that key exists
                        if (additional.length == 2 && schedule.getAppointments().size() > 0 &&
                                schedule.getAppointments().get(0).getTime().getAdditionally().containsKey(additional[0])) {
                            map.put(additional[0], additional[1]);
                        }
                        else
                            System.out.println("Wrong input");
                    }


                    System.out.println("Add additional information about room \"key,value\" or write 0 if you want to stop:");
                    //hashmap room
//                    while (true) {
//                        System.out.println("Add additional information about time:");
//                    }

                    //Correct amount
                    try {
                        schedule.addAppointment(appointment, Integer.parseInt(split[5]));
                    } catch (InvalidDateException e) {
                        System.out.println(e.getMessage());
                    }
                }
                break;
            }
            System.out.println("\n\n\n");
        }
    }
}
