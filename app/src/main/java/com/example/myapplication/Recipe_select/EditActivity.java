package com.example.myapplication.Recipe_select;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.mypage.RetrofitServer.ApiClient;
import com.example.myapplication.mypage.RetrofitServer.ApiInterface;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditActivity extends AppCompatActivity {

    EditText et_title, et_note;
    ProgressDialog progressDialog;

    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        et_title = findViewById(R.id.title);
        et_note = findViewById(R.id.note);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("기다려");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_editer, menu);
        return true;
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save:
                String title = et_title.getText().toString().trim();
                String note = et_note.getText().toString().trim();

                if (title.isEmpty()) {
                    et_title.setError("제목을 작성하세요");
                } else if (note.isEmpty()) {
                    et_note.setError("노트를 작성하세요.");
                } else {
                    saveNote(title, note);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void saveNote(final String title, final String note) {

        progressDialog.show();

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<Note> call = apiInterface.saveNote(title, note);

        call.enqueue(new Callback<Note>() {
            @Override
            public void onResponse(@NotNull Call<Note> call, @NotNull Response<Note> response) {
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    Boolean success = response.body().getSuccess();
                    String message = response.body().getMessage();

                    if (success) {
                        Toast.makeText(EditActivity.this, message, Toast.LENGTH_SHORT).show();
                        finish(); // 메인액티비티로 돌아감

                        Log.e("서버에서 받아온내용", message);
                    }
                } else {
                    assert response.body() != null;
                    Toast.makeText(EditActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    finish();

                }

            }

            @Override
            public void onFailure(@NonNull Call<Note> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(EditActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
}