import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public abstract class Capsule {
    private final String id;
    private final String owner;
    private String title;
    private final LocalDate createdOn;
    private LocalDate unlockOn;
    private boolean opened;

    public Capsule(String id, String owner, String title, LocalDate createdOn,
                   LocalDate unlockOn, boolean opened) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Capsule ID cannot be empty.");
        if (owner == null || owner.isBlank()) throw new IllegalArgumentException("Member name cannot be empty.");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title cannot be empty.");
        if (createdOn == null) throw new IllegalArgumentException("Creation date is required.");
        if (unlockOn == null) throw new IllegalArgumentException("Unlock date is required.");
        if (unlockOn.isBefore(createdOn))
            throw new IllegalArgumentException("Unlock date cannot be before the creation date.");

        this.id = clean(id);
        this.owner = clean(owner);
        this.title = clean(title);
        this.createdOn = createdOn;
        this.unlockOn = unlockOn;
        this.opened = opened;
    }

    private static String clean(String value) {
        return value.trim().replace(",", " ").replace("\n", " ").replace("\r", " ");
    }

    public String getId() { return id; }
    public String getOwner() { return owner; }
    public String getTitle() { return title; }

    public void setTitle(String title) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title cannot be empty.");
        this.title = clean(title);
    }

    public LocalDate getCreatedOn() { return createdOn; }
    public LocalDate getUnlockOn() { return unlockOn; }
    public boolean isOpened() { return opened; }

    public boolean isLocked() { return !opened && DateUtil.today().isBefore(unlockOn); }

    public long daysToUnlock() {
        return Math.max(0, ChronoUnit.DAYS.between(DateUtil.today(), unlockOn));
    }

    public boolean unlocksSoon(int days) {
        return days >= 0 && isLocked() && daysToUnlock() <= days;
    }

    public boolean isReadyToOpen() {
        return !opened && !DateUtil.today().isBefore(unlockOn);
    }

    public void extendLock(LocalDate newUnlockDate) {
        if (newUnlockDate == null) throw new IllegalArgumentException("New unlock date is required.");
        if (!isLocked()) throw new IllegalArgumentException("Only a locked capsule can be extended.");
        if (!newUnlockDate.isAfter(unlockOn))
            throw new IllegalArgumentException("New unlock date must be after the current unlock date (" + unlockOn + ").");
        unlockOn = newUnlockDate;
    }

    public void open() {
        if (opened) throw new IllegalArgumentException("This capsule has already been opened.");
        if (isLocked())
            throw new IllegalArgumentException("Capsule is locked. It can be opened on " + unlockOn
                    + " (" + daysToUnlock() + " day(s) remaining).");
        opened = true;
    }

    public String getStatus() {
        if (opened) return "OPENED";
        return isLocked() ? "LOCKED" : "READY";
    }

    public abstract String revealContent();
    public abstract String getType();
    public abstract String toCsv();

    protected static String escape(String value) {
        return value == null ? "-" : value.replace(",", " ").replace("\n", " ").replace("\r", " ");
    }

    protected String baseCsv(String detail1, String detail2, String detail3) {
        return String.join(",", escape(id), getType(), escape(owner), escape(title),
                createdOn.toString(), unlockOn.toString(), opened ? "OPENED" : "SEALED",
                escape(detail1), escape(detail2), escape(detail3));
    }

    public String basicInfo() {
        return String.format(Locale.US, "%-10s %-16s %-24s %-8s %-12s %-8s",
                id, owner, title, getType(), unlockOn, getStatus());
    }

    @Override
    public String toString() { return basicInfo(); }
}
