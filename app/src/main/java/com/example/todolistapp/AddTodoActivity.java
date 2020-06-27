package com.example.todolistapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.todolistapp.database.Todo;
import com.example.todolistapp.databinding.ActivityAddTodoBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.todolistapp.ToDoListActivity.TODO_ITEM;

public class AddTodoActivity extends AppCompatActivity {
    private static final int CAMERA_REQ = 1;
    private static final int GALLERY_REQ = 2;
    private static final int PERMISSION_REQ = 3;

    private ActivityAddTodoBinding activityAddTodoBinding;
    private Todo toDoItem;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddTodoBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_todo);
        initView();
    }

    private void initView() {
        toDoItem = new Todo();
        activityAddTodoBinding.btnDone.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra(TODO_ITEM, toDoItem);
            setResult(RESULT_OK, intent);
            finish();
        });
        activityAddTodoBinding.btnCancel.setOnClickListener(v -> {
            finish();
            setResult(RESULT_CANCELED);
        });
        activityAddTodoBinding.addImage.setOnClickListener(v -> {
            askForPermission(PERMISSION_REQ);
        });
        activityAddTodoBinding.todoTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim();
                if (!TextUtils.isEmpty(input)) {
                    toDoItem.setTitle(input);
                    activityAddTodoBinding.btnDone.setEnabled(true);
                } else activityAddTodoBinding.btnDone.setEnabled(false);
            }
        });

        activityAddTodoBinding.todoCta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim();
                if (!TextUtils.isEmpty(input)) {
                    toDoItem.setDescription(input);
                }
            }
        });
    }

    private void selectImage() {
        final CharSequence[] options = {getString(R.string.take_photo), getString(R.string.choose_from_gallery), getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddTodoActivity.this);
        builder.setTitle(R.string.add_photo);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals(getString(R.string.take_photo))) {
                    dispatchTakePictureIntent();
                } else if (options[item].equals(getString(R.string.choose_from_gallery))) {
                    dispatchGalleryIntent();
                } else if (options[item].equals(getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void dispatchGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, GALLERY_REQ);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getApplication().getPackageName() + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQ);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getFilesDir();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQ) {
                setImage();
            } else if (requestCode == GALLERY_REQ) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                currentPhotoPath = c.getString(columnIndex);
                c.close();
                setImage();
            }
        }
    }

    private void setImage() {
        Glide.with(this)
                .load(new File(currentPhotoPath))
                .circleCrop()
                .placeholder(R.mipmap.ic_launcher)
                .into(activityAddTodoBinding.addImage);
        toDoItem.setImagePath(currentPhotoPath);
    }

    private void askForPermission(Integer requestCode) {
        if (ContextCompat.checkSelfPermission(AddTodoActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(AddTodoActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddTodoActivity.this, Manifest.permission.CAMERA)) {
                Toast.makeText(AddTodoActivity.this, "Need camera/Gallery permission", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(AddTodoActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);

            } else {
                ActivityCompat.requestPermissions(AddTodoActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
            }
        } else {
            selectImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ) {
            if (ContextCompat.checkSelfPermission(AddTodoActivity.this,
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(AddTodoActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                selectImage();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
