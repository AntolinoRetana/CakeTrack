package com.example.caketrack.Admin.Usuario.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caketrack.Moduls.Usuario;
import com.example.caketrack.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {
    private List<Usuario> listaUsuarios;
    private Context context;
    private List<String> listaUIDS;

    public UsuarioAdapter(Context context, List<Usuario> listaUsuarios) {
        this.context = context;
        this.listaUsuarios = listaUsuarios;
    }

    public UsuarioAdapter(Context context, List<String> listaUIDS, List<Usuario> listaUsuarios) {
        this.context = context;
        this.listaUIDS = listaUIDS;
        this.listaUsuarios = listaUsuarios;
    }

    public UsuarioAdapter() {
    }

    @NonNull
    @Override
    public UsuarioAdapter.UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioAdapter.UsuarioViewHolder holder, int position) {
        Usuario usuario = listaUsuarios.get(position);
        String uid = listaUIDS.get(position);

        holder.tvNombre.setText(usuario.getName());
        holder.tvRol.setText("Rol: " + usuario.getRole());

        holder.btnEditar.setOnClickListener(v -> mostrarDialogoEditarRol(uid, usuario.getRole()));
        holder.btnEliminar.setOnClickListener(v -> confirmarEliminarUsuario(uid));
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public class UsuarioViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNombre, tvAlias, tvRol;
        private ImageView btnEditar, btnEliminar;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvRol = itemView.findViewById(R.id.tvRol);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
    private void mostrarDialogoEditarRol(String uid, String rolActual) {
        final String[] roles = {"admin", "seller", "production"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Selecciona nuevo rol");

        int indexActual = java.util.Arrays.asList(roles).indexOf(rolActual);

        builder.setSingleChoiceItems(roles, indexActual, null);
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            ListView lw = ((AlertDialog) dialog).getListView();
            String nuevoRol = (String) lw.getAdapter().getItem(lw.getCheckedItemPosition());

            FirebaseDatabase.getInstance().getReference("users")
                    .child(uid)
                    .child("role")
                    .setValue(nuevoRol)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(context, "Rol actualizado a " + nuevoRol, Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Error al actualizar rol", Toast.LENGTH_SHORT).show()
                    );
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
    private void confirmarEliminarUsuario(String uid) {
        new AlertDialog.Builder(context)
                .setTitle("Eliminar usuario")
                .setMessage("¿Estás seguro de eliminar este usuario?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    FirebaseDatabase.getInstance().getReference("users")
                            .child(uid)
                            .removeValue()
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(context, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                            )
                            .addOnFailureListener(e ->
                                    Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                            );
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


}
