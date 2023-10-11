package com.example.pruebaenviogmap;


import static com.example.pruebaenviogmap.DataAddresses.Addresses;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DirectionAdapter adapter;
    public static int map = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DirectionAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        Button openMapsButton = findViewById(R.id.openMapsButton);
        Button getDirections = findViewById(R.id.getDirection);
        TextView txtDirection = findViewById(R.id.txtDirection);
        TextView txtQuantityAddresses = findViewById(R.id.txtQuantityAddresses);


        getDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                adapter.resetBackgrounds(true);
                Context context = MainActivity.this;
                String[] preferencesDirecciones = Addresses();
                int quantityAddresses = preferencesDirecciones.length;
                boolean savedSuccessful = Preferences.saveAddresses(context, preferencesDirecciones);

                if (savedSuccessful) {
                    showDialogChooseMap();
                    Toast.makeText(context, "Direcciones guardadas", Toast.LENGTH_LONG).show();
                    String texto = "Tienes " + quantityAddresses + " entregas";
                    txtQuantityAddresses.setText(texto);
                    txtQuantityAddresses.setVisibility(View.VISIBLE);

                    List<Direction> addresses = new ArrayList<>();
                    for (String directionString : preferencesDirecciones) {
                        Direction direction = new Direction(directionString);
                        addresses.add(direction);
                    }

                    adapter.updateAddresses(addresses);
                } else {
                    Toast.makeText(context, "Error al guardar direcciones", Toast.LENGTH_LONG).show();
                }

                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    View itemView = recyclerView.getChildAt(i);
                    itemView.setBackgroundResource(R.drawable.border_black);
                }
            }
        });


        openMapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(map != 1) {
                    List<Direction> selectedaddresses = adapter.getSelectedAddresses();
                    openGoogleMapsWithSelectedDirections(selectedaddresses);
                }else{
                    try {
                        List<Direction> selectedaddresses = adapter.getSelectedAddresses();
                        abrirWazeConDireccionesSeleccionadas(selectedaddresses);
                    } catch (Exception e){
                        Toast.makeText(MainActivity.this, "Waze no está instalado en tu dispositivo.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void showDialogChooseMap() {
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.dialog_mapa);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        dialog.setCanceledOnTouchOutside(false);
        Button btnGMaps = dialog.findViewById(R.id.btnGMaps);
        Button btnWaze = dialog.findViewById(R.id.btnWaze);

        btnGMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map = 0;
                dialog.dismiss();
            }
        });

        btnWaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map = 1;
                dialog.dismiss();
            }
        });
        dialog.show();
    }



    private void showDialogLimitReached() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_limit_exceeded);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        Button btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showDialogLimitReachedWaze() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_limit_exceeded_waze);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        Button btnClose = dialog.findViewById(R.id.btnClose);
        Button btnGmapAviso = dialog.findViewById(R.id.btnGmapsAviso);
        btnGmapAviso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map = 0;
                dialog.dismiss();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void handleLimitReached() {
        showDialogLimitReached();
    }

    void handleWazeLimitReached() {
        showDialogLimitReachedWaze();
    }

    private void openGoogleMapsWithSelectedDirections(List<Direction> adresses) {
        boolean areSelectedAddresses = false;
        String finalDestination = "";
        StringBuilder uriBuilder = new StringBuilder("https://www.google.com/maps/dir/?api=1&travelmode=driving&dir_action=navigate");

        for (int i = adresses.size() - 1; i >= 0; i--) {
            Direction direction = adresses.get(i);
            if (direction.isSelected()) {
                finalDestination = direction.getName();
                break;
            }
        }

        for (int i = 0; i < adresses.size(); i++) {
            Direction direccion = adresses.get(i);
            if (direccion.isSelected() && !direccion.getName().equals(finalDestination)) {
                if (!areSelectedAddresses) {
                    // Agrega el destino final
                    uriBuilder.append("&destination=").append(finalDestination);
                    uriBuilder.append("&waypoints=");
                } else {
                    uriBuilder.append("|");
                }
                uriBuilder.append(direccion.getName());
                areSelectedAddresses = true;
            }
        }

        if (areSelectedAddresses) {
            Uri gmmIntentUri = Uri.parse(uriBuilder.toString());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        } else {
            if(finalDestination == ""){
                Toast.makeText(this, "No se han seleccionado direcciones.", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Selecciona al menos 2 direcciones", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void abrirWazeConDireccionesSeleccionadas(List<Direction> direcciones) {
        boolean areSelectedAddresses = false;
        String finalDestination = "";

        for (int i = direcciones.size() - 1; i >= 0; i--) {
            Direction direccion = direcciones.get(i);
            if (direccion.isSelected()) {
                finalDestination = direccion.getName();
                break;
            }
        }

        StringBuilder uriBuilder = new StringBuilder("https://waze.com/ul?");

        for (int i = 0; i < direcciones.size(); i++) {
            Direction direccion = direcciones.get(i);
            if (direccion.isSelected()) {

                uriBuilder.append("q=").append(direccion.getName().replace(" ", "+"));
                areSelectedAddresses = true;
                break;
            }
        }


        if (areSelectedAddresses) {
            Uri wazeIntentUri = Uri.parse(uriBuilder.toString());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, wazeIntentUri);
            mapIntent.setPackage("com.waze");
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "No se han seleccionado direcciones.", Toast.LENGTH_SHORT).show();
        }
    }



}
