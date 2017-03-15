package in.futurezoom.grouping;

import java.util.ArrayList;
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

    @Override
    public HashMap<String, Integer> getVatFreqMap() {
        return vatFreqMap;
    }

    private HashMap<String, Integer> vatFreqMap = new HashMap<>();

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

    @Override
    public void addRecordToValidGroups(String vat, Record record) {
        addRecordToGroups(vat, record, validGroups);
        updateFrequencyMap(vat, 1);
    }

    @Override
    public void addRecordToInvalidGroups(String vat, Record record) {
        addRecordToGroups(vat, record, invalidGroups);
    }

    @Override
    public void addRecordsToValidGroups(String vat, List<Record> recordsList) {
        addRecordsToGroups(vat, recordsList, validGroups);
        updateFrequencyMap(vat, recordsList.size());
    }

    @Override
    public void addRecordsToInvalidGroups(String vat, List<Record> recordsList) {
        addRecordsToGroups(vat, recordsList, invalidGroups);
    }


    private void updateFrequencyMap(String vat, int number) {
        if (number > 0) {
            vatFreqMap
                    .put(vat, vatFreqMap.get(vat) == null ? number : vatFreqMap.get(vat) + number);
        }
    }

    private void addRecordsToGroups(String vat, List<Record> newRecords,
            HashMap<String, List<Record>> groups) {
        groups.computeIfAbsent(vat, k -> new ArrayList<>()).addAll(newRecords);
    }


    private void addRecordToGroups(String vat, Record record,
            HashMap<String, List<Record>> groups) {
        groups.computeIfAbsent(vat, k -> new ArrayList<>()).add(record);
    }

}
