import java.time.LocalDate;

public class BookCapsule extends Capsule {
    private final String bookTitle;
    private final String author;
    private final String note;

    public BookCapsule(String id, String owner, String title, LocalDate createdOn,
                       LocalDate unlockOn, boolean opened, String bookTitle, String author, String note) {
        super(id, owner, title, createdOn, unlockOn, opened);
        if (bookTitle == null || bookTitle.isBlank()) throw new IllegalArgumentException("Book title cannot be empty.");
        if (author == null || author.isBlank()) throw new IllegalArgumentException("Author cannot be empty.");
        this.bookTitle = clean(bookTitle);
        this.author = clean(author);
        this.note = (note == null || note.isBlank()) ? "-" : clean(note);
    }

    private static String clean(String value) {
        return value.trim().replace(",", " ").replace("\n", " ").replace("\r", " ");
    }

    public String getBookTitle() { return bookTitle; }
    public String getAuthor() { return author; }
    public String getNote() { return note; }

    @Override
    public String revealContent() {
        return "Book recommendation by " + getOwner() + " (saved on " + getCreatedOn() + "):\n"
                + "   Book   : " + bookTitle + "\n"
                + "   Author : " + author + "\n"
                + "   Note   : " + note;
    }

    @Override
    public String getType() { return "BOOK"; }

    @Override
    public String toCsv() { return baseCsv(bookTitle, author, note); }
}
