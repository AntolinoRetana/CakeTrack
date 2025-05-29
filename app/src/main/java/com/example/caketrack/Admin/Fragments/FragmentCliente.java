package com.example.caketrack.Admin.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.caketrack.Admin.Clientes.Adapter.ClienteAdapter;
import com.example.caketrack.Admin.Clientes.moduls.Cliente;
import com.example.caketrack.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentCliente#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCliente extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Mis variables
    private RecyclerView recyclerClientes;
    private FloatingActionButton fabAgregar;
    private ArrayList<Cliente> listaClientes = new ArrayList<>();
    private ClienteAdapter clienteAdapter;
    private ArrayList<String> listaUIDs = new ArrayList<>();

    public FragmentCliente() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentCliente.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCliente newInstance(String param1, String param2) {
        FragmentCliente fragment = new FragmentCliente();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_cliente, container, false);
        View view = inflater.inflate(R.layout.fragment_cliente, container, false);


        recyclerClientes = view.findViewById(R.id.recyclerClientes);
        fabAgregar = view.findViewById(R.id.fabAgregar);

        recyclerClientes.setLayoutManager(new LinearLayoutManager(getContext()));
        clienteAdapter = new ClienteAdapter(getContext(), listaClientes, listaUIDs);
        recyclerClientes.setAdapter(clienteAdapter);

        cargarClientesDesdeFirebase();

        fabAgregar.setOnClickListener(v -> mostrarDialogoAgregarCliente());
        verificarRolUsuario();

        return view;
    }

    private void cargarClientesDesdeFirebase() {
        FirebaseDatabase.getInstance().getReference("clientes")
                .get()
                .addOnSuccessListener(snapshot -> {
                    listaClientes.clear();
                    listaUIDs.clear();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Cliente c = snap.getValue(Cliente.class);
                        if (c != null) {
                            listaClientes.add(c);
                            listaUIDs.add(snap.getKey());
                        }
                    }
                    clienteAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al cargar clientes", Toast.LENGTH_SHORT).show());
    }

    private void mostrarDialogoAgregarCliente() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_agregar_cliente, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        EditText etNombre = dialogView.findViewById(R.id.etNombreCliente);
        EditText etTelefono = dialogView.findViewById(R.id.etTelefonoCliente);
        EditText etDireccion = dialogView.findViewById(R.id.etDireccionCliente);
        EditText etCorreo = dialogView.findViewById(R.id.etCorreoCliente);
        EditText etNotas = dialogView.findViewById(R.id.etNotasCliente);
        Button btnGuardar = dialogView.findViewById(R.id.btnGuardar);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelar);

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            String direccion = etDireccion.getText().toString().trim();
            String correo = etCorreo.getText().toString().trim();
            String notas = etNotas.getText().toString().trim();

            if (nombre.isEmpty() || telefono.isEmpty()) {
                Toast.makeText(getContext(), "Nombre y teléfono son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseDatabase.getInstance().getReference("clientes")
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        long count = snapshot.getChildrenCount();
                        String aliasId = "cliente" + (count + 1);
                        String firebaseId = FirebaseDatabase.getInstance().getReference("clientes").push().getKey();

                        Cliente nuevo = new Cliente(aliasId, nombre, telefono, direccion, correo, notas);

                        FirebaseDatabase.getInstance().getReference("clientes")
                                .child(firebaseId)
                                .setValue(nuevo)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Cliente guardado como " + aliasId, Toast.LENGTH_SHORT).show();
                                    cargarClientesDesdeFirebase();
                                    dialog.dismiss();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(getContext(), "Error al guardar", Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Error al contar clientes", Toast.LENGTH_SHORT).show());
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
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
                    clienteAdapter.setEsAdmin(true);
                } else {
                    // Ocultar botón agregar
                    clienteAdapter.setEsAdmin(false);
                }
                clienteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "No se pudo verificar el rol del usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
