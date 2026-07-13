import java.time.LocalDateTime;

public class Appointment {
    private String appointmentType; 
    private LocalDateTime dateTime;
    private String notes; 

    public Appointment(String appointmentType, LocalDateTime dateTime, String notes) {
        this.appointmentType = appointmentType;
        this.dateTime = dateTime;
        this.notes = notes;
    }

    public Appointment(String appointmentType, LocalDateTime dateTime) {
        this.appointmentType = appointmentType;
        this.dateTime = dateTime;
        this.notes = ""; 
    }

    public String getAppointmentType() { return appointmentType; }
    public LocalDateTime getDateTime() { return dateTime; }
    public String getNotes() { return notes; }

    @Override
    public String toString() {
        return "Type: " + appointmentType + 
               " | Date & Time: " + dateTime + 
               " | Notes: " + (notes.isBlank() ? "None" : notes);
    }
}