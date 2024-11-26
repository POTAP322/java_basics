package airport;

import com.skillbox.airport.Airport;
import com.skillbox.airport.Flight;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        System.out.println("123");

    }

    //при помощи метода getAllAircrafts получаем список всех самолётов на аэропорту и при помощи потоков отфильтровываем его , с учётом того,
    // что надо брать из названия модели символы до знака "-" (из DD-101 взять DD)
    //для этого кадждую модель самолёта делим на две части при помощи .split и уже только потом сравниваем первую часть модели( до знака "-") при помощи .equals с переданным на вход функции model.
    // После фильтрации остаются только нужные модели , считаем их при помощи .count

    public static long findCountAircraftWithModelAirbus(Airport airport, String model) {
        int counter = (int) airport.getAllAircrafts().stream().filter(aircraft -> {
            String aircraftModel = aircraft.getModel();
            String[] parts = aircraftModel.split("-");
            return parts[0].equals(model);
        }).count();
        return counter;
    }

    //создаём пустой словарь map. Получаем все терминалы методом getTerminals и проходимся по ним через forEach.
    //после заполняем словарь именами терминалов(ключ) и количеством припаркованных самолётов(значение)
    public static Map<String, Integer> findMapCountParkedAircraftByTerminalName(Airport airport) {
        Map<String, Integer> map = new HashMap<String,Integer>();
        airport.getTerminals().stream().forEach(terminal -> {
            String name = terminal.getName();
            Integer parkedPlanesCount = (int) terminal.getParkedAircrafts().stream().count();
            map.put(name,parkedPlanesCount);

        });
        return map;
    }


    public static List<Flight> findFlightsLeavingInTheNextHours(Airport airport, int hours) {
        //получаем текущее время
        Instant currentTime = Instant.now();
        //вычисляем конечное время
        //ChronoUnit — это перечисление (enum) в Java, которое представляет собой стандартные единицы времени(часы, минуты и тд)
        Instant endTime = currentTime.plus(hours, ChronoUnit.HOURS);
        //создаём список
        List<Flight> nearestFlights = new ArrayList<>();
        //заполняем список
        //Используем flatMap для преобразования потока терминалов в поток полётов
        //фильтруем рейсы, чтобы включить только те, которые являются вылетами (DEPARTURE) и вылетают в указанный временной диапазон.
        nearestFlights = airport.getTerminals().stream().flatMap(terminal -> terminal.getFlights().stream())
                .filter(flight -> flight.getType() == Flight.Type.DEPARTURE)
                .filter(flight -> !flight.getDate().isBefore(currentTime) && !flight.getDate().isAfter(endTime))
                .collect(Collectors.toList());

        return nearestFlights;
    }

    public static Optional<Flight> findFirstFlightArriveToTerminal(Airport airport, String terminalName) {
        //получаем текущее время

        Instant currentTime = Instant.now();
        Optional<Flight> nearestFlight;
        //сначала фильтруем по имени переданного на вход метода терминала
        //Используем flatMap для преобразования потока терминалов в поток полётов
        //фильтруем рейсы, чтобы включить только те, которые являются прибытиями (arrival) и прилетают позже текущего времени
        // min из Stream API ипользуется для нахождения минимального элемента в потоке на основе заданного компаратора
        //находим рейс с наименьшей разницей во времени между текущим временем и временем рейса
        nearestFlight = airport.getTerminals().stream().filter(terminal -> terminal.getName().equals(terminalName)).flatMap(terminal -> terminal.getFlights().stream())
                .filter(flight -> flight.getType() == Flight.Type.ARRIVAL)
                .filter(flight -> flight.getDate().isAfter(currentTime))
                .min(Comparator.comparing(flight -> ChronoUnit.SECONDS.between(currentTime, flight.getDate())));


        return nearestFlight;
    }
}