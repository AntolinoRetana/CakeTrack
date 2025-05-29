package com.example.caketrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.caketrack.Moduls.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText etNombre, etCorreo, etPassword, etPassword2;
    private TextView tvLogin;
    private Spinner spinnerRol;
    private Button btnRegistrar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Inicialización
        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        etPassword2 = findViewById(R.id.etPassword2);
        spinnerRol = findViewById(R.id.spinnerRol);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        tvLogin = findViewById(R.id.tvLogin);

        mAuth = FirebaseAuth.getInstance();

        // Configurar Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.opciones_rol, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(adapter);

        // Evento de botón
        btnRegistrar.setOnClickListener(v -> registerUser());
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);

        });
    }

    private void registerUser() {
        String nombre = etNombre.getText().toString().trim();
        String email = etCorreo.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String pass2 = etPassword2.getText().toString().trim();
        String rol = spinnerRol.getSelectedItem().toString();

        // Validaciones básicas
        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty() || pass2.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(pass2)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear usuario en Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Enviar correo de verificación
                            user.sendEmailVerification().addOnCompleteListener(verifyTask -> {
                                if (verifyTask.isSuccessful()) {
                                    String uid = user.getUid();
                                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

                                    usersRef.get().addOnCompleteListener(snapshotTask -> {
                                        if (snapshotTask.isSuccessful()) {
                                            long userCount = snapshotTask.getResult().getChildrenCount();
                                            String alias = "usuario" + (userCount + 1);
                                            Usuario userData = new Usuario(alias, nombre, rol);
                                            DatabaseReference refUsuario = usersRef.child(uid);

                                            refUsuario.setValue(userData).addOnCompleteListener(dbTask -> {
                                                if (dbTask.isSuccessful()) {
                                                    Toast.makeText(this, "Verifica tu correo antes de iniciar sesión", Toast.LENGTH_LONG).show();
                                                    finish();
                                                } else {
                                                    Toast.makeText(this, "Error al guardar datos del usuario", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(this, "Error al obtener usuarios", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(this, "Error al enviar correo de verificación", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } else {
                        Toast.makeText(this, "Error al crear usuario: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}