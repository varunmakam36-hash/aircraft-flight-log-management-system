
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

import com.aircraft.flightlog.model.Pilot;
import com.aircraft.flightlog.repository.PilotRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/pilots")
public class PilotController {

    private final PilotRepository pilotRepository;

    public PilotController(PilotRepository pilotRepository) {
        this.pilotRepository = pilotRepository;
    }

    // Display all pilots
    @GetMapping
    public String listPilots(Model model) {
        model.addAttribute("pilotList", pilotRepository.findAll());
        return "pilots";
    }

    // Display the form to add a pilot
    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("pilot", new Pilot());
        return "pilot-form";
    }

    // Save a new pilot or update an existing one
    @PostMapping("/save")
    public String savePilot(
            @Valid @ModelAttribute("pilot") Pilot pilot,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Check for duplicate employee ID
        if (pilot.getEmployeeId() != null
                && !pilot.getEmployeeId().isBlank()
                && pilotRepository.existsByEmployeeId(
                        pilot.getEmployeeId().trim())) {

            boolean editingExistingPilot =
                    pilot.getId() != null
                    && pilotRepository.existsById(pilot.getId());

            if (!editingExistingPilot) {
                bindingResult.rejectValue(
                        "employeeId",
                        "duplicate",
                        "This employee ID already exists.");
            } else {
                Pilot existingPilot = pilotRepository
                        .findById(pilot.getId()).orElse(null);

                if (existingPilot != null
                        && !existingPilot.getEmployeeId()
                                .equalsIgnoreCase(
                                        pilot.getEmployeeId().trim())) {
                    bindingResult.rejectValue(
                            "employeeId",
                            "duplicate",
                            "This employee ID already exists.");
                }
            }
        }

        // Check for duplicate license number
        if (pilot.getLicenseNumber() != null
                && !pilot.getLicenseNumber().isBlank()
                && pilotRepository.existsByLicenseNumber(
                        pilot.getLicenseNumber().trim())) {

            boolean editingExistingPilot =
                    pilot.getId() != null
                    && pilotRepository.existsById(pilot.getId());

            if (!editingExistingPilot) {
                bindingResult.rejectValue(
                        "licenseNumber",
                        "duplicate",
                        "This license number already exists.");
            } else {
                Pilot existingPilot = pilotRepository
                        .findById(pilot.getId()).orElse(null);

                if (existingPilot != null
                        && !existingPilot.getLicenseNumber()
                                .equalsIgnoreCase(
                                        pilot.getLicenseNumber().trim())) {
                    bindingResult.rejectValue(
                            "licenseNumber",
                            "duplicate",
                            "This license number already exists.");
                }
            }
        }

        // Return to the form if validation fails
        if (bindingResult.hasErrors()) {
            return "pilot-form";
        }

        pilot.setEmployeeId(pilot.getEmployeeId().trim());
        pilot.setFullName(pilot.getFullName().trim());
        pilot.setLicenseNumber(pilot.getLicenseNumber().trim());
        pilot.setEmail(pilot.getEmail().trim());
        pilot.setPhone(pilot.getPhone().trim());

        pilotRepository.save(pilot);

        redirectAttributes.addFlashAttribute(
                "success", "Pilot saved successfully.");

        return "redirect:/pilots";
    }

    // Display the form to edit a pilot
    @GetMapping("/edit/{id}")
    public String editPilot(@PathVariable Long id, Model model) {
        Pilot pilot = pilotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid pilot ID: " + id));

        model.addAttribute("pilot", pilot);
        return "pilot-form";
    }

    // Delete a pilot
    @PostMapping("/delete/{id}")
    public String deletePilot(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        pilotRepository.deleteById(id);

        redirectAttributes.addFlashAttribute(
                "success", "Pilot deleted successfully.");

        return "redirect:/pilots";
    }
}
