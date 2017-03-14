package in.futurezoom.grouping;

import java.util.HashMap;
import java.util.List;

/**
 * Created by rubinovk on 08.03.17.
 */
public class SwissGroups implements Groups {

    //Valid format: CHE-123.456.789
    private static final String REGEX = "CHE-\\d{3}.\\d{3}.\\d{3}";
    private static final String PREFIX = "CHE-";
    private static final int VAT_LENGH = 15;
    private static final int VAT_NUM_LENGH = 9;

    private HashMap<String, List<Record>> validGroups = new HashMap<>();
    private HashMap<String, List<Record>> invalidGroups = new HashMap<>();

    public HashMap<String, Boolean> getValidInvalidVatMap() {
        return validInvalidVatMap;
    }

    private HashMap<String, Boolean> validInvalidVatMap = new HashMap<>();

    public HashMap<String, List<Record>> validGroups() {
        return validGroups;
    }

    public HashMap<String, List<Record>> invalidGroups() {
        return invalidGroups;
    }

    @Override
    public String getVatPrefix() {
        return PREFIX;
    }

    @Override
    public String getVatRegex() {
        return REGEX;
    }

    @Override
    public int getVatNumLength() {
        return VAT_NUM_LENGH;
    }
}
