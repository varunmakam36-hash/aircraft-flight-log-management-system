
package com.aircraft.flightlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aircraft.flightlog.model.Aircraft;

public interface AircraftRepository extends JpaRepository<Aircraft, Long> {

    boolean existsByRegistrationNumber(String registrationNumber);
}
