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
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONParser {
    private String filename;
    private String version;
    private String language;
    private File file;

    public JSONParser(File file){
        this.file = file;
        this.filename = getFileName(file.getName());
        this.version = getFileVersion(file.getName());
        this.language = getFileLanguage(file.getName());
    }

    public JSONParser(){

    }

    public void testReg(String file){
        String[] fileInfo = file.split("_|.json");
        for(String item : fileInfo)
        Log.i("DEBUG: test della regex", item);
    }

    public String getFileName(String file){
        String[] fileInfo = file.split("_|.json");
        return fileInfo[0];
    }

    public String getFileVersion(String file){
        String[] fileInfo = file.split("_|.json");
        return fileInfo[1]+fileInfo[2];
    }

    public String getFileLanguage(String file){
        String[] fileInfo = file.split("_|.json");
        try{
            return fileInfo[3];
        }catch(ArrayIndexOutOfBoundsException e){
            return "";
        }
    }

    public ArrayList<Percorso> getFilteredPercorsi(String filter){
        ArrayList<Percorso> result = new ArrayList<Percorso>();
        Pattern pattern = Pattern.compile(":[\\s]*\".+?\"");
        Matcher matcher;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            br.readLine();
            while((line = br.readLine()) != null){
                if(!line.startsWith("]")){
                    matcher = pattern.matcher(line);
                    boolean isToAdd = false;
                    while (matcher.find()) {
                        if (matcher.group().toLowerCase().contains(filter.toLowerCase())) {
                            isToAdd = true;
                            matcher.reset("");
                        }

                    }
                    if(isToAdd) { addPercorso(line, result); }

                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }


        return result;
    }

    /*
    public ArrayList<Percorso> getFilteredPercorsi(String filter){
        StringBuilder text = new StringBuilder();
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

    }*/

    private void addPercorso(String json, List<Percorso> percorsi){
        try {
            JSONObject percorsoJson = new JSONObject(json);
            String idP = percorsoJson.isNull("idPercorso") ? null : percorsoJson.getString("idPercorso");
            String  nome = percorsoJson.isNull("nome") ? null : percorsoJson.getString("nome"),
                    comune = percorsoJson.isNull("comune") ? null : percorsoJson.getString("comune"),
                    provincia = percorsoJson.isNull("provincia") ? null : percorsoJson.getString("provincia"),
                    regione = percorsoJson.isNull("regione") ? null : percorsoJson.getString("regione"),
                    stato = percorsoJson.isNull("stato") ? null : percorsoJson.getString("stato"),
                    descrizione = percorsoJson.isNull("descrizione") ? null : percorsoJson.getString("descrizione"),
                    immagine = percorsoJson.isNull("immagine") ? null : percorsoJson.getString("immagine");
            ArrayList<Attrazione> attrazioni = new ArrayList<Attrazione>();

            JSONArray attrazioniJson = percorsoJson.isNull("attrazioni") ? null : percorsoJson.getJSONArray("attrazioni");
            if(attrazioniJson != null){
                for(int i=0; i<attrazioniJson.length(); i++){
                    Attrazione attrazione;
                    String idA = attrazioniJson.getJSONObject(i).getString("idAttrazione");
                    String nomeAttr = attrazioniJson.getJSONObject(i).getString("nome");
                    attrazione = new Attrazione(idA, nomeAttr);
                    attrazioni.add(attrazione);
                }
            }
            Percorso percorso = new Percorso(idP, nome, comune, provincia, regione, stato, descrizione, immagine, attrazioni);
            percorsi.add(percorso);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
