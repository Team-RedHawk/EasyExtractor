package net.teamredhawk.hawktool;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import net.teamredhawk.hawktool.RecyclerView.Wallpaper.CategoryModel;
import net.teamredhawk.hawktool.RecyclerView.Wallpaper.ItemInterface;
import net.teamredhawk.hawktool.RecyclerView.Wallpaper.WallpaperAdapter;
import net.teamredhawk.hawktool.RecyclerView.Wallpaper.WallpaperModel;
import net.teamredhawk.hawktool.UtilsHelper.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static net.teamredhawk.hawktool.WallpaperActivity.AsyncTaskCategorysParseJson.*;

public class WallpaperActivity extends AppCompatActivity {
    private ArrayList<ItemInterface> mwallpaperAndSectionList = new ArrayList<>();
    private ArrayList<CategoryModel> categorylist = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private SwipeRefreshLayout swipeRefreshLayout;
    int recyclerViewPaddingTop;
    public int theme;
    private Context context;
    private Boolean error= false;
    private RecyclerView recyclerView;
    private WallpaperAdapter mAdapter;
    private String urlCategory = "https://app.teamredhawk.net/wallpapers/ws.php?format=json&method=pwg.categories.getList";
    private String urlImageFromCategory = "https://app.teamredhawk.net/wallpapers/ws.php?format=json&method=pwg.categories.getImages&cat_id=%s&per_page=3";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Selecciona el tema guardado por el usuario
        theme();
        setContentView(R.layout.activity_wallpaper);
        sharedPreferences = this.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        context= WallpaperActivity.this;
        //Configura el status bar y el toolbar
        toolbarStatusBar();
        setTitle(getResources().getString(R.string.custom_wallpaper));
        setUpRecycler();
        swipeToRefresh();
    }

    private void setUpRecycler() {
        recyclerView = findViewById(R.id.wall_recycler);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

            @Override
            public int getSpanSize(int position) {
                if (WallpaperAdapter.SECTION_VIEW == mAdapter.getItemViewType(position)) {
                    return 3;
                }
                return 1;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        new AsyncTaskCategorysParseJson().execute(urlCategory);
    }

    public class AsyncTaskCategorysParseJson extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
        }

        // Adquiriendo los datos con el plugin, el formato recibido es de tipo Json, asi que tenemos que pasarlo a string para poder usarlo.
        @Override
        protected String doInBackground(String... url) {

            urlCategory = url[0];
            try {
                JSONObject jsonObject= JsonParser.readJsonFromUrl(urlCategory);
                int categoryNumber = jsonObject.getJSONObject("result").getJSONArray("categories").length();
                JSONArray jsonArray = jsonObject.getJSONObject("result").getJSONArray("categories");
                for (int i = 0; i < categoryNumber; i++) {

                    categorylist.add(new CategoryModel(Html.fromHtml(jsonArray.getJSONObject(i).getString("name")).toString(), jsonArray.getJSONObject(i).getInt("id")));
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                categorylist.clear();
                error = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (error) {
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_LONG).show();
                mAdapter = new WallpaperAdapter(mwallpaperAndSectionList, context);
                recyclerView.setAdapter(mAdapter);
                swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
                swipeRefreshLayout.setRefreshing(false);
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);
            }else{
                new AsyncTaskWallsParseJson().execute(urlImageFromCategory);
            }
        }
    }

    public class AsyncTaskWallsParseJson extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
        }

        // Adquiriendo los datos con el plugin, el formato recibido es de tipo Json, asi que tenemos que pasarlo a string para poder usarlo.
        @Override
        protected String doInBackground(String... url) {

            urlImageFromCategory = url[0];
            try {
                for (CategoryModel categoria: categorylist){
                    String urlfromcategory = String.format(urlImageFromCategory,categoria.id);
                    JSONObject jsonObject= JsonParser.readJsonFromUrl(urlfromcategory);
                    int imagesNumber= jsonObject.getJSONObject("result").getJSONArray("images").length();
                    if (imagesNumber > 0){
                        JSONArray imagenes = jsonObject.getJSONObject("result").getJSONArray("images");
                        mwallpaperAndSectionList.add(categoria);
                        for (int i=0; i<imagesNumber; i++){
                            String urli= imagenes.getJSONObject(i).getJSONObject("derivatives").getJSONObject("2small").getString("url") ;
                            String vistas= String.valueOf(imagenes.getJSONObject(i).getInt("hit"));
                            String urlfull = imagenes.getJSONObject(i).getString("element_url");
                            mwallpaperAndSectionList.add(new WallpaperModel(urli,vistas,urlfull));
                        }
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                mwallpaperAndSectionList.clear();
                categorylist.clear();
                error = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (error){
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_LONG).show();
            }
            mAdapter = new WallpaperAdapter(mwallpaperAndSectionList, context);
            recyclerView.setAdapter(mAdapter);
            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
            swipeRefreshLayout.setRefreshing(false);
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void swipeToRefresh() {
        swipeRefreshLayout = findViewById(R.id.swipe_container);
        int start = convertToPx(0), end = recyclerViewPaddingTop + convertToPx(16);
        swipeRefreshLayout.setProgressViewOffset(true, start, end);
        TypedValue typedValueColorPrimary = new TypedValue();
        TypedValue typedValueColorAccent = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValueColorPrimary, true);
        getTheme().resolveAttribute(R.attr.colorAccent, typedValueColorAccent, true);
        final int colorPrimary = typedValueColorPrimary.data, colorAccent = typedValueColorAccent.data;
        swipeRefreshLayout.setColorSchemeColors(colorPrimary, colorAccent);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mwallpaperAndSectionList.clear();
                categorylist.clear();
                new AsyncTaskCategorysParseJson().execute(urlCategory);
            }
        });
    }

    public int convertToPx(int dp) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (dp * scale + 0.5f);
    }

    public void toolbarStatusBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void theme() {
        sharedPreferences = getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        theme = sharedPreferences.getInt("THEME", 1);
        settingTheme(theme);
    }

    public void settingTheme(int theme) {
        switch (theme) {
            case 1:
                setTheme(R.style.AppTheme);
                break;
            case 2:
                setTheme(R.style.AppTheme2);
                break;
            default:
                setTheme(R.style.AppTheme);
                break;
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(WallpaperActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }



}
