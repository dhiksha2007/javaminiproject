import java.time.LocalDate;

public final class DateUtil {
    private DateUtil() {}

    public static LocalDate today() {
        String override = System.getProperty("capsule.today");
        if (override != null && !override.isBlank()) {
            try {
                return LocalDate.parse(override.trim());
            } catch (RuntimeException ignored) {
                System.out.println("Invalid capsule.today property; using the system date.");
            }
        }
        return LocalDate.now();
    }
}
