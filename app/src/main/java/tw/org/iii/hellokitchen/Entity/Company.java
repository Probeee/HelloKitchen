package tw.org.iii.hellokitchen.Entity;

/**
 * Created by Kevin on 2017/6/7.
 */

public class Company
{
    private String company_id;
    private String company_name;
    private String company_logo;
    private String company_cover;
    private String company_intro;
    private String company_address;
    private String company_tel;
    private String company_email;
    private String company_owner;
    private String company_password;
    private boolean company_status;

    public Company()
    {
    }

    public Company(String company_id, String company_name, String company_logo, String company_cover, String company_intro, String company_address, String company_tel, String company_email, String company_owner, String company_password, boolean company_status)
    {
        this.company_id = company_id;
        this.company_name = company_name;
        this.company_logo = company_logo;
        this.company_cover = company_cover;
        this.company_intro = company_intro;
        this.company_address = company_address;
        this.company_tel = company_tel;
        this.company_email = company_email;
        this.company_owner = company_owner;
        this.company_password = company_password;
        this.company_status = company_status;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_logo() {
        return company_logo;
    }

    public void setCompany_logo(String company_logo) {
        this.company_logo = company_logo;
    }

    public String getCompany_cover() {
        return company_cover;
    }

    public void setCompany_cover(String company_cover) {
        this.company_cover = company_cover;
    }

    public String getCompany_intro() {
        return company_intro;
    }

    public void setCompany_intro(String company_intro) {
        this.company_intro = company_intro;
    }

    public String getCompany_address() {
        return company_address;
    }

    public void setCompany_address(String company_address) {
        this.company_address = company_address;
    }

    public String getCompany_tel() {
        return company_tel;
    }

    public void setCompany_tel(String company_tel) {
        this.company_tel = company_tel;
    }

    public String getCompany_email() {
        return company_email;
    }

    public void setCompany_email(String company_email) {
        this.company_email = company_email;
    }

    public String getCompany_owner() {
        return company_owner;
    }

    public void setCompany_owner(String company_owner) {
        this.company_owner = company_owner;
    }

    public String getCompany_password() {
        return company_password;
    }

    public void setCompany_password(String company_password) {
        this.company_password = company_password;
    }

    public boolean isCompany_status() {
        return company_status;
    }

    public void setCompany_status(boolean company_status) {
        this.company_status = company_status;
    }
}
