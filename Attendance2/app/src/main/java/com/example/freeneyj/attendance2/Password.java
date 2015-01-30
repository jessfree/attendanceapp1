

package com.example.freeneyj.attendance2;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Password extends Activity {

    EditText pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtitle);
        TextView text = (TextView) findViewById(R.id.add_text);
        text.setText("Enter password   ");
        Button done = (Button) findViewById(R.id.settitle);
        done.setText("GO");
        pass = (EditText) findViewById(R.id.getalarmtitle);
        done.setOnClickListener(check);
        Animation zoom = AnimationUtils.loadAnimation(Password.this, R.anim.fadein);
        findViewById(R.id.add_main).startAnimation(zoom);
    }


    View.OnClickListener check = new View.OnClickListener() {

        public void onClick(View v) {
            // TODO Auto-generated method stub
            SharedPreferences pwd = getSharedPreferences("GTAcampuSettings", MODE_PRIVATE);
            if (pass.getText().toString().hashCode() == (pwd.getInt("Password", 0))) {
                setResult(Activity.RESULT_OK);
                finish();
            } else {
                Toast pswd = new Toast(Password.this);
                pswd = Toast.makeText(getBaseContext(), "Sorry!..Incorrect Password", Toast.LENGTH_LONG);
                pswd.setGravity(Gravity.CENTER, 0, 0);
                pswd.show();
            }
        }

    };

}
