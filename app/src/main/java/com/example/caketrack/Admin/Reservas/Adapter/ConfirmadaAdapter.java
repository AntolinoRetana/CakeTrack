package com.example.caketrack.Admin.Reservas.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caketrack.Admin.Reservas.Moduls.Reserva;
import com.example.caketrack.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ConfirmadaAdapter extends RecyclerView.Adapter<ConfirmadaAdapter.PendienteViewHolder> {
    private List<Reserva> listaReservas;
    private Context context;
    private List<String> listaUIDs;
    private boolean esAdmin;
    public ConfirmadaAdapter(Context context, List<Reserva> listaReservas, List<String> listaUIDs) {
        this.context = context;
        this.listaReservas = listaReservas;
        this.listaUIDs = listaUIDs;
    }
    public void setEsAdmin(boolean esAdmin) {
        this.esAdmin = esAdmin;
    }


    @NonNull
    @Override
    public ConfirmadaAdapter.PendienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reserva, parent, false);
        return new PendienteViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull ConfirmadaAdapter.PendienteViewHolder holder, int position) {
        Reserva reserva = listaReservas.get(position);
        String uid = listaUIDs.get(position);

        holder.tvCliente.setText("Cliente: " + reserva.getClienteNombre());
        holder.tvPastel.setText("Pastel: " + reserva.getPastelNombre());
        holder.tvFecha.setText("Fecha: " + reserva.getFecha());
        holder.tvNotas.setText("Notas: " + reserva.getNotas());
        holder.tvEstado.setText("Estado: " + reserva.getEstado());
        holder.tvPago.setText("Pago: " + reserva.getPago());
        holder.tvFechaCreacion.setText("Fecha de Creación: " + reserva.getFecha_creacion());

        if (!esAdmin) {
            holder.btnEditar.setVisibility(View.VISIBLE);
            holder.btnEliminar.setVisibility(View.GONE);
        } else {
            holder.btnEditar.setVisibility(View.VISIBLE);
            holder.btnEliminar.setVisibility(View.VISIBLE);
        }

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

        holder.btnEditar.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_agregar_reserva, null);
            EditText etFecha = dialogView.findViewById(R.id.etFecha);
            EditText etNotas = dialogView.findViewById(R.id.etNotas);
            Spinner spinnerCliente = dialogView.findViewById(R.id.spinnerCliente);
            Spinner spinnerPastel = dialogView.findViewById(R.id.spinnerPastel);
            Spinner spinnerEstado = dialogView.findViewById(R.id.spinnerEstado);
            EditText etFechaCreacion = dialogView.findViewById(R.id.etFechaCreacion);
            EditText etPago = dialogView.findViewById(R.id.etPago);

            etFecha.setText(reserva.getFecha());
            etNotas.setText(reserva.getNotas());
            etFechaCreacion.setText(reserva.getFecha_creacion());
            etPago.setText(reserva.getPago());

            List<String> listaClientes = new ArrayList<>();
            List<String> listaPasteles = new ArrayList<>();
            List<String> listaEstados = new ArrayList<>();

            ArrayAdapter<String> clienteAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, listaClientes);
            clienteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCliente.setAdapter(clienteAdapter);

            ArrayAdapter<String> pastelAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, listaPasteles);
            pastelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerPastel.setAdapter(pastelAdapter);

            ArrayAdapter<String> estadoAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, listaEstados);
            estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerEstado.setAdapter(estadoAdapter);

            // Para solo lectura pero con texto visible normal
            etFecha.setFocusable(false);
            etFecha.setClickable(false);

            etFechaCreacion.setFocusable(false);
            etFechaCreacion.setClickable(false);

            etPago.setFocusable(false);
            etPago.setClickable(false);

            etNotas.setFocusable(false);
            etNotas.setClickable(false);

// Spinner Cliente y Pastel igual
            spinnerCliente.setEnabled(false);
            spinnerPastel.setEnabled(false);

            // Paso 1: Cargar clientes
            FirebaseDatabase.getInstance().getReference("clientes").get().addOnSuccessListener(snapshotClientes -> {
                for (DataSnapshot clienteSnapshot : snapshotClientes.getChildren()) {
                    String nombre = clienteSnapshot.child("nombre").getValue(String.class);
                    if (nombre != null) listaClientes.add(nombre);
                }
                clienteAdapter.notifyDataSetChanged();

                for (int i = 0; i < listaClientes.size(); i++) {
                    if (listaClientes.get(i).equals(reserva.getClienteNombre())) {
                        spinnerCliente.setSelection(i);
                        break;
                    }
                }

                // Paso 2: Cargar pasteles
                FirebaseDatabase.getInstance().getReference("pasteles").get().addOnSuccessListener(snapshotPasteles -> {
                    for (DataSnapshot pastelSnapshot : snapshotPasteles.getChildren()) {
                        String nombre = pastelSnapshot.child("nombrePastel").getValue(String.class);
                        if (nombre != null) listaPasteles.add(nombre);
                    }
                    pastelAdapter.notifyDataSetChanged();

                    for (int i = 0; i < listaPasteles.size(); i++) {
                        if (listaPasteles.get(i).equals(reserva.getPastelNombre())) {
                            spinnerPastel.setSelection(i);
                            break;
                        }
                    }

                    // Paso 3: Cargar estados (mejor usar estado predefinido)
                    listaEstados.clear();
                    listaEstados.add("Pendiente");
                    listaEstados.add("Confirmada");
                    listaEstados.add("Entregada");
                    listaEstados.add("Cancelada");
                    estadoAdapter.notifyDataSetChanged();

                    for (int i = 0; i < listaEstados.size(); i++) {
                        if (listaEstados.get(i).equals(reserva.getEstado())) {
                            spinnerEstado.setSelection(i);
                            break;
                        }
                    }

                    // Mostrar el diálogo SOLO cuando los 3 spinners están listos
                    new AlertDialog.Builder(context)
                            .setTitle("Editar reserva")
                            .setView(dialogView)
                            .setPositiveButton("Guardar", (dialog, which) -> {
                                if (spinnerCliente.getSelectedItem() == null ||
                                        spinnerPastel.getSelectedItem() == null ||
                                        spinnerEstado.getSelectedItem() == null) {
                                    Toast.makeText(context, "No se han cargado los datos aún.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String nuevaFecha = etFecha.getText().toString().trim();
                                String nuevasNotas = etNotas.getText().toString().trim();
                                String nuevoClienteNombre = spinnerCliente.getSelectedItem().toString();
                                String nuevoPastelNombre = spinnerPastel.getSelectedItem().toString();
                                String nuevoEstado = spinnerEstado.getSelectedItem().toString();
                                String nuevoPago = etPago.getText().toString().trim();
                                String nuevaFechaCreacion = etFechaCreacion.getText().toString().trim();

                                if (nuevaFecha.isEmpty()) {
                                    Toast.makeText(context, "La fecha no puede estar vacía", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Reserva actualizada = new Reserva(
                                        reserva.getClienteId(),
                                        nuevoClienteNombre,
                                        reserva.getPastelId(),
                                        nuevoPastelNombre,
                                        nuevaFecha,
                                        nuevaFechaCreacion,
                                        nuevoEstado,
                                        nuevoPago,
                                        nuevasNotas);

                                FirebaseDatabase.getInstance().getReference("reservas")
                                        .child(uid)
                                        .setValue(actualizada)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(context, "Reserva actualizada", Toast.LENGTH_SHORT).show();
                                            listaReservas.set(position, actualizada);
                                            notifyItemChanged(position);
                                        });
                            })
                            .setNegativeButton("Cancelar", null)
                            .show();
                });
            });
        });
    }

    @Override
    public int getItemCount() {
        return listaReservas.size();
    }

    public class PendienteViewHolder extends RecyclerView.ViewHolder {
        TextView tvCliente, tvPastel, tvFecha, tvNotas, tvEstado, tvPago, tvFechaCreacion;
        ImageView btnEditar, btnEliminar;
        public PendienteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCliente = itemView.findViewById(R.id.tvCliente);
            tvPastel = itemView.findViewById(R.id.tvPastel);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvNotas = itemView.findViewById(R.id.tvNotas);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvPago = itemView.findViewById(R.id.tvPago);
            tvFechaCreacion = itemView.findViewById(R.id.tvFechaCreacion);
        }
    }
}
