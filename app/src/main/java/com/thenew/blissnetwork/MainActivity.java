package com.thenew.blissnetwork;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity {

    private Button btnScan;
    private ListView wifiList;
    private ArrayList<String> networkNames;
    private ArrayAdapter<String> adapter;

    private static final String PREFS_NAME = "SavedWifiPasswords";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnScan = findViewById(R.id.btnScan);
        wifiList = findViewById(R.id.wifiList);
        networkNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, networkNames);
        wifiList.setAdapter(adapter);

        btnScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scanWifi();
                }
            });

        // SINGLE CLICK: Instantly connect or ask for password
        wifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selectedSSID = networkNames.get(position);

                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    String savedPassword = prefs.getString(selectedSSID, null);

                    if (savedPassword != null) {
                        Toast.makeText(MainActivity.this, "Using saved password...", Toast.LENGTH_SHORT).show();
                        connectToNetwork(selectedSSID, savedPassword);
                    } else {
                        showConnectDialog(selectedSSID, "");
                    }
                }
            });

        // LONG CLICK: Manage Saved Network (Edit/Forget Options)
        wifiList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    final String selectedSSID = networkNames.get(position);

                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    final String savedPassword = prefs.getString(selectedSSID, null);

                    // If we don't have this network saved, don't show the manage menu
                    if (savedPassword == null) {
                        return false; 
                    }

                    String[] options = {"Modify Password", "Forget Network"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(selectedSSID);
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    // User selected "Modify Password" -> Pre-fill with existing password
                                    showConnectDialog(selectedSSID, savedPassword);
                                } else if (which == 1) {
                                    // User selected "Forget Network" -> Clear from SharedPreferences
                                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                                    editor.remove(selectedSSID);
                                    editor.apply();
                                    Toast.makeText(MainActivity.this, "Network forgotten", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    builder.show();

                    return true; // Returns true to tell Android we handled the long click event
                }
            });
    }

    private void scanWifi() {
        networkNames.clear();
        Toast.makeText(this, "Scanning hardware...", Toast.LENGTH_SHORT).show();

        new Thread(new Runnable() {
                @Override
                public void run() {
                    List<String> results = RootUtil.executeWithOutput("iw dev wlan0 scan | grep SSID");
                    final Set<String> uniqueSSIDs = new HashSet<>();

                    for (String line : results) {
                        if (line.contains("SSID:")) {
                            String cleanName = line.split("SSID:")[1].trim();
                            if (!cleanName.isEmpty() && !cleanName.contains("\\x00")) {
                                uniqueSSIDs.add(cleanName);
                            }
                        }
                    }

                    runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                networkNames.addAll(uniqueSSIDs);
                                adapter.notifyDataSetChanged();
                                if (networkNames.isEmpty()) {
                                    Toast.makeText(MainActivity.this, "No networks found.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Scan complete!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }
            }).start();
    }

    // Adjusted to accept a 'defaultPassword' parameter for modifications
    private void showConnectDialog(final String ssid, String defaultPassword) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Connect to " + ssid);

        final EditText input = new EditText(this);
        input.setHint("Password (leave blank for open networks)");

        // If a password was passed in, pre-fill the text box and put the selection cursor at the end
        if (defaultPassword != null && !defaultPassword.isEmpty()) {
            input.setText(defaultPassword);
            input.setSelection(defaultPassword.length());
        }

        builder.setView(input);

        builder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String password = input.getText().toString();

                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                    if (!password.isEmpty()) {
                        editor.putString(ssid, password);
                    } else {
                        editor.remove(ssid); // If they clear the text box entirely, treat it as an open or forgotten network
                    }
                    editor.apply();

                    connectToNetwork(ssid, password);
                }
            });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

        builder.show();
    }

    private void connectToNetwork(final String ssid, final String password) {
        Toast.makeText(this, "Connecting...", Toast.LENGTH_SHORT).show();

        new Thread(new Runnable() {
                @Override
                public void run() {
                    String script;
                    if (password.isEmpty()) {
                        script = "cmd wifi connect-network \"" + ssid + "\" open";
                    } else {
                        script = "cmd wifi connect-network \"" + ssid + "\" wpa2 \"" + password + "\"";
                    }

                    RootUtil.execute(script);

                    runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Connection command sent!", Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            }).start();
    }
}

