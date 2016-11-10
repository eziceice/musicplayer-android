package ryan.finalassignment;

import android.hardware.input.InputManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class FileActivity extends AppCompatActivity {

    private ArrayList<HashMap<String, Object>> al;
    private Button selectBtn;
    private LinearLayout layoutSelect;
    private LinearLayout layoutCreate;
    private InputManager inputManager;
    private String musicFilePath = "";

    private File selectedFile;
    private boolean canSelect = false;
    private HashMap<String, Integer> lastPositions = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED, getIntent());

        setContentView(R.layout.activity_file);

    }
}
