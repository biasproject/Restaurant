package org.example.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * O clasă Singleton care ține minte starea ofertelor active.
 * Managerul va scrie aici, iar Serviciul de Oferte va citi de aici.
 */
public class OfferManager {

    private static OfferManager instance;

    private final OfferRepository offerRepository = new OfferRepository();
    private final Map<String, OfferConfig> offers = new HashMap<>();

    private OfferManager() {
        // Load from DB (create defaults if missing)
        List<OfferConfig> all = offerRepository.findAll();
        for (OfferConfig oc : all) {
            offers.put(oc.getName(), oc);
        }

        // Ensure defaults exist
        ensureDefault("HAPPY_HOUR", "Fiecare a doua băutură are reducere 50%");
        ensureDefault("MEAL_DEAL", "La orice Pizza, cel mai ieftin desert are reducere 25%");
        ensureDefault("PARTY_PACK", "La 4 Pizza comandate, una (cea mai ieftină) este gratuită");
    }

    private void ensureDefault(String name, String desc) {
        if (!offers.containsKey(name)) {
            OfferConfig oc = new OfferConfig(name, false, desc);
            offerRepository.save(oc);
            offers.put(name, oc);
        }
    }

    public static OfferManager getInstance() {
        if (instance == null) {
            instance = new OfferManager();
        }
        return instance;
    }

    public boolean isHappyHourActive() { return offers.getOrDefault("HAPPY_HOUR", new OfferConfig()).isActive(); }
    public void setHappyHourActive(boolean active) { setActive("HAPPY_HOUR", active); }

    public boolean isMealDealActive() { return offers.getOrDefault("MEAL_DEAL", new OfferConfig()).isActive(); }
    public void setMealDealActive(boolean active) { setActive("MEAL_DEAL", active); }

    public boolean isPartyPackActive() { return offers.getOrDefault("PARTY_PACK", new OfferConfig()).isActive(); }
    public void setPartyPackActive(boolean active) { setActive("PARTY_PACK", active); }

    private void setActive(String name, boolean active) {
        OfferConfig oc = offers.get(name);
        if (oc == null) {
            oc = new OfferConfig(name, active, "");
            offerRepository.save(oc);
            offers.put(name, oc);
            return;
        }
        oc.setActive(active);
        offerRepository.update(oc);
    }
}