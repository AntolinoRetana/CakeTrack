package com.example.caketrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.caketrack.Menu.MainDashboardActivityProduction;
import com.example.caketrack.Menu.MainDashboardActivitySeller;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsuario, etPassword;
    private Button btnLogin;
    private FirebaseAuth miAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        miAuth = FirebaseAuth.getInstance();
        btnLogin.setOnClickListener(v -> {
            //RegisterUsers();
//            RegisterUserswithAuthentication();
            IniciarSesion();
        });

    }
    private void IniciarSesion() {
        String email = etUsuario.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        miAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = miAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();

                            // Consultar la base de datos para obtener el rol
                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(uid)
                                    .child("role")
                                    .get()
                                    .addOnSuccessListener(dataSnapshot -> {
                                        if (dataSnapshot.exists()) {
                                            String rol = dataSnapshot.getValue(String.class);

                                            // Redirigir según el rol
                                            if ("admin".equals(rol)) {
                                                startActivity(new Intent(this, MainDashboardActivity.class));
                                            } else if ("seller".equals(rol)) {
                                                startActivity(new Intent(this, MainDashboardActivitySeller.class));
                                            } else if ("production".equals(rol)) {
                                                startActivity(new Intent(this, MainDashboardActivityProduction.class));
                                            } else {
                                                Toast.makeText(this, "Rol no reconocido", Toast.LENGTH_SHORT).show();
                                            }
                                            finish(); // evitar volver al login
                                        } else {
                                            Toast.makeText(this, "Rol no encontrado", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Error al obtener rol", Toast.LENGTH_SHORT).show();
                                    });

                        }
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


//    private void RegisterUsers(){
//        String email = etUsuario.getText().toString();
//        String password = etPassword.getText().toString();
//        if(password.isEmpty() || password.isBlank() ||email.isEmpty() || email.isBlank()){
//            System.out.println("Campos vacios");
//        }
//        else{
//            miAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, task -> {
//            if(task.isSuccessful()){
//                Toast.makeText(this,"Usuario creado",Toast.LENGTH_SHORT).show();
//            }
//            else{
//                System.out.println("Fallo al crear usuario" + task.getException().getMessage());
//            }
//            });
//        }
//    }

//    private void IniciarSesion(){
//        String email = etUsuario.getText().toString();
//        String password = etPassword.getText().toString();
//        if(password.isEmpty() || password.isBlank() ||email.isEmpty() || email.isBlank()){
//            System.out.println("Campos vacios");
//        }
//        else{
//            miAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
//               if(task.isSuccessful()){
//                   Toast.makeText(this,"Usuario iniciado",Toast.LENGTH_SHORT).show();
//                   startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
//
//               }
//               else{
//                   System.out.println("Fallo el login" + task.getException().getMessage());
//               }
//            });
//        }
//    }

    ///
//    private void RegisterUserswithAuthentication(){
//        String email = etUsuario.getText().toString();
//        String password = etPassword.getText().toString();
//        if(password.isEmpty() || password.isBlank() ||email.isEmpty() || email.isBlank()){
//            System.out.println("Campos vacios");
//        }
//        else{
//            miAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, task -> {
//                if(task.isSuccessful()){
//                    FirebaseUser user = miAuth.getCurrentUser();
//                    if(user != null){
//                        user.sendEmailVerification().addOnCompleteListener(task1 ->
//                        {
//                            if(task1.isSuccessful()){
//                                Toast.makeText(this,"Email de verificacion enviado",Toast.LENGTH_SHORT).show();
//                            }
//                            else{
//                                System.out.println("Fallo al enviar email de verificacion" + task1.getException().getMessage());
//                            }
//                        });
//                    }
//                    Toast.makeText(this,"Usuario creado",Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    System.out.println("Fallo al crear usuario" + task.getException().getMessage());
//                }
//            });
//        }
//    }
}