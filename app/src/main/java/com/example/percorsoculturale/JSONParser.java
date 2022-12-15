package com.example.percorsoculturale;

import android.content.Context;
import android.util.Log;

import com.example.percorsoculturale.tables.Attrazione;
import com.example.percorsoculturale.tables.Percorso;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class JSONParser {

    private StringBuilder text;
    private File file;

    public JSONParser(File file){
        this.text = new StringBuilder();
        this.file = file;
    }


    public ArrayList<Percorso> getFilteredPercorsi(String filter){
        text = new StringBuilder();
        ArrayList<Percorso> result = new ArrayList<Percorso>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            int cont = 0;
            boolean isJSON = false,
                    isPercorso = false,
                    isArray = false;
            while((line = br.readLine()) != null){
                if(line.contains("{") && !isJSON)
                    isJSON = true;
                else if(line.contains("{") && !isPercorso)
                    isPercorso = true;
                else if(line.contains("[") && !isArray && isPercorso)
                    isArray = true;
                else if(line.contains("]"))
                    isArray = false;
                else if(line.contains("}") && !isArray) {
                    text.append(line.substring(0, line.indexOf("}")+1));
                    if(text.substring(0).contains(filter)){
                        addPercorso(text.substring(0), result);
                        Log.i("DEBUG: contenuto json", text.substring(0));
                    }
                    text = new StringBuilder();
                    isPercorso = false;
                }else if(line.contains("}") && !isPercorso)
                    isJSON = false;
                if(isPercorso){
                    text.append(line);
                }


            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;

    }

    private void addPercorso(String json, List<Percorso> percorsi){
        try {
            JSONObject percorsoJson = new JSONObject(json);
            String idP = percorsoJson.isNull("id") ? null : percorsoJson.getString("id");
            String  nome = percorsoJson.isNull("nome") ? null : percorsoJson.getString("nome"),
                    comune = percorsoJson.isNull("comune") ? null : percorsoJson.getString("comune"),
                    provincia = percorsoJson.isNull("provincia") ? null : percorsoJson.getString("provincia"),
                    regione = percorsoJson.isNull("regione") ? null : percorsoJson.getString("regione"),
                    stato = percorsoJson.isNull("stato") ? null : percorsoJson.getString("stato");
            ArrayList<Attrazione> attrazioni = new ArrayList<Attrazione>();

            JSONArray attrazioniJson = percorsoJson.isNull("attrazioni") ? null : percorsoJson.getJSONArray("attrazioni");
            if(attrazioniJson != null){
                for(int i=0; i<attrazioniJson.length(); i++){
                    Attrazione attrazione;
                    String idA = attrazioniJson.getJSONObject(i).getString("id");
                    String nomeAttr = attrazioniJson.getJSONObject(i).getString("nome");
                    attrazione = new Attrazione(idA, nomeAttr);
                    attrazioni.add(attrazione);
                }
            }
            Percorso percorso = new Percorso(idP, nome, comune, provincia, regione, stato, attrazioni);
            percorsi.add(percorso);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
