package com.mohamed.lawyer.logs;

import com.mohamed.lawyer.lawsuit.Lawsuit;
import com.mohamed.lawyer.lawsuit.LawsuitRepository;
import com.mohamed.lawyer.whatsapp.WhatsAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class LogsService {

    private final LogsRepository logsRepository;
    private final LawsuitRepository lawsuitRepository;
    private final LogsMapper logsMapper;
    private final WhatsAppService whatsAppService;

    private final String CACHE_VALUE = "logs_";

    @Cacheable(value = CACHE_VALUE, key = "'all_logs'")
    public List<LogsResponse> getAllLogs() {
        return logsRepository.findAll()
                .stream()
                .map(logsMapper::toResponse)
                .toList();
    }
    @Cacheable(value = CACHE_VALUE, key ="#id")
    public LogsResponse getLogById(Long id) {
        Logs logs = logsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Log not found"));
        return logsMapper.toResponse(logs);
    }

   @Cacheable(value = CACHE_VALUE, key = "#lawsuitId")
    public List<LogsResponse> getLogsByLawsuitId(Long lawsuitId) {
        return logsRepository.findByLawsuitId(lawsuitId)
                .stream()
                .map(logsMapper::toResponse)
                .toList();
    }
    @CacheEvict(value = CACHE_VALUE, key = "#lawsuitId")
    public Long createLog(LogsRequest request, Long lawsuitId) {
        Lawsuit lawsuit = lawsuitRepository.findById(lawsuitId)
                .orElseThrow(() -> new IllegalArgumentException("Lawsuit not found"));

        Logs logs = logsMapper.ToLogs(request);
        logs.setDate(LocalDateTime.now());
        logs.setLawsuit(lawsuit);

        Logs savedLog = logsRepository.save(logs);

        // Send WhatsApp notification to client
        if (lawsuit.getClientPhone() != null && !lawsuit.getClientPhone().isEmpty()) {
            whatsAppService.sendLogNotification(
                lawsuit.getClientPhone(),
                lawsuit.getClientName(),
                lawsuit.getName(),
                request.message()
            );
        }

        return savedLog.getId();
    }

    @CacheEvict(value = CACHE_VALUE, allEntries = true)
    public void updateLog(Long id, LogsRequest request) {
        Logs logs = logsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Log not found"));

        logs.setMessage(request.message());
        logsRepository.save(logs);
    }

    public void deleteLog(Long id) {
        if (!logsRepository.existsById(id)) {
            throw new IllegalArgumentException("Log not found");
        }
        logsRepository.deleteById(id);
    }
}
