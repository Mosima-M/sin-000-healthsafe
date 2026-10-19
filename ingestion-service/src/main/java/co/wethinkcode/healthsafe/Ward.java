package co.wethinkcode.healthsafe;

public class Ward {
    private String hubId;
    private String department;
    private String wing;
    private String bedsAvailable;

    public Ward(String hubId, String wing, String department, String bedsAvailable){
        this.hubId = hubId;
        this.wing = wing;
        this.department = department;
        this.bedsAvailable = bedsAvailable;
    }
    public String getHubId(){return hubId;}
    public String getWing(){return wing;}
    public String getDepartment(){return department;}
    public String getBedsAvailable(){return bedsAvailable;}
}
