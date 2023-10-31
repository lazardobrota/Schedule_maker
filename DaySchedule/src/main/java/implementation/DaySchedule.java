package implementation;

import exceptions.InvalidDateException;
import specification.Appointment;
import specification.Room;
import specification.Schedule;
import specification.Time;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DaySchedule extends Schedule {

    public DaySchedule(LocalDate startDate, LocalDate endDate) {
        super(startDate, endDate);
    }

    @Override
    public void initialization() {

    }

    @Override
    public boolean addRooms(Room room) {
        return this.getRooms().add(room); // returns true if element doesnt exist so its added to the list
    }

    @Override
    public boolean addAppointment(Appointment appointment, int day) throws InvalidDateException {
        return true;
    }

    @Override
    public boolean removeAppointment(Appointment appointment, int day) throws InvalidDateException{
        return true;
    }

    @Override
    public boolean changeAppointment(Appointment oldAppoint, int day, Appointment newAppoint) throws InvalidDateException {
        return false;
    }



    @Override
    public List<Appointment> search(LocalDate date, Time time, boolean isAvailable) {
        return null;
    }

    @Override
    public List<Appointment> search(LocalDate startDate, LocalDate endDate, int day, Time time, boolean isAvailable) {
        return null;
    }

    @Override
    public List<Appointment> search(LocalDate date, Time time, Room room, boolean isAvailable) {
        return null;
    }

    @Override
    public List<Appointment> search(LocalDate startDate, LocalDate endDate, int day, Time time, Room room, boolean isAvailable) {
        return null;
    }


}
