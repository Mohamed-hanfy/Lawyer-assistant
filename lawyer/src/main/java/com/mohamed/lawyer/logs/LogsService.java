package com.mohamed.lawyer.logs;

import com.mohamed.lawyer.lawsuit.Lawsuit;
import com.mohamed.lawyer.lawsuit.LawsuitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class LogsService {

    private final LogsRepository logsRepository;
    private final LawsuitRepository lawsuitRepository;
    private final LogsMapper logsMapper;

    public List<LogsResponse> getAllLogs() {
        return logsRepository.findAll()
                .stream()
                .map(logsMapper::toResponse)
                .toList();
    }

    public LogsResponse getLogById(Long id) {
        Logs logs = logsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Log not found"));
        return logsMapper.toResponse(logs);
    }

    public List<LogsResponse> getLogsByLawsuitId(Long lawsuitId) {
        return logsRepository.findByLawsuitId(lawsuitId)
                .stream()
                .map(logsMapper::toResponse)
                .toList();
    }

    public Long createLog(LogsRequest request, Long lawsuitId) {
        Lawsuit lawsuit = lawsuitRepository.findById(lawsuitId)
                .orElseThrow(() -> new IllegalArgumentException("Lawsuit not found"));

        Logs logs = logsMapper.ToLogs(request);
        logs.setDate(LocalDateTime.now());
        logs.setLawsuit(lawsuit);

        return logsRepository.save(logs).getId();
    }

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
