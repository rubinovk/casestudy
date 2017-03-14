package in.futurezoom.grouping;

import java.util.HashMap;
import java.util.List;

/**
 * Created by rubinovk on 08.03.17.
 */
public interface Groups {

    HashMap<String, List<Record>> validGroups();

    HashMap<String, List<Record>> invalidGroups();


    HashMap<String, Boolean> getValidInvalidVatMap();

    String getVatPrefix();

    String getVatRegex();

    int getVatNumLength();
}
