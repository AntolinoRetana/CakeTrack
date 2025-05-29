package com.example.caketrack.Admin.Clientes.Adapter;

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

import com.example.caketrack.Admin.Clientes.moduls.Cliente;
import com.example.caketrack.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder> {

    private List<Cliente> listaClientes;
    public Context context;
    private List<String> listaUIDs;
    private boolean esAdmin;
    public ClienteAdapter(List<Cliente> listaClientes, Context context) {
        this.listaClientes = listaClientes;
        this.context = context;
    }

    public void setEsAdmin(boolean esAdmin) {
        this.esAdmin = esAdmin;
    }
    public ClienteAdapter(Context context, List<Cliente> listaClientes, List<String> listaUIDs) {
        this.context = context;
        this.listaClientes = listaClientes;
        this.listaUIDs = listaUIDs;
    }

    @NonNull
    @Override
    public ClienteAdapter.ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cliente, parent, false);
        return new ClienteViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ClienteAdapter.ClienteViewHolder holder, int position) {
        Cliente cliente = listaClientes.get(position);
        String uid = listaUIDs.get(position);
        holder.tvNombreCliente.setText(cliente.getNombre());
        holder.tvTelefonoCliente.setText("Teléfono: " + cliente.getTelefono());
        holder.tvDireccionCliente.setText("Dirección: " + cliente.getDireccion());
        holder.tvCorreoCliente.setText("Correo: " + cliente.getCorreo());


        if (!esAdmin) {
            holder.btnEditar.setVisibility(View.GONE);
            holder.btnEliminar.setVisibility(View.GONE);
        } else {
            holder.btnEditar.setVisibility(View.VISIBLE);
            holder.btnEliminar.setVisibility(View.VISIBLE);
        }

        // Eliminar
        holder.btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Eliminar cliente")
                    .setMessage("¿Estás seguro de eliminar este cliente?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        FirebaseDatabase.getInstance().getReference("clientes")
                                .child(uid)
                                .removeValue()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(context, "Cliente eliminado", Toast.LENGTH_SHORT).show();
                                    listaClientes.remove(position);
                                    listaUIDs.remove(position);
                                    notifyItemRemoved(position);
                                });
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        // Editar
        holder.btnEditar.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_agregar_cliente, null);

            EditText etNombre = dialogView.findViewById(R.id.etNombreCliente);
            EditText etTelefono = dialogView.findViewById(R.id.etTelefonoCliente);
            EditText etDireccion = dialogView.findViewById(R.id.etDireccionCliente);
            EditText etCorreo = dialogView.findViewById(R.id.etCorreoCliente);
            EditText etNotas = dialogView.findViewById(R.id.etNotasCliente);

            etNombre.setText(cliente.getNombre());
            etTelefono.setText(cliente.getTelefono());
            etDireccion.setText(cliente.getDireccion());
            etCorreo.setText(cliente.getCorreo());
            etNotas.setText(cliente.getNotas());

            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setView(dialogView)
                    .create(); // No uses setPositiveButton aquí


            dialogView.findViewById(R.id.btnGuardar).setOnClickListener(view -> {
                Cliente actualizado = new Cliente(
                        etNombre.getText().toString().trim(),
                        etTelefono.getText().toString().trim(),
                        etDireccion.getText().toString().trim(),
                        etCorreo.getText().toString().trim(),
                        etNotas.getText().toString().trim()
                );

                FirebaseDatabase.getInstance().getReference("clientes")
                        .child(uid)
                        .setValue(actualizado)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Cliente actualizado", Toast.LENGTH_SHORT).show();
                            listaClientes.set(position, actualizado);
                            notifyItemChanged(position);
                            dialog.dismiss();
                        });
            });

            dialogView.findViewById(R.id.btnCancelar).setOnClickListener(view -> dialog.dismiss());

            dialog.show();
        });


    }

    @Override
    public int getItemCount() {
        return listaClientes.size();
    }

    public class ClienteViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreCliente, tvTelefonoCliente, tvDireccionCliente, tvCorreoCliente, tvNotasCliente;
        ImageView btnEditar, btnEliminar;
        public ClienteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreCliente = itemView.findViewById(R.id.tvNombreCliente);
            tvTelefonoCliente = itemView.findViewById(R.id.tvTelefonoCliente);
            tvDireccionCliente = itemView.findViewById(R.id.tvDireccionCliente);
            tvCorreoCliente = itemView.findViewById(R.id.tvCorreoCliente);
            tvNotasCliente = itemView.findViewById(R.id.tvNotasCliente);
            btnEditar = itemView.findViewById(R.id.btnEditarCliente);
           btnEliminar = itemView.findViewById(R.id.btnEliminarCliente);
        }
    }
}
