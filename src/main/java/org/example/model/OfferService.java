package org.example.model;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OfferService {

    public double aplicaOferte(Comanda comanda) {
        double totalReduceri = 0;
        OfferManager offerManager = OfferManager.getInstance();

        if (offerManager.isHappyHourActive()) {
            totalReduceri += aplicaHappyHour(comanda);
        }
        if (offerManager.isMealDealActive()) {
            totalReduceri += aplicaMealDeal(comanda);
        }
        if (offerManager.isPartyPackActive()) {
            totalReduceri += aplicaPartyPack(comanda);
        }

        return totalReduceri;
    }


    private double aplicaHappyHour(Comanda comanda) {
        double reducereHappyHour = 0;

        List<ComandaItem> bauturi = comanda.getItems().stream()
                .filter(item -> item.getProdus() instanceof Bautura)
                .collect(Collectors.toList());

        int numarTotalBauturi = 0;
        for (ComandaItem itemBautura : bauturi) {
            numarTotalBauturi += itemBautura.getCantitate();
        }

        int numarReduceri = numarTotalBauturi / 2;

        if (numarReduceri > 0) {
            List<Bautura> toateBauturileComandateIndividual = comanda.getItems().stream()
                    .filter(item -> item.getProdus() instanceof Bautura)
                    .flatMap(item -> java.util.stream.Stream.generate(() -> (Bautura) item.getProdus()).limit(item.getCantitate()))
                    .sorted(Comparator.comparing(Produs::getPret))
                    .collect(Collectors.toList());

            for (int i = 0; i < numarReduceri; i++) {
                reducereHappyHour += toateBauturileComandateIndividual.get(i).getPret() * 0.5;
            }
        }

        return reducereHappyHour;
    }

    private double aplicaMealDeal(Comanda comanda) {
        boolean arePizza = comanda.getItems().stream()
                .anyMatch(item -> item.getProdus() instanceof Pizza);

        if (arePizza) {
            return comanda.getItems().stream()
                    .filter(item -> item.getProdus().getCategorie().equalsIgnoreCase("Desert"))
                    .min(Comparator.comparing(item -> item.getProdus().getPret()))
                    .map(itemDesert -> itemDesert.getPretLaVanzare() * 0.25)
                    .orElse(0.0);
        }

        return 0.0;
    }


    private double aplicaPartyPack(Comanda comanda) {
        int numarPizza = comanda.getItems().stream()
                .filter(item -> item.getProdus() instanceof Pizza)
                .mapToInt(ComandaItem::getCantitate)
                .sum();

        if (numarPizza >= 4) {
            return comanda.getItems().stream()
                    .filter(item -> item.getProdus() instanceof Pizza)
                    .min(Comparator.comparing(item -> item.getProdus().getPret()))
                    .map(ComandaItem::getPretLaVanzare)
                    .orElse(0.0);
        }

        return 0.0;
    }
}