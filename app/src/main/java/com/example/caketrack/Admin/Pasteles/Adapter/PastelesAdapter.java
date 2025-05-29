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
    private OnImageSelectionListener imageSelectionListener;
    private boolean esAdmin;

    // Interface para manejar la selección de imágenes
    public interface OnImageSelectionListener {
        void onSelectImageForEdit(int position, String uid, Pasteles pastel);
    }
    public void setEsAdmin(boolean esAdmin) {
        this.esAdmin = esAdmin;
    }

    public PastelesAdapter(Context context, List<Pasteles> lista, List<String> listaUIDs) {
        this.context = context;
        this.lista = lista;
        this.listaUIDs = listaUIDs;
    }

    public void setImageSelectionListener(OnImageSelectionListener listener) {
        this.imageSelectionListener = listener;
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

        if (!esAdmin) {
            holder.btnEditar.setVisibility(View.GONE);
            holder.btnEliminar.setVisibility(View.GONE);
        } else {
            holder.btnEditar.setVisibility(View.VISIBLE);
            holder.btnEliminar.setVisibility(View.VISIBLE);
        }

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
                            try {
                                StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(pastel.getImageUrl());
                                imageRef.delete(); // Opcional: manejar éxito/error
                            } catch (Exception e) {
                                // Continuar con eliminación aunque falle borrar imagen
                            }
                        }

                        FirebaseDatabase.getInstance().getReference("pasteles")
                                .child(uid)
                                .removeValue()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(context, "Pastel eliminado", Toast.LENGTH_SHORT).show();
                                    lista.remove(position);
                                    listaUIDs.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, lista.size());
                                });
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        // Editar - Delegamos al Fragment
        holder.btnEditar.setOnClickListener(v -> {
            if (imageSelectionListener != null) {
                imageSelectionListener.onSelectImageForEdit(position, uid, pastel);
            }
        });
    }

    // Método para actualizar un pastel específico
    public void actualizarPastel(int position, String uid, String id, String nombre, String descripcion,
                                 double precio, String tamano, boolean disponible, int cantidad, String imageUrl) {
        Pasteles actualizado = new Pasteles(id, nombre, descripcion, precio, tamano, disponible, cantidad, imageUrl);

        FirebaseDatabase.getInstance().getReference("pasteles")
                .child(uid)
                .setValue(actualizado)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Pastel actualizado", Toast.LENGTH_SHORT).show();
                    lista.set(position, actualizado);
                    notifyItemChanged(position);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            imageViewPastel = itemView.findViewById(R.id.imageViewItemPastel);
        }
    }
}