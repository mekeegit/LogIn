package com.miki.login;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;


public class ProfileActivity extends AppCompatActivity{


    private FirebaseAuth firebaseAuth;
    private TextView textEmail;
    //private Button mButton;


    private static final String MIME_TEXT_PLAIN = "text/plain";
    private NfcAdapter mNfcAdapter = null;
    PieChart pieChart;
    private TextView mText;
    private int[] yData = {50,50};
    private String[] xData = {"red", "green"};
    private FloatingActionButton callButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.profileTitle);
        setSupportActionBar(toolbar);

        callButton = (FloatingActionButton) findViewById(R.id.firstFab);
        //callButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("white")));

        callButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v == callButton){

                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:0722679229"));

              //  if (ActivityCompat.checkSelfPermission(ProfileActivity.this,
                 //       android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
               //     return;
                //}
                startActivity(callIntent);
              }
            }
        });


        // textEmail = (TextView) findViewById(R.id.viewEmail);
        //mButton  = (Button) findViewById(R.id.logoutButton);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //mButton.setOnClickListener(this);
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        //textEmail.setText("Hello, "+ user.getEmail());

        mText = (TextView)  findViewById(R.id.text);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLay);
        final AlphaAnimation alpha = new AlphaAnimation(0.5F, 0.5F);
        final AlphaAnimation def = new AlphaAnimation(1,1);
        alpha.setDuration(0); // Make animation instant
        alpha.setFillAfter(true); // Tell it to persist after the animation ends
        def.setDuration(0); // Make animation instant
        def.setFillAfter(true); // Tell it to persist after the animation ends

        final FloatingActionsMenu fabMenu = (FloatingActionsMenu) findViewById(R.id.fabMenu);
       
        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                relativeLayout.startAnimation(alpha);
            }

            @Override
            public void onMenuCollapsed() {
                relativeLayout.startAnimation(def);
            }
        });


        pieChart = (PieChart)findViewById(R.id.idPieChart);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setTransparentCircleRadius(0);
        pieChart.setHoleRadius(0);
        //pieChart.setDescription("ddd");
        addDataSet();

        // relativeLayout.addView(textView);

        if (!mNfcAdapter.isEnabled()) {
            mText.setText("NFC DISABLED!");
        } else {
            mText.setText("NFC is enabled");
        }

        handleIntent(getIntent());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        if(id == R.id.logout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }




    private void addDataSet() {

        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();
        Context context = this;

        for(int i = 0; i< yData.length; i++){
            yEntrys.add(new PieEntry(yData[i], i));
        }
        //     for(int i = 1; i< xData.length; i++){
        //       xEntrys.add(xData[i]);
        // }
        //creating data set
       PieDataSet pieDataSet = new PieDataSet(yEntrys, "blablabla");

        // pieDataSet.setValueTextSize(20);
        pieDataSet.setColors(new int[] {R.color.complete, R.color.incomplete}, context);

        //create pie data obj
        PieData pieData = new PieData(pieDataSet);
        pieData.setDrawValues(false);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }


    @Override
    protected void onResume(){

        super.onResume();
        setupForegroundDispatch(this, mNfcAdapter);

    }


    @Override
    protected void onPause(){
        stopForegroundDispatch(this, mNfcAdapter);
        super.onPause();

    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {

        adapter.disableForegroundDispatch(activity);

    }

    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter){

        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try{
            filters[0].addDataType(MIME_TEXT_PLAIN);
        }catch (IntentFilter.MalformedMimeTypeException e){

            throw new RuntimeException("Check mime type");
        }
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);

    }



    @Override
    protected void onNewIntent(Intent intent){
        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);

            } else {

            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
    }

    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        //Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }




        private String readText(NdefRecord record) throws UnsupportedEncodingException {

            /*
         * See NFC forum "Text Record Type Definition" at 3.2.1
         * http://www.nfc-forum.org/specs/
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }
        



        @Override
        protected void onPostExecute(String result){

            if(result != null){

                mText.setText("content: "+ result);
                //int intResult = Integer.parseInt(result);

            }
        }






    }



}
