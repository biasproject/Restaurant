package org.example.service;

import org.example.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class OfferService {

    private final OfferManager offerManager = OfferManager.getInstance();

    public void applyOffers(Comanda comanda) {
        // Remove any existing discount lines (produs == null && pretLaVanzare < 0)
        List<ComandaItem> original = new ArrayList<>(comanda.getItems());
        original.removeIf(i -> i.getProdus() == null && i.getPretLaVanzare() < 0);
        comanda.setItems(original);

        double totalReduceri = 0.0;

        // Happy Hour: every 2nd alcoholic drink 50% off
        if (offerManager.isHappyHourActive()) {
            List<ComandaItem> drinks = comanda.getItems().stream()
                    .filter(i -> i.getProdus() instanceof Bautura || (i.getProdus() != null && i.getProdus().getCategorie() != null && i.getProdus().getCategorie().toLowerCase().contains("alcool")))
                    .collect(Collectors.toList());
            // Expand by quantity
            List<ComandaItem> drinkUnits = new ArrayList<>();
            for (ComandaItem di : drinks) {
                for (int k = 0; k < di.getCantitate(); k++) drinkUnits.add(di);
            }
            // Sort by price to pick which units get discount? We'll pick every 2nd unit in order (cheapest preference optional)
            for (int idx = 1; idx < drinkUnits.size(); idx += 2) {
                ComandaItem unit = drinkUnits.get(idx);
                double discount = unit.getPretLaVanzare() * 0.5;
                totalReduceri += discount;
                // Add discount line
                ComandaItem discountLine = new ComandaItem(null, 1, comanda);
                discountLine.setNumeProdusLaVanzare("HappyHour Discount");
                discountLine.setPretLaVanzare(-discount);
                comanda.getItems().add(discountLine);
            }
        }

        // Meal Deal: For each pizza ordered, the cheapest dessert gets 25% off
        if (offerManager.isMealDealActive()) {
            List<ComandaItem> pizzas = comanda.getItems().stream()
                    .filter(i -> i.getProdus() instanceof Pizza || (i.getProdus() != null && i.getProdus().getCategorie() != null && i.getProdus().getCategorie().toLowerCase().contains("pizza")))
                    .collect(Collectors.toList());
            List<ComandaItem> desserts = comanda.getItems().stream()
                    .filter(i -> i.getProdus() != null && i.getProdus().getCategorie() != null && i.getProdus().getCategorie().toLowerCase().contains("desert"))
                    .collect(Collectors.toList());
            int applicable = pizzas.stream().mapToInt(ComandaItem::getCantitate).sum();
            // For each pizza, apply discount to the cheapest available dessert
            for (int i = 0; i < applicable; i++) {
                ComandaItem cheapest = null;
                for (ComandaItem d : desserts) {
                    if (d.getCantitate() <= 0) continue;
                    if (cheapest == null || d.getPretLaVanzare() < cheapest.getPretLaVanzare()) cheapest = d;
                }
                if (cheapest == null) break;
                double discount = cheapest.getPretLaVanzare() * 0.25;
                totalReduceri += discount;
                ComandaItem discountLine = new ComandaItem(null, 1, comanda);
                discountLine.setNumeProdusLaVanzare("MealDeal Discount");
                discountLine.setPretLaVanzare(-discount);
                comanda.getItems().add(discountLine);
                // reduce available quantity on that dessert (we don't alter original item quantity, but could mark applied times)
                cheapest.setCantitate(cheapest.getCantitate() - 1);
                if (cheapest.getCantitate() <= 0) desserts.remove(cheapest);
            }
        }

        // Party Pack: For each set of 4 pizzas, cheapest pizza free
        if (offerManager.isPartyPackActive()) {
            List<ComandaItem> pizzas = comanda.getItems().stream()
                    .filter(i -> i.getProdus() instanceof Pizza || (i.getProdus() != null && i.getProdus().getCategorie() != null && i.getProdus().getCategorie().toLowerCase().contains("pizza")))
                    .collect(Collectors.toList());
            int pizzaCount = pizzas.stream().mapToInt(ComandaItem::getCantitate).sum();
            int freeCount = pizzaCount / 4;
            for (int f = 0; f < freeCount; f++) {
                // find cheapest pizza unit
                ComandaItem cheapest = null;
                for (ComandaItem p : pizzas) {
                    if (p.getCantitate() <= 0) continue;
                    if (cheapest == null || p.getPretLaVanzare() < cheapest.getPretLaVanzare()) cheapest = p;
                }
                if (cheapest == null) break;
                double discount = cheapest.getPretLaVanzare();
                totalReduceri += discount;
                ComandaItem discountLine = new ComandaItem(null, 1, comanda);
                discountLine.setNumeProdusLaVanzare("PartyPack Free Pizza");
                discountLine.setPretLaVanzare(-discount);
                comanda.getItems().add(discountLine);
                // reduce the counted qty for algorithm
                cheapest.setCantitate(cheapest.getCantitate() - 1);
                if (cheapest.getCantitate() <= 0) pizzas.remove(cheapest);
            }
        }

        comanda.setTotalReduceri(totalReduceri);
        comanda.calculeazaTotaluri();
        comanda.setTotalFinal(comanda.getSubtotal() - totalReduceri);
    }
}

