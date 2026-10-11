
package com.aircraft.flightlog;

import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.aircraft.flightlog.model.Aircraft;
import com.aircraft.flightlog.model.FlightLog;
import com.aircraft.flightlog.model.Pilot;
import com.aircraft.flightlog.repository.AircraftRepository;
import com.aircraft.flightlog.repository.FlightLogRepository;
import com.aircraft.flightlog.repository.MaintenanceRepository;
import com.aircraft.flightlog.repository.PilotRepository;

@Controller
public class HomeController {

    private final AircraftRepository aircraftRepository;
    private final PilotRepository pilotRepository;
    private final FlightLogRepository flightLogRepository;
    private final MaintenanceRepository maintenanceRepository;

    public HomeController(
            AircraftRepository aircraftRepository,
            PilotRepository pilotRepository,
            FlightLogRepository flightLogRepository,
            MaintenanceRepository maintenanceRepository) {
        this.aircraftRepository = aircraftRepository;
        this.pilotRepository = pilotRepository;
        this.flightLogRepository = flightLogRepository;
        this.maintenanceRepository = maintenanceRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("totalAircraft", aircraftRepository.count());
        model.addAttribute("totalPilots", pilotRepository.count());
        model.addAttribute("totalFlights", flightLogRepository.count());
        model.addAttribute("totalMaintenance", maintenanceRepository.count());

        double totalHours = flightLogRepository.findAll()
                .stream()
                .filter(flight -> flight.getFlightHours() != null)
                .mapToDouble(FlightLog::getFlightHours)
                .sum();

        model.addAttribute("totalFlightHours", totalHours);

        var aircraftMap = aircraftRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Aircraft::getId,
                        Function.identity()));

        var pilotMap = pilotRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Pilot::getId,
                        Function.identity()));

        var recentFlights = flightLogRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getId().compareTo(a.getId()))
                .limit(5)
                .toList();

        model.addAttribute("recentFlights", recentFlights);
        model.addAttribute("aircraftMap", aircraftMap);
        model.addAttribute("pilotMap", pilotMap);
        model.addAttribute("aircraftList", aircraftRepository.findAll());

        return "dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
