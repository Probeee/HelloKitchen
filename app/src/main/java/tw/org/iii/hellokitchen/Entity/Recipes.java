package tw.org.iii.hellokitchen.Entity;

 /**
 * Created by Kevin on 2017/6/7.
 */

public class Recipes
{
    private String recipe_id;
    private String recipe_name;
    private String member_id;
    private String upload_date;
    private Boolean recipe_status;
    private String recipe_amount;
    private String recipe_cooktime;
    private String recipe_picture;

    public Recipes()
    {
        this.recipe_id = "";
        this.recipe_name = "";
        this.member_id = "";
        this.upload_date = "";
        this.recipe_status = false;
        this.recipe_amount = "";
        this.recipe_cooktime = "";
        this.recipe_picture = "";
    }

    public Recipes(String recipe_id, String recipe_name, String member_id, String upload_date, Boolean recipe_status, String recipe_amount, String recipe_cooktime, String recipe_picture)
    {
        this.recipe_id = recipe_id;
        this.recipe_name = recipe_name;
        this.member_id = member_id;
        this.upload_date = upload_date;
        this.recipe_status = recipe_status;
        this.recipe_amount = recipe_amount;
        this.recipe_cooktime = recipe_cooktime;
        this.recipe_picture = recipe_picture;
    }


    public String getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(String recipe_id) {
        this.recipe_id = recipe_id;
    }

    public String getRecipe_name() {
        return recipe_name;
    }

    public void setRecipe_name(String recipe_name) {
        this.recipe_name = recipe_name;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getUpload_date() {
        return upload_date;
    }

    public void setUpload_date(String upload_date) {
        this.upload_date = upload_date;
    }

    public Boolean getRecipe_status() {
        return recipe_status;
    }

    public void setRecipe_status(Boolean recipe_status) {
        this.recipe_status = recipe_status;
    }

    public String getRecipe_amount() {
        return recipe_amount;
    }

    public void setRecipe_amount(String recipe_amount) {
        this.recipe_amount = recipe_amount;
    }

    public String getRecipe_cooktime() {
        return recipe_cooktime;
    }

    public void setRecipe_cooktime(String recipe_cooktime) {
        this.recipe_cooktime = recipe_cooktime;
    }

    public String getRecipe_picture() {
        return recipe_picture;
    }

    public void setRecipe_picture(String recipe_picture) {
        this.recipe_picture = recipe_picture;
    }


}
