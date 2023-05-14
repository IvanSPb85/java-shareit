package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constant.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    Collection<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
            long userId, LocalDateTime start, LocalDateTime end, Sort sort);

    Collection<Booking> findAllByBookerIdAndEndIsBefore(long userId, LocalDateTime end, Sort sort);

    Collection<Booking> findAllByBookerIdAndStartIsAfter(long userId, LocalDateTime after, Sort sort);

    Collection<Booking> findAllByBookerIdAndStatus(long userId, Status status, Sort sort);

}
