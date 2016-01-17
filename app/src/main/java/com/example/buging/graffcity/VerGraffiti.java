package com.example.buging.graffcity;

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

import com.github.snowdream.android.widget.SmartImageView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Buging on 27-12-2015.
 */
public class VerGraffiti  extends AppCompatActivity {

    private List<String> IMAGES;
    private float startX;
    private float startY;
    private int posicion = 0;

    private TextView tv_nombre;
    private TextView tv_descripcion;
    private RatingBar rb_puntuacion;
    private String id_graffiti;
    private String nombre_graffiti;
    private String descripcion_graffiti;
    private String link_graffiti1;
    private String link_graffiti2;
    private String link_graffiti3;
    private String link_graffiti4;
    private int id_usuario;
    private boolean tiene_cal;
    private float calificacion;
    private int update_calificacion;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.layout_ver_graffiti);
        setContentView(R.layout.layout_ver_graffiti);
        tv_nombre = (TextView) findViewById(R.id.tv_nombre);
        tv_descripcion = (TextView) findViewById(R.id.tv_descripcion);
        rb_puntuacion = (RatingBar) findViewById(R.id.valoracion);
        pager = (ViewPager) findViewById(R.id.pager);
        IMAGES = new ArrayList<String>();


        tiene_cal = false;
        calificacion = 0;

        rb_puntuacion.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //rb_puntuacion.setClickable(false);
                if (rating != calificacion) {
                    rb_puntuacion.setEnabled(false);
                    update_calificacion = (int) rating;
                    new CargarDatosGraffiti().execute("calificar");

                }
            }
        });


        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                posicion = position;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        pager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP: {
                        float endX = event.getX();
                        float endY = event.getY();
                        if (isAClick(startX, endX, startY, endY)) {
                            Intent ver_graffiti_full = new Intent(getApplicationContext(), ImageFill.class);
                            Bundle bolsa = new Bundle();
                            bolsa.putString("link1", link_graffiti1);
                            bolsa.putString("link2", link_graffiti2);
                            bolsa.putString("link3", link_graffiti3);
                            bolsa.putString("link4", link_graffiti4);
                            bolsa.putString("posicion",Integer.toString(posicion));
                            ver_graffiti_full.putExtras(bolsa);
                            startActivity(ver_graffiti_full);
                        }
                        break;
                    }
                }
                return false;
            }
        });

        //se extraer datos de graffiti
        Intent in = getIntent();
        Bundle bolsa = in.getExtras();
        id_graffiti = bolsa.getString("id");
        nombre_graffiti = bolsa.getString("nombre");
        descripcion_graffiti = bolsa.getString("descripcion");
        link_graffiti1 = bolsa.getString("link1");
        link_graffiti2 = bolsa.getString("link2");
        link_graffiti3 = bolsa.getString("link3");
        link_graffiti4 = bolsa.getString("link4");
        SharedPreferences settings = getApplicationContext().getSharedPreferences("session", 0);
        id_usuario = settings.getInt("id", 0);

        new CargarDatosGraffiti().execute("foto");

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

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////7
        //visualizar imagenes
        ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

        pagerAdapter.addAll(IMAGES);
        pager.setAdapter(pagerAdapter);

        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    }


    //detecta si la variación en la foto es un click o no
    private boolean isAClick(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        if (differenceX > 5  || differenceY > 5) {
            return false;
        }
        return true;
    }

    private void setNombre(String info) {

        tv_nombre.setText(info);
        tv_nombre.setMovementMethod(new ScrollingMovementMethod());
    }

    private void setDescripcion(String info) {

        tv_descripcion.setText(info);
        tv_descripcion.setMovementMethod(new ScrollingMovementMethod());
    }

    private class CargarDatosGraffiti extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            if (urls[0] == "foto") {
                Consultas c = new Consultas();
                calificacion = c.tieneCalificacion(id_graffiti,Integer.toString(id_usuario));
                return "foto";
            }else if (urls[0] == "calificar") {
                Consultas c = new Consultas();
                Boolean s = c.calificar(tiene_cal, Integer.toString(id_usuario), id_graffiti, Integer.toString(update_calificacion));
                if(s==true){
                    return "correcto";
                }else{
                    return "fallido";
                }
            }
            return "";

        }
        /////////////////////////////////////////////////////////////////////////////////////////////////////
        //
        /////////////////////////////////////////////////////////////////////////////////////////////////////
        @Override
        protected void onPostExecute(String result) {
            if(result == "foto") {
                try {
                    //Rect rect = new Rect(foto_graffiti.getLeft(), foto_graffiti.getTop(), foto_graffiti.getRight(), foto_graffiti.getBottom());
                    //foto_graffiti.setImageUrl(link_graffiti, rect);
                    if(calificacion==-1){
                        calificacion = 0;
                        rb_puntuacion.setRating(0);
                        tiene_cal = false;
                    }else{
                        rb_puntuacion.setRating(calificacion);
                        tiene_cal = true;
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplication(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                }

                setNombre(nombre_graffiti);
                setDescripcion(descripcion_graffiti);
            }else if(result == "correcto"){
                tiene_cal = true;
                rb_puntuacion.setEnabled(true);
                rb_puntuacion.setRating(update_calificacion);
                calificacion = (float)update_calificacion;
                Toast.makeText(getApplication(), "Calificación realizada con exito", Toast.LENGTH_SHORT).show();
            }else if(result == "fallido"){
                rb_puntuacion.setEnabled(true);
                rb_puntuacion.setRating(update_calificacion);
                Toast.makeText(getApplication(), "Error al realizar la calificación", Toast.LENGTH_SHORT).show();

            }
        }
    }

}
