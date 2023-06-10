package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;

@Component
public class BookingDtoValidator {
    public void validateBookingTime(BookItemRequestDto bookingDto) throws ValidationException {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()))
            throw new ValidationException("Окончание аренды не может быть раньше ее начала!");
        if (bookingDto.getStart().equals(bookingDto.getEnd()))
            throw new ValidationException("Окончание аренды не может совпадать с ее началом");
    }
}
