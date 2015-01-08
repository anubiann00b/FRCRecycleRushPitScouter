package org.huntingtonrobotics.frcrecyclerushpitscouter;

/* Saving and loading Local Files
*allow criminal intent to save and load data from a JSON file stored on the device's filesystem
* Each app has a directory in its sandbox
* keeping files in the sandbox protects them from being accessed by other apps and users
* app can also store files in external storage
* EXP SD Card
* chapter focuses on internal storage

 */

/*Saving and Loading Data in CI
*Involves two proccess:
* *to save data into a storable format then write that data to a file
* * In loading first read the formated data and then parse it into what the app needs
 */

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by 2015H_000 on 12/24/2014.
 */

public class FRCRecycleRushPitScouterJSONSerializer {

    private Context mContext;
    private String mFileName;

    public FRCRecycleRushPitScouterJSONSerializer(Context c, String f) {
        mContext = c;
        mFileName = f;
    }

    //load teams from file system
    public ArrayList<Team> loadTeams() throws IOException, JSONException {
        ArrayList<Team> teams = new ArrayList<Team>();
        BufferedReader reader = null;
        try {
            //open and read the file into a stringBuilder
            InputStream in = mContext.openFileInput(mFileName);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                //line breaks are omitted and irrelevant
                 jsonString.append(line);
            }
            //parse the JSON using JSONTokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            //build the array of teams from JSONObjects
            for (int i = 0; i < array.length(); i++) {
                teams.add(new Team(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            //ignore
        } finally {
            //ensures the underlying file handle is freed up
            if (reader != null)
                reader.close();
        }
        return teams;
    }




    public void saveTeams(ArrayList<Team> teams) throws JSONException, IOException{
        //Build an array in JSON
        JSONArray array = new JSONArray();
        for (Team c : teams) {
            array.put(c.toJSON());
        }
        //write file to disk
        Writer writer = null;
        try{
            OutputStream out = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        } finally{
            if (writer!= null)
                writer.close();
        }

    }

}