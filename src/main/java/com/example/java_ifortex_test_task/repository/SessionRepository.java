package com.example.java_ifortex_test_task.repository;

import com.example.java_ifortex_test_task.entity.DeviceType;
import com.example.java_ifortex_test_task.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface SessionRepository extends JpaRepository<Session, Long> {


    @Query(value = """
                SELECT 
                    s.id AS session_id,
                    s.device_type AS session_device_type,
                    s.ended_at_utc AS session_ended_at,
                    s.started_at_utc AS session_started_at,
                    s.user_id AS session_user_id,

                    u.id AS user_id,
                    u.first_name AS user_first_name,
                    u.last_name AS user_last_name,
                    u.middle_name AS user_middle_name,
                    u.email AS user_email,
                    u.deleted AS user_deleted

                FROM sessions s
                JOIN users u ON s.user_id = u.id
                WHERE s.device_type = :deviceCode
                ORDER BY s.started_at_utc ASC 
                LIMIT 1
            """, nativeQuery = true)
    List<Object[]> getFirstDesktopSession(@Param("deviceCode") int deviceCode);

    @Query(value = """
            SELECT
                    s.id AS session_id,
                    s.device_type AS session_device_type,
                    s.ended_at_utc AS session_ended_at,
                    s.started_at_utc AS session_started_at,
                    s.user_id AS session_user_id,

                    u.id AS user_id,
                    u.first_name AS user_first_name,
                    u.last_name AS user_last_name,
                    u.middle_name AS user_middle_name,
                    u.email AS user_email,
                    u.deleted AS user_deleted 
            FROM sessions s
            JOIN users u ON s.user_id = u.id
            WHERE s.ended_at_utc IS NOT NULL
            AND s.ended_at_utc < :endDate
            AND u.deleted = false
            ORDER BY s.started_at_utc DESC 
            """, nativeQuery = true)
    List<Object[]> getSessionsFromActiveUsersEndedBefore2025(@Param("endDate") LocalDateTime endDate);


}