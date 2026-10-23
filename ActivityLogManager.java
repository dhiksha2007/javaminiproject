import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActivityLogManager {
    private static final String FILE_NAME = "activity.csv";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ActivityLogManager() { createFileIfNeeded(); }

    private void createFileIfNeeded() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("DateTime,CapsuleID,Member,Action,Details\n");
            } catch (IOException e) {
                System.out.println("Could not create activity file: " + e.getMessage());
            }
        }
    }

    public void record(String capsuleId, String member, String action, String details) {
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            writer.write(String.format("%s,%s,%s,%s,%s%n",
                    LocalDateTime.now().format(FORMATTER), clean(capsuleId), clean(member),
                    clean(action), clean(details)));
        } catch (IOException e) {
            System.out.println("Could not save activity: " + e.getMessage());
        }
    }

    private String clean(String value) {
        return value == null ? "-" : value.replace(",", " ").replace("\n", " ").replace("\r", " ");
    }

    public void displayLog() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("No activity file found.");
            return;
        }
        System.out.println("\n========== ACTIVITY LOG ==========");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) System.out.println(line);
        } catch (IOException e) {
            System.out.println("Could not read activity log: " + e.getMessage());
        }
    }
}
