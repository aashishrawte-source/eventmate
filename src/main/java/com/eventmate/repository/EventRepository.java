package com.eventmate.repository;

import com.eventmate.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    // Find events for an organizer that overlap with the given times.
    @Query("""
        select e from Event e
        where e.createdBy = :organizerId
          and e.id <> coalesce(:excludeId, -1)
          and (
               (e.startTime < :endTime and e.endTime > :startTime)
          )
        """)
    List<Event> findOverlappingEvents(
            @Param("organizerId") Long organizerId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime,
            @Param("excludeId") Long excludeId
    );

    List<Event> findByCreatedBy(Long organizerId);
}
