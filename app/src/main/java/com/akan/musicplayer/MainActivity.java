package com.akan.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    String []items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listViewId);

        Dexter.withContext(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        displaySongs();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

    }

    public ArrayList<File> fetchSongs(File file){
        ArrayList<File>arrayList = new ArrayList<>();
        File[] songs = file.listFiles();

        for(File myFile: songs){
            if(!myFile.isHidden() && myFile.isDirectory()){
                arrayList.addAll(fetchSongs(myFile));
            }
            else{
                if(myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith(".")){
                    arrayList.add(myFile);
                }
            }
        }
        return arrayList;
    }

    public void displaySongs(){
        ArrayList<File> mysongs = fetchSongs(Environment.getExternalStorageDirectory());
        items = new String[mysongs.size()];

        for(int i=0;i<mysongs.size();i++){
            items[i] = mysongs.get(i).getName().replace(".mp3","");
        }
        customAdapter customAdapter = new customAdapter();
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songName = (String) listView.getItemAtPosition(position);

                startActivity(new Intent(getApplicationContext(),Play_Song.class)
                .putExtra("songs",mysongs)
                        .putExtra("songname",songName)
                        .putExtra("pos",position)
                );
            }
        });
    }

    class customAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.list_item,null);
            TextView txtSong = view.findViewById(R.id.txtSong);
            txtSong.setSelected(true);
            txtSong.setText(items[position]);
            return view;
        }
    }
}