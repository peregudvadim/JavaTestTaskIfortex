package com.example.java_ifortex_test_task.service;

import com.example.java_ifortex_test_task.dto.SessionResponseDTO;
import com.example.java_ifortex_test_task.entity.DeviceType;
import com.example.java_ifortex_test_task.entity.Session;
import com.example.java_ifortex_test_task.entity.User;
import com.example.java_ifortex_test_task.mapper.SessionMapper;
import com.example.java_ifortex_test_task.repository.SessionRepository;
import com.example.java_ifortex_test_task.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;

    // Returns the first (earliest) desktop Session
    public SessionResponseDTO getFirstDesktopSession() {
        List<Object[]> results = sessionRepository.getFirstDesktopSession(DeviceType.DESKTOP.getCode());
        if (results == null || results.isEmpty() || results.get(0) == null) {
            throw new EntityNotFoundException("No desktop sessions found");
        }
        Object[] result = results.get(0);

        Session session = mapToSession(result);
        return sessionMapper.toDto(session);
    }


    // Returns only Sessions from Active users that were ended before 2025
    public List<SessionResponseDTO> getSessionsFromActiveUsersEndedBefore2025() {
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        List<Object[]> results = sessionRepository.getSessionsFromActiveUsersEndedBefore2025(endDate);

        if (results == null || results.isEmpty() || results.get(0) == null) {
            throw new EntityNotFoundException("No sessions found");
        }
        List<Session> sessions = new ArrayList<>();
        for (Object[] obj : results) {

            sessions.add(mapToSession(obj));
        }


        return sessions.stream()
                .map(sessionMapper::toDto)
                .toList();

    }

    private Session mapToSession(Object[] result) {

        if (result == null || result.length < 11) {
            throw new IllegalArgumentException("Invalid result array for mapping Session");
        }

        Long sessionId = ((Number) result[0]).longValue();         // session_id
        int deviceCode = ((Number) result[1]).intValue();          // session_device_type
        DeviceType deviceType = DeviceType.fromCode(deviceCode);   // converting the code to enum
        Timestamp endedAt = (Timestamp) result[2];                 // session_ended_at
        Timestamp startedAt = (Timestamp) result[3];               // session_started_at
        Long userIdFromSession = ((Number) result[4]).longValue(); // session_user_id

        Long userId = ((Number) result[5]).longValue();            // user_id
        String firstName = (String) result[6];                     // user_first_name
        String lastName = (String) result[7];                      // user_last_name
        String middleName = (String) result[8];                    // user_middle_name
        String email = (String) result[9];                         // user_email
        Boolean deleted = (Boolean) result[10];                    // user_deleted

        User user = new User();
        user.setId(userId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setMiddleName(middleName);
        user.setEmail(email);
        user.setDeleted(deleted);

        Session session = new Session();
        session.setId(sessionId);
        session.setDeviceType(deviceType);
        session.setEndedAtUtc(endedAt != null ? endedAt.toLocalDateTime() : null);
        session.setStartedAtUtc(startedAt != null ? startedAt.toLocalDateTime() : null);
        session.setUser(user);

        return session;
    }
}
