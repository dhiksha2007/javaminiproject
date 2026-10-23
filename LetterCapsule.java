import java.time.LocalDate;

public class LetterCapsule extends Capsule {
    private final String message;

    public LetterCapsule(String id, String owner, String title, LocalDate createdOn,
                         LocalDate unlockOn, boolean opened, String message) {
        super(id, owner, title, createdOn, unlockOn, opened);
        if (message == null || message.isBlank()) throw new IllegalArgumentException("Message cannot be empty.");
        this.message = message.trim().replace(",", " ").replace("\n", " ").replace("\r", " ");
    }

    public String getMessage() { return message; }

    @Override
    public String revealContent() {
        return "Letter from " + getOwner() + " (written on " + getCreatedOn() + "):\n   \"" + message + "\"";
    }

    @Override
    public String getType() { return "LETTER"; }

    @Override
    public String toCsv() { return baseCsv(message, "-", "-"); }
}
