package com.example.caketrack.Admin.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.caketrack.Admin.Clientes.moduls.Cliente;
import com.example.caketrack.Admin.Pasteles.Moduls.Pasteles;
import com.example.caketrack.Admin.Reservas.Adapter.ConfirmadaAdapter;
import com.example.caketrack.Admin.Reservas.Adapter.PendientesAdapter;
import com.example.caketrack.Admin.Reservas.Moduls.Reserva;
import com.example.caketrack.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentReservasListas#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentReservasListas extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerReservas;
    private FloatingActionButton fabAgregar;
    private ArrayList<Reserva> listaReservas = new ArrayList<>();
    private ConfirmadaAdapter reservaAdapter;
    private ArrayList<String> listaUIDs = new ArrayList<>();

    private Map<String, Cliente> mapaClientes = new HashMap<>();
    private Map<String, Pasteles> mapaPasteles = new HashMap<>();

    public FragmentReservasListas() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentReservasListas.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentReservasListas newInstance(String param1, String param2) {
        FragmentReservasListas fragment = new FragmentReservasListas();
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reservas_listas, container, false);
        recyclerReservas = view.findViewById(R.id.recyclerReservasConfirmada);
        fabAgregar = view.findViewById(R.id.fabAgregarReservaConfirmada);

        recyclerReservas.setLayoutManager(new LinearLayoutManager(getContext()));
        reservaAdapter = new ConfirmadaAdapter(getContext(), listaReservas, listaUIDs);
        recyclerReservas.setAdapter(reservaAdapter);

        cargarClientesYPasteles();
        cargarReservasDesdeFirebase();

        fabAgregar.setOnClickListener(v -> mostrarDialogoAgregarReserva());
        verificarRolUsuario();

        return view;
    }

    private void cargarClientesYPasteles() {
        FirebaseDatabase.getInstance().getReference("clientes")
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Cliente cliente = snap.getValue(Cliente.class);
                        if (cliente != null) {
                            mapaClientes.put(snap.getKey(), cliente);
                        }
                    }
                });

        FirebaseDatabase.getInstance().getReference("pasteles")
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Pasteles pastel = snap.getValue(Pasteles.class);
                        if (pastel != null) {
                            mapaPasteles.put(snap.getKey(), pastel);
                        }
                    }
                });
    }

    private void cargarReservasDesdeFirebase() {
        FirebaseDatabase.getInstance().getReference("reservas")
                .get()
                .addOnSuccessListener(snapshot -> {
                    listaReservas.clear();
                    listaUIDs.clear();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Reserva r = snap.getValue(Reserva.class);
                        if (r != null && !"pendiente".equalsIgnoreCase(r.getEstado())) {
                            listaReservas.add(r);
                            listaUIDs.add(snap.getKey());
                        }


                    }

                    reservaAdapter.notifyDataSetChanged();
                });
    }

    private void mostrarDialogoAgregarReserva() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_agregar_reserva, null);
        Spinner spinnerCliente = dialogView.findViewById(R.id.spinnerCliente);
        Spinner spinnerPastel = dialogView.findViewById(R.id.spinnerPastel);
        Spinner spinnerEstado = dialogView.findViewById(R.id.spinnerEstado);
        EditText etFecha = dialogView.findViewById(R.id.etFecha);
        EditText etNotas = dialogView.findViewById(R.id.etNotas);
        EditText etFechaCreacion = dialogView.findViewById(R.id.etFechaCreacion);
        EditText etPago = dialogView.findViewById(R.id.etPago);

        List<String> clienteNombres = new ArrayList<>();
        List<String> pastelNombres = new ArrayList<>();
        List<String> clienteKeys = new ArrayList<>(mapaClientes.keySet());
        List<String> pastelKeys = new ArrayList<>(mapaPasteles.keySet());

        for (String key : clienteKeys) clienteNombres.add(mapaClientes.get(key).getNombre());
        for (String key : pastelKeys) pastelNombres.add(mapaPasteles.get(key).getNombrePastel());

        spinnerCliente.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, clienteNombres));
        spinnerPastel.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, pastelNombres));

        String[] estados = {"pendiente", "confirmada", "en_produccion", "lista_para_entrega", "entregada", "cancelada"};
        ArrayAdapter<String> adapterEstado = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, estados);
        spinnerEstado.setAdapter(adapterEstado);

        new AlertDialog.Builder(getContext())
                .setTitle("Agregar Reserva")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    int indexCliente = spinnerCliente.getSelectedItemPosition();
                    int indexPastel = spinnerPastel.getSelectedItemPosition();

                    String fecha = etFecha.getText().toString().trim();
                    String notas = etNotas.getText().toString().trim();
                    String fechaCreacion = etFechaCreacion.getText().toString().trim();
                    String estado = spinnerEstado.getSelectedItem().toString();
                    String pago = etPago.getText().toString().trim();

                    String keyCliente = clienteKeys.get(indexCliente);
                    String keyPastel = pastelKeys.get(indexPastel);

                    Cliente cliente = mapaClientes.get(keyCliente);
                    Pasteles pastel = mapaPasteles.get(keyPastel);

                    String id = "reserva" + (listaReservas.size() + 1);
                    String firebaseId = FirebaseDatabase.getInstance().getReference("reservas").push().getKey();

                    //Reserva nueva = new Reserva(id, keyCliente, cliente.getNombre(), keyPastel, pastel.getNombrePastel(),fecha, fechaCreacion, estado, pago, notas);
                    Reserva nueva = new Reserva(keyCliente, cliente.getNombre(), keyPastel, pastel.getNombrePastel(),fecha, fechaCreacion, estado, pago, notas);

                    FirebaseDatabase.getInstance().getReference("reservas")
                            .child(firebaseId)
                            .setValue(nueva)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Reserva agregada", Toast.LENGTH_SHORT).show();
                                cargarReservasDesdeFirebase();
                            });
                })
                .setNegativeButton("Cancelar", null)
                .show();
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
                    reservaAdapter.setEsAdmin(false);
                } else {
                    // Ocultar botón agregar
                    reservaAdapter.setEsAdmin(false);
                }
                reservaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "No se pudo verificar el rol del usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }
}