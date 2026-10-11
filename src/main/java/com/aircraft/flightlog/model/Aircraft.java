
package com.aircraft.flightlog.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "aircraft")
public class Aircraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Registration number is required")
    @Size(max = 50, message = "Registration number must be at most 50 characters")
    private String registrationNumber;

    @NotBlank(message = "Manufacturer is required")
    @Size(max = 100, message = "Manufacturer must be at most 100 characters")
    private String manufacturer;

    @NotBlank(message = "Aircraft model is required")
    @Size(max = 100, message = "Model must be at most 100 characters")
    private String model;

    @NotBlank(message = "Please select an aircraft status")
    private String status;

    public Aircraft() {
    }

    public Long getId() {
        return id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
