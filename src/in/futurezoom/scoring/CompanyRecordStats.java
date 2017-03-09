package in.futurezoom.scoring;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.futurezoom.Util;
import in.futurezoom.grouping.Groups;

/**
 * Created by rubinovk on 08.03.17.
 */
public class CompanyRecordStats {

    private final String vat;
    private final Groups groups;


    private List<RecordScore> recordScoreList = new ArrayList<>();

    // delayed till the survey_date
    private int unpaid_records = 0;
    private int delayed_records = 0;
    private int paid_ontime_records = 0;
    private float sum_delayed = 0.0f;

    private long max_delay = 0;
    private float max_amount_unpaid = 0;

    public CompanyRecordStats(String vat, Groups groups) {
        this.vat = vat;
        this.groups = groups;
    }


    public void addUnpaidRecord() {
        this.unpaid_records++;
    }


    public void addDelayedRecord() {
        this.delayed_records++;
    }


    public void addPaidOntimeRecord() {
        this.paid_ontime_records++;
    }


    public void addAmountDelayed(float amount) {
        this.sum_delayed += amount;
    }

    public long getMax_delay() {
        return max_delay;
    }

    public void update_max_delay(long delay) {
        max_delay = delay > max_delay ? delay : max_delay;
    }

    public float getMax_amount_unpaid() {
        return max_amount_unpaid;
    }

    public void update_max_amount_unpaid(float amount) {
        max_amount_unpaid = amount > max_amount_unpaid ? amount : max_amount_unpaid;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append("\nStats for " + Util.formatNumberKey(vat, groups));
        builder.append("\n");
        builder.append("Total paid on time records: " + paid_ontime_records);
        builder.append("\n");
        builder.append("Total delayed paid records: " + delayed_records);
        builder.append("\n");
        builder.append("Total unpaid records: " + unpaid_records);
        builder.append("\n");
        builder.append("Max amount unpaid: " + max_amount_unpaid);
        builder.append("\n");
        builder.append("Aggregate amount unpaid/delayed: " + sum_delayed);
        builder.append("\n");
        builder.append("Max payment delay (days): " + TimeUnit.MILLISECONDS.toDays(max_delay));
        return builder.toString();
    }

    public List<RecordScore> getRecordScoreList() {
        return recordScoreList;
    }

    public void addRecordScore(RecordScore score) {
        recordScoreList.add(score);
    }
}


