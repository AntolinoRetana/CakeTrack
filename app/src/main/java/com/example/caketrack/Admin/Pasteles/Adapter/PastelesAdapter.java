package com.example.caketrack.Admin.Pasteles.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.caketrack.Admin.Pasteles.Moduls.Pasteles;
import com.example.caketrack.R;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PastelesAdapter extends RecyclerView.Adapter<PastelesAdapter.PastelesViewHolder>{
    private Context context;
    private List<Pasteles> lista;
    private List<String> listaUIDs;
    private static final int PICK_IMAGE_REQUEST = 101;
    private Uri imageUriSeleccionada;
    private ImageView currentImageView;

    public PastelesAdapter(Context context, List<Pasteles> lista, List<String> listaUIDs) {
        this.context = context;
        this.lista = lista;
        this.listaUIDs = listaUIDs;
    }

    public PastelesAdapter() {
    }

    @NonNull
    @Override
    public PastelesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pastel, parent, false);
        return new PastelesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PastelesViewHolder holder, int position) {
        Pasteles pastel = lista.get(position);
        String uid = listaUIDs.get(position);

        holder.tvNombre.setText(pastel.getNombrePastel());
        holder.tvDescripcion.setText(pastel.getDescripcion());
        holder.tvPrecio.setText("Precio: $" + pastel.getPrecio());
        holder.tvTamano.setText("Tamaño: " + pastel.getTamano());
        holder.tvCantidad.setText("Cantidad disponible: " + pastel.getCantidadDisponible());
        holder.tvDisponible.setText(pastel.isDisponible() ? "Disponible" : "No disponible");
        holder.tvDisponible.setTextColor(pastel.isDisponible() ? 0xFF4CAF50 : 0xFFF44336);

        // Cargar imagen si existe
        if (holder.imageViewPastel != null) {
            if (pastel.getImageUrl() != null && !pastel.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(pastel.getImageUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(holder.imageViewPastel);
            } else {
                holder.imageViewPastel.setImageResource(R.drawable.ic_launcher_background);
            }
        }

        // Eliminar
        holder.btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Eliminar pastel")
                    .setMessage("¿Estás seguro de eliminar este pastel?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        // Eliminar imagen de Storage si existe
                        if (pastel.getImageUrl() != null && !pastel.getImageUrl().isEmpty()) {
                            StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(pastel.getImageUrl());
                            imageRef.delete(); // Opcional: manejar éxito/error
                        }

                        FirebaseDatabase.getInstance().getReference("pasteles")
                                .child(uid)
                                .removeValue()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(context, "Pastel eliminado", Toast.LENGTH_SHORT).show();
                                    lista.remove(position);
                                    listaUIDs.remove(position);
                                    notifyItemRemoved(position);
                                });
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        // Editar
        holder.btnEditar.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_agregar_pastel, null);
            EditText etNombre = dialogView.findViewById(R.id.etNombrePastel);
            EditText etDescripcion = dialogView.findViewById(R.id.etDescripcionPastel);
            EditText etPrecio = dialogView.findViewById(R.id.etPrecioPastel);
            EditText etTamano = dialogView.findViewById(R.id.etTamanoPastel);
            EditText etCantidad = dialogView.findViewById(R.id.etCantidadPastel);
            EditText etDisponible = dialogView.findViewById(R.id.etDisponiblePastel);
            ImageView imageViewDialog = dialogView.findViewById(R.id.imageViewPastel);
            Button btnSeleccionarImagen = dialogView.findViewById(R.id.btnSeleccionarImagen);

            // Rellenar campos
            etNombre.setText(pastel.getNombrePastel());
            etDescripcion.setText(pastel.getDescripcion());
            etPrecio.setText(String.valueOf(pastel.getPrecio()));
            etTamano.setText(pastel.getTamano());
            etCantidad.setText(String.valueOf(pastel.getCantidadDisponible()));
            etDisponible.setText(pastel.isDisponible() ? "1" : "0");

            // Cargar imagen actual
            if (pastel.getImageUrl() != null && !pastel.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(pastel.getImageUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(imageViewDialog);
            }

            // Reset para nueva selección
            imageUriSeleccionada = null;
            currentImageView = imageViewDialog;

            btnSeleccionarImagen.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                // Nota: Para manejar onActivityResult en Adapter necesitarías implementar
                // un sistema de callback hacia el Fragment o Activity
                // Por simplicidad, este ejemplo muestra la estructura básica
            });

            new AlertDialog.Builder(context)
                    .setTitle("Editar pastel")
                    .setView(dialogView)
                    .setPositiveButton("Guardar", (dialog, which) -> {
                        try {
                            String nombre = etNombre.getText().toString().trim();
                            String descripcion = etDescripcion.getText().toString().trim();
                            String precioStr = etPrecio.getText().toString().trim();
                            String tamano = etTamano.getText().toString().trim();
                            String disponibleStr = etDisponible.getText().toString().trim();
                            String cantidadStr = etCantidad.getText().toString().trim();

                            if (nombre.isEmpty() || precioStr.isEmpty() || cantidadStr.isEmpty() || disponibleStr.isEmpty()) {
                                Toast.makeText(context, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            double precio = Double.parseDouble(precioStr);
                            int cantidad = Integer.parseInt(cantidadStr);
                            boolean disponible = disponibleStr.equals("1");

                            // Si hay nueva imagen seleccionada, subirla
                            if (imageUriSeleccionada != null) {
                                StorageReference ref = FirebaseStorage.getInstance().getReference("pasteles")
                                        .child(System.currentTimeMillis() + ".jpg");

                                ref.putFile(imageUriSeleccionada)
                                        .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                                            String nuevaUrl = uri.toString();
                                            actualizarPastel(uid, position, pastel.getId(), nombre, descripcion, precio, tamano, disponible, cantidad, nuevaUrl);
                                        }));
                            } else {
                                // Mantener la imagen actual
                                actualizarPastel(uid, position, pastel.getId(), nombre, descripcion, precio, tamano, disponible, cantidad, pastel.getImageUrl());
                            }

                        } catch (Exception e) {
                            Toast.makeText(context, "Error al guardar pastel: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void actualizarPastel(String uid, int position, String id, String nombre, String descripcion,
                                  double precio, String tamano, boolean disponible, int cantidad, String imageUrl) {
        Pasteles actualizado = new Pasteles(id, nombre, descripcion, precio, tamano, disponible, cantidad, imageUrl);

        FirebaseDatabase.getInstance().getReference("pasteles")
                .child(uid)
                .setValue(actualizado)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Pastel actualizado", Toast.LENGTH_SHORT).show();
                    lista.set(position, actualizado);
                    notifyItemChanged(position);
                });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class PastelesViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, tvPrecio, tvTamano, tvCantidad, tvDisponible;
        ImageView btnEditar, btnEliminar, imageViewPastel;

        public PastelesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombrePastel);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionPastel);
            tvPrecio = itemView.findViewById(R.id.tvPrecioPastel);
            tvTamano = itemView.findViewById(R.id.tvTamanoPastel);
            tvCantidad = itemView.findViewById(R.id.tvCantidadPastel);
            tvDisponible = itemView.findViewById(R.id.tvDisponiblePastel);
            btnEditar = itemView.findViewById(R.id.btnEditarPastel);
            btnEliminar = itemView.findViewById(R.id.btnEliminarPastel);
            imageViewPastel = itemView.findViewById(R.id.imageViewItemPastel); // Asegúrate de que este ID exista
        }
    }
}