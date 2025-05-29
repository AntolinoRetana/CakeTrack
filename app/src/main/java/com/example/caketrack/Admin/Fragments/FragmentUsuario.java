package com.example.caketrack.Admin.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.caketrack.Admin.Usuario.Adapter.UsuarioAdapter;
import com.example.caketrack.Moduls.Usuario;
import com.example.caketrack.R;
import com.example.caketrack.RegisterActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentUsuario#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentUsuario extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Variables
    private RecyclerView recyclerUsuarios;
    private UsuarioAdapter usuarioAdapter;
    private final ArrayList<Usuario> listaUsuarios = new ArrayList<>();
    private final ArrayList<String> listaUIDs = new ArrayList<>();
    private FloatingActionButton fabAgregar;


    public FragmentUsuario() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentUsuario.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentUsuario newInstance(String param1, String param2) {
        FragmentUsuario fragment = new FragmentUsuario();
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
        View view = inflater.inflate(R.layout.fragment_usuario, container, false);
        recyclerUsuarios = view.findViewById(R.id.recyclerUsuarios);
        recyclerUsuarios.setLayoutManager(new LinearLayoutManager(getContext()));
        usuarioAdapter = new UsuarioAdapter( getContext() , listaUIDs, listaUsuarios);
        recyclerUsuarios.setAdapter(usuarioAdapter);
        fabAgregar = view.findViewById(R.id.fabAgregarUsuario);

        fabAgregar.setOnClickListener(v -> mostrarAgregarUsuario());
        cargarUsuarios();

        return view;
    }
    private void cargarUsuarios() {
        FirebaseDatabase.getInstance().getReference("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listaUsuarios.clear();
                        listaUIDs.clear(); // Limpia la lista de UIDs también
                        for (DataSnapshot userSnap : snapshot.getChildren()) {
                            Usuario u = userSnap.getValue(Usuario.class);
                            if (u != null) {
                                listaUsuarios.add(u);
                                listaUIDs.add(userSnap.getKey()); // Guarda el UID
                            }
                        }
                        usuarioAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error al cargar usuarios", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void mostrarAgregarUsuario() {
        Intent intent = new Intent(getContext(), RegisterActivity.class);
        startActivity(intent);
    }

}