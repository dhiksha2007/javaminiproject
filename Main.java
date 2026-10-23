import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        ActivityLogManager activityLog = new ActivityLogManager();
        CapsuleManager capsuleManager = new CapsuleManager(activityLog);

        System.out.println("==============================================");
        System.out.println("   SMART LIBRARY TIME CAPSULE SYSTEM");
        System.out.println("   Today: " + DateUtil.today());
        System.out.println("==============================================");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> createCapsule(capsuleManager);
                case 2 -> capsuleManager.displayAllCapsules();
                case 3 -> openCapsule(capsuleManager);
                case 4 -> capsuleManager.displayUnlockAlerts();
                case 5 -> viewMemberCapsules(capsuleManager);
                case 6 -> activityLog.displayLog();
                case 7 -> showCapsuleDetails(capsuleManager);
                case 8 -> extendLock(capsuleManager);
                case 9 -> {
                    capsuleManager.saveCapsules();
                    System.out.println("\nCapsules saved. Thank you!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n--------------- MAIN MENU --------------------");
        System.out.println("1. Create Capsule");
        System.out.println("2. View All Capsules");
        System.out.println("3. Open Capsule");
        System.out.println("4. Unlock Alerts");
        System.out.println("5. View Member's Capsules");
        System.out.println("6. View Activity Log");
        System.out.println("7. Search Capsule");
        System.out.println("8. Extend Lock Period");
        System.out.println("9. Exit");
        System.out.println("-----------------------------------------------");
    }

    private static void createCapsule(CapsuleManager manager) {
        System.out.println("\n========== CREATE CAPSULE ==========");
        String id = readText("Enter capsule ID: ");
        if (manager.findCapsule(id) != null) {
            System.out.println("A capsule with this ID already exists.");
            return;
        }
        String member = readText("Enter member name: ");
        String title = readText("Enter capsule title: ");
        LocalDate unlockDate = readFutureDate("Enter unlock date (YYYY-MM-DD): ");

        System.out.println("\nSelect capsule type:");
        System.out.println("1. Letter to the future");
        System.out.println("2. Book recommendation");
        int type = readInt("Enter type: ");

        try {
            Capsule capsule;
            LocalDate today = DateUtil.today();
            if (type == 1) {
                String message = readText("Enter your message: ");
                capsule = new LetterCapsule(id, member, title, today, unlockDate, false, message);
            } else if (type == 2) {
                String bookTitle = readText("Enter book title: ");
                String author = readText("Enter author: ");
                System.out.print("Enter a note for your future self (optional): ");
                String note = scanner.nextLine().trim();
                capsule = new BookCapsule(id, member, title, today, unlockDate, false, bookTitle, author, note);
            } else {
                System.out.println("Invalid capsule type.");
                return;
            }

            if (manager.addCapsule(capsule))
                System.out.println("Capsule sealed successfully. It unlocks on " + unlockDate + ".");
            else
                System.out.println("A capsule with this ID already exists.");
        } catch (IllegalArgumentException e) {
            System.out.println("Could not create capsule: " + e.getMessage());
        }
    }

    private static void openCapsule(CapsuleManager manager) {
        System.out.println("\n========== OPEN CAPSULE ==========");
        String id = readText("Enter capsule ID: ");
        Capsule capsule = manager.openCapsule(id);
        if (capsule != null) {
            System.out.println("\nCapsule opened successfully!");
            System.out.println("Title : " + capsule.getTitle());
            System.out.println(capsule.revealContent());
        }
    }

    private static void viewMemberCapsules(CapsuleManager manager) {
        System.out.println("\n========== MEMBER'S CAPSULES ==========");
        manager.displayByMember(readText("Enter member name: "));
    }

    private static void extendLock(CapsuleManager manager) {
        System.out.println("\n========== EXTEND LOCK PERIOD ==========");
        String id = readText("Enter capsule ID: ");
        Capsule capsule = manager.findCapsule(id);
        if (capsule == null) {
            System.out.println("Capsule not found.");
            return;
        }
        System.out.println("Current unlock date: " + capsule.getUnlockOn());
        LocalDate newDate = readDate("Enter new unlock date (YYYY-MM-DD): ");
        if (manager.extendLock(id, newDate))
            System.out.println("Lock period extended. New unlock date: " + newDate);
    }

    private static void showCapsuleDetails(CapsuleManager manager) {
        System.out.println("\n========== SEARCH CAPSULE ==========");
        Capsule capsule = manager.findCapsule(readText("Enter capsule ID: "));
        if (capsule == null) {
            System.out.println("Capsule not found.");
            return;
        }
        System.out.println("\nCapsule ID    : " + capsule.getId());
        System.out.println("Member        : " + capsule.getOwner());
        System.out.println("Title         : " + capsule.getTitle());
        System.out.println("Capsule Type  : " + capsule.getType());
        System.out.println("Created On    : " + capsule.getCreatedOn());
        System.out.println("Unlock Date   : " + capsule.getUnlockOn());
        System.out.println("Status        : " + capsule.getStatus());
        if (capsule.isLocked()) System.out.println("Days Remaining: " + capsule.daysToUnlock());
        if (capsule.isOpened()) System.out.println("This capsule has already been opened.");
    }

    private static int readInt(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private static String readText(String message) {
        while (true) {
            System.out.print(message);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("This field cannot be empty.");
        }
    }

    private static LocalDate readFutureDate(String message) {
        while (true) {
            LocalDate date = readDate(message);
            if (date.isAfter(DateUtil.today())) return date;
            System.out.println("Unlock date must be after today (" + DateUtil.today() + ").");
        }
    }

    private static LocalDate readDate(String message) {
        while (true) {
            try {
                System.out.print(message);
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Use YYYY-MM-DD, for example 2027-01-31.");
            }
        }
    }
}
