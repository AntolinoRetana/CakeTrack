package com.example.caketrack.Admin.Pasteles.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caketrack.Admin.Pasteles.Moduls.Pasteles;
import com.example.caketrack.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class PastelesAdapter extends RecyclerView.Adapter<PastelesAdapter.PastelesViewHolder>{
    private Context context;
    private List<Pasteles> lista;
    private List<String> listaUIDs;

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

        // Eliminar
        holder.btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Eliminar pastel")
                    .setMessage("¿Estás seguro de eliminar este pastel?")
                    .setPositiveButton("Sí", (dialog, which) -> {
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

        // Editar (simplificado - lo ideal sería abrir un diálogo personalizado)
        holder.btnEditar.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_agregar_pastel, null);
            EditText etNombre = dialogView.findViewById(R.id.etNombrePastel);
            EditText etDescripcion = dialogView.findViewById(R.id.etDescripcionPastel);
            EditText etPrecio = dialogView.findViewById(R.id.etPrecioPastel);
            EditText etTamano = dialogView.findViewById(R.id.etTamanoPastel);
            EditText etCantidad = dialogView.findViewById(R.id.etCantidadPastel);
            EditText etDisponible = dialogView.findViewById(R.id.etDisponiblePastel);

            // Rellenar campos
            etNombre.setText(pastel.getNombrePastel());
            etDescripcion.setText(pastel.getDescripcion());
            etPrecio.setText(String.valueOf(pastel.getPrecio()));
            etTamano.setText(pastel.getTamano());
            etCantidad.setText(String.valueOf(pastel.getCantidadDisponible()));
            etDisponible.setText(pastel.isDisponible() ? "1" : "0");

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

                            Pasteles actualizado = new Pasteles(
                                    pastel.getId(),
                                    nombre,
                                    descripcion,
                                    precio,
                                    tamano,
                                    disponible,
                                    cantidad
                            );

                            FirebaseDatabase.getInstance().getReference("pasteles")
                                    .child(uid)
                                    .setValue(actualizado)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Pastel actualizado", Toast.LENGTH_SHORT).show();
                                        lista.set(position, actualizado);
                                        notifyItemChanged(position);
                                    });

                        } catch (Exception e) {
                            Toast.makeText(context, "Error al guardar pastel: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class PastelesViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, tvPrecio, tvTamano, tvCantidad, tvDisponible;
        ImageView btnEditar, btnEliminar;
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
        }
    }
}
