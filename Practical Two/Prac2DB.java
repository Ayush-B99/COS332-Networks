import java.io.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Prac2DB {
    private ArrayList<Appointment> appointments;
    private String filename = "appointments.dat";
    private int nextId = 1;
    
    public Prac2DB() {
        appointments = new ArrayList<>();
        loadFromFile();
    }
    
    // Add an appointment
    public void addAppointment(String date, String time, String person, String details) {
        Appointment app = new Appointment(nextId++, date, time, person, details);
        appointments.add(app);
        saveToFile();
    }
    
    // Search appointments by person
    public ArrayList<Appointment> searchByPerson(String person) {
        ArrayList<Appointment> results = new ArrayList<>();
        for (Appointment app : appointments) {
            if (app.getPerson().toLowerCase().contains(person.toLowerCase())) {
                results.add(app);
            }
        }
        return results;
    }
    
    // Get appointments by specific date
    public ArrayList<Appointment> getAppointmentsByDate(String date) {
        ArrayList<Appointment> results = new ArrayList<>();
        for (Appointment app : appointments) {
            if (app.getDate().equals(date)) {
                results.add(app);
            }
        }
        // Sort by time
        results.sort((a, b) -> a.getTime().compareTo(b.getTime()));
        return results;
    }
    
    // Get appointments for next 7 days (upcoming)
    public ArrayList<Appointment> getUpcomingAppointments() {
        ArrayList<Appointment> results = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        LocalDate weekFromNow = today.plusDays(7);
        
        for (Appointment app : appointments) {
            try {
                LocalDate appDate = LocalDate.parse(app.getDate(), formatter);
                if ((appDate.isEqual(today) || appDate.isAfter(today)) && appDate.isBefore(weekFromNow)) {
                    results.add(app);
                }
            } catch (Exception e) {
                // Skip invalid dates
            }
        }
        
        // Sort by date then time
        results.sort((a, b) -> {
            int dateCompare = a.getDate().compareTo(b.getDate());
            if (dateCompare != 0) return dateCompare;
            return a.getTime().compareTo(b.getTime());
        });
        
        return results;
    }
    
    // Delete an appointment by ID
    public boolean deleteAppointment(int id) {
        for (int i = 0; i < appointments.size(); i++) {
            if (appointments.get(i).getId() == id) {
                appointments.remove(i);
                saveToFile();
                return true;
            }
        }
        return false;
    }
    
    // Modify an appointment
    public boolean modifyAppointment(int id, String date, String time, String person, String details) {
        for (Appointment app : appointments) {
            if (app.getId() == id) {
                // Create new appointment with same ID
                int index = appointments.indexOf(app);
                appointments.set(index, new Appointment(id, date, time, person, details));
                saveToFile();
                return true;
            }
        }
        return false;
    }
    
    // Get appointment by ID
    public Appointment getAppointmentById(int id) {
        for (Appointment app : appointments) {
            if (app.getId() == id) {
                return app;
            }
        }
        return null;
    }
    
    // Get all appointments
    public ArrayList<Appointment> getAllAppointments() {
        return appointments;
    }
    
    // Sort appointments
    public ArrayList<Appointment> getSortedAppointments(String sortBy) {
        ArrayList<Appointment> sorted = new ArrayList<>(appointments);
        
        switch (sortBy.toLowerCase()) {
            case "date":
                sorted.sort((a, b) -> a.getDate().compareTo(b.getDate()));
                break;
            case "person":
                sorted.sort((a, b) -> a.getPerson().compareTo(b.getPerson()));
                break;
            case "time":
                sorted.sort((a, b) -> a.getTime().compareTo(b.getTime()));
                break;
            default:
                // Default sort by date
                sorted.sort((a, b) -> a.getDate().compareTo(b.getDate()));
        }
        
        return sorted;
    }
    
    // Save to file
    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filename))) {
            oos.writeObject(appointments);
        } catch (IOException e) {
            System.err.println("Error saving appointments: " + e.getMessage());
        }
    }
    
    // Load from file
    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        File file = new File(filename);
        if (!file.exists()) {
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filename))) {
            appointments = (ArrayList<Appointment>) ois.readObject();
            
            // Find the highest ID to continue from
            for (Appointment app : appointments) {
                if (app.getId() >= nextId) {
                    nextId = app.getId() + 1;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading appointments: " + e.getMessage());
        }
    }
}
