package in.futurezoom.grouping;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by rubinovk on 08.03.17.
 */
public class GroupingTest {

    Groups group = new SwissGroups();
    Grouping grouping;

    String[][] validInvalidInput =
            new String[][]{{"CHE109.793.257", "12/08/2014", "02/22/2015", "02/20/2015", "16863"},
                    {"CHE-103.635.820", "07/19/2014", "09/27/2014", "09/24/2014", "36001.5"}};

    // insert 1 after 7
    String[][] distanceOneInput =
            new String[][]{{"CHE-115.471.861", "12/08/2014", "02/22/2015", "02/20/2015", "16863"},
                    {"CHE-115.478.61", "07/19/2014", "09/27/2014", "09/24/2014", "36001.5"}};

    // second entry can't be matched to anything since it's distance is 2 edits (51 and + 1)
    String[][] distanceTwoInput =
            new String[][]{{"CHE-115.471.861", "12/08/2014", "02/22/2015", "02/20/2015", "16863"},
                    {"CHE-151.478.61", "07/19/2014", "09/27/2014", "09/24/2014", "36001.5"}};

    // transpose 68 and 86
    String[][] transpositionDistanceOneInput =
            new String[][]{{"CHE-115.471.861", "12/08/2014", "02/22/2015", "02/20/2015", "16863"},
                    {"CHE-115.471.681", "07/19/2014", "09/27/2014", "09/24/2014", "36001.5"}};

    @Before
    public void setup() {

        grouping = new Grouping(group);
    }

    @Test
    public void sortNewRecord() throws Exception {
        for (String[] line : validInvalidInput) {
            grouping.sortNewRecord(line);
        }
        assertEquals(1, group.validGroups().size());
        assertEquals(1, group.invalidGroups().size());
    }

    @Test
    public void matchAndMerge1() throws Exception {
        for (String[] line : distanceOneInput) {
            grouping.sortNewRecord(line);
        }
        assertEquals(1, group.validGroups().size());
        assertEquals(1, group.invalidGroups().size());

        grouping.processInvalidGroups();

        assertEquals(1, group.validGroups().size());
        assertEquals(0, group.invalidGroups().size());
    }


    @Test
    public void matchTransposed() throws Exception {
        for (String[] line : transpositionDistanceOneInput) {
            grouping.sortNewRecord(line);
        }
        assertEquals(2, group.validGroups().size());
        assertEquals(0, group.invalidGroups().size());

        grouping.processSuspiciousGroups();

        assertEquals(1, group.validGroups().size());
        assertEquals(0, group.invalidGroups().size());
    }


    @Test
    public void matchTwoWithDistance2() throws Exception {
        for (String[] line : distanceTwoInput) {
            grouping.sortNewRecord(line);
        }
        assertEquals(1, group.validGroups().size());
        assertEquals(1, group.invalidGroups().size());

        grouping.processInvalidGroups();

        assertEquals(1, group.validGroups().size());
        assertEquals(1, group.invalidGroups().size());
    }


    @Test
    public void matchAndMerge2() throws Exception {
        for (String[] line : distanceTwoInput) {
            grouping.sortNewRecord(line);
        }
        assertEquals(1, group.validGroups().size());
        assertEquals(1, group.invalidGroups().size());

        grouping.processSuspiciousGroups();

        assertEquals(1, group.validGroups().size());
        assertEquals(1, group.invalidGroups().size());
    }

}