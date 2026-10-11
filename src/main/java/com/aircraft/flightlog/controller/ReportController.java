
package com.aircraft.flightlog.controller;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.aircraft.flightlog.repository.AircraftRepository;
import com.aircraft.flightlog.repository.FlightLogRepository;
import com.aircraft.flightlog.repository.MaintenanceRepository;
import com.aircraft.flightlog.repository.PilotRepository;

@Controller
public class ReportController {

    private final FlightLogRepository flightLogRepository;
    private final AircraftRepository aircraftRepository;
    private final PilotRepository pilotRepository;
    private final MaintenanceRepository maintenanceRepository;

    public ReportController(
            FlightLogRepository flightLogRepository,
            AircraftRepository aircraftRepository,
            PilotRepository pilotRepository,
            MaintenanceRepository maintenanceRepository) {

        this.flightLogRepository = flightLogRepository;
        this.aircraftRepository = aircraftRepository;
        this.pilotRepository = pilotRepository;
        this.maintenanceRepository = maintenanceRepository;
    }

    @GetMapping("/reports")
    public String showReports(Model model) {

        var flightList = flightLogRepository.findAll();

        model.addAttribute("totalFlights", flightLogRepository.count());
        model.addAttribute("totalAircraft", aircraftRepository.count());
        model.addAttribute("totalPilots", pilotRepository.count());
        model.addAttribute("totalMaintenance", maintenanceRepository.count());

        double totalFlightHours = flightList.stream()
                .filter(flight -> flight.getFlightHours() != null)
                .mapToDouble(flight -> flight.getFlightHours())
                .sum();

        Map<String, Long> flightStatusCounts = flightList.stream()
                .collect(Collectors.groupingBy(
                        flight -> flight.getStatus() == null
                                ? "Unknown"
                                : flight.getStatus(),
                        Collectors.counting()
                ));

        model.addAttribute("totalFlightHours", totalFlightHours);
        model.addAttribute("flightList", flightList);
        model.addAttribute("maintenanceList", maintenanceRepository.findAll());
        model.addAttribute("flightStatusCounts", flightStatusCounts);

        return "reports";
    }
}
