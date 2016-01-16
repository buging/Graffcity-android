package com.example.buging.graffcity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
//import com.google.android.gms.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Buging on 26-12-2015.
 */
public class PublicarGraffity extends Fragment{

    private String APP_DIRECTORY = "myPictureApp/";
    private String MEDIA_DIRECTORY = APP_DIRECTORY + "media";
    private String[] TEMPORAL_PICTURE_NAME = new String[] {"temporal.jpg","tempora2.jpg","tempora3.jpg","tempora4.jpg"};


    private final int PHOTO_CODE = 100;
    private final int SELECT_PICTURE = 200;

    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;

    private Cloudinary cloudinary;
    private JSONObject Result;
    private File file;
    private Button uploadImage;
    private String url_image1;
    private String url_image2;
    private String url_image3;
    private String url_image4;
    private View formPublicaGraffity;
    private View mProgressView;

    private String[] dir = new String[] {"","","",""};
    private int pos_dir;

    private EditText et_nombre;
    private EditText et_descripcion;
    private Spinner s_ubicacion;
    private Spinner s_autor;

    private int id_autor;
    private int opcion_autor;
    private int opcion_ubicacion;
    private int obtener_gps = 1;
    private double latitud = 0;
    private double longitud = 0;
    private double lis_latitud = 0;
    private double lis_longitud = 0;
    private String nom;
    private String des;
    private LocationManager verificador_gps;

    private GoogleMap map;
    private MapView mapView;
    private Context context;
    private boolean realizarZoom = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_publicar_graffity, container, false);
        context = rootView.getContext();

        //Se le coloca el icono para agregar una imagen
        imageView1 = (ImageView) rootView.findViewById(R.id.setPicture1);
        imageView2 = (ImageView) rootView.findViewById(R.id.setPicture2);
        imageView3 = (ImageView) rootView.findViewById(R.id.setPicture3);
        imageView4 = (ImageView) rootView.findViewById(R.id.setPicture4);
        uploadImage = (Button) rootView.findViewById(R.id.uploadImage);
        imageView1.setImageResource(R.drawable.agregar_image);
        imageView2.setImageResource(R.drawable.agregar_image);
        imageView3.setImageResource(R.drawable.agregar_image);
        imageView4.setImageResource(R.drawable.agregar_image);

        formPublicaGraffity = rootView.findViewById(R.id.ScrollViewPublicarGraffity);
        mProgressView = rootView.findViewById(R.id.publicar_graffiti_progress);


        et_nombre = (EditText) rootView.findViewById(R.id.et_nombre_graffiti);
        et_descripcion = (EditText) rootView.findViewById(R.id.et_descripcion_graffiti);
        s_ubicacion = (Spinner) rootView.findViewById(R.id.spinnerUbicacion);
        s_autor = (Spinner) rootView.findViewById(R.id.spinnerAutor);

        String[] ubicaciones = {"GPS", "Anonima"};

        SharedPreferences settings = getActivity().getSharedPreferences("session", 0);
        String a = settings.getString("nickName", "").toString();
        String[] autores = {a, "Anonimo"};

        opcion_autor = 0;
        opcion_ubicacion = 0;

        id_autor = settings.getInt("id", 0);

        /////////////////////////////////////////////////////////////////////////////////
        mapView = (MapView) rootView.findViewById(R.id.mapa_salvacion);
        mapView.onCreate(savedInstanceState);
        map = mapView.getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);

        //ver si gps esta a la escucha
        verificador_gps = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        ////////////////////////////////////////////////////////////////////////////////

        SystemUtilities su = new SystemUtilities(getActivity().getApplicationContext());
        final String internet;
        if(su.isNetworkAvailable()){
            internet = "si";
        }else{
            internet = "no";
        }

        if(internet == "si") {
            if (verificador_gps.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);//creamos un zoom para ver el mapa
                map.animateCamera(zoom);//aplicamos el zoom al mapa
                map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        //se obtiene la latitud y longitud indicada por el gps
                        lis_latitud = location.getLatitude();
                        lis_longitud = location.getLongitude();
                        SystemClock.sleep(1000);

                        if (realizarZoom == false && lis_latitud != 0 && lis_longitud != 0) {
                            realizarZoom = true;

                            CameraUpdate cam = CameraUpdateFactory.newLatLng(new LatLng(lis_latitud, lis_longitud));
                            map.animateCamera(cam);//realizar ajuste de pantalla

                            map.setOnMyLocationChangeListener(null); //se termina el proceso de escucha del gps tan solo se realiza una vez

                            Toast.makeText(getActivity(), "Mapa se esta posicionando", Toast.LENGTH_LONG).show();

                        }


                    }
                });
            } else {
                //******  HERE's the PROBLEM  ********
                Toast.makeText(getActivity(), "Debe activar GPS", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getActivity(), "No tiene conexión a internet", Toast.LENGTH_LONG).show();
        }

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos_dir = 0;
                final CharSequence[] options = {"Tomar foto", "Elegir de galeria","Eliminar Foto", "Cancelar"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Abrir desde");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int seleccion) {
                        if (options[seleccion] == "Tomar foto") {
                            openCamera();
                        } else if (options[seleccion] == "Elegir de galeria") {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent, "Selecciona app de imagen"), SELECT_PICTURE);
                        } else if (options[seleccion] == "Eliminar Foto") {
                            imageView1.setImageResource(R.drawable.agregar_image);
                            dir[pos_dir] = "";
                            dialog.dismiss();
                        } else if (options[seleccion] == "Cancelar") {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos_dir = 1;
                final CharSequence[] options = {"Tomar foto", "Elegir de galeria","Eliminar Foto", "Cancelar"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Abrir desde");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int seleccion) {
                        if (options[seleccion] == "Tomar foto") {
                            openCamera();
                        } else if (options[seleccion] == "Elegir de galeria") {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent, "Selecciona app de imagen"), SELECT_PICTURE);
                        } else if (options[seleccion] == "Eliminar Foto") {
                            imageView2.setImageResource(R.drawable.agregar_image);
                            dir[pos_dir] = "";
                            dialog.dismiss();
                        } else if (options[seleccion] == "Cancelar") {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos_dir = 2;
                final CharSequence[] options = {"Tomar foto", "Elegir de galeria","Eliminar Foto", "Cancelar"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Abrir desde");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int seleccion) {
                        if (options[seleccion] == "Tomar foto") {
                            openCamera();
                        } else if (options[seleccion] == "Elegir de galeria") {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent, "Selecciona app de imagen"), SELECT_PICTURE);
                        } else if (options[seleccion] == "Eliminar Foto") {
                            imageView3.setImageResource(R.drawable.agregar_image);
                            dir[pos_dir] = "";
                            dialog.dismiss();
                        } else if (options[seleccion] == "Cancelar") {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos_dir = 3;
                final CharSequence[] options = {"Tomar foto", "Elegir de galeria","Eliminar Foto", "Cancelar"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Abrir desde");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int seleccion) {
                        if (options[seleccion] == "Tomar foto") {
                            openCamera();
                        } else if (options[seleccion] == "Elegir de galeria") {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent, "Selecciona app de imagen"), SELECT_PICTURE);
                        } else if (options[seleccion] == "Eliminar Foto") {
                            imageView4.setImageResource(R.drawable.agregar_image);
                            dir[pos_dir] = "";
                            dialog.dismiss();
                        } else if (options[seleccion] == "Cancelar") {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_nombre.getText().toString().length()<=3){
                    Toast.makeText(getActivity(), "Nombre de graffity no ingresado", Toast.LENGTH_LONG).show();
                    return;
                }else if(et_descripcion.getText().toString().length()<=3) {
                    Toast.makeText(getActivity(), "Descripción de graffity no ingresada", Toast.LENGTH_LONG).show();
                    return;
                }else if(dir[0] == "" && dir[1] == "" && dir[2] == "" && dir[3] == ""){
                    Toast.makeText(getActivity(), "Debe ingresar a lo menos una imagen.", Toast.LENGTH_LONG).show();
                    return;
                }
                uploadImage.setEnabled(false);
                showProgress(true);
                Toast.makeText(getActivity(), "Publicando graffity", Toast.LENGTH_LONG).show();
                nom = et_nombre.getText().toString();
                des = et_descripcion.getText().toString();
                HashMap config = new HashMap();
                config.put("cloud_name", "graffcity");
                config.put("api_key", "123528223989847");//I have changed the key and secret
                config.put("api_secret", "Qug_jj6le_fu5Gig89IAnD-sHhQ");
                cloudinary = new Cloudinary(config);
                new UploadImage(getActivity().getApplicationContext()).execute();
            }
        });

        s_ubicacion.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, ubicaciones));
        s_ubicacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                if (position == 0) {
                    //y se realiza la busqueda de los lugares cercanos
                    SystemUtilities su = new SystemUtilities(getActivity().getApplicationContext());
                    final String internet;
                    if (su.isNetworkAvailable()) {
                        internet = "si";
                    } else {
                        internet = "no";
                    }

                    if (internet == "si") {
                        if (verificador_gps.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            latitud = lis_latitud;
                            longitud = lis_longitud;
                            opcion_ubicacion = 0;
                        } else {
                            //******  HERE's the PROBLEM  ********
                            latitud = 0;
                            longitud = 0;
                            opcion_ubicacion = 1;
                            Toast.makeText(getActivity(), "Debe activar GPS", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "No tiene conexión a internet", Toast.LENGTH_LONG).show();
                        opcion_ubicacion = 1;
                    }
                } else if (position == 1) {
                    opcion_ubicacion = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                latitud = lis_latitud;
                longitud = lis_longitud;
                opcion_ubicacion = 0;
            }
        });

        s_autor.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, autores));
        s_autor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                if (position == 0) {
                    opcion_autor = 0;
                } else if (position == 1) {
                    opcion_autor = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                opcion_autor = 0;
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    private void openCamera() {
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        file.mkdirs();

        dir[pos_dir] = Environment.getExternalStorageDirectory() + File.separator
                + MEDIA_DIRECTORY + File.separator + TEMPORAL_PICTURE_NAME[pos_dir];

        File newFile = new File(dir[pos_dir]);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
        startActivityForResult(intent, PHOTO_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PHOTO_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    dir[pos_dir] = Environment.getExternalStorageDirectory() + File.separator
                            + MEDIA_DIRECTORY + File.separator + TEMPORAL_PICTURE_NAME[pos_dir];
                    decodeBitmap(dir[pos_dir]);
                }
                break;

            case SELECT_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri path = data.getData();
                    dir[pos_dir] = getRealPathFromURI(getActivity(), path);
                    if(pos_dir == 0) {
                        imageView1.setImageURI(path);
                    }else if(pos_dir == 1){
                        imageView2.setImageURI(path);
                    }else if(pos_dir == 2){
                        imageView3.setImageURI(path);
                    }else if(pos_dir == 3){
                        imageView4.setImageURI(path);
                    }
                }
                break;
        }

    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void decodeBitmap(String ruta) {
        dir[pos_dir] = ruta;
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeFile(ruta);

        if(pos_dir == 0) {
            imageView1.setImageBitmap(bitmap);
        }else if(pos_dir == 1){
            imageView2.setImageBitmap(bitmap);
        }else if(pos_dir == 2){
            imageView3.setImageBitmap(bitmap);
        }else if(pos_dir == 3){
            imageView4.setImageBitmap(bitmap);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            formPublicaGraffity.setVisibility(show ? View.GONE : View.VISIBLE);
            formPublicaGraffity.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    formPublicaGraffity.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            formPublicaGraffity.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



    private class UploadImage extends AsyncTask<String, Void, String> {

        private Context context;
        private double lat;
        private double lon;
        private int id_au;

        public UploadImage(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";

            if(dir==null){
                return "Imagen no seleccionada";
            }

            try {
                if(dir[0] != ""){
                    file = new File(dir[0]);
                    Result = new JSONObject(cloudinary.uploader().upload(file, ObjectUtils.emptyMap()));
                    url_image1 = Result.optString("url");
                }else if(dir[0] == ""){
                    url_image1 = "";
                }

                if(dir[1] != ""){
                    file = new File(dir[1]);
                    Result = new JSONObject(cloudinary.uploader().upload(file, ObjectUtils.emptyMap()));
                    url_image2 = Result.optString("url");
                }else if(dir[1] == ""){
                    url_image2 = "";
                }

                if(dir[2] != ""){
                    file = new File(dir[2]);
                    Result = new JSONObject(cloudinary.uploader().upload(file, ObjectUtils.emptyMap()));
                    url_image3 = Result.optString("url");
                }else if(dir[2] == ""){
                    url_image3 = "";
                }

                if(dir[3] != ""){
                    file = new File(dir[3]);
                    Result = new JSONObject(cloudinary.uploader().upload(file, ObjectUtils.emptyMap()));
                    url_image4 = Result.optString("url");
                }else if(dir[3] == ""){
                    url_image4 = "";
                }

                if(opcion_ubicacion == 1){
                    lat = 0;
                    lon = 0;
                }else if(opcion_ubicacion  == 0){
                    lat = lis_latitud;
                    lon = lis_longitud;
                }

                if(opcion_autor == 0){
                    id_au = id_autor;
                }else if(opcion_autor == 1){
                    id_au = 1;
                }

                Consultas c = new Consultas();
                return c.publicarGraffitis(id_au, 1, nom, des, lat, lon, url_image1,url_image2,url_image3,url_image4, 0, false);


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            uploadImage.setEnabled(true);
            showProgress(false);
        }


    }
}
