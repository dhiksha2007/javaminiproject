SMART LIBRARY TIME CAPSULE SYSTEM (Java console application)

IMPORTANT ABOUT THE PDF:
The cover page says "SMART INVENTORY & SUPPLY CHAIN TRACKER", but the report body,
abstract, design, and source-code chapter describe "SMART LIBRARY TIME CAPSULE SYSTEM".
This code follows the detailed report body and its seven Java classes.

REQUIREMENTS
- JDK 17 or later
- VS Code, IntelliJ, Eclipse, or a terminal

HOW TO RUN IN VS CODE
1. Extract this ZIP.
2. Open the folder SmartLibraryTimeCapsuleSystem in VS Code.
3. Open Terminal > New Terminal.
4. Compile all Java files:
   javac *.java
5. Run:
   java Main

FEATURES
1. Create a letter-to-the-future capsule.
2. Create a book recommendation capsule.
3. View all capsules and status (LOCKED, READY, OPENED).
4. Open a capsule only after the unlock date.
5. Show ready and next-30-days unlock alerts.
6. View capsules belonging to one member.
7. Search by capsule ID.
8. Extend a still-locked capsule's unlock date.
9. View a timestamped activity log.
10. Save/load capsule data using capsules.csv and activity.csv.

DATA FILES
The application automatically creates capsules.csv and activity.csv in the current working
directory. Keep them in the same folder where you run `java Main`.

DATE DEMO
You can simulate a date for testing without changing your computer clock:
java -Dcapsule.today=2027-01-01 Main
Run the command from the folder containing the compiled class files.

NOTE
This is a console application, not a graphical/web application. Text stored in CSV files is
simplified by replacing commas and line breaks with spaces.
