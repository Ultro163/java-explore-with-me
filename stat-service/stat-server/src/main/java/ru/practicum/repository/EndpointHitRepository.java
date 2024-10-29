package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.ViewStats;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query("""
                SELECT new ru.practicum.dto.ViewStats(e.app, e.uri, count(distinct e.ip))
                FROM EndpointHit as e
                where e.timestamp between :startTime and :endTime
                group by e.app, e.uri
                order by count(e.ip) desc
            """)
    List<ViewStats> findAllViewStatsByBetweenTimestampAndUniqueIp(LocalDateTime startTime, LocalDateTime endTime);

    @Query("""
                SELECT new ru.practicum.dto.ViewStats(e.app, e.uri, count(e.ip))
                FROM EndpointHit as e
                where e.timestamp between :startTime and :endTime
                group by e.app, e.uri
                order by count(e.ip) desc
            """)
    List<ViewStats> findAllViewStatsByBetweenTimestamp(LocalDateTime startTime, LocalDateTime endTime);

    @Query("""
                SELECT new ru.practicum.dto.ViewStats(e.app, e.uri, count(distinct e.ip))
                FROM EndpointHit as e
                where e.timestamp between :startTime and :endTime
                and e.uri in :uris
                group by e.app, e.uri
                order by count(e.ip) desc
            """)
    List<ViewStats> findAllViewStatsByUrisAndBetweenTimestampAndUniqueIp(LocalDateTime startTime,
                                                                         LocalDateTime endTime,
                                                                         List<String> uris);

    @Query("""
                SELECT new ru.practicum.dto.ViewStats(e.app, e.uri, count(e.ip))
                FROM EndpointHit as e
                where e.timestamp between :startTime and :endTime
                and e.uri in :uris
                group by e.app, e.uri
                order by count(e.ip) desc
            """)
    List<ViewStats> findAllViewStatsByUrisAndBetweenTimestamp(LocalDateTime startTime,
                                                              LocalDateTime endTime,
                                                              List<String> uris);
}