package org.example.model;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OfferService {

    public double aplicaOferte(Comanda comanda) {
        double totalReduceri = 0;
        OfferManager offerManager = OfferManager.getInstance();

        // Aplicăm fiecare regulă de ofertă, dacă este activă
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

    /**
     * REGULA 1: La fiecare a doua băutură, prețul e redus la jumătate.
     */
    private double aplicaHappyHour(Comanda comanda) {
        double reducereHappyHour = 0;

        // 1. Facem o listă doar cu itemii care sunt băuturi
        List<ComandaItem> bauturi = comanda.getItems().stream()
                .filter(item -> item.getProdus() instanceof Bautura)
                .collect(Collectors.toList());

        // 2. Numărăm câte băuturi individuale sunt (ex: 3 beri = 3 băuturi)
        int numarTotalBauturi = 0;
        for (ComandaItem itemBautura : bauturi) {
            numarTotalBauturi += itemBautura.getCantitate();
        }

        // 3. Calculăm câte reduceri trebuie aplicate (una pentru fiecare pereche)
        int numarReduceri = numarTotalBauturi / 2;

        if (numarReduceri > 0) {
            // Sortăm băuturile de la cea mai ieftină la cea mai scumpă
            List<Bautura> toateBauturileComandateIndividual = comanda.getItems().stream()
                    .filter(item -> item.getProdus() instanceof Bautura)
                    .flatMap(item -> java.util.stream.Stream.generate(() -> (Bautura) item.getProdus()).limit(item.getCantitate()))
                    .sorted(Comparator.comparing(Produs::getPret))
                    .collect(Collectors.toList());

            // Aplicăm reducerea pe cele mai ieftine 'numarReduceri' băuturi
            for (int i = 0; i < numarReduceri; i++) {
                reducereHappyHour += toateBauturileComandateIndividual.get(i).getPret() * 0.5; // 50% reducere
            }
        }

        return reducereHappyHour;
    }

    /**
     * REGULA 2: Cine ia Pizza, primește cel mai ieftin desert comandat cu 25% reducere.
     */
    private double aplicaMealDeal(Comanda comanda) {
        // Verificăm dacă există cel puțin o pizza în comandă
        boolean arePizza = comanda.getItems().stream()
                .anyMatch(item -> item.getProdus() instanceof Pizza);

        if (arePizza) {
            // Căutăm cel mai ieftin desert
            return comanda.getItems().stream()
                    .filter(item -> item.getProdus().getCategorie().equalsIgnoreCase("Desert"))
                    .min(Comparator.comparing(item -> item.getProdus().getPret()))
                    .map(itemDesert -> itemDesert.getPretLaVanzare() * 0.25) // 25% reducere
                    .orElse(0.0); // Dacă nu găsește desert, reducerea e 0
        }

        return 0.0;
    }

    /**
     * REGULA 3: La 4 Pizza comandate, una e din partea casei (cea mai ieftină).
     */
    private double aplicaPartyPack(Comanda comanda) {
        // Numărăm câte pizza sunt în total
        int numarPizza = comanda.getItems().stream()
                .filter(item -> item.getProdus() instanceof Pizza)
                .mapToInt(ComandaItem::getCantitate)
                .sum();

        if (numarPizza >= 4) {
            // Căutăm pizza cea mai ieftină din comandă
            return comanda.getItems().stream()
                    .filter(item -> item.getProdus() instanceof Pizza)
                    .min(Comparator.comparing(item -> item.getProdus().getPret()))
                    .map(ComandaItem::getPretLaVanzare) // Reducerea este prețul întreg al celei mai ieftine pizza
                    .orElse(0.0);
        }

        return 0.0;
    }
}