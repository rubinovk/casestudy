package in.futurezoom;

import java.util.Calendar;
import java.util.Date;

import in.futurezoom.grouping.Grouping;
import in.futurezoom.grouping.SwissGroups;
import in.futurezoom.scoring.Scoring;

public class Main {

    public static final String CSV_FILE = "invoices.csv";

    public static void main(String[] args) {

        // Assuming for the case we don't care where do we receive csv from

        SwissGroups swissGroups = new SwissGroups();
        Grouping grouping = new Grouping(swissGroups);
        // populate the valid/invalid groups
        if (!grouping.readCsv(CSV_FILE)) {
            System.out.println(
                    "Can't read input file. Please check " + CSV_FILE + "is in the folder");
            return;
        }

        System.out.println("==Intermediate information about grouping companies:");
        grouping.processInvalidGroups();
        grouping.processSuspiciousGroups();
        grouping.reportUnresolvedInvalidGroups();


        Calendar cal = Calendar.getInstance();
        //        cal.set(2015,5,12); // can survey in the past
        Date survey_date = cal.getTime();
        Scoring scoring = new Scoring(swissGroups, survey_date, 0.9f, 0.1f);
        scoring.calculateScores();

    }


}
