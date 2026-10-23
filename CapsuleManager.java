import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CapsuleManager {
    private static final String CAPSULE_FILE = "capsules.csv";
    private static final int ALERT_DAYS = 30;

    private final ArrayList<Capsule> capsules = new ArrayList<>();
    private final ActivityLogManager activityLog;

    public CapsuleManager(ActivityLogManager activityLog) {
        this.activityLog = activityLog;
        loadCapsules();
    }

    public boolean addCapsule(Capsule capsule) {
        if (findCapsule(capsule.getId()) != null) return false;
        capsules.add(capsule);
        activityLog.record(capsule.getId(), capsule.getOwner(), "CREATED", "Locked until " + capsule.getUnlockOn());
        saveCapsules();
        return true;
    }

    public Capsule findCapsule(String id) {
        for (Capsule capsule : capsules)
            if (capsule.getId().equalsIgnoreCase(id.trim())) return capsule;
        return null;
    }

    public List<Capsule> getCapsules() { return new ArrayList<>(capsules); }

    public Capsule openCapsule(String id) {
        Capsule capsule = findCapsule(id);
        if (capsule == null) {
            System.out.println("Capsule not found.");
            return null;
        }
        try {
            capsule.open();
            activityLog.record(capsule.getId(), capsule.getOwner(), "OPENED", "Capsule opened");
            saveCapsules();
            return capsule;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            activityLog.record(capsule.getId(), capsule.getOwner(), "OPEN_DENIED", capsule.daysToUnlock() + " day(s) remaining");
            return null;
        }
    }

    public boolean extendLock(String id, LocalDate newDate) {
        Capsule capsule = findCapsule(id);
        if (capsule == null) {
            System.out.println("Capsule not found.");
            return false;
        }
        try {
            LocalDate oldDate = capsule.getUnlockOn();
            capsule.extendLock(newDate);
            activityLog.record(capsule.getId(), capsule.getOwner(), "EXTENDED", oldDate + " to " + newDate);
            saveCapsules();
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void displayAllCapsules() {
        if (capsules.isEmpty()) {
            System.out.println("\nNo capsules available.");
            return;
        }
        printTableHeader("CAPSULE LIBRARY");
        for (Capsule capsule : capsules) System.out.println(capsule);
    }

    public void displayByMember(String member) {
        boolean found = false;
        for (Capsule capsule : capsules) {
            if (capsule.getOwner().equalsIgnoreCase(member.trim())) {
                if (!found) {
                    printTableHeader("CAPSULES OF " + member.toUpperCase());
                    found = true;
                }
                System.out.println(capsule);
            }
        }
        if (!found) System.out.println("No capsules found for this member.");
    }

    public void displayUnlockAlerts() {
        boolean found = false;
        System.out.println("\n========== UNLOCK ALERTS ==========");
        for (Capsule capsule : capsules) {
            if (capsule.isReadyToOpen()) {
                found = true;
                System.out.println("READY TO OPEN: " + capsule.getId() + " | " + capsule.getTitle()
                        + " | Member: " + capsule.getOwner() + " | Unlocked on: " + capsule.getUnlockOn());
            } else if (capsule.unlocksSoon(ALERT_DAYS)) {
                found = true;
                System.out.println("UNLOCKING SOON: " + capsule.getId() + " | " + capsule.getTitle()
                        + " | Member: " + capsule.getOwner() + " | In " + capsule.daysToUnlock()
                        + " day(s) (" + capsule.getUnlockOn() + ")");
            }
        }
        if (!found) System.out.println("No unlock alerts.");
    }

    public void saveCapsules() {
        try (FileWriter writer = new FileWriter(CAPSULE_FILE)) {
            writer.write("ID,TYPE,MEMBER,TITLE,CREATED,UNLOCK_ON,STATE,DETAIL1,DETAIL2,DETAIL3\n");
            for (Capsule capsule : capsules) writer.write(capsule.toCsv() + "\n");
        } catch (IOException e) {
            System.out.println("Could not save capsules: " + e.getMessage());
        }
    }

    private void loadCapsules() {
        File file = new File(CAPSULE_FILE);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                if (line.isBlank()) continue;
                try {
                    String[] data = line.split(",", -1);
                    if (data.length < 10) throw new IllegalArgumentException("Too few columns");
                    String id = data[0], type = data[1], owner = data[2], title = data[3];
                    LocalDate created = LocalDate.parse(data[4]);
                    LocalDate unlock = LocalDate.parse(data[5]);
                    boolean opened = "OPENED".equalsIgnoreCase(data[6]);
                    Capsule capsule;
                    if ("LETTER".equalsIgnoreCase(type)) {
                        capsule = new LetterCapsule(id, owner, title, created, unlock, opened, data[7]);
                    } else if ("BOOK".equalsIgnoreCase(type)) {
                        capsule = new BookCapsule(id, owner, title, created, unlock, opened, data[7], data[8], data[9]);
                    } else throw new IllegalArgumentException("Unknown capsule type");
                    if (findCapsule(id) == null) capsules.add(capsule);
                } catch (Exception e) {
                    System.out.println("Skipping invalid capsule record: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load capsules: " + e.getMessage());
        }
    }

    private void printTableHeader(String heading) {
        System.out.println("\n============================== " + heading + " ==============================");
        System.out.printf("%-10s %-16s %-24s %-8s %-12s %-8s%n",
                "ID", "MEMBER", "TITLE", "TYPE", "UNLOCK ON", "STATUS");
        System.out.println("----------------------------------------------------------------------------------------------");
    }
}
