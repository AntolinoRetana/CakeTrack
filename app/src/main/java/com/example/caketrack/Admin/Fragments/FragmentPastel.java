package com.example.caketrack.Admin.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.caketrack.Admin.Pasteles.Adapter.PastelesAdapter;
import com.example.caketrack.Admin.Pasteles.Moduls.Pasteles;
import com.example.caketrack.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentPastel#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPastel extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Mis variables
    private RecyclerView recyclerPasteles;
    private PastelesAdapter pastelAdapter;
    private ArrayList<Pasteles> listaPasteles = new ArrayList<>();
    private ArrayList<String> listaUIDs = new ArrayList<>();
    private FloatingActionButton fabAgregar;

    public FragmentPastel() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentPastel.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentPastel newInstance(String param1, String param2) {
        FragmentPastel fragment = new FragmentPastel();
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
        View view = inflater.inflate(R.layout.fragment_pastel, container, false);

        recyclerPasteles = view.findViewById(R.id.recyclerPasteles);
        fabAgregar = view.findViewById(R.id.fabAgregarPastel);

        recyclerPasteles.setLayoutManager(new LinearLayoutManager(getContext()));
        pastelAdapter = new PastelesAdapter(getContext(), listaPasteles, listaUIDs);
        recyclerPasteles.setAdapter(pastelAdapter);

        cargarPastelesDesdeFirebase();

        fabAgregar.setOnClickListener(v -> mostrarDialogoAgregarPastel());

        return view;
    }

    private void cargarPastelesDesdeFirebase() {
        FirebaseDatabase.getInstance().getReference("pasteles")
                .get()
                .addOnSuccessListener(snapshot -> {
                    listaPasteles.clear();
                    listaUIDs.clear();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Pasteles p = snap.getValue(Pasteles.class);
                        if (p != null) {
                            listaPasteles.add(p);
                            listaUIDs.add(snap.getKey());
                        }
                    }
                    pastelAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al cargar pasteles", Toast.LENGTH_SHORT).show());
    }

    private void mostrarDialogoAgregarPastel() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_agregar_pastel, null);

        EditText etNombre = dialogView.findViewById(R.id.etNombrePastel);
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcionPastel);
        EditText etPrecio = dialogView.findViewById(R.id.etPrecioPastel);
        EditText etTamano = dialogView.findViewById(R.id.etTamanoPastel);
        EditText etCantidad = dialogView.findViewById(R.id.etCantidadPastel);
        EditText etDisponible = dialogView.findViewById(R.id.etDisponiblePastel);

        new AlertDialog.Builder(getContext())
                .setTitle("Agregar Pastel")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = etNombre.getText().toString().trim();
                    String descripcion = etDescripcion.getText().toString().trim();
                    String precioStr = etPrecio.getText().toString().trim();
                    String tamano = etTamano.getText().toString().trim();
                    String cantidadStr = etCantidad.getText().toString().trim();
                    String disponibleStr = etDisponible.getText().toString().trim();

                    if (nombre.isEmpty() || precioStr.isEmpty() || cantidadStr.isEmpty() || disponibleStr.isEmpty()) {
                        Toast.makeText(getContext(), "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double precio = Double.parseDouble(precioStr);
                    int cantidad = Integer.parseInt(cantidadStr);
                    boolean disponible = disponibleStr.equals("1");

                    FirebaseDatabase.getInstance().getReference("pasteles")
                            .get()
                            .addOnSuccessListener(snapshot -> {
                                long count = snapshot.getChildrenCount();
                                String pastelId = "pastel" + (count + 1);
                                String firebaseId = FirebaseDatabase.getInstance().getReference("pasteles").push().getKey();

                                Pasteles nuevo = new Pasteles(pastelId, nombre, descripcion, precio, tamano, disponible, cantidad);

                                FirebaseDatabase.getInstance().getReference("pasteles")
                                        .child(firebaseId)
                                        .setValue(nuevo)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "Pastel agregado", Toast.LENGTH_SHORT).show();
                                            cargarPastelesDesdeFirebase();
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(getContext(), "Error al guardar pastel", Toast.LENGTH_SHORT).show());
                            });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}