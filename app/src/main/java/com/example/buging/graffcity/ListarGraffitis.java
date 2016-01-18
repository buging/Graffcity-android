package com.example.buging.graffcity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Buging on 02-01-2016.
 */
public class ListarGraffitis extends Fragment {


    private ListView photos;
    private Context context;

    private ArrayList<Datos> arraydatos;
    private MySimpleArrayAdapter adapter;

    private int inicio;
    private int fin;
    private int puedo;
    private int cont;

    private List<String> id_graffiti;
    private List<String> nombre_graffiti;
    private List<String> descripcion_graffiti;
    private List<String> link_graffiti1;
    private List<String> link_graffiti2;
    private List<String> link_graffiti3;
    private List<String> link_graffiti4;
    private List<String> puntuacion_graffiti;
    //private MySimpleArrayAdapter adapter;
    private ProgressBar progress;

    private int currentScrollState;
    private int currentFirstVisibleItem;
    private int currentVisibleItemCount;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        photos = (ListView) rootView.findViewById(R.id.listViewGraffitis);
        context = rootView.getContext();
        progress = (ProgressBar) rootView.findViewById(R.id.progress_list);
        //populateListView();
        id_graffiti = new ArrayList<String>();
        nombre_graffiti = new ArrayList<String>();
        descripcion_graffiti = new ArrayList<String>();
        link_graffiti1 = new ArrayList<String>();
        link_graffiti2 = new ArrayList<String>();
        link_graffiti3 = new ArrayList<String>();
        link_graffiti4 = new ArrayList<String>();
        puntuacion_graffiti = new ArrayList<String>();

        arraydatos = new ArrayList<Datos>();
        adapter = new MySimpleArrayAdapter(context,arraydatos);
        photos.setAdapter(adapter);

        currentScrollState = 0;
        currentFirstVisibleItem = 0;
        currentVisibleItemCount = 0;

        inicio = 0;//0
        fin = 5;//5
        puedo = 1;
        cont = 0;

        progress.setVisibility(View.VISIBLE);

        SystemUtilities su = new SystemUtilities(getActivity().getApplicationContext());
        final String internet;
        if(su.isNetworkAvailable()){
            internet = "si";
        }else{
            internet = "no";
            progress.setVisibility(View.INVISIBLE);
            Toast.makeText(context, "No tiene conexión a internet", Toast.LENGTH_SHORT).show();
        }

        photos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent ver_graffiti = new Intent(context, VerGraffiti.class);
                    Bundle bolsa = new Bundle();
                    bolsa.putString("id", id_graffiti.get(position));
                    bolsa.putString("link1", link_graffiti1.get(position));
                    bolsa.putString("link2", link_graffiti2.get(position));
                    bolsa.putString("link3", link_graffiti3.get(position));
                    bolsa.putString("link4", link_graffiti4.get(position));
                    bolsa.putString("puntuacion", puntuacion_graffiti.get(position));
                    bolsa.putString("nombre", nombre_graffiti.get(position));
                    bolsa.putString("descripcion", descripcion_graffiti.get(position));
                    ver_graffiti.putExtras(bolsa);
                    startActivity(ver_graffiti);
            }

        });

        photos.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                currentScrollState = scrollState;
                isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                currentFirstVisibleItem = firstVisibleItem;
                currentVisibleItemCount = visibleItemCount;
            }

            private void isScrollCompleted() {
                if (currentVisibleItemCount > 0 && currentScrollState == SCROLL_STATE_IDLE) {
                    if(puedo == 1) {
                        puedo = 0;
                        progress.setVisibility(View.VISIBLE);
                        new ListaGraffitis().execute(internet);
                    }
                }
            }
        });

        new ListaGraffitis().execute(internet);

        return rootView;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Clase que es la encargada de realizar la consulta al servicio django
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private class ListaGraffitis extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            if (urls[0] == "si") {

                Consultas c = new Consultas();
                String r = c.graffitisListados(inicio, fin);

                if (r.length() < 5) {
                    return "no";
                } else {
                    return r;
                }
            }else{
                return "no";
            }
        }
        /////////////////////////////////////////////////////////////////////////////////////////////////////
        //Una vez terminada la consulta nos devolvera un string el cual será utilizado como arreglo para
        //marcar los lugares cercanos en el mapa de google maps
        /////////////////////////////////////////////////////////////////////////////////////////////////////
        @Override
        protected void onPostExecute(String result) {

            if(result == "no"){
                Toast.makeText(context, "No se han podido cargar graffitis", Toast.LENGTH_LONG).show();
                progress.setVisibility(View.INVISIBLE);
                return;
            }

            JsonHandler js = new JsonHandler();
            String[][] arreglo = js.getGraffitisLista(result);

            //se recorre el arreglo y dependiendo de a la categoria que pertenece el lugar se ´pintara de ciertos colores el marker.
            if(arreglo[0][0] != "no") {

                Datos datos;
                for (int i = 0; i < arreglo.length; i++) {
                    id_graffiti.add(arreglo[i][2]);
                    nombre_graffiti.add(arreglo[i][4]);
                    descripcion_graffiti.add(arreglo[i][1]);
                    link_graffiti1.add(arreglo[i][3]);
                    link_graffiti2.add(arreglo[i][7]);
                    link_graffiti3.add(arreglo[i][8]);
                    link_graffiti4.add(arreglo[i][9]);
                    puntuacion_graffiti.add(arreglo[i][10]);
                    datos =  new Datos(link_graffiti1.get(cont),nombre_graffiti.get(cont), descripcion_graffiti.get(cont), Integer.parseInt(puntuacion_graffiti.get(cont)));
                    arraydatos.add(datos);
                    adapter.notifyDataSetChanged();
                    cont++;
                }

                inicio = fin+1;
                fin = fin+5;
                puedo = 1;
                progress.setVisibility(View.INVISIBLE);

            }

        }
    }
}
