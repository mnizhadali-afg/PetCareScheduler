import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Pet {
    private String petId;
    private String name;
    private String speciesBreed;
    private int age;
    private String ownerName;
    private String contactInfo;
    private LocalDate registrationDate;
    private List<Appointment> appointments;

    public Pet(String petId, String name, String speciesBreed, int age, 
               String ownerName, String contactInfo, LocalDate registrationDate) {
        this.petId = petId;
        this.name = name;
        this.speciesBreed = speciesBreed;
        this.age = age;
        this.ownerName = ownerName;
        this.contactInfo = contactInfo;
        this.registrationDate = registrationDate;
        this.appointments = new ArrayList<>();
    }

    public String getPetId() { return petId; }
    public String getName() { return name; }
    public String getSpeciesBreed() { return speciesBreed; }
    public int getAge() { return age; }
    public String getOwnerName() { return ownerName; }
    public String getContactInfo() { return contactInfo; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public List<Appointment> getAppointments() { return appointments; }

    public void addAppointment(Appointment appointment) {
        if (appointment != null) {
            this.appointments.add(appointment);
        }
    }

    @Override
    public String toString() {
        return "Pet ID: " + petId + 
               "\nName: " + name + 
               "\nSpecies/Breed: " + speciesBreed + 
               "\nAge: " + age + 
               "\nOwner Name: " + ownerName + 
               "\nContact Info: " + contactInfo + 
               "\nRegistration Date: " + registrationDate + 
               "\nNumber of Appointments: " + appointments.size();
    }
}