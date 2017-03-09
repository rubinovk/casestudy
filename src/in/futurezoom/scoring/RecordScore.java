package in.futurezoom.scoring;

import java.util.concurrent.TimeUnit;

/**
 * Created by rubinovk on 09.03.17.
 * <p>
 * Computation of score for an individual record of a company
 * <p>
 * Relative to the max observed delays/amounts.
 * <p>
 * To give more weight to longer delays, the delay score is a quadratic function
 */
class RecordScore {

    private long delay;
    private float amount;

    public void setDelay(long delay) {
        assert delay >= 0;
        this.delay = delay;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float compute(long max_observed_delay, float max_observed_amount, float delay_weight,
            float amount_weight) {
        if (delay == 0) {
            return 1;
        } else {
            return delay_weight * delayScore(delay,
                    max_observed_delay) + amount_weight * amountScore(amount, max_observed_amount);
        }
    }

    /**
     * to account for larger delays this is a square function
     * <p>
     * larger - better
     *
     * @param delay
     * @param max_observed_delay
     * @return
     */
    private float delayScore(long delay, long max_observed_delay) {
        assert max_observed_delay >= delay;
        float max_delay_days = TimeUnit.MILLISECONDS.toDays(max_observed_delay);
        float delay_days = TimeUnit.MILLISECONDS.toDays(delay);

        float difference = max_delay_days - delay_days;
        return (difference * difference) / (max_delay_days * max_delay_days);
    }

    /**
     * this score is linear to the amount delayed
     * <p>
     * larger - better
     *
     * @param amount
     * @param max_observed_amount
     * @return
     */
    private float amountScore(float amount, float max_observed_amount) {
        assert max_observed_amount >= amount;
        return (max_observed_amount - amount) / max_observed_amount;
    }
}
