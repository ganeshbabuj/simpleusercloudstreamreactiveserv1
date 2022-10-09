package com.example.cloudstream.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationSummaryCollection {

    private List<RegistrationSummary> items;

}
