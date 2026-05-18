package com.ict602.zakatgold;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    EditText etWeight, etGoldValue;
    RadioGroup rgGoldType;
    RadioButton rbKeep, rbWear;
    TextView btnCalculate, btnReset;
    TextView tvTotalValue, tvZakatPayable, tvTotalZakat, tvResultLabel;
    CardView cardResult;

    static final double NISAB_KEEP = 85.0;
    static final double NISAB_WEAR = 200.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Gold Zakat Calculator");
        }
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        // Make 3-dot menu icon white
        toolbar.getOverflowIcon().setTint(getResources().getColor(R.color.white));

        etWeight       = findViewById(R.id.etWeight);
        etGoldValue    = findViewById(R.id.etGoldValue);
        rgGoldType     = findViewById(R.id.rgGoldType);
        rbKeep         = findViewById(R.id.rbKeep);
        rbWear         = findViewById(R.id.rbWear);
        btnCalculate   = findViewById(R.id.btnCalculate);
        btnReset       = findViewById(R.id.btnReset);
        tvTotalValue   = findViewById(R.id.tvTotalValue);
        tvZakatPayable = findViewById(R.id.tvZakatPayable);
        tvTotalZakat   = findViewById(R.id.tvTotalZakat);
        tvResultLabel  = findViewById(R.id.tvResultLabel);
        cardResult     = findViewById(R.id.cardResult);

        cardResult.setVisibility(View.GONE);

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateZakat();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFields();
            }
        });
    }

    private void calculateZakat() {
        String weightStr    = etWeight.getText().toString().trim();
        String goldValueStr = etGoldValue.getText().toString().trim();

        if (weightStr.isEmpty()) {
            etWeight.setError("Please enter the weight of gold.");
            etWeight.requestFocus();
            return;
        }

        if (goldValueStr.isEmpty()) {
            etGoldValue.setError("Please enter the gold value per gram.");
            etGoldValue.requestFocus();
            return;
        }

        int selectedId = rgGoldType.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select gold type: Keep or Wear.", Toast.LENGTH_SHORT).show();
            return;
        }

        double weight    = Double.parseDouble(weightStr);
        double goldValue = Double.parseDouble(goldValueStr);

        if (weight <= 0) {
            etWeight.setError("Weight must be greater than 0.");
            etWeight.requestFocus();
            return;
        }

        if (goldValue <= 0) {
            etGoldValue.setError("Gold value must be greater than 0.");
            etGoldValue.requestFocus();
            return;
        }

        boolean isKeep   = (selectedId == R.id.rbKeep);
        double nisab     = isKeep ? NISAB_KEEP : NISAB_WEAR;
        String typeLabel = isKeep ? "Keep  (Nisab: 85g)" : "Wear  (Nisab: 200g)";

        double totalGoldValue     = weight * goldValue;
        double zakatableWeight    = weight - nisab;
        double zakatableGoldValue = (zakatableWeight > 0) ? (zakatableWeight * goldValue) : 0;
        double totalZakat         = zakatableGoldValue * 0.025;

        cardResult.setVisibility(View.VISIBLE);
        tvResultLabel.setText("Result  —  " + typeLabel);
        tvTotalValue.setText(String.format("RM %.2f", totalGoldValue));

        if (zakatableWeight <= 0) {
            tvZakatPayable.setText(String.format(
                    "RM 0.00  (below %.0fg nisab)", nisab));
            tvTotalZakat.setText("RM 0.00");
        } else {
            tvZakatPayable.setText(String.format("RM %.2f", zakatableGoldValue));
            tvTotalZakat.setText(String.format("RM %.2f", totalZakat));
        }
    }

    private void resetFields() {
        etWeight.setText("");
        etGoldValue.setText("");
        rgGoldType.clearCheck();
        cardResult.setVisibility(View.GONE);
        etWeight.requestFocus();
        Toast.makeText(this, "Fields have been cleared.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            shareApp();
            return true;
        } else if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareApp() {
        String appUrl = "https://github.com/yourusername/ZakatGoldCalculator";
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Gold Zakat Calculator App - ICT602");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Check out this Gold Zakat Calculator app!\n\nDownload: " + appUrl);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
}
