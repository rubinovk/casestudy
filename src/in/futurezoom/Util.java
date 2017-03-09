package in.futurezoom;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import in.futurezoom.grouping.Groups;

/**
 * Created by rubinovk on 07.03.17.
 */
public class Util {

    public static boolean isValidVat(String vat, Groups groups) {
        return vat.matches(groups.getVatRegex());
    }


    public static Date stringToDate(String string) {
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        try {
            return format.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Cannot read date, using today's date instead. " + e.toString());
            return Calendar.getInstance().getTime();
        }
    }

    public static String numberifyVat(String vat) {
        return vat.replaceAll("\\D", "");
    }

    // can belong to each group
    public static String formatNumberKey(String vatNumberKey, Groups groups) {
        StringBuilder builder = new StringBuilder(vatNumberKey);
        builder.insert(3, ".");
        builder.insert(7, ".");
        builder.insert(0, groups.getVatPrefix());
        return builder.toString();
    }
}
