
package com.aircraft.flightlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aircraft.flightlog.model.Pilot;

public interface PilotRepository extends JpaRepository<Pilot, Long> {

    boolean existsByEmployeeId(String employeeId);

    boolean existsByLicenseNumber(String licenseNumber);
}
