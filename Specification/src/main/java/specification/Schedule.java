package specification;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import exceptions.InvalidDateException;
import importexport.ConfigMapping;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import specification.serializer.MyAppointmentSerializer;

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Getter
@Setter
public abstract class Schedule {

    //Begging and ending date of schedule
    private LocalDate startDate;
    private LocalDate endDate;

    //TODO ADD startTime and EndTime

    private HashSet<Room> rooms;//hashSet so there is only one of every class

    private List<Appointment> appointments; //every index represents one row
    private List<LocalDate> exclusiveDays; // Working Sundays
    private List<LocalDate> notWorkingDays; // doesn't include Sunday and Saturday

    public Schedule(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;

        rooms = new HashSet<>();
        appointments = new ArrayList<>();
        exclusiveDays = new ArrayList<>();
        notWorkingDays = new ArrayList<>();
    }

    //Add days to startDate to be on that specific date
    protected LocalDate findDateWithDay(LocalDate startDate, int day) {
        day %= 7; //it has 7 days in the week

        if (day <= 0)
            day += 7; // 0 Sunday, -1 Saturday,...

        //How many days between startDate and day we want, it only works if day is ahead then startDate
        int test = startDate.getDayOfWeek().getValue();
        DayOfWeek hello = startDate.getDayOfWeek();
        int addDays = day - startDate.getDayOfWeek().getValue();

        //If startDate is ahead then go backwards to the day given and add 7 day to go to the next week of that day
        if (addDays < 0)
            addDays += 7;

        return startDate.plusDays(addDays);
    }

    //Takes range of time and makes more one day times
    protected List<Time> makeTimes(Time time, int day) throws InvalidDateException{
        List<Time> times = new ArrayList<>();

        LocalDate date = findDateWithDay(time.getStartDate(), day);
        int weeks = weeksBetween(date, time.getEndDate()); //throws exception

        for (int i = 0; i <= weeks; i++) {
            Time t = new Time(time);
            t.setStartDate(date);
            t.setEndDate(date);

            times.add(t);

            date = date.plusDays(7);
        }

        return times;
    }

    protected boolean compareAdditional(Map<String, String> map, Map<String, String> map2) {
        for (Map.Entry<String, String> set : map2.entrySet()) {
            //If our element doesn't have that key, or they are not the same, return false
            if (!map.containsKey(set.getKey()) || !map.get(set.getKey()).equals(set.getValue()))
                return false;
        }
        return true;
    }

    protected boolean compareTime(Time time, Time time2) {
        //Different with overridden equals method
        if (!time.equals(time2))
            return false;

        //Doesn't look for anything in hashMap
        if (time2.getAdditionally() == null)
            return true;

        //Looking for additional that doesn't exist at all
        if (time.getAdditionally() == null)
            return false;

        return compareAdditional(time.getAdditionally(), time2.getAdditionally());
    }

    protected boolean compareRoom(Room room, Room room2) {
        //Different with overridden equals method
        if (!room.equals(room2))
            return false;

        //Doesn't look for anything in hashMap
        if (room2.getAdditionally() == null)
            return true;

        //Looking for additional that doesn't exist at all
        if (room.getAdditionally() == null)
            return false;

        return compareAdditional(room.getAdditionally(), room2.getAdditionally());
    }

    protected int weeksBetween(LocalDate startDate, LocalDate endDate) throws InvalidDateException{
        int weeksBetween = (int) ChronoUnit.WEEKS.between(startDate, endDate);

        //Check rules

        //End date is before start date
        if (weeksBetween < 0 || endDate.isBefore(startDate)) {
            throw new InvalidDateException("Invalid startDate: " + startDate + " and endDate: "+ endDate);
        }

        return weeksBetween;
    }

    /**
     * Takes json file and its config and maps everything accordingly to class Objects Room and Time
     * @param filePath Path to file that will be imported
     * @param configPath Path to config
     * @return boolean, true if file has been imported
     * @throws IOException Something went wrong with import of file
     * @throws InvalidDateException Some dates in file are invalid
     */
    public boolean importJson(String filePath, String configPath)  throws IOException, InvalidDateException{
        loadJson(filePath, configPath);
        return true;
    }

    /**
     * Makes file with given path that will have exported data in json format
     * @param path Path to file that will be exported to
     * @return boolean, true if export went well
     * @throws IOException Something went wrong with export to file
     */
    public boolean exportJson(String path) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.registerModule(new JavaTimeModule()); // LocaleDateTime needs this

        SimpleModule module = new SimpleModule();
        module.addSerializer(Appointment.class, new MyAppointmentSerializer());
        objectMapper.registerModule(module);
        objectMapper.writeValue(new File(path), this.getAppointments());
        return true;
    }


    private void loadJson(String filePath, String configPath) throws IOException, InvalidDateException{

        //Makes index, custom name and original name
        List<ConfigMapping> columnMappings = readConfig(configPath); //makes config in to list, every element is one row
        Map<Integer, String> mappings = new HashMap<>(); //sorted map with indexes
        for(ConfigMapping configMapping : columnMappings) {
            mappings.put(configMapping.getIndex(), configMapping.getOriginal()); //sets index and original
        }
        Collections.sort(columnMappings); //sort by indexes

        FileReader fileReader = new FileReader(filePath);
        ObjectMapper objectMapper = new ObjectMapper();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(mappings.get(-1));

        //Turn blocks of json info into rows
        List<JsonNode> jsonBlocks = objectMapper.readValue(fileReader, new TypeReference<List<JsonNode>>(){});

        for (JsonNode jsonNode : jsonBlocks) {
            Appointment appointment = new Appointment(new Room(), new Time());

            for (ConfigMapping configMapping : columnMappings) {
                int columnIndex = configMapping.getIndex();

                if (columnIndex == -1) continue;

                String columnCustomName = configMapping.getCustom();
                switch (mappings.get(columnIndex)) {
                    case "roomName": //Room name
                        appointment.getRoom().setRoomName(jsonNode.get(columnCustomName).asText());
                        break;
                    case "roomAdditional": //hashmap of room
                        appointment.getRoom().getAdditionally().put(columnCustomName, jsonNode.get(columnCustomName).asText());
                        break;
                    case "start": //sets startDate and startTime
                        LocalDateTime startDateTime = LocalDateTime.parse(jsonNode.get(columnCustomName).asText(), formatter);
                        appointment.getTime().setStartDate(LocalDate.of(startDateTime.getYear(), startDateTime.getMonth(), startDateTime.getDayOfMonth()));
                        appointment.getTime().setStartTime(LocalTime.of(startDateTime.getHour(), startDateTime.getMinute()));
                        break;
                    case "end": //sets endDate and endTime
                        LocalDateTime endDateTime = LocalDateTime.parse(jsonNode.get(columnCustomName).asText(), formatter);
                        appointment.getTime().setEndDate(LocalDate.of(endDateTime.getYear(), endDateTime.getMonth(), endDateTime.getDayOfMonth()));
                        appointment.getTime().setEndTime(LocalTime.of(endDateTime.getHour(), endDateTime.getMinute()));
                        break;
                    case "day": //set day
                        appointment.getTime().setDay(Integer.parseInt(jsonNode.get(columnCustomName).asText()));
                        break;
                    case "timeAdditional": //hashmap of time
                        appointment.getTime().getAdditionally().put(columnCustomName, jsonNode.get(columnCustomName).asText());
                        break;
                }
            }

            this.addAppointment(appointment, appointment.getTime().getDay());
        }
    }

    /**
     * Takes csv file and its config and maps everything accordingly to class Objects Room and Time
     * @param filePath Path to file that will be imported
     * @param configPath Path to config
     * @return boolean, true if file has been imported
     * @throws IOException Something went wrong with import of file
     * @throws InvalidDateException Some dates in file are invalid
     */
    public boolean importCSV(String filePath, String configPath) throws IOException, InvalidDateException {
        loadCSV(filePath, configPath);
        return true;
    }


    private void loadCSV(String filePath, String configPath) throws IOException, InvalidDateException {
        List<ConfigMapping> columnMappings = readConfig(configPath); //makes config in to list, every element is one row
        Map<Integer, String> mappings = new HashMap<>(); //map indexes
        for(ConfigMapping configMapping : columnMappings) {
            mappings.put(configMapping.getIndex(), configMapping.getOriginal()); //sets index and original
        }

        Collections.sort(columnMappings); //sort by indexes
        FileReader fileReader = new FileReader(filePath);
        CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(fileReader);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(mappings.get(-1)); //gets how date should be formatted since its index is always -1

        //Goes throw csv file
        for (CSVRecord record : parser) {
            Appointment appointment = new Appointment(new Room(), new Time()); //new appointment for that row

            //Goes throw all index
            for (ConfigMapping configMapping : columnMappings) {
                int columnIndex = configMapping.getIndex();

                if(columnIndex == -1) continue;

                String columnCustomName = configMapping.getCustom(); //save custom name for additional if needed
                switch (mappings.get(columnIndex)) {
                    case "roomName": //Room name
                        appointment.getRoom().setRoomName(record.get(columnIndex));
                        break;
                    case "roomAdditional": //hashmap of room
                        appointment.getRoom().getAdditionally().put(columnCustomName, record.get(columnIndex));
                        break;
                    case "start": //sets startDate and startTime
                        LocalDateTime startDateTime = LocalDateTime.parse(record.get(columnIndex), formatter);
                        appointment.getTime().setStartDate(LocalDate.of(startDateTime.getYear(), startDateTime.getMonth(), startDateTime.getDayOfMonth()));
                        appointment.getTime().setStartTime(LocalTime.of(startDateTime.getHour(), startDateTime.getMinute()));
                        break;
                    case "end": //sets endDate and endTime
                        LocalDateTime endDateTime = LocalDateTime.parse(record.get(columnIndex), formatter);
                        appointment.getTime().setEndDate(LocalDate.of(endDateTime.getYear(), endDateTime.getMonth(), endDateTime.getDayOfMonth()));
                        appointment.getTime().setEndTime(LocalTime.of(endDateTime.getHour(), endDateTime.getMinute()));
                        break;
                    case "day": //set day
                        appointment.getTime().setDay(Integer.parseInt(record.get(columnIndex)));
                        break;
                    case "timeAdditional": //hashmap of time
                        appointment.getTime().getAdditionally().put(columnCustomName, record.get(columnIndex));
                        break;
                }
            }

            this.addAppointment(appointment, appointment.getTime().getDay());
        }
    }

    private static List<ConfigMapping>  readConfig(String filePath) throws FileNotFoundException {
        List<ConfigMapping> mappings = new ArrayList<>();

        File file = new File(filePath); //takes file
        Scanner scanner = new Scanner(file); //reads file

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] splitLine = line.split(" ", 3); //split three-way

            mappings.add(new ConfigMapping(Integer.parseInt(splitLine[0]), splitLine[1], splitLine[2]));
        }

        scanner.close();


        return mappings;
    }

    /**
     * Makes file with given path that will have exported data in csv format
     * @param path Path to file that will be exported to
     * @param configPath Path to config
     * @return boolean, true if export went well
     * @throws IOException Something went wrong with export to file
     */
    public boolean exportCSV(String path, String configPath) throws IOException{
        writeData(path, configPath);
        return true;
    }


    private void writeData(String path,  String configPath) throws IOException{
        // Create a FileWriter and CSVPrinter
        FileWriter fileWriter = new FileWriter(path);
        CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);

        List<ConfigMapping> columnMappings = readConfig(configPath); //sets index, custom and original
        Map<Integer, String> mappings = new HashMap<>(); //sortirana mapa sa indeksima
        for(ConfigMapping configMapping : columnMappings) {
            mappings.put(configMapping.getIndex(), configMapping.getOriginal()); //sets index and original
        }
        Collections.sort(columnMappings); //sort by indexes

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(mappings.get(-1));

        //It needs to set it same index range as import

        List<List<String>> appointmentList = new ArrayList<>();
        //Goes throw appointments
        for (Appointment appoint : this.getAppointments()) {
            List<String> save = new ArrayList<>();
            //Goes throw all index
            for (ConfigMapping entry : columnMappings) {
                int columnIndex = entry.getIndex();

                if(columnIndex == -1) continue;

                String columnName = entry.getCustom(); //save custom name for additional if needed
                switch (mappings.get(columnIndex)) {
                    case "roomName": //Room name
                        save.add(appoint.getRoom().getRoomName());
                        break;
                    case "roomAdditional": //hashmap of room
                        save.add(appoint.getRoom().getAdditionally().get(columnName));
                        break;
                    case "start": //add startDate and startTime
                        LocalDateTime startDateTime = LocalDateTime.of(appoint.getTime().getStartDate(), appoint.getTime().getStartTime());
                        save.add(startDateTime.format(formatter));
                        break;
                    case "end": //add endDate and endTime
                        LocalDateTime endDateTime = LocalDateTime.of(appoint.getTime().getEndDate(), appoint.getTime().getEndTime());
                        save.add(endDateTime.format(formatter));
                        break;
                    case "day": //add day
                        save.add(String.valueOf(appoint.getTime().getDay()));
                        break;
                    case "timeAdditional": //hashmap of time
                        save.add(appoint.getTime().getAdditionally().get(columnName));
                        break;
                }
            }

            appointmentList.add(save);
        }

        //Print every row in list
        for (List<String> save : appointmentList) {
            csvPrinter.printRecord(save);
        }

        csvPrinter.close();
        fileWriter.close();
    }

    /**
     * Makes file with given path that will have exported data in pdf format
     * @param path Path to file that will be exported to
     * @param configPath Path to config
     * @return boolean, true if export went well
     * @throws IOException Something went wrong with export to file
     */
    public boolean exportPDF(String path, String configPath) throws IOException {
        List<ConfigMapping> columnMappings = readConfig(configPath); //sets index, custom and original
        Map<Integer, String> mappings = new HashMap<>(); //sortirana mapa sa indeksima
        for(ConfigMapping configMapping : columnMappings) {
            mappings.put(configMapping.getIndex(), configMapping.getOriginal()); //sets index and original
        }
        Collections.sort(columnMappings); //sort by indexes

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(mappings.get(-1));

        PDDocument document = new PDDocument(); //new document
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        //Text
        contentStream.beginText();

        //For title
        contentStream.setFont(PDType1Font.TIMES_BOLD, 20);
        contentStream.newLineAtOffset(25, 700);
        contentStream.showText("Available and taken appointments");

        //Normaln text
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 14);
        contentStream.newLineAtOffset(0, -20);

        StringBuilder headers = new StringBuilder();
        for (ConfigMapping configMapping : columnMappings) {
            if (configMapping.getIndex() == -1) continue;
            headers.append(configMapping.getCustom() + ", ");
        }
        headers = new StringBuilder(headers.toString().substring(0 , headers.length() - 2)); // remove last ", "
        contentStream.showText(headers.toString());
        contentStream.newLineAtOffset(0, -15);

        for (Appointment appoint : this.getAppointments()) {
            StringBuilder save = new StringBuilder();
            for (ConfigMapping configMapping : columnMappings) {
                int columnIndex = configMapping.getIndex();

                if(columnIndex == -1) continue;

                String columnName = configMapping.getCustom(); //save custom name for additional if needed
                switch (mappings.get(columnIndex)) {
                    case "roomName": //Room name
                        save.append(appoint.getRoom().getRoomName()).append(", ");
                        break;
                    case "roomAdditional": //hashmap of room
                        save.append(appoint.getRoom().getAdditionally().get(columnName)).append(", ");
                        break;
                    case "start": //add startDate and startTime
                        LocalDateTime startDateTime = LocalDateTime.of(appoint.getTime().getStartDate(), appoint.getTime().getStartTime());
                        save.append(startDateTime.format(formatter)).append(", ");
                        break;
                    case "end": //add endDate and endTime
                        LocalDateTime endDateTime = LocalDateTime.of(appoint.getTime().getEndDate(), appoint.getTime().getEndTime());
                        save.append(endDateTime.format(formatter)).append(", ");
                        break;
                    case "day": //add day
                        save.append(String.valueOf(appoint.getTime().getDay())).append(", ");
                        break;
                    case "timeAdditional": //hashmap of time
                        save.append(appoint.getTime().getAdditionally().get(columnName)).append(", ");
                        break;
                }
            }

            contentStream.showText(save.toString().substring(0, save.length() - 2));
            contentStream.newLineAtOffset(0, -15);
        }



        contentStream.endText();
        contentStream.close();

        document.save(path);
        document.close();
        return true;
    }

    //TODO Da li je bolje imati exception ili boolean
    //Call this function to check parametars of dates
    protected boolean isValidDate(LocalDate date) throws InvalidDateException {
        //For weekend
        //For Sunday and isn't an exclusive day
        if (date.getDayOfWeek().getValue() == 7 && !exclusiveDays.contains(date)) {
            return false;
            //throw new InvalidDateException("Sunday(7), isn't working date");
        }
        //If it's Saturday and isn't an exclusive day
        if (date.getDayOfWeek().getValue() == 6 && !exclusiveDays.contains(date)) {
            return false;
            //throw new InvalidDateException("Saturday(6), isn't working date");
        }

        //Isn't between start and end date of table
        if (date.isBefore(this.getStartDate()) || date.isAfter(this.getEndDate())) {
            return false;
            //throw new InvalidDateException("Put date inside range of table");
        }

        return true;
    }

    //TODO Fix documentation arguments
    /**
     * initializes empty table and fills list of all Exclusive days(Working Sundays) and Not working days (doesn't include Sunday and Saturday)
     */
    public  void initialization(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;

        rooms = new HashSet<>();
        appointments = new ArrayList<>();
        exclusiveDays = new ArrayList<>();
        notWorkingDays = new ArrayList<>();
    }

    /**
     * Adds new room to HashSet of all rooms
     * @param room
     * @return boolean that returns TRUE if room has been added
     */
    public boolean addRooms(Room room) {
        return this.getRooms().add(room); // returns true if element doesn't exist, so it's added to the list
    }

    /**
     * Add appointment to List if there isn't already that appointment in there
     * @param appointment Has room and time information about appointment that needs to be added
     * @param day On what day should this be
     * @return Boolean that returns TRUE if appointment has been added
     * @throws InvalidDateException If date isn't valid
     */
    public abstract boolean addAppointment(Appointment appointment, int day) throws InvalidDateException;


    /**
     * Removes appointment from list if it exists
     * @param appointment Has room and time information about appointment that needs to be removed
     * @param day On what day should this be
     * @return Boolean that returns TRUE if appointment has been removed
     * @throws InvalidDateException If date isn't valid
     */
    public abstract boolean removeAppointment(Appointment appointment, int day) throws InvalidDateException;

    //TODO Da li treba dva appointmenta ili drugacije provera
    /**
     * Check if old appointment exist and sets date to new date if it isnt already taken
     * @param oldAppoint needs to be removed
     * @return boolean return true if it can remove old one and add new one
     */
    /**
     * Changes old appointments date to new one
     * @param oldAppoint Has room and time information about appointment that needs to be changed
     * @param day On what day should this be
     * @param startDate New start date for old Appointment
     * @param endDate  Old start date for old Appointment
     * @return Boolean return true if it can remove old one and add new one
     * @throws InvalidDateException If date isn't valid
     */
    public abstract boolean changeAppointment(Appointment oldAppoint, int day, LocalDate startDate, LocalDate endDate) throws InvalidDateException;

    /**
     * Searches for Appointment in table with given information
     * @param time Range od dates, times and additional information
     * @param day search for this day
     * @param isAvailable Does user want available appointments or list of all appointments
     * @return List of appointment that satisfy conditions
     * @throws InvalidDateException If date isn't valid
     */
    public abstract List<Appointment> search(Time time, int day, boolean isAvailable) throws InvalidDateException;

    /**
     * Searches for Appointment in table with given information
     * @param time Range od dates, times and additional information
     * @param day Search for this day
     * @param room Information about room that needs to be found
     * @param isAvailable Does user want available appointments or list of all appointments
     * @return List of appointment that satisfy conditions
     * @throws InvalidDateException If date isn't valid
     */
    public abstract List<Appointment> search(Time time, int day, Room room, boolean isAvailable) throws InvalidDateException;

    /**
     * Searches for Appointment in table with given information
     * @param time Range od dates, times and additional information
     * @param day Search for this day
     * @param roomAdditionally Additional information about the rooms without its name
     * @param isAvailable Does user want available appointments or list of all appointments
     * @return List of appointment that satisfy conditions
     * @throws InvalidDateException If date isn't valid
     */
    public abstract List<Appointment> search(Time time, int day, Map<String, String> roomAdditionally, boolean isAvailable) throws InvalidDateException;

    /**
     * Searches for Appointment in table with given information
     * @param date Search for this day
     * @param isAvailable Does user want available appointments or list of all appointments
     * @return List of appointment that satisfy conditions
     * @throws InvalidDateException If date isn't valid
     */
    public abstract List<Appointment> search(LocalDate date, boolean isAvailable) throws InvalidDateException;

    /**
     * Searches for Appointment in table with given information
     * @param date Search for this day
     * @param room Information about room that needs to be found
     * @param isAvailable Does user want available appointments or list of all appointments
     * @return List of appointment that satisfy conditions
     * @throws InvalidDateException If date isn't valid
     */
    public abstract List<Appointment> search(LocalDate date, Room room, boolean isAvailable) throws InvalidDateException;

    /**
     * Searches for Appointment in table with given information
     * @param date Search for this day
     * @param roomAdditionally Additional information about the rooms without its name
     * @param isAvailable Does user want available appointments or list of all appointments
     * @return List of appointment that satisfy conditions
     * @throws InvalidDateException If date isn't valid
     */
    public abstract List<Appointment> search(LocalDate date, Map<String, String> roomAdditionally, boolean isAvailable) throws InvalidDateException;

    //TODO needs to be private, its public because of testing and it needs to be in specification
    //TODO Na osnovu prosledjenog appointmenta, nalazi u listi appointments sve to tome odgovara i sacuva, zatim nad tim radi convertToAvailable!!!!!
    //From Appointments make list of all available appointments
    protected List<Appointment> convertToAvailable(List<Appointment> appointments) {
        List<Appointment> availables = new ArrayList<>();
        LocalTime startTime = LocalTime.of(0, 0);
        LocalDate startDate = this.getStartDate();
        int i = 0;
        for (; i < appointments.size(); i++) {
            //Make new available appointment
            availables.addAll(makeAvailableAppointment(appointments.get(i), startTime, appointments.get(i).getTime().getStartTime(), startDate, appointments.get(i).getTime().getStartDate()));

            startTime = appointments.get(i).getTime().getEndTime();//save end time for beginning of next available appointment
            startDate = appointments.get(i).getTime().getEndDate();

            //New Room so end this room and start from beginning
            if (i + 1 != appointments.size() && !appointments.get(i).getRoom().equals(appointments.get(i + 1).getRoom())) {
                //End room
                availables.addAll(makeAvailableAppointment(appointments.get(i), startTime, LocalTime.of(23, 59), startDate, this.getEndDate()));

                //Start new Room
                startDate = this.getStartDate();
                startTime = LocalTime.of(0, 0);

            }
        }

        //End room
        i--;
        availables.addAll(makeAvailableAppointment(appointments.get(i), startTime, LocalTime.of(23, 59), startDate, this.getEndDate()));

        return availables;
    }

    /**
     * Takes arguments and makes new available Appointment
     * @param appointment real Appointment
     * @param startTime is end time of appointment before this one
     * @param endTime is start time of current appointment
     * @param startDate is end date of appointment before this one
     * @param endDate is start date of current appointment
     * @return new available Appointment between last and current appointment
     */
    protected List<Appointment> makeAvailableAppointment(Appointment appointment, LocalTime startTime, LocalTime endTime, LocalDate startDate, LocalDate endDate) {
        Appointment available = new Appointment(new Room(appointment.getRoom()), new Time(appointment.getTime()));
        available.getTime().setStartTime(startTime); //set start time from last appointments end time
        available.getTime().setEndTime(endTime); //set last possible time

        available.getTime().setStartDate(startDate); //set start date from last appointments end time
        available.getTime().setEndDate(endDate); //set last date on schedule
        available.getTime().setDay(appointment.getTime().getStartDate().getDayOfWeek().getValue()); //sets day

        return makeOneFromMultiDay(available);
    }

    protected List<Appointment> makeOneFromMultiDay(Appointment appointment) {
        List<Appointment> appointments = new ArrayList<>();

        Appointment tmp = new Appointment(new Room(appointment.getRoom()), new Time(appointment.getTime()));

        boolean flag = false;
        while (!tmp.getTime().getStartDate().equals(tmp.getTime().getEndDate())) {
            tmp.getTime().setDay(tmp.getTime().getStartDate().getDayOfWeek().getValue());
            Appointment a = new Appointment(new Room(tmp.getRoom()), new Time(tmp.getTime()));

            a.getTime().setStartTime(LocalTime.of(0, 0));//todo should use starttime
            a.getTime().setEndTime(LocalTime.of(23, 59));//todo should use endtime
            a.getTime().setEndDate(a.getTime().getStartDate());
            //First time start from apponitments start time
            if (!flag) {
                a.getTime().setStartTime(tmp.getTime().getStartTime());
                flag = true;
            }
            appointments.add(a);

            //Go to next day
            tmp.getTime().setStartDate(tmp.getTime().getStartDate().plusDays(1));
        }

        tmp.getTime().setDay(tmp.getTime().getStartDate().getDayOfWeek().getValue());
        //If flag is true it means there were days between dates so last day start from 00:00
        if (flag)
            tmp.getTime().setStartTime(LocalTime.of(0, 0));
        appointments.add(tmp);//And last day
        return appointments;
    }
}
