package in.futurezoom.grouping;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        final String vat = nextLine[0];
        Record record = new Record(vat, nextLine[1], nextLine[2], nextLine[3], nextLine[4]);

        // only numbers are significant for comparison and make the vat code unique
        String intVat = Util.numberifyVat(vat);

        //  does not consider suspicious records with swapped numbers, they are valid
        if (Util.isValidVat(vat, groups)) {
            groups.addRecordToValidGroups(intVat, record);
        } else {
            groups.addRecordToInvalidGroups(intVat, record);
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
                    groups.addRecordsToValidGroups(validKey,
                            groups.invalidGroups().get(invalidKey));
                    toBeRemoved.add(invalidKey);
                    break;
                } else if (invalidKey.length() == groups.getVatNumLength()) {
                    // keys that have 9 numbers are valid, just do not have matching valid ones
                    System.out.println("Corrected valid key to be saved: " + Util
                            .formatNumberKey(invalidKey, groups));
                    groups.addRecordsToValidGroups(invalidKey,
                            groups.invalidGroups().get(invalidKey));
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

        // todo  - externalize
        final HashMap<String, Integer> vatFreqMap = groups.getVatFreqMap();
        if (!vatFreqMap.containsValue(1)) {
            return;
        }
        // O(n)
        vatFreqMap.entrySet().removeIf(entry -> entry.getValue() != 1);

        Set<String> singleKeys = new HashSet<>();
        singleKeys.addAll(vatFreqMap.keySet());

        // this is a suspicious group, only one entry
        for (String suspiciousGroupValidKey : singleKeys) {
            for (String normalKey : groups.validGroups().keySet()) {
                // for valid vats, if three triples differ, then the edit_distance is for sure > 1
                // (not using two, because two triples can be affected by transposition)
                if (allThreeSubtriplesDiffer(suspiciousGroupValidKey, normalKey)) {
                    continue;
                }
                // check Damerau distance with the other key
                // it's either transposition or 1 step substitution, since the keys are valid, no
                // deletion or insertion
                if (damerauAlgorithm
                        .distance(suspiciousGroupValidKey, normalKey) == EDIT_DISTANCE) {
                    System.out.println("Suspiciously similar groups will be merged: " + Util
                            .formatNumberKey(normalKey, groups) + " " + "and" + " " + Util
                            .formatNumberKey(suspiciousGroupValidKey, groups));
                    // merge the groups
                    groups.addRecordsToValidGroups(normalKey,
                            groups.validGroups().get(suspiciousGroupValidKey));
                    toBeRemoved.add(suspiciousGroupValidKey);
                }
            }
        }
        for (String key : toBeRemoved) {
            groups.validGroups().remove(key);
        }
    }

    private boolean allThreeSubtriplesDiffer(String suspiciousGroupValidKey, String normalKey) {
        return suspiciousGroupValidKey.substring(0, 3).hashCode() != normalKey.substring(0, 3)
                .hashCode() && suspiciousGroupValidKey.substring(3, 6).hashCode() != normalKey
                .substring(3, 6).hashCode() && suspiciousGroupValidKey.substring(6, 9)
                .hashCode() != normalKey.substring(6, 9).hashCode();
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


}
