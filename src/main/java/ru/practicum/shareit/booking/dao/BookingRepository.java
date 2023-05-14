package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
