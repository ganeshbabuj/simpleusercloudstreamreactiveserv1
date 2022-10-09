package com.example.cloudstream.resource;

import lombok.Data;

@Data
public class RegistrationSummary {

    private AgeBand ageBand;
    private int totalCount;
    private int activatedCount;
    private int rejectedCount;

}
