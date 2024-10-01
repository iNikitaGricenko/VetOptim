package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.dto.PetHealthSummary;
import com.wolfhack.vetoptim.petmanagement.model.MedicalRecord;
import com.wolfhack.vetoptim.petmanagement.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetHealthAnalyticsService {

    private final MedicalRecordRepository medicalRecordRepository;

    public PetHealthSummary getPetHealthSummary(Long petId) {
        log.info("Fetching health summary for Pet ID: {}", petId);
        List<MedicalRecord> medicalRecords = new ArrayList<>(medicalRecordRepository.findAllByPetId(petId));

        if (medicalRecords.isEmpty()) {
            log.info("No health records available for Pet ID: {}", petId);
            return new PetHealthSummary("No health records available.");
        }

        medicalRecords.sort(Comparator.comparing(MedicalRecord::getDateOfTreatment).reversed());

        int numberOfVisits = medicalRecords.size();
        String latestCondition = medicalRecords.getFirst().getDiagnosis();
        Map<String, Long> conditionFrequency = medicalRecords.stream()
            .collect(Collectors.groupingBy(MedicalRecord::getDiagnosis, Collectors.counting()));

        log.info("Health summary calculated for Pet ID: {}", petId);
        return new PetHealthSummary(numberOfVisits, latestCondition, calculateHealthTrend(conditionFrequency));
    }

    private String calculateHealthTrend(Map<String, Long> conditionFrequency) {
        return conditionFrequency.entrySet().stream()
            .filter(entry -> entry.getValue() > 1)
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("No recurring conditions");
    }
}
