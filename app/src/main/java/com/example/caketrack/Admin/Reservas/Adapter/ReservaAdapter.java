package com.example.caketrack.Admin.Reservas.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caketrack.Admin.Reservas.Moduls.Reserva;
import com.example.caketrack.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder> {
    private List<Reserva> listaReservas;
    private Context context;
    private List<String> listaUIDs;

    public ReservaAdapter(Context context, List<Reserva> listaReservas, List<String> listaUIDs) {
        this.context = context;
        this.listaReservas = listaReservas;
        this.listaUIDs = listaUIDs;
    }

    @NonNull
    @Override
    public ReservaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reserva, parent, false);
        return new ReservaViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull ReservaViewHolder holder, int position) {
        Reserva reserva = listaReservas.get(position);
        String uid = listaUIDs.get(position);

        holder.tvCliente.setText("Cliente: " + reserva.getClienteNombre());
        holder.tvPastel.setText("Pastel: " + reserva.getPastelNombre());
        holder.tvFecha.setText("Fecha: " + reserva.getFecha());
        holder.tvNotas.setText("Notas: " + reserva.getNotas());

        holder.btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Eliminar reserva")
                    .setMessage("¿Estás seguro de eliminar esta reserva?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        FirebaseDatabase.getInstance().getReference("reservas")
                                .child(uid).removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Reserva eliminada", Toast.LENGTH_SHORT).show();
                                    listaReservas.remove(position);
                                    listaUIDs.remove(position);
                                    notifyItemRemoved(position);
                                });
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        // Puedes agregar funcionalidad de editar similar aquí si quieres
    }

    @Override
    public int getItemCount() {
        return listaReservas.size();
    }

    public class ReservaViewHolder extends RecyclerView.ViewHolder {
        TextView tvCliente, tvPastel, tvFecha, tvNotas;
        ImageView btnEditar, btnEliminar;
        public ReservaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCliente = itemView.findViewById(R.id.tvCliente);
            tvPastel = itemView.findViewById(R.id.tvPastel);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvNotas = itemView.findViewById(R.id.tvNotas);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
