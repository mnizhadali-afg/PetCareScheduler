import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PetCareScheduler {

	private static final ArrayList<Pet> registeredPetsList = new ArrayList<>();
	private static final String FILE_NAME = "PetCareData.txt";
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

	public static void main(String[] args) {
		// Load data on startup
		loadDataFromFile();

		Scanner scanner = new Scanner(System.in);
		boolean running = true;

		while (running) {
			System.out.println("\n--- PetCare Scheduler Menu ---");
			System.out.println("1. Register Pet");
			System.out.println("2. Schedule Appointment");
			System.out.println("3. Display Data");
			System.out.println("4. Generate Reports");
			System.out.println("5. Save & Exit");
			System.out.print("Please enter your choice (1-5): ");

			String choice = scanner.nextLine().strip();

			switch (choice) {
			case "1":
				registerPet(scanner);
				break;
			case "2":
				scheduleAppointment(scanner);
				break;
			case "3":
				displayData(scanner);
				break;
			case "4":
				generateReports(scanner);
				break;
			case "5":
				saveDataToFile();
				running = false;
				break;
			default:
				System.out.println("Invalid choice! Choose 1-5.");
				break;
			}
		}
		scanner.close();
	}

	private static void loadDataFromFile() {
		File dataFile = new File(FILE_NAME);
		if (!dataFile.exists())
			return;

		try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
			String line;
			Pet currentPet = null;
			String id = "", name = "", breed = "", owner = "", contact = "";
			int age = 0;
			LocalDate regDate = null;

			while ((line = reader.readLine()) != null) {
				line = line.strip();
				if (line.isEmpty())
					continue;

				if (line.startsWith("ID:"))
					id = line.substring(3);
				else if (line.startsWith("Name:"))
					name = line.substring(5);
				else if (line.startsWith("Breed:"))
					breed = line.substring(6);
				else if (line.startsWith("Age:"))
					age = Integer.parseInt(line.substring(4));
				else if (line.startsWith("Owner:"))
					owner = line.substring(6);
				else if (line.startsWith("Contact:"))
					contact = line.substring(8);
				else if (line.startsWith("RegDate:"))
					regDate = LocalDate.parse(line.substring(8), dateFormatter);
				else if (line.equals("PET_END")) {
					currentPet = new Pet(id, name, breed, age, owner, contact, regDate);
					registeredPetsList.add(currentPet);
				} else if (line.equals("APPT_START")) {
					String apptType = "";
					LocalDateTime apptDateTime = null;
					String apptNotes = "";

					while ((line = reader.readLine()) != null) {
						line = line.strip();
						if (line.equals("APPT_END"))
							break;
						if (line.startsWith("Type:"))
							apptType = line.substring(5);
						else if (line.startsWith("DateTime:"))
							apptDateTime = LocalDateTime.parse(line.substring(9), dateTimeFormatter);
						else if (line.startsWith("Notes:"))
							apptNotes = line.substring(6);
					}
					if (currentPet != null) {
						currentPet.addAppointment(
								new Appointment(apptType, apptDateTime, apptNotes.equals("None") ? "" : apptNotes));
					}
				}
			}
			System.out.println("Data loaded successfully. (" + registeredPetsList.size() + " records)");
		} catch (Exception e) {
			System.err.println("Error reading initialization file: " + e.getMessage());
		}
	}

	public static void registerPet(Scanner scanner) {
		System.out.println("\n--- Register a New Pet ---");
		String petId = "";
		while (true) {
			System.out.print("Enter Unique Pet ID: ");
			petId = scanner.nextLine().strip();
			if (petId.isEmpty())
				continue;

			boolean isDuplicate = false;
			for (Pet p : registeredPetsList) {
				if (p.getPetId().equalsIgnoreCase(petId)) {
					isDuplicate = true;
					break;
				}
			}
			if (isDuplicate)
				System.out.println("Error: Duplicate ID!");
			else
				break;
		}

		System.out.print("Enter Pet Name: ");
		String name = scanner.nextLine().strip();
		System.out.print("Enter Species/Breed: ");
		String breed = scanner.nextLine().strip();

		int age = -1;
		while (age < 0) {
			System.out.print("Enter Pet Age: ");
			try {
				age = Integer.parseInt(scanner.nextLine().strip());
			} catch (NumberFormatException e) {
				System.out.println("Invalid number!");
			}
		}

		System.out.print("Enter Owner's Name: ");
		String owner = scanner.nextLine().strip();
		System.out.print("Enter Contact Info: ");
		String contact = scanner.nextLine().strip();

		LocalDate registrationDate = null;
		while (registrationDate == null) {
			System.out.print("Enter Reg Date (MM/dd/yyyy) or Enter for current date: ");
			String dateInput = scanner.nextLine().strip();
			if (dateInput.isEmpty())
				registrationDate = LocalDate.now();
			else {
				try {
					registrationDate = LocalDate.parse(dateInput, dateFormatter);
				} catch (DateTimeParseException e) {
					System.out.println("Incorrect format!");
				}
			}
		}

		registeredPetsList.add(new Pet(petId, name, breed, age, owner, contact, registrationDate));
		System.out.println("Success! " + name + " registered.");
	}

	public static void scheduleAppointment(Scanner scanner) {
		System.out.println("\n--- Schedule an Appointment ---");
		System.out.print("Enter Pet ID: ");
		String petId = scanner.nextLine().strip();
		Pet targetPet = null;
		for (Pet p : registeredPetsList) {
			if (p.getPetId().equalsIgnoreCase(petId)) {
				targetPet = p;
				break;
			}
		}
		if (targetPet == null) {
			System.out.println("Pet not found!");
			return;
		}

		System.out.print("Enter type (Vet Visit, Vaccination, Grooming): ");
		String type = scanner.nextLine().strip();

		LocalDateTime apptTime = null;
		while (apptTime == null) {
			System.out.print("Enter date & time (MM/dd/yyyy HH:mm): ");
			try {
				apptTime = LocalDateTime.parse(scanner.nextLine().strip(), dateTimeFormatter);
			} catch (DateTimeParseException e) {
				System.out.println("Incorrect format!");
			}
		}

		System.out.print("Notes (Optional): ");
		String notes = scanner.nextLine().strip();

		targetPet.addAppointment(new Appointment(type, apptTime, notes));
		System.out.println("Success! Appointment scheduled.");
	}

	public static void displayData(Scanner scanner) {
		System.out
				.println("\n--- Display Submenu ---\n1. All Pets\n2. Specific Pet\n3. Upcoming Appts\n4. Past History");
		String opt = scanner.nextLine().strip();
		if (opt.equals("1")) {
			for (Pet p : registeredPetsList)
				System.out.println(p + "\n-----------------");
		} else if (opt.equals("2")) {
			System.out.print("Enter Pet ID: ");
			String id = scanner.nextLine().strip();
			for (Pet p : registeredPetsList) {
				if (p.getPetId().equalsIgnoreCase(id)) {
					System.out.println(p);
					p.getAppointments().forEach(System.out::println);
				}
			}
		} else if (opt.equals("3")) {
			registeredPetsList
					.forEach(p -> p.getAppointments().stream().filter(a -> a.getDateTime().isAfter(LocalDateTime.now()))
							.forEach(a -> System.out.println(p.getName() + " -> " + a)));
		} else if (opt.equals("4")) {
			registeredPetsList.forEach(
					p -> p.getAppointments().stream().filter(a -> a.getDateTime().isBefore(LocalDateTime.now()))
							.forEach(a -> System.out.println(p.getName() + " -> " + a)));
		}
	}

	public static void generateReports(Scanner scanner) {
		System.out.println("\n--- Reports ---\n1. Next Week's Appointments\n2. Overdue Checkups (>6 Mos)");
		String opt = scanner.nextLine().strip();
		if (opt.equals("1")) {
			LocalDateTime nextWeek = LocalDateTime.now().plusDays(7);
			registeredPetsList.forEach(p -> p.getAppointments().stream()
					.filter(a -> a.getDateTime().isAfter(LocalDateTime.now()) && a.getDateTime().isBefore(nextWeek))
					.forEach(a -> System.out.println(p.getName() + " has " + a.getAppointmentType())));
		} else if (opt.equals("2")) {
			for (Pet p : registeredPetsList) {
			    LocalDateTime lastVisit = null;
			    for (Appointment a : p.getAppointments()) {
			        if (a.getDateTime().isBefore(LocalDateTime.now())
			                && (lastVisit == null || a.getDateTime().isAfter(lastVisit))) {
			            lastVisit = a.getDateTime(); // Fixed: assigned the LocalDateTime field
			        }
			    }
			    if (lastVisit != null && ChronoUnit.MONTHS.between(lastVisit, LocalDateTime.now()) >= 6) {
			        System.out.println(p.getName() + " is overdue! Last visit: " 
			                + lastVisit.format(dateTimeFormatter)); // Fixed: formatting lastVisit directly
			    }
			}
		}
	}

	public static void saveDataToFile() {
		try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
			for (Pet pet : registeredPetsList) {
				writer.println("PET_START\nID:" + pet.getPetId() + "\nName:" + pet.getName() + "\nBreed:"
						+ pet.getSpeciesBreed() + "\nAge:" + pet.getAge() + "\nOwner:" + pet.getOwnerName()
						+ "\nContact:" + pet.getContactInfo() + "\nRegDate:"
						+ pet.getRegistrationDate().format(dateFormatter));
				for (Appointment appt : pet.getAppointments()) {
					writer.println("APPT_START\nType:" + appt.getAppointmentType() + "\nDateTime:"
							+ appt.getDateTime().format(dateTimeFormatter) + "\nNotes:"
							+ (appt.getNotes().isBlank() ? "None" : appt.getNotes()) + "\nAPPT_END");
				}
				writer.println("PET_END\n");
			}
			System.out.println("Data saved. Goodbye!");
		} catch (IOException e) {
			System.err.println("Save Error: " + e.getMessage());
		}
	}
}