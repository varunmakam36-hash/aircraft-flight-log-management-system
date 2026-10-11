
package com.aircraft.flightlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aircraft.flightlog.model.FlightLog;

public interface FlightLogRepository extends JpaRepository<FlightLog, Long> {

    boolean existsByFlightNumber(String flightNumber);
}
