package in.futurezoom.scoring;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.futurezoom.Util;
import in.futurezoom.grouping.Groups;
import in.futurezoom.grouping.Record;


/**
 * score from 0 to 100
 * <p>
 * always repays on the due_date or before  = 100
 * <p>
 * the longer after due_date, the lower the score
 * <p>
 * single large delay is worse than many small ones
 */
public class Scoring {
    private final Groups groups;
    private final Date survey_date;
    private long max_observed_delay = 0;
    private float max_observed_amount_unpaid = 0;
    private float delay_weight = 1.0f;
    private float amount_weight = 0.0f;
    private HashMap<String, CompanyRecordStats> allScores = new HashMap<>();

    public Scoring(Groups groups, Date survey_date, float delay_weight, float amount_weight) {
        assert delay_weight + amount_weight == 1.0f;
        this.groups = groups;
        this.survey_date = survey_date;
        this.delay_weight = delay_weight;
        this.amount_weight = amount_weight;
    }


    /**
     * first we calculate statistics for the whole set to get max observed values for
     * normalizing scores and consider only records before the survey_date
     * <p>
     * then, based on the observed variation of the values, we calculate scores for each company
     */
    public void calculateScores() {

        if (survey_date.after(Calendar.getInstance().getTime())) {
            System.out.println("Attention, survey date is in the future");
            // return;
        }

        for (String key : groups.validGroups().keySet()) {
            allScores.put(key, calculateStatsForCompany(key, groups.validGroups().get(key)));
        }
        System.out.println("\nBaseline information about all companies:");
        System.out.println("Max observed delay in the company: " + TimeUnit.MILLISECONDS
                .toDays(max_observed_delay) + " (days)");
        System.out.println("Max observed unpaid amount: " + max_observed_amount_unpaid);
        System.out.println();


        CompanyScore companyScore =
                new CompanyScore(max_observed_delay, max_observed_amount_unpaid, delay_weight,
                        amount_weight);
        for (String key : allScores.keySet()) {
            CompanyRecordStats stats = allScores.get(key);
            System.out.println(stats);
            System.out.println("Company " + Util
                    .formatNumberKey(key, groups) + " repayment score is: " + companyScore
                    .compute(stats.getRecordScoreList()));
        }
    }


    public CompanyRecordStats calculateStatsForCompany(String vat, List<Record> companyRecordList) {

        CompanyRecordStats stats = new CompanyRecordStats(vat, groups);

        for (Record record : companyRecordList) {
            RecordScore recordScore = new RecordScore();

            Date dueDate = Util.stringToDate(record.getDueDate());
            Date startDate = Util.stringToDate(record.getStartDate());
            float amount = Float.valueOf(record.getAmount());

            // if survey_date is before the due_date or if survey_date is before start_date, then
            // do not count this record
            if (survey_date.before(startDate) || survey_date.before(dueDate)) {
                continue;
            }

            // invoice is unpaid by the survey date
            if (isUnpaid(record.getRepaymentDate())) {
                stats.addUnpaidRecord();
                stats.addAmountDelayed(amount);
                stats.update_max_amount_unpaid(amount);
                long delay = survey_date.getTime() - dueDate.getTime();
                stats.update_max_delay(delay);
                recordScore.setDelay(delay);
                recordScore.setAmount(amount);
            }
            if (isPaid(record.getRepaymentDate())) {
                Date repaymentDate = Util.stringToDate(record.getRepaymentDate());

                // is paid after the due date
                if (repaymentDate.after(dueDate)) {
                    stats.addDelayedRecord();
                    stats.addAmountDelayed(amount);
                    stats.update_max_amount_unpaid(amount);
                    long delay = repaymentDate.getTime() - dueDate.getTime();
                    recordScore.setDelay(delay);
                    recordScore.setAmount(amount);
                    stats.update_max_delay(delay);

                } else {
                    // is paid on time or before the due date
                    stats.addPaidOntimeRecord();
                    recordScore.setDelay(0);
                }
            }
            stats.addRecordScore(recordScore);
        }
        max_observed_delay = stats.getMax_delay() > max_observed_delay ? stats
                .getMax_delay() : max_observed_delay;

        max_observed_amount_unpaid =
                stats.getMax_amount_unpaid() > max_observed_amount_unpaid ? stats
                        .getMax_amount_unpaid() : max_observed_amount_unpaid;

        return stats;
    }


    private boolean isUnpaid(String repaymentDateRecord) {
        return repaymentDateRecord == null || repaymentDateRecord.isEmpty();
    }


    private boolean isPaid(String repaymentDateRecord) {
        return repaymentDateRecord != null && !repaymentDateRecord.isEmpty();
    }


}
