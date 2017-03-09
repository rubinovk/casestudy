package in.futurezoom.scoring;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by rubinovk on 09.03.17.
 */
public class RecordScoreTest {

    long max_delay = TimeUnit.DAYS.toMillis(100);
    float max_amount = 5000.0f;
    float delay_weight = 0.9f;
    float amount_weight = 0.1f;

    @Test
    public void compute() throws Exception {
        RecordScore recordScore = new RecordScore();
        recordScore.setAmount(1000.0f);
        long delay = TimeUnit.DAYS.toMillis(8);
        recordScore.setDelay(delay);
        assertTrue(recordScore.compute(max_delay, max_amount, delay_weight, amount_weight) <= 1);
        assertTrue(recordScore.compute(max_delay, max_amount, delay_weight, amount_weight) >= 0);
    }


    @Test
    public void computeMaxValues() throws Exception {
        RecordScore recordScore = new RecordScore();
        recordScore.setAmount(max_amount);
        recordScore.setDelay(max_delay);
        assertEquals(0.0f, recordScore.compute(max_delay, max_amount, delay_weight, amount_weight));
    }

    @Test
    public void computeMinValues() throws Exception {
        RecordScore recordScore = new RecordScore();
        recordScore.setAmount(0.0f);
        recordScore.setDelay(0);
        assertEquals(1.0f, recordScore.compute(max_delay, max_amount, delay_weight, amount_weight));
    }


    @Test
    public void computeDelay0() throws Exception {
        RecordScore recordScore = new RecordScore();
        recordScore.setAmount(max_amount);
        recordScore.setDelay(0);
        assertEquals(1, recordScore.compute(max_delay, max_amount, delay_weight, amount_weight));
    }


    @Test
    public void computeWeighted() throws Exception {
        RecordScore recordScore = new RecordScore();
        recordScore.setAmount(max_amount);
        recordScore.setDelay(TimeUnit.DAYS.toMillis(1));
        // one day delay over 100 days with low weight of amount
        assertTrue(recordScore.compute(max_delay, max_amount, delay_weight, amount_weight) > 0.88f);
    }


}