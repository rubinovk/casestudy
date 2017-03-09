package in.futurezoom.scoring;

import java.util.List;

/**
 * Created by rubinovk on 09.03.17.
 * <p>
 * Aggregate repayment score for a company
 * <p>
 * it's possible to specify whether we give more weight to the delay or to the amount unpaid
 */
class CompanyScore {
    private final long max_observed_delay;
    private final float max_observed_amount;
    private final float delay_weight;
    private final float amount_weight;

    public CompanyScore(long max_observed_delay, float max_observed_amount, float delay_weight,
            float amount_weight) {
        this.max_observed_delay = max_observed_delay;
        this.max_observed_amount = max_observed_amount;
        this.delay_weight = delay_weight;
        this.amount_weight = amount_weight;
    }


    public int compute(List<RecordScore> recordScoreList) {
        float aggregate_delay_score = 0.0f;
        for (RecordScore recordScore : recordScoreList) {
            aggregate_delay_score += recordScore
                    .compute(max_observed_delay, max_observed_amount, delay_weight, amount_weight);
        }
        return finalScore(aggregate_delay_score, recordScoreList.size());
    }

    /**
     * Weighted arithmetic mean of scores. Weighed by the numbers of delayed and paid scores.
     */
    private int finalScore(float aggregate_score, int totalNumberOfRecords) {
        return (int) (((aggregate_score) / (float) totalNumberOfRecords) * 100);
    }
}
