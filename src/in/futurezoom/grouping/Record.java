package in.futurezoom.grouping;

/**
 * Created by rubinovk on 07.03.17.
 */
public class Record {

    private String vat;
    private String start_date;
    private String due_date;
    private String repayment_date;
    private String amount;

    private String correctedVat;

    public Record(String vat, String start_date, String due_date, String repayment_date,
            String amount) {
        this.vat = vat;
        this.start_date = start_date;
        this.due_date = due_date;
        this.repayment_date = repayment_date;
        this.amount = amount;
    }


    public void setCorrectedVat(String correctedVat) {
        this.correctedVat = correctedVat;
    }

    public String getVat() {
        return vat;
    }

    public String getRepaymentDate() {
        return repayment_date;
    }

    public String getDueDate() {
        return due_date;
    }

    public String getAmount() {
        return amount;
    }

    public String getStartDate() {
        return start_date;
    }
}
