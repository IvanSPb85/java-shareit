package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constant.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long userId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
            long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndIsBefore(long userId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartIsAfter(long userId, LocalDateTime after, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatus(long userId, Status status, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1 order by b.start desc")
    List<Booking> findAllByOwner(long ownerId, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1 and current_timestamp between b.start and b.end" +
            " order by b.end desc ")
    List<Booking> findAllByOwnerAndCurrentState(long ownerId, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < current_timestamp" +
            " order by b.end desc ")
    List<Booking> findAllByOwnerAndPastState(long ownerId, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > current_timestamp" +
            " order by b.end desc ")
    List<Booking> findAllByOwnerAndFutureState(long ownerId, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1 and status = ?2" +
            " order by b.end desc ")
    List<Booking> findAllByOwnerAndWaitingState(long ownerId, Status status, Pageable pageable);

    Collection<Booking> findAllByItemIdAndStatus(long itemId, Status status);

    Collection<Booking> findAllByItemIdInAndStatus(List<Long> itemIdList, Status status);

    boolean existsByBookerIdAndItemIdAndStatusAndEndIsBefore(long userId, long itemId, Status status,
                                                             LocalDateTime dateTime);
}
