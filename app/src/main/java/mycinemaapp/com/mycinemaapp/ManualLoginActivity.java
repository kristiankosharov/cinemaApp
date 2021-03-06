package mycinemaapp.com.mycinemaapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import database.SQLite.User;
import database.SQLite.UsersDataSource;
import helpers.SessionManager;

/**
 * Created by kristian on 15-3-25.
 */
public class ManualLoginActivity extends Activity implements View.OnClickListener {

    private ImageView userPicture, back;
    private EditText names, email, password;
    private static final int TAKE_PICTURE = 1;
    private Uri imageUri;
    private Button logIn;
    private TextView signIn, createAccount, terms;
    private SessionManager sm;
    private boolean isOk = false;

    private UsersDataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_login_activity);
        sm = new SessionManager(this);
        userPicture = (ImageView) findViewById(R.id.user_icon);
        names = (EditText) findViewById(R.id.names);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.pass);
        logIn = (Button) findViewById(R.id.log_in);
        back = (ImageView) findViewById(R.id.back);
        terms = (TextView) findViewById(R.id.terms);
        signIn = (TextView) findViewById(R.id.sign_in);
        createAccount = (TextView) findViewById(R.id.create_account);
        signIn.setOnClickListener(this);
        createAccount.setOnClickListener(this);
        terms.setOnClickListener(this);
        back.setOnClickListener(this);
        logIn.setOnClickListener(listenerFromCreateAcc());

        datasource = new UsersDataSource(this);
        datasource.open();
        sm.setUserNames(names.getText().toString());
//        float density = getResources().getDisplayMetrics().density;
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (120 * density), (int) (120 * density));
//        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        params.addRule(RelativeLayout.ABOVE, R.id.names);
//        params.setMargins(0, 0, 0, (int) (20 * density));
//        userPicture.setLayoutParams(params);
        userPicture.setOnClickListener(this);


//        File photo = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pic.jpg");
//        Toast.makeText(this, photo.toString(), Toast.LENGTH_LONG).show();
//        if (photo != null) {
//            userPicture.setImageURI(Uri.fromFile(photo));
//        } else {
//            userPicture.setImageResource(R.drawable.add_picture);
//        }
//        Picasso.with(this)
//                .load("http://hdwallpapersmart.com/wp-content/uploads/2014/01/Funny-Nature-Hd-Wallpapers.jpg")
//                .transform(new RoundedImageView())
//                .into(userPicture);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_icon:
                hideKeyboard(this);
                TakePictureFragment takePictureFragment = new TakePictureFragment(userPicture);
                FragmentTransaction takePictureTransaction = getFragmentManager().beginTransaction();
                takePictureTransaction.addToBackStack("Take Picture Fragment");
                takePictureTransaction.add(R.id.fragment_container, takePictureFragment);
                takePictureTransaction.commit();
                break;
            case R.id.back:
                onBackPressed();
                break;
            case R.id.sign_in:
                signIn.setTextColor(Color.WHITE);
                createAccount.setTextColor(getResources().getColor(R.color.light_gray));
                logIn.setOnClickListener(listenerFromLogin());
                userPicture.setVisibility(View.INVISIBLE);
                names.setVisibility(View.GONE);
                terms.setText(getResources().getString(R.string.forgot_password));
                terms.setOnClickListener(termsFromLogin());
                break;
            case R.id.create_account:
                createAccount.setTextColor(Color.WHITE);
                signIn.setTextColor(getResources().getColor(R.color.light_gray));
                logIn.setOnClickListener(listenerFromCreateAcc());
                userPicture.setVisibility(View.VISIBLE);
                names.setVisibility(View.VISIBLE);
                terms.setText(getResources().getString(R.string.terms));
                break;
            case R.id.terms:
                break;
        }

    }

    public boolean validateCreateElements(String namesString, String emailString, String passString) {
        boolean check = true;
        // If email is empty or not matching of pattern set error on EditText
        if (emailString.isEmpty()
                || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailString)
                .matches()) {
            email.setError("Please input correct e-mail!");
            check = false;
        }
        // If password is empty or smaller than 4 symbols set error on EditText
        if (passString.isEmpty() || passString.length() < 4) {
            password.setError("Input password at least 4 symbols!");
            check = false;
        }
        // If names is empty or smaller than 2 symbols set error on EditText
        if (namesString.isEmpty() || namesString.length() < 2) {
            names.setError("Input at least 2 symbols!");
            check = false;
        }

        return check;
    }

    public boolean validateLoginElements(String emailString, String passString) {
        boolean check = true;
        // If email is empty or not matching of pattern set error on EditText
        if (emailString.isEmpty()
                || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailString)
                .matches()) {
            email.setError("Please input correct e-mail!");
            check = false;
        }
        // If password is empty or smaller than 4 symbols set error on EditText
        if (passString.isEmpty() || passString.length() < 4) {
            password.setError("Please input correct password!");
            check = false;
        }
        return check;
    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View f = activity.getCurrentFocus();
        if (null != f && null != f.getWindowToken() && EditText.class.isAssignableFrom(f.getClass()))
            imm.hideSoftInputFromWindow(f.getWindowToken(), 0);
        else
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    private void withoutImage() {
        isOk = false;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ManualLoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setMessage("Save without profile image?");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private View.OnClickListener listenerFromCreateAcc() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String namesString = names.getText().toString();
                String emailString = email.getText().toString();
                String passString = password.getText().toString();

                List<User> userList = datasource.getAllComments();
                boolean isDuplicate = false;
                if (validateCreateElements(namesString, emailString, passString)) {
                    sm.setUserNames(namesString);
                    sm.setEmail(emailString);
                    sm.setUserPassword(passString);
                    for (int i = 0; i < userList.size(); i++) {
                        if (userList.get(i).getUserEmail().equals(emailString)) {
                            isDuplicate = true;

                        } else {
                            isDuplicate = false;
                        }
                    }
                    String imagePath;

//                    Toast.makeText(ManualLoginActivity.this, "SNIMANA:" + sm.getMyProfileAvatarCapturePath() + "IZBRANA" + sm.getMyProfileAvatarPath(), Toast.LENGTH_LONG).show();

                    if (sm.getMyProfileAvatarPath() != null) {
                        imagePath = sm.getMyProfileAvatarPath();
                    } else {
                        imagePath = "";
                    }
                    if (sm.getMyProfileAvatarCapturePath() != null) {
                        imagePath = sm.getMyProfileAvatarCapturePath();
                    } else if (imagePath.equals("")) {
                        imagePath = "";
                    }
                    Toast.makeText(ManualLoginActivity.this, imagePath, Toast.LENGTH_LONG).show();
                    datasource.createUsers(namesString, emailString, passString, imagePath);
                    sm.setRemember(true);
                    isOk = true;
                }
                if (sm.getMyProfileAvatarPath() == null && sm.getMyProfileAvatarCapturePath() == null && isOk && !isDuplicate) {
                    withoutImage();
                }
                if (isDuplicate) {
                    Toast.makeText(ManualLoginActivity.this, "This e-mail is hired!", Toast.LENGTH_LONG).show();
                    isOk = false;
                } else if (isOk) {
                    Intent intent = new Intent(ManualLoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }

            }
        };
        return listener;
    }

    private View.OnClickListener listenerFromLogin() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailString = email.getText().toString();
                String pass = password.getText().toString();
                if (validateLoginElements(emailString, pass)) {

                    List<User> values = datasource.getAllComments();
                    boolean hasUser = true;
                    for (int i = 0; i < values.size(); i++) {
                        if (values.get(i).getUserEmail().equals(emailString) && values.get(i).getPassword().equals(pass)) {
                            sm.setUserNames(values.get(i).getUserName());
                            hasUser = true;
                            sm.setRemember(true);
                            sm.setEmail(emailString);
                            sm.setUserPassword(pass);
                            Intent intent = new Intent(ManualLoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            return;
                        } else {
                            hasUser = false;
                        }
                    }
                    if (!hasUser) {
                        Toast.makeText(ManualLoginActivity.this, "Incorrect user e-mail or password!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        return listener;
    }

    private View.OnClickListener termsFromLogin() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ManualLoginActivity.this);
                View view = getLayoutInflater().inflate(R.layout.forgot_password, null);
                EditText email = (EditText) view.findViewById(R.id.email);
                builder.setView(getLayoutInflater().inflate(R.layout.forgot_password, null));
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ManualLoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        };
        return listener;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        datasource.close();
    }
}
