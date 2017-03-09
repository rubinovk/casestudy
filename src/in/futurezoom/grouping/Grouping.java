package in.futurezoom.grouping;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.futurezoom.Util;
import info.debatty.java.stringsimilarity.Damerau;

/**
 * Created by rubinovk on 07.03.17.
 */
public class Grouping {

    private final Groups groups;

    // Damerau-Levenshtein algorithm detects exactly the kind of user typos we target
    // Calculates edit distance with transpositions
    private Damerau damerauAlgorithm = new Damerau();
    // we're interested in typos with edit distance 1
    private static final int EDIT_DISTANCE = 1;


    public Grouping(Groups groups) {
        this.groups = groups;
    }

    /**
     * read csv, "numberify" vats, create valid and invalid groups
     */
    public boolean readCsv(String fileName) {
        String[] nextLine;
        try {
            CSVReader reader = new CSVReader(new FileReader(fileName));
            // skip header line
            reader.readNext();
            while ((nextLine = reader.readNext()) != null) {
                sortNewRecord(nextLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Can't open csv file " + e.toString());
            return false;
        }
        return true;
    }

    /**
     * populate groups of records with valid and invalid vats
     *
     * @param nextLine
     */
    void sortNewRecord(String[] nextLine) {
        Record record = new Record(nextLine[0], nextLine[1], nextLine[2], nextLine[3], nextLine[4]);

        // only numbers are significant for comparison and make the vat code unique
        String intVat = Util.numberifyVat(nextLine[0]);

        //  does not consider suspicious records with swapped numbers, they are valid
        if (Util.isValidVat(nextLine[0], groups)) {
            addRecordToGroups(intVat, record, groups.validGroups());
        } else {
            addRecordToGroups(intVat, record, groups.invalidGroups());
        }
    }


    /**
     * Assumption: in theory there can be several matches for mistyped entries. Here we take the
     * first matching one.
     */
    public void processInvalidGroups() {

        List<String> toBeRemoved = new ArrayList<>();
        for (String invalidKey : groups.invalidGroups().keySet()) {

            // find keys in the validGroups whose LDD is == 1
            for (String validKey : groups.validGroups().keySet()) {
                // recognize vats that differ in one "edit" distance
                if (damerauAlgorithm.distance(validKey, invalidKey) == EDIT_DISTANCE) {
                    // move those records to validGroups
                    System.out.println("Similar keys will be merged: " + Util
                            .formatNumberKey(validKey, groups) + " " + "and " + Util
                            .formatNumberKey(invalidKey, groups) + "");
                    addRecordsToGroups(validKey, groups.invalidGroups().get(invalidKey),
                            groups.validGroups());

                    toBeRemoved.add(invalidKey);
                    break;
                } else if (invalidKey.length() == groups.getVatNumLength()) {
                    // keys that have 9 numbers are valid, just do not have matching valid ones
                    System.out.println("Corrected valid key to be saved: " + Util
                            .formatNumberKey(invalidKey, groups));
                    addRecordsToGroups(invalidKey, groups.invalidGroups().get(invalidKey),
                            groups.validGroups());
                    toBeRemoved.add(invalidKey);
                    break;
                }
            }
        }

        //  remove from the invalid group after correction
        for (String key : toBeRemoved) {
            groups.invalidGroups().remove(key);
        }


    }


    /**
     * we consider suspicious groups that have only one payment record
     */
    public void processSuspiciousGroups() {

        List<String> toBeRemoved = new ArrayList<>();
        for (String suspiciousGroupValidKey : groups.validGroups().keySet()) {
            if (groups.validGroups().get(suspiciousGroupValidKey).size() == 1) {
                // this is a suspicious group, only one entry
                // check Damerau distance with the other keys
                for (String normalKey : groups.validGroups().keySet()) {
                    if (damerauAlgorithm
                            .distance(suspiciousGroupValidKey, normalKey) == EDIT_DISTANCE) {
                        System.out.println("Suspiciously similar groups will be merged: " + Util
                                .formatNumberKey(normalKey, groups) + " " + "and" + " " + Util
                                .formatNumberKey(suspiciousGroupValidKey, groups));
                        // merge the groups
                        addRecordsToGroups(normalKey,
                                groups.validGroups().get(suspiciousGroupValidKey),
                                groups.validGroups());
                        toBeRemoved.add(suspiciousGroupValidKey);
                    }
                }
            }
        }
        for (String key : toBeRemoved) {
            groups.validGroups().remove(key);
        }
    }

    /**
     * if invalidGroups is not empty after resolution,
     * use it in the final reporting for manual inspection
     */
    public void reportUnresolvedInvalidGroups() {
        if (!groups.invalidGroups().isEmpty()) {
            System.out
                    .println("The following groups could not be resolved. Please inspect manually");
            for (String key : groups.invalidGroups().keySet()) {
                System.out.println("Vat: " + groups.invalidGroups().get(key).get(0).getVat());
            }
        }

    }

    private void addRecordsToGroups(String vat, List<Record> newRecords,
            HashMap<String, List<Record>> groups) {
        List<Record> origRecordList = groups.get(vat);
        if (origRecordList == null) {
            origRecordList = new ArrayList<>();
        }
        origRecordList.addAll(newRecords);
    }


    private void addRecordToGroups(String vat, Record record,
            HashMap<String, List<Record>> groups) {
        List<Record> recordList = groups.computeIfAbsent(vat, k -> new ArrayList<>());
        recordList.add(record);
    }


}
