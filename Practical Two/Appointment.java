import java.io.Serializable;

public class Appointment implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String date;     
    private String time;      
    private String person;    
    private String details;  
    
    public Appointment(int id, String date, String time, String person, String details) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.person = person;
        this.details = details;
    }
    
    // Getters
    public int getId() { 
        return id; 
    }

    public String getDate() { 
        return date;
        }

    public String getTime() { 
        return time; 
    }

    public String getPerson() { 
        return person; 
    }

    public String getDetails() { 
        return details; 
    }
    
    @Override
    public String toString() {
        return String.format("[%d] %s at %s with %s - %s", id, date, time, person, details);
    }
}
