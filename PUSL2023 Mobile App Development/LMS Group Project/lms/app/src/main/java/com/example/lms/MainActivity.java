package com.example.lms;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    EditText editText;
    Button btn;

    StorageReference storageReference;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        btn = findViewById(R.id.btn);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("uploadPDF");

        btn.setEnabled(false);

        editText.setOnClickListener(v -> selectPDF());
    }

    private void selectPDF() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "PDF FILE SELECT"),12 );


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==12 && resultCode==RESULT_OK && data!=null && data.getData()!=null ){

            btn.setEnabled(true);
            editText.setText(data.getDataString().substring(data.getDataString().lastIndexOf("/") +1 ));

            btn.setOnClickListener(v -> uploadPDFFileFirebase(data.getData()));


        }

    }

    private void uploadPDFFileFirebase(Uri data) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("File is loading...");
        progressDialog.show();

        StorageReference reference=storageReference.child("upload" +System.currentTimeMillis()+".pdf");

        reference.putFile(data)

                .addOnSuccessListener(taskSnapshot -> {

                    Task<Uri> uriTask=taskSnapshot .getStorage().getDownloadUrl();
                    while (!uriTask.isComplete());
                    Uri uri = uriTask.getResult();

                    putPDF putPDF = new putPDF(editText.getText().toString(), uri.toString() );
                    databaseReference.child(Objects.requireNonNull(databaseReference.push().getKey())).setValue(putPDF);
                    Toast.makeText(MainActivity.this, "File Upload", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();

                }).addOnProgressListener(snapshot -> {

                    double progress=(100.0 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                    progressDialog.setMessage("File Uploaded..." +(int) progress+ "%" );

                });

    }

    public void retrievePDF(View view) {

        startActivity(new Intent(getApplicationContext(),downloadlecpdf.class));

    }
}
