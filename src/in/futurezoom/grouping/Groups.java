package in.futurezoom.grouping;

import java.util.HashMap;
import java.util.List;

/**
 * Created by rubinovk on 08.03.17.
 */
public interface Groups {

    HashMap<String, Integer> getVatFreqMap();

    HashMap<String, List<Record>> validGroups();

    HashMap<String, List<Record>> invalidGroups();

    String getVatPrefix();

    String getVatRegex();

    int getVatNumLength();

    void addRecordToValidGroups(String vat, Record record);

    void addRecordToInvalidGroups(String vat, Record record);

    void addRecordsToValidGroups(String vat, List<Record> recordsList);

    void addRecordsToInvalidGroups(String vat, List<Record> recordsList);
}
