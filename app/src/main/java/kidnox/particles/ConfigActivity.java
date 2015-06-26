package kidnox.particles;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;

public class ConfigActivity extends Activity {

    EditText editText;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        editText = (EditText) findViewById(R.id.et_config);
        editText.setText(App.loadConfigAsString());
    }

    public void onSaveClick(View v) {
        if(disableActionFor(500) && App.saveConfig(editText.getText().toString())) {
            finish();
        }
    }

    public void onResetClick(View v) {
        if(!disableActionFor(500)) return;
        App.reset();
        editText.setText(App.loadConfigAsString());
    }

    boolean actionDisabled;
    private boolean disableActionFor(long delay) {
        if(actionDisabled) return false;
        actionDisabled = true;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override public void run() {
                actionDisabled = false;
            }
        }, delay);
        return true;
    }

}
