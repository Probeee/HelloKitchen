package tw.org.iii.hellokitchen.Entity;

/**
 * Created by Kevin on 2017/6/7.
 */

public class Recipes_Material
{
    private String recipe_id;
    private String material_id;
    private String material_name;
    private String material_amount;
    private String material_picture;

    public Recipes_Material() {
    }

    public Recipes_Material(String recipe_id, String material_id, String material_name, String material_amount, String material_picture) {
        this.recipe_id = recipe_id;
        this.material_id = material_id;
        this.material_name = material_name;
        this.material_amount = material_amount;
        this.material_picture = material_picture;
    }

    public String getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(String recipe_id) {
        this.recipe_id = recipe_id;
    }

    public String getMaterial_id() {
        return material_id;
    }

    public void setMaterial_id(String material_id) {
        this.material_id = material_id;
    }

    public String getMaterial_name() {
        return material_name;
    }

    public void setMaterial_name(String material_name) {
        this.material_name = material_name;
    }

    public String getMaterial_amount() {
        return material_amount;
    }

    public void setMaterial_amount(String material_amount) {
        this.material_amount = material_amount;
    }

    public String getMaterial_picture() {
        return material_picture;
    }

    public void setMaterial_picture(String material_picture) {
        this.material_picture = material_picture;
    }
}
