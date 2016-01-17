package com.example.buging.graffcity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.ScaleGestureDetector;
import android.widget.Toast;

import com.github.snowdream.android.widget.SmartImageView;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Buging on 27-12-2015.
 */
public class ImageFill extends AppCompatActivity{

    private List<String> IMAGES;
    private SmartImageView foto_graffiti_fill;
    private String link_graffiti1;
    private String link_graffiti2;
    private String link_graffiti3;
    private String link_graffiti4;
    private int posicion;
    private ViewPager pager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_fil);

        Intent in = getIntent();
        Bundle bolsa = in.getExtras();
        link_graffiti1 = bolsa.getString("link1");
        link_graffiti2 = bolsa.getString("link2");
        link_graffiti3 = bolsa.getString("link3");
        link_graffiti4 = bolsa.getString("link4");
        posicion = Integer.parseInt(bolsa.getString("posicion"));
        pager = (ViewPager) findViewById(R.id.pager_fil);
        IMAGES = new ArrayList<String>();

        if(link_graffiti1!=""){
            IMAGES.add(link_graffiti1);
        }
        if(link_graffiti2!=""){
            IMAGES.add(link_graffiti2);
        }
        if(link_graffiti3!=""){
            IMAGES.add(link_graffiti3);
        }
        if(link_graffiti4!=""){
            IMAGES.add(link_graffiti4);
        }

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //visualizar imagenes
        ScreenSlidePagerAdapterFil pagerAdapter = new ScreenSlidePagerAdapterFil(getSupportFragmentManager());

        pagerAdapter.addAll(IMAGES);
        pager.setAdapter(pagerAdapter);

        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator_fil);
        indicator.setViewPager(pager);
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////

        pager.setCurrentItem(posicion);

    }

    /*private class CargarImagenGraffiti extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            return urls[0];

        }
        /////////////////////////////////////////////////////////////////////////////////////////////////////
        //
        /////////////////////////////////////////////////////////////////////////////////////////////////////
        @Override
        protected void onPostExecute(String result) {
            try{
                Rect rect = new Rect(foto_graffiti_fill.getLeft(),foto_graffiti_fill.getTop(),foto_graffiti_fill.getRight(),foto_graffiti_fill.getBottom());
                foto_graffiti_fill.setImageUrl(result,rect);
            }catch(Exception e){
                Toast.makeText(getApplication(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }*/

}
