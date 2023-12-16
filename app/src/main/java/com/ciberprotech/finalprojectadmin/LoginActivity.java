package com.ciberprotech.finalprojectadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ciberprotech.finalprojectadmin.model.AuthResponse;
import com.ciberprotech.finalprojectadmin.service.FruitShopService;
import com.ciberprotech.finalprojectadmin.service.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Retrofit retrofit = RetrofitClient.getClient();
        FruitShopService loginService = retrofit.create(FruitShopService.class);

        TextView emailTxt = findViewById(R.id.textViewEmail);
        EditText editEmail = findViewById(R.id.txtEmail);
        Button btnVerify = findViewById(R.id.btnverify);

        TextView vcodeTxt = findViewById(R.id.textViewVCode);
        EditText editVcode = findViewById(R.id.editTextVcode);
        Button btnlogin = findViewById(R.id.btnlogin);

        emailTxt.setVisibility(View.VISIBLE);
        editEmail.setVisibility(View.VISIBLE);
        btnVerify.setVisibility(View.VISIBLE);

        vcodeTxt.setVisibility(View.GONE);
        editVcode.setVisibility(View.GONE);
        btnlogin.setVisibility(View.GONE);

        //Verify Button
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailView = findViewById(R.id.txtEmail);
                String emailAddress = emailView.getText().toString();

                Call<AuthResponse> call = loginService.sendVcode(emailAddress);
                call.enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (response.isSuccessful()) {
                            AuthResponse responce = response.body();
                            if (responce != null) {
                                String status = responce.getStatus();

                                if (status.equals("success")) {
                                    Toast.makeText(getApplicationContext(), "Check your email inbox and enter verification code", Toast.LENGTH_SHORT).show();

                                    emailTxt.setVisibility(View.GONE);
                                    editEmail.setVisibility(View.GONE);
                                    btnVerify.setVisibility(View.GONE);

                                    vcodeTxt.setVisibility(View.VISIBLE);
                                    editVcode.setVisibility(View.VISIBLE);
                                    btnlogin.setVisibility(View.VISIBLE);

                                } else {
                                    Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Try again later.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }

                });

            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vcode = editVcode.getText().toString();

                Call<AuthResponse> call = loginService.login(vcode);
                call.enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (response.isSuccessful()) {
                            AuthResponse responce = response.body();
                            if (responce != null) {
                                String status = responce.getStatus();
                                if (status.equals("success")) {
                                    Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                                    finish();

                                } else {
                                    Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
                                    editVcode.setText("");
                                    editEmail.setText("");

                                    emailTxt.setVisibility(View.VISIBLE);
                                    editEmail.setVisibility(View.VISIBLE);
                                    btnVerify.setVisibility(View.VISIBLE);

                                    vcodeTxt.setVisibility(View.GONE);
                                    editVcode.setVisibility(View.GONE);
                                    btnlogin.setVisibility(View.GONE);

                                }

                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Try again later.", Toast.LENGTH_SHORT).show();
                            editVcode.setText("");
                            editEmail.setText("");

                            emailTxt.setVisibility(View.VISIBLE);
                            editEmail.setVisibility(View.VISIBLE);
                            btnVerify.setVisibility(View.VISIBLE);

                            vcodeTxt.setVisibility(View.GONE);
                            editVcode.setVisibility(View.GONE);
                            btnlogin.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Try again later.", Toast.LENGTH_SHORT).show();
                        editVcode.setText("");
                        editEmail.setText("");

                        emailTxt.setVisibility(View.VISIBLE);
                        editEmail.setVisibility(View.VISIBLE);
                        btnVerify.setVisibility(View.VISIBLE);

                        vcodeTxt.setVisibility(View.GONE);
                        editVcode.setVisibility(View.GONE);
                        btnlogin.setVisibility(View.GONE);

                    }

                });

            }
        });

    }
}