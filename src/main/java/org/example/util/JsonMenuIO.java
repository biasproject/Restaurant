package org.example.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.model.Bautura;
import org.example.model.Mancare;
import org.example.model.Pizza;
import org.example.model.Produs;
import org.example.model.ProdusRepository;

import java.io.*;
import java.util.*;

public class JsonMenuIO {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void exportMenu(List<Produs> produse, File targetFile) throws IOException {
        Map<String, List<JsonObject>> map = new LinkedHashMap<>();

        for (Produs p : produse) {
            JsonObject jo = new JsonObject();
            jo.addProperty("nume", p.getNume());
            jo.addProperty("pret", p.getPret());
            jo.addProperty("categorie", p.getCategorie());

            if (p instanceof Pizza) {
                Pizza pizza = (Pizza) p;
                jo.addProperty("blat", pizza.getBlat());
                jo.addProperty("sos", pizza.getSos());
                jo.addProperty("areExtraMozzarella", pizza.isAreExtraMozzarella());
                jo.addProperty("areCiuperci", pizza.isAreCiuperci());
                jo.addProperty("areSalam", pizza.isAreSalam());
                jo.addProperty("areAnanas", pizza.isAreAnanas());
            } else if (p instanceof Bautura) {
                Bautura b = (Bautura) p;
                jo.addProperty("volume", b.getVolume());
            } else if (p instanceof Mancare) {
                Mancare m = (Mancare) p;
                jo.addProperty("gramaj", m.getGramaj());
                jo.addProperty("esteVegetarian", m.getEsteVegetarian());
            }

            String cat = p.getCategorie() != null ? p.getCategorie() : "Altele";
            map.computeIfAbsent(cat, k -> new ArrayList<>()).add(jo);
        }

        try (Writer writer = new FileWriter(targetFile)) {
            gson.toJson(map, writer);
        }
    }

    public static List<Produs> importMenu(File sourceFile, ProdusRepository produsRepository) throws IOException {
        List<Produs> created = new ArrayList<>();
        try (Reader reader = new FileReader(sourceFile)) {
            JsonElement root = JsonParser.parseReader(reader);
            if (!root.isJsonObject()) return created;
            JsonObject rootObj = root.getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : rootObj.entrySet()) {
                JsonArray arr = entry.getValue().getAsJsonArray();
                for (JsonElement el : arr) {
                    if (!el.isJsonObject()) continue;
                    JsonObject jo = el.getAsJsonObject();

                    String nume = jo.has("nume") ? jo.get("nume").getAsString() : "Produs Nou";
                    float pret = jo.has("pret") ? jo.get("pret").getAsFloat() : 0f;
                    String categorie = jo.has("categorie") ? jo.get("categorie").getAsString() : entry.getKey();

                    Produs produs = null;

                    // Detectăm tipul după proprietăți specifice
                    if (jo.has("blat") || jo.has("sos")) {
                        String blat = jo.has("blat") ? jo.get("blat").getAsString() : "Standard";
                        String sos = jo.has("sos") ? jo.get("sos").getAsString() : "Rosii";
                        boolean extra = jo.has("areExtraMozzarella") && jo.get("areExtraMozzarella").getAsBoolean();
                        boolean ciuperci = jo.has("areCiuperci") && jo.get("areCiuperci").getAsBoolean();
                        boolean salam = jo.has("areSalam") && jo.get("areSalam").getAsBoolean();
                        boolean ananas = jo.has("areAnanas") && jo.get("areAnanas").getAsBoolean();
                        produs = new Pizza(nume, pret, categorie, blat, sos, extra, ciuperci, salam, ananas);
                    } else if (jo.has("volume")) {
                        float volume = jo.get("volume").getAsFloat();
                        produs = new Bautura(nume, pret, categorie, volume, categorie);
                    } else if (jo.has("gramaj") || jo.has("grmaj") || jo.has("esteVegetarian")) {
                        float gramaj = jo.has("gramaj") ? jo.get("gramaj").getAsFloat() : (jo.has("grmaj") ? jo.get("grmaj").getAsFloat() : 0f);
                        boolean veg = jo.has("esteVegetarian") && jo.get("esteVegetarian").getAsBoolean();
                        produs = new Mancare(nume, pret, categorie, gramaj, veg);
                    } else {
                        // Fallback: încercăm să importăm ca Mancare simplă
                        produs = new Mancare(nume, pret, categorie, 0f, false);
                    }

                    // Salvează în baza de date prin repository
                    produsRepository.salveaza(produs);
                    created.add(produs);
                }
            }
        }
        return created;
    }
}

