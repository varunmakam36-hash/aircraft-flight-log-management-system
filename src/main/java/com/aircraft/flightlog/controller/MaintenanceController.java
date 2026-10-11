
package com.aircraft.flightlog.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
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

import com.aircraft.flightlog.model.Maintenance;
import com.aircraft.flightlog.repository.AircraftRepository;
import com.aircraft.flightlog.repository.MaintenanceRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/maintenance")
public class MaintenanceController {

    private final MaintenanceRepository maintenanceRepository;
    private final AircraftRepository aircraftRepository;

    public MaintenanceController(
            MaintenanceRepository maintenanceRepository,
            AircraftRepository aircraftRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.aircraftRepository = aircraftRepository;
    }

    @GetMapping
    public String listMaintenance(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            Model model) {

        if (startDate != null && endDate != null
                && endDate.isBefore(startDate)) {
            model.addAttribute(
                    "filterError",
                    "End date cannot be before start date.");
            model.addAttribute(
                    "maintenanceList", maintenanceRepository.findAll());
        } else {
            model.addAttribute(
                    "maintenanceList",
                    maintenanceRepository.findAll().stream()
                            .filter(record ->
                                    startDate == null
                                    || (record.getMaintenanceDate() != null
                                    && !record.getMaintenanceDate()
                                            .isBefore(startDate)))
                            .filter(record ->
                                    endDate == null
                                    || (record.getMaintenanceDate() != null
                                    && !record.getMaintenanceDate()
                                            .isAfter(endDate)))
                            .toList());
        }

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "maintenance";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("maintenance", new Maintenance());
        model.addAttribute("aircraftList", aircraftRepository.findAll());
        return "maintenance-form";
    }

    @PostMapping("/save")
    public String saveMaintenance(
            @Valid @ModelAttribute("maintenance") Maintenance maintenance,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (maintenance.getMaintenanceDate() != null
                && maintenance.getNextDueDate() != null
                && maintenance.getNextDueDate()
                        .isBefore(maintenance.getMaintenanceDate())) {

            bindingResult.rejectValue(
                    "nextDueDate",
                    "date.invalid",
                    "Next due date cannot be before the maintenance date.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("aircraftList", aircraftRepository.findAll());
            return "maintenance-form";
        }

        maintenanceRepository.save(maintenance);

        redirectAttributes.addFlashAttribute(
                "success", "Maintenance record saved successfully.");

        return "redirect:/maintenance";
    }

    @GetMapping("/edit/{id}")
    public String editMaintenance(
            @PathVariable Long id, Model model) {

        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid maintenance ID: " + id));

        model.addAttribute("maintenance", maintenance);
        model.addAttribute("aircraftList", aircraftRepository.findAll());

        return "maintenance-form";
    }

    @PostMapping("/delete/{id}")
    public String deleteMaintenance(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        maintenanceRepository.deleteById(id);

        redirectAttributes.addFlashAttribute(
                "success", "Maintenance record deleted successfully.");

        return "redirect:/maintenance";
    }
}
