package com.lab2.task3;

import java.util.ArrayList;

public class Journal {

    public final ArrayList<JournalRecord> marks;

    public Journal() {
        this.marks = new ArrayList<>();
    }

    public void addMark(String tutorName, Student student, int week, int mark) {
        synchronized (this.marks) {
            this.marks.add(new JournalRecord(tutorName, student, week, mark));
        }
    }

    public void listJournal() {
        for (JournalRecord record : this.marks) {
            System.out.println(record);
        }
    }

}

record JournalRecord(String tutorName, Student student, int week, int mark) {

    public String toString() {
        return String.format("In the week %d the tutor %s put the student %s the following mark %d",
                week, tutorName, student.name(), mark);
    }
}
