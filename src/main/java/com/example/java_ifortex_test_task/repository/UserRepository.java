package com.example.java_ifortex_test_task.repository;

import com.example.java_ifortex_test_task.entity.DeviceType;
import com.example.java_ifortex_test_task.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = """
            WITH user_mobile_sessions AS (
                SELECT 
                    user_id,
                    MAX(started_at_utc) as last_session,
                    COUNT(*) as sessions_count
                FROM sessions
                WHERE device_type = :deviceCode
                GROUP BY user_id
            )
            SELECT 
                u.id AS user_id,
                u.first_name AS user_first_name,
                u.last_name AS user_last_name,
                u.middle_name AS user_middle_name,
                u.email AS user_email,
                u.deleted AS user_deleted,
                ums.last_session AS last_session_date,
                ums.sessions_count AS mobile_sessions_count
            FROM users u
            JOIN user_mobile_sessions ums ON u.id = ums.user_id
            WHERE u.deleted = false
            ORDER BY ums.last_session DESC
            """, nativeQuery = true)
    List<Object[]> getUsersWithAtLeastOneMobileSession(@Param("deviceCode") int deviceCode);

    @Query(value = """
            SELECT 
                u.id AS user_id,
                u.first_name AS user_first_name,
                u.last_name AS user_last_name,
                u.middle_name AS user_middle_name,
                u.email AS user_email,
                u.deleted AS user_deleted,
                usc.session_count AS total_sessions
            FROM users u
            JOIN (
                SELECT user_id, COUNT(*) AS session_count
                FROM sessions
                GROUP BY user_id
                ORDER BY session_count DESC
                LIMIT 1
            ) usc ON u.id = usc.user_id
            WHERE u.deleted = false
            """, nativeQuery = true)
    List<Object[]> getUserWithMostSessions();
}
