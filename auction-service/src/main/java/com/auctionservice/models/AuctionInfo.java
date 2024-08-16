package com.auctionservice.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "actionsInfo")
public class AuctionInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotEmpty
    @Size(max = 40)
    private String title;
    private String description;
    @NotNull
    @Min(100)
    private double price;
    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate startDate;
    @NotNull
    private LocalTime startTime;
    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate endDate;
    @NotNull
    private LocalTime endTime;
    @NotEmpty
    @Size(min = 7,max = 40)
    private String ownerEmail;
    @NotEmpty
    @Size(max = 120)
    private String photoUrl;
    private String uniqueCode;
    private Boolean isValid = true;

}
