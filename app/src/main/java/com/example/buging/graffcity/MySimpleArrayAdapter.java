package com.example.buging.graffcity;

/**
 * Created by Buging on 02-01-2016.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MySimpleArrayAdapter extends BaseAdapter {
    protected Context context;
    protected ArrayList<Datos> items;

    private static final String PIC_URL = "screenslidepagefragment.picurl";
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public MySimpleArrayAdapter(Context context, ArrayList<Datos> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item, parent, false);
        }

        Datos datos = items.get(position);
        Datos dir = items.get(position);

        TextView tv_nombre = (TextView) v.findViewById(R.id.nombre_item);
        tv_nombre.setText(datos.getNombre());

        TextView tv_descripcion = (TextView) v.findViewById(R.id.descripcion_item);
        tv_descripcion.setText(datos.getDescripcion());

        RatingBar rb_puntuacion = (RatingBar) v.findViewById(R.id.valoracion_item);
        rb_puntuacion.setRating(datos.getPuntuacion());

        ImageView imageView = (ImageView) v.findViewById(R.id.foto_item);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(null)
                .showImageForEmptyUri(null)
                .showImageOnFail(null)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));

        imageLoader.getInstance()
                .displayImage(datos.getFoto(), imageView, options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {
                    }
                });

        return v;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /*public class MySimpleArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private String[] values;
    private List<String> nombre_graffity;
    //private List<String> descripcion_graffity;
    //private List<String> link_graffity;
    //private List<String> puntaje_graffity;

    private static final String PIC_URL = "screenslidepagefragment.picurl";
    private ImageLoader imageLoader;
    private DisplayImageOptions options;


    public MySimpleArrayAdapter(Context context ,String[] values, List<String> nombre_graffity) {
        super(context, R.layout.list_item, values);
        this.context = context;
        this.values = values;
        this.nombre_graffity = nombre_graffity;
        //this.descripcion_graffity = descripcion_graffity;
        //this.link_graffity = link_graffity;
        //this.puntaje_graffity = puntajeGraffity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item, parent, false);
        TextView tv_nombre = (TextView) rowView.findViewById(R.id.nombre_item);
        TextView tv_descripcion = (TextView) rowView.findViewById(R.id.descripcion_item);
        RatingBar rb_puntuacion = (RatingBar) rowView.findViewById(R.id.valoracion_item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.foto_item);
        tv_nombre.setText(nombre_graffity.get(position));
        tv_descripcion.setText(nombre_graffity.get(position).substring(0, 32)+"...");
        rb_puntuacion.setRating(0);

        imageView.setImageResource(R.drawable.logo);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(null)
                .showImageForEmptyUri(null)
                .showImageOnFail(null)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));

        imageLoader.getInstance()
                .displayImage(link_graffity.get(position), imageView, options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {
                    }
                });



        return rowView;
    }*/



    /*private final Context context;
    private final String[] values;

    public MySimpleArrayAdapter(Context context, String[] values) {
        super(context, R.layout.list_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.nombre_item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.foto_item);
        textView.setText(values[position]);
        // Change the icon for Windows and iPhone
        String s = values[position];
        if (s.startsWith("Rojo") || s.startsWith("Verde")) {
            imageView.setImageResource(R.drawable.logo);
        } else {
            imageView.setImageResource(R.drawable.agregar_image);
        }

        return rowView;
    }*/
}