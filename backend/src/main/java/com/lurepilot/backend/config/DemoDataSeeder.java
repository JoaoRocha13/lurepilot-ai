package com.lurepilot.backend.config;

import com.lurepilot.backend.model.Catch;
import com.lurepilot.backend.model.FishSpecies;
import com.lurepilot.backend.model.FishingPlan;
import com.lurepilot.backend.model.FishingPlanLure;
import com.lurepilot.backend.model.FishingSession;
import com.lurepilot.backend.model.FishingSpot;
import com.lurepilot.backend.model.Lure;
import com.lurepilot.backend.model.LureLibraryItem;
import com.lurepilot.backend.model.SessionEvent;
import com.lurepilot.backend.model.SessionLure;
import com.lurepilot.backend.repository.CatchRepository;
import com.lurepilot.backend.repository.FishSpeciesRepository;
import com.lurepilot.backend.repository.FishingPlanLureRepository;
import com.lurepilot.backend.repository.FishingPlanRepository;
import com.lurepilot.backend.repository.FishingSessionRepository;
import com.lurepilot.backend.repository.FishingSpotRepository;
import com.lurepilot.backend.repository.LureLibraryItemRepository;
import com.lurepilot.backend.repository.LureRepository;
import com.lurepilot.backend.repository.SessionEventRepository;
import com.lurepilot.backend.repository.SessionLureRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DemoDataSeeder implements CommandLineRunner {

    private final FishSpeciesRepository fishSpeciesRepository;
    private final LureLibraryItemRepository lureLibraryItemRepository;
    private final LureRepository lureRepository;
    private final FishingSpotRepository fishingSpotRepository;
    private final FishingPlanRepository fishingPlanRepository;
    private final FishingPlanLureRepository fishingPlanLureRepository;
    private final FishingSessionRepository fishingSessionRepository;
    private final SessionEventRepository sessionEventRepository;
    private final SessionLureRepository sessionLureRepository;
    private final CatchRepository catchRepository;

    public DemoDataSeeder(
            FishSpeciesRepository fishSpeciesRepository,
            LureLibraryItemRepository lureLibraryItemRepository,
            LureRepository lureRepository,
            FishingSpotRepository fishingSpotRepository,
            FishingPlanRepository fishingPlanRepository,
            FishingPlanLureRepository fishingPlanLureRepository,
            FishingSessionRepository fishingSessionRepository,
            SessionEventRepository sessionEventRepository,
            SessionLureRepository sessionLureRepository,
            CatchRepository catchRepository
    ) {
        this.fishSpeciesRepository = fishSpeciesRepository;
        this.lureLibraryItemRepository = lureLibraryItemRepository;
        this.lureRepository = lureRepository;
        this.fishingSpotRepository = fishingSpotRepository;
        this.fishingPlanRepository = fishingPlanRepository;
        this.fishingPlanLureRepository = fishingPlanLureRepository;
        this.fishingSessionRepository = fishingSessionRepository;
        this.sessionEventRepository = sessionEventRepository;
        this.sessionLureRepository = sessionLureRepository;
        this.catchRepository = catchRepository;
    }

    @Override
    public void run(String... args) {
        FishSpecies bass = seedFishSpecies();
        DemoLibraryItems libraryItems = seedLureLibrary();
        DemoLures lures = seedLureBox(bass, libraryItems);
        DemoSpots spots = seedFishingSpots(bass);
        FishingPlan plan = seedFishingPlan(spots.reservoirSpot(), bass, lures);
        seedFishingSessions(spots, plan, bass, lures);
    }

    private FishSpecies seedFishSpecies() {
        FishSpecies bass = findFishSpecies("Black Bass");
        if (bass == null) {
            bass = fishSpeciesRepository.save(new FishSpecies(
                    "Black Bass",
                    "FRESHWATER",
                    "Predador de agua doce que tende a procurar estruturas, sombras e zonas com cobertura.",
                    "/demo/images/fish/freshwater/black-bass.png",
                    "Gosta de margens com vegetacao, pedras, troncos e zonas onde possa emboscar presas.",
                    "Mais ativo ao nascer e por do sol; pode alimentar-se durante o dia com sombra ou vento leve.",
                    "surface, mid-water, bottom, vegetation, structure",
                    "shallows, vegetation, structures, drop-offs, rocky-areas",
                    "Senko, Crankbait, Spinnerbait, Frog, Jerkbait"
            ));
        }

        return bass;
    }

    private DemoLibraryItems seedLureLibrary() {
        LureLibraryItem softBait = findLureLibraryItem("Senko");
        if (softBait == null) {
            softBait = lureLibraryItemRepository.save(new LureLibraryItem(
                    "Senko",
                    "Soft bait",
                    "/demo/images/lures/senko.png",
                    "Easy",
                    "High",
                    "Amostra versatil para apresentar de forma discreta quando o peixe esta desconfiado.",
                    "Trabalhar devagar, com pausas, junto ao fundo ou perto de estruturas.",
                    "Queda controlada, arrasto lento e pausas",
                    "Agua clara, pouca corrente, pressao de pesca alta ou peixe pouco ativo.",
                    "/demo/images/lure-action-icons/bottom-fishing-icon.png",
                    "/demo/images/lure-actions/bottom-fishing.png"
            ));
        }

        LureLibraryItem crankbait = findLureLibraryItem("Crankbait");
        if (crankbait == null) {
            crankbait = lureLibraryItemRepository.save(new LureLibraryItem(
                    "Crankbait",
                    "Crankbait",
                    "/demo/images/lures/crankbait.png",
                    "Medium",
                    "High",
                    "Boa amostra para cobrir agua e encontrar peixe ativo.",
                    "Lancar em leque, variar velocidade e tocar ocasionalmente em pedra ou estrutura.",
                    "Retrieve continuo com pausas curtas",
                    "Agua ligeiramente turva, vento leve e peixe ativo em meia agua.",
                    "/demo/images/lure-action-icons/crank-retrieve-icon.png",
                    "/demo/images/lure-actions/crank-retrieve.png"
            ));
        }

        LureLibraryItem popper = findLureLibraryItem("Popper");
        if (popper == null) {
            popper = lureLibraryItemRepository.save(new LureLibraryItem(
                    "Popper",
                    "Topwater",
                    "/demo/images/lures/popper.png",
                    "Easy",
                    "High",
                    "Amostra de superficie para momentos visuais quando ha atividade no topo.",
                    "Usar com toques curtos e pausas, especialmente perto de margens e sombras.",
                    "Pops curtos, cadencia lenta e pausas.",
                    "Nascer do sol, por do sol, agua calma e sinais de atividade a superficie.",
                    "/demo/images/lure-action-icons/top-water-icon.png",
                    "/demo/images/lure-actions/top-water.png"
            ));
        }

        return new DemoLibraryItems(softBait, crankbait, popper);
    }

    private DemoLures seedLureBox(FishSpecies bass, DemoLibraryItems libraryItems) {
        Lure greenSoftBait = findLure("Vinil verde natural");
        if (greenSoftBait == null) {
            greenSoftBait = new Lure(
                    "Vinil verde natural",
                    "Soft bait",
                    "Green pumpkin",
                    "8 cm",
                    7.0,
                    "Demo Tackle",
                    "Amostra discreta para agua clara.",
                    bass.getName(),
                    "Freshwater"
            );
            greenSoftBait.setLibraryItem(libraryItems.softBait());
        }
        greenSoftBait.setLibraryItem(libraryItems.softBait());
        greenSoftBait = lureRepository.save(greenSoftBait);

        Lure smallCrankbait = findLure("Crankbait pequeno natural");
        if (smallCrankbait == null) {
            smallCrankbait = new Lure(
                    "Crankbait pequeno natural",
                    "Crankbait",
                    "Natural shad",
                    "5 cm",
                    9.0,
                    "Demo Tackle",
                    "Bom para procurar peixe ativo.",
                    bass.getName(),
                    "Freshwater"
            );
            smallCrankbait.setLibraryItem(libraryItems.crankbait());
        }
        smallCrankbait.setLibraryItem(libraryItems.crankbait());
        smallCrankbait = lureRepository.save(smallCrankbait);

        Lure surfacePopper = findLure("Popper branco");
        if (surfacePopper == null) {
            surfacePopper = new Lure(
                    "Popper branco",
                    "Topwater",
                    "White",
                    "6 cm",
                    10.0,
                    "Demo Tackle",
                    "Guardar para pouca luz ou sinais a superficie.",
                    bass.getName(),
                    "Freshwater"
            );
            surfacePopper.setLibraryItem(libraryItems.popper());
        }
        surfacePopper.setLibraryItem(libraryItems.popper());
        surfacePopper = lureRepository.save(surfacePopper);

        return new DemoLures(greenSoftBait, smallCrankbait, surfacePopper);
    }

    private DemoSpots seedFishingSpots(FishSpecies bass) {
        FishingSpot reservoirSpot = findFishingSpot("Albufeira Demo - Margem Norte");
        if (reservoirSpot == null) {
            reservoirSpot = fishingSpotRepository.save(new FishingSpot(
                    "Albufeira Demo - Margem Norte",
                    "Zona com pedra, vegetacao baixa e varias entradas de sombra ao fim do dia.",
                      39.6321,
                      -8.6713,
                      "Freshwater",
                      "RESERVOIR",
                      bass.getName()
            ));
        }

        FishingSpot riverSpot = findFishingSpot("Rio Demo - Curva com corrente lenta");
        if (riverSpot == null) {
            riverSpot = fishingSpotRepository.save(new FishingSpot(
                    "Rio Demo - Curva com corrente lenta",
                    "Curva com agua mais parada, margem com canas e pequenas zonas de profundidade.",
                      39.7442,
                      -8.8078,
                      "Freshwater",
                      "RIVER",
                      "Achiga"
            ));
        }

        return new DemoSpots(reservoirSpot, riverSpot);
    }

    private FishingPlan seedFishingPlan(FishingSpot reservoirSpot, FishSpecies bass, DemoLures lures) {
        if (fishingPlanRepository.count() > 0) {
            return fishingPlanRepository.findAll().get(0);
        }

        FishingPlan plan = fishingPlanRepository.save(new FishingPlan(
                reservoirSpot,
                LocalDate.now().plusDays(2),
                LocalTime.of(7, 0),
                bass.getName(),
                "Clear",
                "Normal",
                "Comecar discreto junto a estruturas. Se nao houver atividade, cobrir agua com crankbait."
        ));

        fishingPlanLureRepository.save(new FishingPlanLure(plan, lures.greenSoftBait()));
        fishingPlanLureRepository.save(new FishingPlanLure(plan, lures.smallCrankbait()));
        fishingPlanLureRepository.save(new FishingPlanLure(plan, lures.surfacePopper()));

        return plan;
    }

    private void seedFishingSessions(DemoSpots spots, FishingPlan plan, FishSpecies bass, DemoLures lures) {
        if (fishingSessionRepository.count() > 0) {
            return;
        }

        FishingSession session = fishingSessionRepository.save(new FishingSession(
                spots.reservoirSpot(),
                plan,
                LocalDate.now().minusDays(3),
                LocalTime.of(6, 45),
                LocalTime.of(9, 15),
                bass.getName(),
                "Clear",
                "Normal",
                "Sessao demo com atividade cedo e agua calma.",
                true
        ));

        sessionLureRepository.save(new SessionLure(
                session,
                lures.greenSoftBait(),
                LocalTime.of(6, 50),
                LocalTime.of(7, 35),
                "Toques leves junto a vegetacao."
        ));

        sessionLureRepository.save(new SessionLure(
                session,
                lures.smallCrankbait(),
                LocalTime.of(7, 40),
                LocalTime.of(8, 30),
                "Cobriu agua e encontrou peixe ativo junto a pedra."
        ));

        sessionEventRepository.save(new SessionEvent(
                session,
                LocalTime.of(7, 5),
                "OBSERVATION",
                "Peixe pequeno a fugir junto a uma sombra na margem."
        ));

        sessionEventRepository.save(new SessionEvent(
                session,
                LocalTime.of(8, 10),
                "STRIKE",
                "Ataque no crankbait depois de bater numa zona de pedra."
        ));

        catchRepository.save(new Catch(
                session,
                bass.getName(),
                1,
                34.0,
                0.7,
                true,
                "Captura demo libertada em boas condicoes."
        ));

        fishingSessionRepository.save(new FishingSession(
                spots.riverSpot(),
                null,
                LocalDate.now().minusDays(10),
                LocalTime.of(18, 30),
                LocalTime.of(20, 0),
                "Achiga",
                "Slightly stained",
                "Low",
                "Sessao demo sem capturas, util para mostrar historico negativo.",
                false
        ));
    }

    private FishSpecies findFishSpecies(String name) {
        return fishSpeciesRepository.findAll()
                .stream()
                .filter(fishSpecies -> name.equalsIgnoreCase(fishSpecies.getName()))
                .findFirst()
                .orElse(null);
    }

    private LureLibraryItem findLureLibraryItem(String name) {
        return lureLibraryItemRepository.findAll()
                .stream()
                .filter(lureLibraryItem -> name.equalsIgnoreCase(lureLibraryItem.getName()))
                .findFirst()
                .orElse(null);
    }

    private Lure findLure(String name) {
        return lureRepository.findAll()
                .stream()
                .filter(lure -> name.equalsIgnoreCase(lure.getName()))
                .findFirst()
                .orElse(null);
    }

    private FishingSpot findFishingSpot(String name) {
        return fishingSpotRepository.findAll()
                .stream()
                .filter(fishingSpot -> name.equalsIgnoreCase(fishingSpot.getName()))
                .findFirst()
                .orElse(null);
    }

    private record DemoLibraryItems(LureLibraryItem softBait, LureLibraryItem crankbait, LureLibraryItem popper) {
    }

    private record DemoLures(Lure greenSoftBait, Lure smallCrankbait, Lure surfacePopper) {
    }

    private record DemoSpots(FishingSpot reservoirSpot, FishingSpot riverSpot) {
    }
}
