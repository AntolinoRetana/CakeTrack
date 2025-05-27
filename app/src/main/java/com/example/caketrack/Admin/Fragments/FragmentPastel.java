package com.example.caketrack.Admin.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caketrack.Admin.Pasteles.Adapter.PastelesAdapter;
import com.example.caketrack.Admin.Pasteles.Moduls.Pasteles;
import com.example.caketrack.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class FragmentPastel extends Fragment {

    private RecyclerView recyclerPasteles;
    private PastelesAdapter pastelAdapter;
    private ArrayList<Pasteles> listaPasteles = new ArrayList<>();
    private ArrayList<String> listaUIDs = new ArrayList<>();
    private FloatingActionButton fabAgregar;
    private static final int PICK_IMAGE_REQUEST = 100;
    private Uri imageUriSeleccionada;
    private ImageView imageViewPreview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pastel, container, false);

        recyclerPasteles = view.findViewById(R.id.recyclerPasteles);
        fabAgregar = view.findViewById(R.id.fabAgregarPastel);

        recyclerPasteles.setLayoutManager(new LinearLayoutManager(getContext()));
        pastelAdapter = new PastelesAdapter(getContext(), listaPasteles, listaUIDs);
        recyclerPasteles.setAdapter(pastelAdapter);

        cargarPastelesDesdeFirebase();

        fabAgregar.setOnClickListener(v -> mostrarDialogoAgregarPastel());

        return view;
    }

    private void cargarPastelesDesdeFirebase() {
        FirebaseDatabase.getInstance().getReference("pasteles")
                .get()
                .addOnSuccessListener(snapshot -> {
                    listaPasteles.clear();
                    listaUIDs.clear();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Pasteles p = snap.getValue(Pasteles.class);
                        if (p != null) {
                            listaPasteles.add(p);
                            listaUIDs.add(snap.getKey());
                        }
                    }
                    pastelAdapter.notifyDataSetChanged();
                });
    }

    private void mostrarDialogoAgregarPastel() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_agregar_pastel, null);

        EditText etNombre = dialogView.findViewById(R.id.etNombrePastel);
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcionPastel);
        EditText etPrecio = dialogView.findViewById(R.id.etPrecioPastel);
        EditText etTamano = dialogView.findViewById(R.id.etTamanoPastel);
        EditText etCantidad = dialogView.findViewById(R.id.etCantidadPastel);
        EditText etDisponible = dialogView.findViewById(R.id.etDisponiblePastel);
        imageViewPreview = dialogView.findViewById(R.id.imageViewPastel);
        Button btnSeleccionar = dialogView.findViewById(R.id.btnSeleccionarImagen);

        btnSeleccionar.setOnClickListener(v -> abrirGaleria());

        new AlertDialog.Builder(getContext())
                .setTitle("Agregar Pastel")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = etNombre.getText().toString().trim();
                    String descripcion = etDescripcion.getText().toString().trim();
                    double precio = Double.parseDouble(etPrecio.getText().toString().trim());
                    String tamano = etTamano.getText().toString().trim();
                    int cantidad = Integer.parseInt(etCantidad.getText().toString().trim());
                    boolean disponible = etDisponible.getText().toString().trim().equals("1");

                    if (imageUriSeleccionada != null) {
                        StorageReference ref = FirebaseStorage.getInstance().getReference("pasteles")
                                .child(System.currentTimeMillis() + ".jpg");

                        ref.putFile(imageUriSeleccionada)
                                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                                    String url = uri.toString();
                                    guardarPastel(nombre, descripcion, precio, tamano, disponible, cantidad, url);
                                }));
                    } else {
                        guardarPastel(nombre, descripcion, precio, tamano, disponible, cantidad, "");
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUriSeleccionada = data.getData();
            imageViewPreview.setImageURI(imageUriSeleccionada);
        }
    }

    private void guardarPastel(String nombre, String descripcion, double precio, String tamano, boolean disponible, int cantidad, String imageUrl) {
        FirebaseDatabase.getInstance().getReference("pasteles")
                .get()
                .addOnSuccessListener(snapshot -> {
                    long count = snapshot.getChildrenCount();
                    String pastelId = "pastel" + (count + 1);
                    String firebaseId = FirebaseDatabase.getInstance().getReference("pasteles").push().getKey();

                    Pasteles nuevo = new Pasteles(pastelId, nombre, descripcion, precio, tamano, disponible, cantidad);
                    nuevo.setImagenUrl(imageUrl);

                    FirebaseDatabase.getInstance().getReference("pasteles")
                            .child(firebaseId)
                            .setValue(nuevo)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Pastel agregado", Toast.LENGTH_SHORT).show();
                                cargarPastelesDesdeFirebase();
                            });
                });
    }
}
