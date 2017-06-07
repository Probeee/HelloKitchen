package tw.org.iii.hellokitchen.Entity;

/**
 * Created by Kevin on 2017/6/7.
 */

public class Method
{
    private String recipe_id;
    private String method_id;
    private String method_detail;
    private String method_picture;

    public Method()
    {
    }

    public Method(String recipe_id, String method_id, String method_detail, String method_picture)
    {
        this.recipe_id = recipe_id;
        this.method_id = method_id;
        this.method_detail = method_detail;
        this.method_picture = method_picture;
    }

    public String getRecipe_id(){
        return recipe_id;
    }

    public void setRecipe_id(String recipe_id) {
        this.recipe_id = recipe_id;
    }

    public String getMethod_id() {
        return method_id;
    }

    public void setMethod_id(String method_id) {
        this.method_id = method_id;
    }

    public String getMethod_detail() {
        return method_detail;
    }

    public void setMethod_detail(String method_detail) {
        this.method_detail = method_detail;
    }

    public String getMethod_picture() {
        return method_picture;
    }

    public void setMethod_picture(String method_picture) {
        this.method_picture = method_picture;
    }
}
