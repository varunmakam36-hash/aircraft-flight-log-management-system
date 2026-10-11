
package com.aircraft.flightlog.controller;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aircraft.flightlog.model.FlightLog;
import com.aircraft.flightlog.repository.AircraftRepository;
import com.aircraft.flightlog.repository.FlightLogRepository;
import com.aircraft.flightlog.repository.PilotRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/flight-logs")
public class FlightLogController {

    private final FlightLogRepository flightLogRepository;
    private final AircraftRepository aircraftRepository;
    private final PilotRepository pilotRepository;

    public FlightLogController(
            FlightLogRepository flightLogRepository,
            AircraftRepository aircraftRepository,
            PilotRepository pilotRepository) {

        this.flightLogRepository = flightLogRepository;
        this.aircraftRepository = aircraftRepository;
        this.pilotRepository = pilotRepository;
    }

    // Display all flight logs and support searching
    @GetMapping
    public String listFlights(
            @RequestParam(required = false) String keyword,
            Model model) {

        var flights = flightLogRepository.findAll();

        Map<Long, com.aircraft.flightlog.model.Aircraft> aircraftMap =
                aircraftRepository.findAll()
                        .stream()
                        .collect(Collectors.toMap(
                                aircraft -> aircraft.getId(),
                                aircraft -> aircraft));

        Map<Long, com.aircraft.flightlog.model.Pilot> pilotMap =
                pilotRepository.findAll()
                        .stream()
                        .collect(Collectors.toMap(
                                pilot -> pilot.getId(),
                                pilot -> pilot));

        if (keyword != null && !keyword.isBlank()) {
            String search = keyword.trim().toLowerCase();

            flights = flights.stream()
                    .filter(flight -> {
                        boolean matchesFlightNumber =
                                flight.getFlightNumber() != null
                                && flight.getFlightNumber()
                                        .toLowerCase().contains(search);

                        boolean matchesStatus =
                                flight.getStatus() != null
                                && flight.getStatus()
                                        .toLowerCase().contains(search);

                        var aircraft =
                                aircraftMap.get(flight.getAircraftId());

                        boolean matchesAircraft =
                                aircraft != null
                                && aircraft.getRegistrationNumber() != null
                                && aircraft.getRegistrationNumber()
                                        .toLowerCase().contains(search);

                        var pilot = pilotMap.get(flight.getPilotId());

                        boolean matchesPilot =
                                pilot != null
                                && pilot.getFullName() != null
                                && pilot.getFullName()
                                        .toLowerCase().contains(search);

                        return matchesFlightNumber
                                || matchesStatus
                                || matchesAircraft
                                || matchesPilot;
                    })
                    .toList();
        }

        model.addAttribute("aircraftMap", aircraftMap);
        model.addAttribute("pilotMap", pilotMap);
        model.addAttribute("flightList", flights);
        model.addAttribute("keyword", keyword);

        return "flight-logs";
    }

    // Display the form to add a flight log
    @GetMapping("/new")
    public String showAddForm(Model model) {

        model.addAttribute("flightLog", new FlightLog());
        model.addAttribute("aircraftList",
                aircraftRepository.findAll());
        model.addAttribute("pilotList",
                pilotRepository.findAll());

        return "flight-log-form";
    }

    // Save a new flight log or update an existing one
    @PostMapping("/save")
    public String saveFlight(
            @Valid @ModelAttribute FlightLog flightLog,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Departure and arrival airports must be different
        if (flightLog.getDepartureAirport() != null
                && flightLog.getArrivalAirport() != null
                && !flightLog.getDepartureAirport().isBlank()
                && !flightLog.getArrivalAirport().isBlank()
                && flightLog.getDepartureAirport().trim()
                        .equalsIgnoreCase(
                                flightLog.getArrivalAirport().trim())) {

            bindingResult.rejectValue(
                    "arrivalAirport",
                    "sameAirport",
                    "Departure and arrival airports cannot be the same.");
        }

        // Return to the form when validation fails
        if (bindingResult.hasErrors()) {
            model.addAttribute("aircraftList",
                    aircraftRepository.findAll());
            model.addAttribute("pilotList",
                    pilotRepository.findAll());

            return "flight-log-form";
        }

        // Prevent duplicate flight numbers
        boolean flightNumberExists =
                flightLogRepository.existsByFlightNumber(
                        flightLog.getFlightNumber());

        if (flightNumberExists) {
            boolean editingExistingFlight =
                    flightLog.getId() != null
                    && flightLogRepository.existsById(
                            flightLog.getId());

            if (!editingExistingFlight) {
                bindingResult.rejectValue(
                        "flightNumber",
                        "duplicate",
                        "This flight number already exists.");

                model.addAttribute("aircraftList",
                        aircraftRepository.findAll());
                model.addAttribute("pilotList",
                        pilotRepository.findAll());

                return "flight-log-form";
            }
        }

        flightLogRepository.save(flightLog);

        redirectAttributes.addFlashAttribute(
                "success",
                "Flight log saved successfully.");

        return "redirect:/flight-logs";
    }

    // Display the form to edit a flight log
    @GetMapping("/edit/{id}")
    public String editFlight(
            @PathVariable Long id,
            Model model) {

        FlightLog flightLog = flightLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid flight log ID: " + id));

        model.addAttribute("flightLog", flightLog);
        model.addAttribute("aircraftList",
                aircraftRepository.findAll());
        model.addAttribute("pilotList",
                pilotRepository.findAll());

        return "flight-log-form";
    }

    // Delete a flight log
    @PostMapping("/delete/{id}")
    public String deleteFlight(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        flightLogRepository.deleteById(id);

        redirectAttributes.addFlashAttribute(
                "success",
                "Flight log deleted successfully.");

        return "redirect:/flight-logs";
    }
}
