package com.gridnine.testing.service.impl;

import com.gridnine.testing.model.Flight;
import com.gridnine.testing.model.Segment;
import com.gridnine.testing.service.FilterService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Бизнес-логика по работе с фильтрацией набора перелётов согласно различным правилам
 */
public class FilterServiceImpl implements FilterService {
    /**
     * Исключение вылетов до текущего момента времени из общего списка
     *
     * @param flightBuilder общий список перелетов
     * @return отфильтрованный список без вылетов до текущего момента времени
     */
    @Override
    public List<Flight> removeFlightUpToTheCurrentPointInTime(List<Flight> flightBuilder) {

        return flightBuilder.stream()
                .filter(flight -> flight.getSegments().stream()
                        .allMatch(segment -> segment.getDepartureDate().isAfter(LocalDateTime.now())))
                .collect(Collectors.toList());
    }

    /**
     * Исключение перелетов, где сегменты с датой прилёта раньше даты вылета из общего списка
     *
     * @param flightBuilder общий список перелетов
     * @return отфильтрованный список без перелетов,
     * где сегменты с датой прилёта раньше даты вылета из общего списка
     */
    @Override
    public List<Flight> removeSegmentsWithAnArrivalDateEarlierThanTheDepartureDate(List<Flight> flightBuilder) {

        return flightBuilder.stream()
                .filter(flight -> flight.getSegments().stream()
                        .allMatch(segment -> segment.getArrivalDate().isAfter(segment.getDepartureDate())))
                .collect(Collectors.toList());
    }

    /**
     * Исключение перелетов, где общее время, проведённое на земле, превышает два часа
     *
     * @param flightBuilder общий список перелетов
     * @return отфильтрованный список без перелетов,
     * где общее время, проведённое на земле, превышает два часа
     */
    @Override
    public List<Flight> removeTimeSpentOnEarthExceedsTwoHours(List<Flight> flightBuilder) {

        return flightBuilder.stream()
                .filter(flight -> calculateTotalGroundTime(flight) <= Duration.ofHours(2).toMinutes())
                .collect(Collectors.toList());
    }

    /**
     * Вычисление общего времени на земле
     *
     * @param flight время полета.
     * @return общее время время на земле,
     */
    private static long calculateTotalGroundTime(Flight flight) {
        List<Segment> segments = flight.getSegments();
        long totalGroundTime = 0;

        for (int i = 0; i < segments.size() - 1; i++) {
            LocalDateTime arrival = segments.get(i).getArrivalDate();
            LocalDateTime nextDeparture = segments.get(i + 1).getDepartureDate();
            totalGroundTime += Duration.between(arrival, nextDeparture).toMinutes();
        }
//        System.out.println("Общее время на земле="+totalGroundTime+" время полета= "+flight);
        return totalGroundTime;
    }
}
