package tw.org.iii.hellokitchen.Activity;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import tw.org.iii.hellokitchen.R;
import tw.org.iii.hellokitchen.Utility.CustomOkHttp3Downloader;

public class ActRecipeDetail extends AppCompatActivity
{
    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_recipe_detail);
        InitialComponent();
    }

    private void InitialComponent()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.detail_toolbar_layout);
        Bundle bundle = getIntent().getExtras();
        String recipeDetailTitle = bundle.getString("recipeTitle");
        String recipeImageURL = bundle.getString("recipeImageURL");
        Log.d("recipeTitle",recipeDetailTitle);
        Log.d("recipeImageURL",recipeImageURL);
        try
        {
            Picasso picasso = new Picasso.Builder(this)
                    .downloader(new CustomOkHttp3Downloader(this))
                    .build();
            Picasso.setSingletonInstance(picasso);
        } catch (IllegalStateException ignored)
        {

        }
        collapsingToolbarLayout.setTitle(recipeDetailTitle);
        Picasso.with(this).load(recipeImageURL).config(Bitmap.Config.ALPHA_8).into(new Target()
        {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
            {
                collapsingToolbarLayout.setBackground(new BitmapDrawable(getApplicationContext().getResources(), bitmap));
                collapsingToolbarLayout.getBackground().setAlpha(75);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable)
            {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable)
            {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.detail_fab);
        fab.setVisibility(View.INVISIBLE);
         /*
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
