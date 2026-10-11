
package com.aircraft.flightlog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aircraft.flightlog.model.Aircraft;
import com.aircraft.flightlog.repository.AircraftRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/aircraft")
public class AircraftController {

    private final AircraftRepository aircraftRepository;

    public AircraftController(AircraftRepository aircraftRepository) {
        this.aircraftRepository = aircraftRepository;
    }

    @GetMapping
    public String listAircraft(Model model) {
        model.addAttribute("aircraftList", aircraftRepository.findAll());
        return "aircraft";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("aircraft", new Aircraft());
        return "aircraft-form";
    }

    @PostMapping("/save")
    public String saveAircraft(
            @Valid @ModelAttribute("aircraft") Aircraft aircraft,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Check for duplicate registration numbers
        if (aircraft.getRegistrationNumber() != null
                && !aircraft.getRegistrationNumber().isBlank()
                && aircraftRepository.existsByRegistrationNumber(
                        aircraft.getRegistrationNumber().trim())) {

            boolean editingExistingAircraft =
                    aircraft.getId() != null
                    && aircraftRepository.existsById(aircraft.getId());

            if (!editingExistingAircraft) {
                bindingResult.rejectValue(
                        "registrationNumber",
                        "duplicate",
                        "This registration number already exists.");
            } else {
                Aircraft existingAircraft =
                        aircraftRepository.findById(aircraft.getId())
                                .orElse(null);

                if (existingAircraft != null
                        && !existingAircraft.getRegistrationNumber()
                                .equalsIgnoreCase(
                                        aircraft.getRegistrationNumber().trim())) {

                    bindingResult.rejectValue(
                            "registrationNumber",
                            "duplicate",
                            "This registration number already exists.");
                }
            }
        }

        // Return to the form if validation fails
        if (bindingResult.hasErrors()) {
            return "aircraft-form";
        }

        aircraft.setRegistrationNumber(
                aircraft.getRegistrationNumber().trim());

        aircraftRepository.save(aircraft);

        redirectAttributes.addFlashAttribute(
                "success", "Aircraft saved successfully.");

        return "redirect:/aircraft";
    }

    @GetMapping("/edit/{id}")
    public String editAircraft(@PathVariable Long id, Model model) {
        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid aircraft ID: " + id));

        model.addAttribute("aircraft", aircraft);
        return "aircraft-form";
    }

    @PostMapping("/delete/{id}")
    public String deleteAircraft(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        aircraftRepository.deleteById(id);

        redirectAttributes.addFlashAttribute(
                "success", "Aircraft deleted successfully.");

        return "redirect:/aircraft";
    }
}
