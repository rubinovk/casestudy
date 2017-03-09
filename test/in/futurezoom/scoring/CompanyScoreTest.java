package in.futurezoom.scoring;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * Created by rubinovk on 09.03.17.
 */
public class CompanyScoreTest {


    long max_delay = TimeUnit.DAYS.toMillis(100);
    long bigdelay = TimeUnit.DAYS.toMillis(98);
    long smalldelay = TimeUnit.DAYS.toMillis(9);
    float max_amount = 5000.0f;
    float delay_weight = 0.9f;
    float amount_weight = 0.1f;

    List<RecordScore> oneBigDelayList = new ArrayList<>();
    List<RecordScore> twoBigDelaysList = new ArrayList<>();
    List<RecordScore> manySmallDelaysList = new ArrayList<>();

    CompanyScore companyScore;

    @Before
    public void setUp() throws Exception {

        RecordScore bigRecordScore = new RecordScore();
        bigRecordScore.setAmount(1000.0f);
        bigRecordScore.setDelay(bigdelay);

        RecordScore noDelayRecordScore = new RecordScore();
        noDelayRecordScore.setDelay(0);

        oneBigDelayList.add(bigRecordScore);
        oneBigDelayList.add(noDelayRecordScore);

        twoBigDelaysList.add(bigRecordScore);
        twoBigDelaysList.add(bigRecordScore);
        twoBigDelaysList.add(noDelayRecordScore);

        RecordScore smallRecordScore = new RecordScore();
        smallRecordScore.setAmount(1000.0f);
        smallRecordScore.setDelay(smalldelay);

        for (int i = 0; i < 10; i++) {
            manySmallDelaysList.add(smallRecordScore);
        }
        manySmallDelaysList.add(noDelayRecordScore);

        companyScore = new CompanyScore(max_delay, max_amount, delay_weight, amount_weight);
    }

    /**
     * one big delay is more significant (gives smaller score) than several little delays
     *
     * @throws Exception
     */
    @Test
    public void oneBigVsManySmall() throws Exception {
        assertTrue(
                companyScore.compute(oneBigDelayList) < companyScore.compute(manySmallDelaysList));
    }

    @Test
    public void multipleDelaysAreWorse() throws Exception {
        assertTrue(companyScore.compute(twoBigDelaysList) < companyScore.compute(oneBigDelayList));
    }

}