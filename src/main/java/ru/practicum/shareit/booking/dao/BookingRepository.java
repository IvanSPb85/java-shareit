package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constant.Status;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    Collection<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
            long userId, LocalDateTime start, LocalDateTime end, Sort sort);

    Collection<Booking> findAllByBookerIdAndEndIsBefore(long userId, LocalDateTime end, Sort sort);

    Collection<Booking> findAllByBookerIdAndStartIsAfter(long userId, LocalDateTime after, Sort sort);

    Collection<Booking> findAllByBookerIdAndStatus(long userId, Status status, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1 order by b.start desc")
    Collection<Booking> findAllByOwner(long ownerId);

    @Query("select b from Booking b where b.item.owner.id = ?1 and current_timestamp between b.start and b.end" +
            " order by b.end desc ")
    Collection<Booking> findAllByOwnerAndCurrentState(long ownerId);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < current_timestamp" +
            " order by b.end desc ")
    Collection<Booking> findAllByOwnerAndPastState(long ownerId);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > current_timestamp" +
            " order by b.end desc ")
    Collection<Booking> findAllByOwnerAndFutureState(long ownerId);

    @Query("select b from Booking b where b.item.owner.id = ?1 and status = ?2" +
            " order by b.end desc ")
    Collection<Booking> findAllByOwnerAndWaitingState(long ownerId, Status status);

    Collection<Booking> findAllByItemId(long itemId);
}
