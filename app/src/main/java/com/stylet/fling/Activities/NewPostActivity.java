package com.stylet.fling.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stylet.fling.Model.BlogPost;
import com.stylet.fling.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class NewPostActivity extends AppCompatActivity {

    private Toolbar newPostToolbar;

    private ImageView newPostImage;
    private EditText newPostDesc;
    private Button newPostBtn;

    private TextView addimage;
    CircleImageView userimage;

    private Uri postImageUri = null;

    private ProgressBar newPostProgress;

    private StorageReference storageReference;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private String current_user_id;
    private String current_user_name;
    private String current_user_image;

    private Bitmap compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userimage = findViewById(R.id.userimage);



        current_user_id = firebaseAuth.getCurrentUser().getUid();
        current_user_name = firebaseAuth.getCurrentUser().getDisplayName();

        current_user_image = firebaseAuth.getCurrentUser().getPhotoUrl().toString();


        addimage = findViewById(R.id.add_image);
        newPostImage = findViewById(R.id.new_post_image);
        newPostDesc = findViewById(R.id.new_post_desc);
        newPostBtn = findViewById(R.id.post_btn);
        newPostProgress = findViewById(R.id.new_post_progress);

        Glide.with(getApplicationContext())
                .load(user.getPhotoUrl())
                .into(userimage);

        newPostProgress.setVisibility(View.GONE);
        newPostImage.setVisibility(View.GONE);

        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newPostImage.getVisibility() == View.GONE){
                    newPostImage.setVisibility(View.VISIBLE);
                }
            }
        });


        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        //.setMinCropResultSize(512, 512)
                        //.setAspectRatio(1, 1)
                        .start(NewPostActivity.this);

            }
        });


        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String desc = newPostDesc.getText().toString();

                if(!TextUtils.isEmpty(desc) && postImageUri != null){

                    if (newPostProgress.getVisibility() == View.GONE)
                        newPostProgress.setVisibility(View.VISIBLE);

                    final String randomName = UUID.randomUUID().toString();

                    // PHOTO UPLOAD
                    File newImageFile = new File(postImageUri.getPath());
                    try {

                        compressedImageFile = new Compressor(NewPostActivity.this)
                                .setMaxHeight(720)
                                .setMaxWidth(720)
                                .setQuality(50)
                                .compressToBitmap(newImageFile);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] imageData = baos.toByteArray();

                    // PHOTO UPLOAD

                    final StorageReference imagePathFile = storageReference.child("post_images").child(randomName + ".jpg");
                    imagePathFile.putBytes(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imagePathFile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUri = uri.toString();

                                    File newThumbFile = new File(postImageUri.getPath());
                                    try {

                                        compressedImageFile = new Compressor(NewPostActivity.this)
                                                .setMaxHeight(100)
                                                .setMaxWidth(100)
                                                .setQuality(1)
                                                .compressToBitmap(newThumbFile);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] thumbData = baos.toByteArray();


                                    final StorageReference imageThumbPathFile = storageReference.child("post_images/thumbs").child(randomName + ".jpg");
                                    imageThumbPathFile.putBytes(thumbData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                                            imageThumbPathFile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String downloadthumbUri = uri.toString();

                                                    BlogPost blogPost = new BlogPost(current_user_name, current_user_image, current_user_id, downloadUri, desc, downloadthumbUri);
                                                    
                                                    addPost(blogPost);


                                                    newPostProgress.setVisibility(View.INVISIBLE);

                                                }
                                            });

                                        }
                                    });
                                }
                            });

                        }
                    });
                }else if(!TextUtils.isEmpty(desc) && postImageUri == null){

                    newPostProgress.setVisibility(View.VISIBLE);

                    BlogPost blogPost = new BlogPost(current_user_name, current_user_image, current_user_id, null, desc, null);
                    addPost(blogPost);

                    newPostProgress.setVisibility(View.INVISIBLE);
                }

                else if (TextUtils.isEmpty(desc) && postImageUri != null){

                    newPostProgress.setVisibility(View.VISIBLE);

                    final String randomName = UUID.randomUUID().toString();

                    // PHOTO UPLOAD
                    File newImageFile = new File(postImageUri.getPath());
                    try {

                        compressedImageFile = new Compressor(NewPostActivity.this)
                                .setMaxHeight(720)
                                .setMaxWidth(720)
                                .setQuality(50)
                                .compressToBitmap(newImageFile);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] imageData = baos.toByteArray();

                    // PHOTO UPLOAD

                    final StorageReference imagePathFile = storageReference.child("post_images").child(randomName + ".jpg");
                    imagePathFile.putBytes(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imagePathFile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUri = uri.toString();

                                    File newThumbFile = new File(postImageUri.getPath());
                                    try {

                                        compressedImageFile = new Compressor(NewPostActivity.this)
                                                .setMaxHeight(100)
                                                .setMaxWidth(100)
                                                .setQuality(1)
                                                .compressToBitmap(newThumbFile);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] thumbData = baos.toByteArray();


                                    final StorageReference imageThumbPathFile = storageReference.child("post_images/thumbs").child(randomName + ".jpg");
                                    imageThumbPathFile.putBytes(thumbData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                                            imageThumbPathFile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String downloadthumbUri = uri.toString();

                                                    BlogPost blogPost = new BlogPost(current_user_name, current_user_image, current_user_id, downloadUri, null, downloadthumbUri);

                                                    addPost(blogPost);


                                                    newPostProgress.setVisibility(View.INVISIBLE);

                                                }
                                            });

                                        }
                                    });
                                }
                            });

                        }
                    });



                }

                else {
                    Toast.makeText(NewPostActivity.this, "Fill at least one field", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void addPost(BlogPost blogPost) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Posts").push();

        String key = myRef.getKey();

        blogPost.setPostKey(key);

        myRef.setValue(blogPost).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(NewPostActivity.this, "Post was added", Toast.LENGTH_SHORT).show();
                Intent mainIntent = new Intent(NewPostActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewPostActivity.this, "Database Error: "+ e.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                newPostDesc.setHint("Wanna write about this image???");
                postImageUri = result.getUri();
                newPostImage.setImageURI(postImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Toast.makeText(NewPostActivity.this, error.toString(), Toast.LENGTH_LONG).show();

            }
        }

    }

}
