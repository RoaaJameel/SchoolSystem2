package com.example.schoolsystem2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;
    private int currentMonkeyImageResId = R.drawable.monkey_open_eyes;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText usernameEditText = findViewById(R.id.username);
        EditText passwordEditText = findViewById(R.id.password);
        ImageView monkeyImage = findViewById(R.id.monkeyImage);
        Button loginButton = findViewById(R.id.loginButton);

        if (savedInstanceState != null) {
            String savedUsername = savedInstanceState.getString("username");
            String savedPassword = savedInstanceState.getString("password");

            if (savedUsername != null) {
                usernameEditText.setText(savedUsername);
            }
            if (savedPassword != null) {
                passwordEditText.setText(savedPassword);
            }
        }

        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.password,
                0,
                0,
                0);

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    passwordEditText.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.password,
                            0,
                            isPasswordVisible ? R.drawable.eye_show_password : R.drawable.eye_hide_password,
                            0);

                    if (isPasswordVisible) {
                        fadeImageChange(monkeyImage, R.drawable.monkey_peek);
                    } else {
                        fadeImageChange(monkeyImage, R.drawable.monkey_closed_eyes);
                    }

                } else {
                    passwordEditText.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.password,
                            0,
                            0,
                            0);

                    fadeImageChange(monkeyImage, R.drawable.monkey_open_eyes);
                }
            }

            @Override public void afterTextChanged(Editable s) { }
        });

        passwordEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (passwordEditText.getCompoundDrawables()[DRAWABLE_END] != null) {
                    int drawableWidth = passwordEditText.getCompoundDrawables()[DRAWABLE_END].getBounds().width();
                    if (event.getRawX() >= (passwordEditText.getRight() - drawableWidth - passwordEditText.getPaddingEnd())) {
                        if (isPasswordVisible) {
                            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(
                                    R.drawable.password,
                                    0,
                                    R.drawable.eye_hide_password,
                                    0);
                            isPasswordVisible = false;

                            if (passwordEditText.getText().length() > 0) {
                                fadeImageChange(monkeyImage, R.drawable.monkey_closed_eyes);
                            } else {
                                fadeImageChange(monkeyImage, R.drawable.monkey_open_eyes);
                            }
                        } else {
                            passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(
                                    R.drawable.password,
                                    0,
                                    R.drawable.eye_show_password,
                                    0);
                            isPasswordVisible = true;

                            fadeImageChange(monkeyImage, R.drawable.monkey_peek);
                        }
                        passwordEditText.setSelection(passwordEditText.getText().length());
                        return true;
                    }
                }
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(username, password);
        });
    }


    private void fadeImageChange(ImageView imageView, int newImageResId) {
        if (currentMonkeyImageResId == newImageResId) {
            return;
        }
        currentMonkeyImageResId = newImageResId;

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(imageView, "alpha", 1f, 0f);
        fadeOut.setDuration(200);
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                imageView.setImageResource(newImageResId);
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f);
                fadeIn.setDuration(200);
                fadeIn.start();
            }
        });
        fadeOut.start();
    }

    private void loginUser(String username, String password) {
        String url = "http://10.0.2.2/Android/login.php";

        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        Log.d("LOGIN_RESPONSE", response.toString());

                        String status = response.getString("status");
                        if (status.equals("success")) {
                            JSONObject userObject = response.getJSONObject("user");
                            String role = userObject.getString("role");
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

                            if (role.equals("admin")) {
                                Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (role.equals("teacher")) {
                                 Intent intent = new Intent(LoginActivity.this, TeacherActivity.class);
                                 startActivity(intent);
                                 finish();
                            } else if (role.equals("student")) {
                               //  Intent intent = new Intent(LoginActivity.this, ManageStuentsActivity.class);
                               //  startActivity(intent);
                               //  finish();
                            }
                        } else {
                            String message = response.getString("message");
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Response parsing error", Toast.LENGTH_LONG).show();
                    }

                },
                error -> {
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        Toast.makeText(this, "Server responded with status code: " + statusCode, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Error connecting to server: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    error.printStackTrace();
                }
        ) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        EditText usernameEditText = findViewById(R.id.username);
        EditText passwordEditText = findViewById(R.id.password);

        outState.putString("username", usernameEditText.getText().toString());
        outState.putString("password", passwordEditText.getText().toString());
    }


}
