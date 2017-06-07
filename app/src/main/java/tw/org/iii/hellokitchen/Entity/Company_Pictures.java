package tw.org.iii.hellokitchen.Entity;

/**
 * Created by Kevin on 2017/6/7.
 */

public class Company_Pictures
{
    private String picture_id;
    private String company_id;
    private String picture_path;
    private String picture_name;
    private String picture_description;

    public Company_Pictures()
    {
    }
    public Company_Pictures(String picture_id, String company_id, String picture_path, String picture_name, String picture_description)
    {
        this.picture_id = picture_id;
        this.company_id = company_id;
        this.picture_path = picture_path;
        this.picture_name = picture_name;
        this.picture_description = picture_description;
    }

    public String getPicture_id() {
        return picture_id;
    }

    public void setPicture_id(String picture_id) {
        this.picture_id = picture_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getPicture_path() {
        return picture_path;
    }

    public void setPicture_path(String picture_path) {
        this.picture_path = picture_path;
    }

    public String getPicture_name() {
        return picture_name;
    }

    public void setPicture_name(String picture_name) {
        this.picture_name = picture_name;
    }

    public String getPicture_description() {
        return picture_description;
    }

    public void setPicture_description(String picture_description) {
        this.picture_description = picture_description;
    }
}
