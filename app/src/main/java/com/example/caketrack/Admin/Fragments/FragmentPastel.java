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
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.caketrack.Admin.Pasteles.Adapter.PastelesAdapter;
import com.example.caketrack.Admin.Pasteles.Moduls.Pasteles;
import com.example.caketrack.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class FragmentPastel extends Fragment implements PastelesAdapter.OnImageSelectionListener {

    private RecyclerView recyclerPasteles;
    private PastelesAdapter pastelAdapter;
    private ArrayList<Pasteles> listaPasteles = new ArrayList<>();
    private ArrayList<String> listaUIDs = new ArrayList<>();
    private FloatingActionButton fabAgregar;

    // Para manejar selección de imágenes
    private static final int PICK_IMAGE_REQUEST_ADD = 100;
    private static final int PICK_IMAGE_REQUEST_EDIT = 101;
    private Uri imageUriSeleccionada;
    private ImageView imageViewPreview;
    private LinearLayout layoutPlaceholder; // Agregado para controlar el placeholder

    // Para manejar edición
    private int posicionEditando = -1;
    private String uidEditando = "";
    private Pasteles pastelEditando = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pastel, container, false);

        recyclerPasteles = view.findViewById(R.id.recyclerPasteles);
        fabAgregar = view.findViewById(R.id.fabAgregarPastel);

        recyclerPasteles.setLayoutManager(new LinearLayoutManager(getContext()));
        pastelAdapter = new PastelesAdapter(getContext(), listaPasteles, listaUIDs);
        pastelAdapter.setImageSelectionListener(this); // Configurar listener
        recyclerPasteles.setAdapter(pastelAdapter);

        cargarPastelesDesdeFirebase();

        fabAgregar.setOnClickListener(v -> mostrarDialogoAgregarPastel());
        verificarRolUsuario();

        return view;
    }

    // Implementación del callback para edición
    @Override
    public void onSelectImageForEdit(int position, String uid, Pasteles pastel) {
        posicionEditando = position;
        uidEditando = uid;
        pastelEditando = pastel;
        mostrarDialogoEditarPastel();
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
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al cargar pasteles: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        layoutPlaceholder = dialogView.findViewById(R.id.layoutPlaceholder);
        Button btnSeleccionarImagen = dialogView.findViewById(R.id.btnSeleccionarImagen);

        // Configurar estado inicial - mostrar placeholder, ocultar imagen
        mostrarPlaceholder();
        imageUriSeleccionada = null;

        // Configurar click en el placeholder para abrir galería
        layoutPlaceholder.setOnClickListener(v -> abrirGaleriaParaAgregar());
        btnSeleccionarImagen.setOnClickListener(v -> abrirGaleriaParaAgregar());

        new AlertDialog.Builder(getContext())
                .setTitle("Agregar Pastel")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    try {
                        String nombre = etNombre.getText().toString().trim();
                        String descripcion = etDescripcion.getText().toString().trim();
                        String precioStr = etPrecio.getText().toString().trim();
                        String tamano = etTamano.getText().toString().trim();
                        String cantidadStr = etCantidad.getText().toString().trim();
                        String disponibleStr = etDisponible.getText().toString().trim();

                        if (nombre.isEmpty() || precioStr.isEmpty() || cantidadStr.isEmpty() || disponibleStr.isEmpty()) {
                            Toast.makeText(getContext(), "Por favor completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        double precio = Double.parseDouble(precioStr);
                        int cantidad = Integer.parseInt(cantidadStr);
                        boolean disponible = disponibleStr.equals("1");

                        if (imageUriSeleccionada != null) {
                            subirImagenYGuardar(nombre, descripcion, precio, tamano, disponible, cantidad, imageUriSeleccionada, false);
                        } else {
                            guardarPastel(nombre, descripcion, precio, tamano, disponible, cantidad, "");
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Error en formato de números. Verifica precio y cantidad.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoEditarPastel() {
        if (pastelEditando == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_agregar_pastel, null);

        EditText etNombre = dialogView.findViewById(R.id.etNombrePastel);
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcionPastel);
        EditText etPrecio = dialogView.findViewById(R.id.etPrecioPastel);
        EditText etTamano = dialogView.findViewById(R.id.etTamanoPastel);
        EditText etCantidad = dialogView.findViewById(R.id.etCantidadPastel);
        EditText etDisponible = dialogView.findViewById(R.id.etDisponiblePastel);

        imageViewPreview = dialogView.findViewById(R.id.imageViewPastel);
        layoutPlaceholder = dialogView.findViewById(R.id.layoutPlaceholder);
        Button btnSeleccionarImagen = dialogView.findViewById(R.id.btnSeleccionarImagen);

        // Rellenar campos con datos existentes
        etNombre.setText(pastelEditando.getNombrePastel());
        etDescripcion.setText(pastelEditando.getDescripcion());
        etPrecio.setText(String.valueOf(pastelEditando.getPrecio()));
        etTamano.setText(pastelEditando.getTamano());
        etCantidad.setText(String.valueOf(pastelEditando.getCantidadDisponible()));
        etDisponible.setText(pastelEditando.isDisponible() ? "1" : "0");

        // Manejar imagen existente
        imageUriSeleccionada = null; // Reset para nueva selección
        if (pastelEditando.getImageUrl() != null && !pastelEditando.getImageUrl().isEmpty()) {
            mostrarImagenExistente(pastelEditando.getImageUrl());
        } else {
            mostrarPlaceholder();
        }

        // Configurar listeners
        layoutPlaceholder.setOnClickListener(v -> abrirGaleriaParaEditar());
        btnSeleccionarImagen.setOnClickListener(v -> abrirGaleriaParaEditar());

        new AlertDialog.Builder(getContext())
                .setTitle("Editar Pastel")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    try {
                        String nombre = etNombre.getText().toString().trim();
                        String descripcion = etDescripcion.getText().toString().trim();
                        String precioStr = etPrecio.getText().toString().trim();
                        String tamano = etTamano.getText().toString().trim();
                        String cantidadStr = etCantidad.getText().toString().trim();
                        String disponibleStr = etDisponible.getText().toString().trim();

                        if (nombre.isEmpty() || precioStr.isEmpty() || cantidadStr.isEmpty() || disponibleStr.isEmpty()) {
                            Toast.makeText(getContext(), "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        double precio = Double.parseDouble(precioStr);
                        int cantidad = Integer.parseInt(cantidadStr);
                        boolean disponible = disponibleStr.equals("1");

                        if (imageUriSeleccionada != null) {
                            // Nueva imagen seleccionada, subirla
                            subirImagenYActualizar(nombre, descripcion, precio, tamano, disponible, cantidad, imageUriSeleccionada);
                        } else {
                            // Mantener imagen actual
                            pastelAdapter.actualizarPastel(posicionEditando, uidEditando, pastelEditando.getId(),
                                    nombre, descripcion, precio, tamano, disponible, cantidad, pastelEditando.getImageUrl());
                        }

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error al actualizar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Muestra el placeholder y oculta la imagen
     */
    private void mostrarPlaceholder() {
        if (layoutPlaceholder != null && imageViewPreview != null) {
            layoutPlaceholder.setVisibility(View.VISIBLE);
            imageViewPreview.setVisibility(View.GONE);
        }
    }

    /**
     * Muestra la imagen seleccionada y oculta el placeholder
     */
    private void mostrarImagenSeleccionada(Uri imageUri) {
        if (layoutPlaceholder != null && imageViewPreview != null) {
            layoutPlaceholder.setVisibility(View.GONE);
            imageViewPreview.setVisibility(View.VISIBLE);

            // Cargar la imagen usando Glide para mejor rendimiento
            Glide.with(this)
                    .load(imageUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(imageViewPreview);
        }
    }

    /**
     * Muestra una imagen existente desde URL y oculta el placeholder
     */
    private void mostrarImagenExistente(String imageUrl) {
        if (layoutPlaceholder != null && imageViewPreview != null) {
            layoutPlaceholder.setVisibility(View.GONE);
            imageViewPreview.setVisibility(View.VISIBLE);

            // Cargar imagen desde URL usando Glide
            Glide.with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(imageViewPreview);
        }
    }

    private void abrirGaleriaParaAgregar() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST_ADD);
    }

    private void abrirGaleriaParaEditar() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST_EDIT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUriSeleccionada = data.getData();

            // Mostrar la imagen seleccionada inmediatamente
            mostrarImagenSeleccionada(imageUriSeleccionada);
        }
    }

    private void subirImagenYGuardar(String nombre, String descripcion, double precio, String tamano,
                                     boolean disponible, int cantidad, Uri imageUri, boolean esEdicion) {
        // Mostrar progreso (opcional)
        Toast.makeText(getContext(), "Subiendo imagen...", Toast.LENGTH_SHORT).show();

        StorageReference ref = FirebaseStorage.getInstance().getReference("pasteles")
                .child(System.currentTimeMillis() + ".jpg");

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    String url = uri.toString();
                    if (esEdicion) {
                        pastelAdapter.actualizarPastel(posicionEditando, uidEditando, pastelEditando.getId(),
                                nombre, descripcion, precio, tamano, disponible, cantidad, url);
                    } else {
                        guardarPastel(nombre, descripcion, precio, tamano, disponible, cantidad, url);
                    }
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al subir imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    if (esEdicion) {
                        pastelAdapter.actualizarPastel(posicionEditando, uidEditando, pastelEditando.getId(),
                                nombre, descripcion, precio, tamano, disponible, cantidad, pastelEditando.getImageUrl());
                    } else {
                        guardarPastel(nombre, descripcion, precio, tamano, disponible, cantidad, "");
                    }
                });
    }

    private void subirImagenYActualizar(String nombre, String descripcion, double precio, String tamano,
                                        boolean disponible, int cantidad, Uri imageUri) {
        subirImagenYGuardar(nombre, descripcion, precio, tamano, disponible, cantidad, imageUri, true);
    }

    private void guardarPastel(String nombre, String descripcion, double precio, String tamano, boolean disponible, int cantidad, String imageUrl) {
        FirebaseDatabase.getInstance().getReference("pasteles")
                .get()
                .addOnSuccessListener(snapshot -> {
                    long count = snapshot.getChildrenCount();
                    String pastelId = "pastel" + (count + 1);
                    String firebaseId = FirebaseDatabase.getInstance().getReference("pasteles").push().getKey();

                    Pasteles nuevo = new Pasteles(pastelId, nombre, descripcion, precio, tamano, disponible, cantidad, imageUrl);

                    if (firebaseId != null) {
                        FirebaseDatabase.getInstance().getReference("pasteles")
                                .child(firebaseId)
                                .setValue(nuevo)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Pastel agregado exitosamente", Toast.LENGTH_SHORT).show();
                                    cargarPastelesDesdeFirebase();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Error al guardar en base de datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al obtener datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void verificarRolUsuario() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(uid).child("role");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String rol = snapshot.getValue(String.class);
                if ("admin".equals(rol)) {
                    // Mostrar botón agregar
                    fabAgregar.setVisibility(View.VISIBLE);
                    pastelAdapter.setEsAdmin(true);
                } else {
                    // Ocultar botón agregar
                    fabAgregar.setVisibility(View.GONE);
                    pastelAdapter.setEsAdmin(false);
                }
                pastelAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "No se pudo verificar el rol del usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }
}