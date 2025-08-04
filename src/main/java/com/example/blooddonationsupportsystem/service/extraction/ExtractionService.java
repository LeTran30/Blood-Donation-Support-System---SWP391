package com.example.blooddonationsupportsystem.service.extraction;

import com.example.blooddonationsupportsystem.dtos.request.extraction.ExtractionRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.dtos.responses.extraction.ExtractionDetailResponse;
import com.example.blooddonationsupportsystem.dtos.responses.extraction.ExtractionResponse;
import com.example.blooddonationsupportsystem.models.*;
import com.example.blooddonationsupportsystem.repositories.*;
import com.example.blooddonationsupportsystem.utils.InventoryStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExtractionService implements IExtractionService {

    private final ExtractionRepository extractionRepository;
    private final ExtractionDetailRepository extractionDetailRepository;
    private final InventoryRepository inventoryRepository;
    private final BloodTypeRepository bloodTypeRepository;
    private final BloodComponentRepository bloodComponentRepository;

    @Transactional
    @Override
    public ResponseEntity<?> createExtraction(ExtractionRequest request) {
        try {
            BloodType bloodType = bloodTypeRepository.findById(request.getBloodTypeId())
                    .orElseThrow(() -> new RuntimeException("Blood type not found"));

            BloodComponent bloodComponent = bloodComponentRepository.findById(request.getBloodComponentId())
                    .orElseThrow(() -> new RuntimeException("Blood component not found"));

            List<Inventory> inventories = inventoryRepository
                    .findByBloodTypeAndBloodComponentAndStatus(
                            bloodType, bloodComponent, InventoryStatus.AVAILABLE
                    );

            inventories.sort(Comparator.comparing(Inventory::getAddedDate));
            if (inventories.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        ResponseObject.builder()
                                .status(HttpStatus.BAD_REQUEST)
                                .message("There is no suitable blood left in stock.")
                                .build()
                );
            }

            int availableVolume = inventories.stream().mapToInt(Inventory::getQuantity).sum();
            int requestedVolume = request.getTotalVolumeExtraction();

            if (availableVolume < requestedVolume) {
                return ResponseEntity.badRequest().body(
                        ResponseObject.builder()
                                .status(HttpStatus.BAD_REQUEST)
                                .message("Not enough blood to extract. " + (requestedVolume - availableVolume) + " ml short.")
                                .build()
                );
            }

            Extraction extraction = Extraction.builder()
                    .bloodType(bloodType)
                    .bloodComponent(bloodComponent)
                    .volumeExtracted(request.getTotalVolumeExtraction())
                    .notes(request.getNotes())
                    .extractedAt(request.getExtractedAt())
                    .build();

            extraction = extractionRepository.save(extraction);

            int remaining = requestedVolume;
            List<ExtractionDetail> detailList = new ArrayList<>();

            for (Inventory inventory : inventories) {
                if (remaining == 0) break;

                int extractVolume = Math.min(inventory.getQuantity(), remaining);
                inventory.setQuantity(inventory.getQuantity() - extractVolume);
                if (inventory.getQuantity() == 0) inventory.setStatus(InventoryStatus.USED);

                inventoryRepository.save(inventory);

                if (extractVolume > 0) {
                    ExtractionDetail detail = ExtractionDetail.builder()
                            .extraction(extraction)
                            .inventory(inventory)
                            .volumeExtracted(extractVolume)
                            .build();
                    detailList.add(detail);
                }


                remaining -= extractVolume;
            }

            extractionDetailRepository.saveAll(detailList);

            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.CREATED)
                            .message("Extraction created successfully")
                            .data(Map.of("extractionId", extraction.getExtractionId()))
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    @Override
    public ResponseEntity<?> getExtractionById(Integer id) {
        return extractionRepository.findById(id).map(extraction ->
                ResponseEntity.ok(ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Extraction retrieved")
                        .data(mapToResponse(extraction))
                        .build())
        ).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseObject.builder().status(HttpStatus.NOT_FOUND).message("Not found").build()
        ));
    }

    @Override
    public ResponseEntity<?> getAllExtractions(Integer bloodTypeId, Integer bloodComponentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());

        Specification<Extraction> spec = (root, query, builder) -> null;
        if (bloodTypeId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("bloodType").get("bloodTypeId"), bloodTypeId));
        }
        if (bloodComponentId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("bloodComponent").get("componentId"), bloodComponentId));
        }

        Page<Extraction> pageResult = extractionRepository.findAll(spec, pageable);
        List<ExtractionResponse> list = pageResult.getContent().stream().map(this::mapToResponse).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("content", list);
        response.put("totalItems", pageResult.getTotalElements());
        response.put("totalPages", pageResult.getTotalPages());
        response.put("currentPage", page);

        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("List extractions")
                .data(response)
                .build());
    }


    @Override
    public ResponseEntity<?> getExtractionDetailsByExtractionId(Integer extractionId) {
        List<ExtractionDetail> details = extractionDetailRepository.findByExtraction_ExtractionId(extractionId);
        List<ExtractionDetailResponse> response = details.stream().map(detail ->
                ExtractionDetailResponse.builder()
                        .extractionId(detail.getExtraction().getExtractionId())
                        .inventoryId(detail.getInventory().getInventoryId())
                        .volumeExtracted(detail.getVolumeExtracted())
                        .build()
        ).collect(Collectors.toList());

        return ResponseEntity.ok(ResponseObject.builder().status(HttpStatus.OK).message("Extraction details").data(response).build());
    }

    @Override
    public ResponseEntity<?> deleteExtraction(Integer id) {
        Optional<Extraction> extractionOpt = extractionRepository.findById(id);
        if (extractionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder().status(HttpStatus.NOT_FOUND).message("Not found").build());
        }

        extractionDetailRepository.deleteByExtraction_ExtractionId(id);
        extractionRepository.deleteById(id);

        return ResponseEntity.ok(ResponseObject.builder().status(HttpStatus.OK).message("Deleted successfully").build());
    }

    @Transactional
    @Override
    public ResponseEntity<?> updateExtraction(Integer id, ExtractionRequest request) {
        Optional<Extraction> opt = extractionRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder().status(HttpStatus.NOT_FOUND).message("Extraction not found").build()
            );
        }

        Extraction extraction = opt.get();
        extraction.setNotes(request.getNotes());
        extraction.setExtractedAt(request.getExtractedAt());
        extraction.setVolumeExtracted(request.getTotalVolumeExtraction());
        extractionRepository.save(extraction);

        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Extraction updated successfully")
                .data(Map.of("extractionId", extraction.getExtractionId()))
                .build());
    }

    private ExtractionResponse mapToResponse(Extraction extraction) {
        List<ExtractionDetailResponse> details = extractionDetailRepository.findByExtraction_ExtractionId(extraction.getExtractionId())
                .stream()
                .map(detail -> ExtractionDetailResponse.builder()
                        .extractionId(detail.getExtraction().getExtractionId())
                        .inventoryId(detail.getInventory().getInventoryId())
                        .volumeExtracted(detail.getVolumeExtracted())
                        .build())
                .collect(Collectors.toList());

        return ExtractionResponse.builder()
                .extractionId(extraction.getExtractionId())
                .bloodTypeId(extraction.getBloodType().getBloodTypeId())
                .bloodTypeName(extraction.getBloodType().getTypeName())
                .bloodComponentId(extraction.getBloodComponent().getComponentId())
                .bloodComponentName(extraction.getBloodComponent().getComponentName())
                .totalVolumeExtraction(extraction.getVolumeExtracted())
                .notes(extraction.getNotes())
                .details(details)
                .extractedAt(extraction.getExtractedAt())
                .createdAt(extraction.getCreateAt())
                .updatedAt(extraction.getUpdateAt())
                .build();
    }
}