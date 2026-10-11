
package com.aircraft.flightlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aircraft.flightlog.model.Maintenance;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
}
