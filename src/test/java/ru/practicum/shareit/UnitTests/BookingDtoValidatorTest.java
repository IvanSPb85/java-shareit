package ru.practicum.shareit.UnitTests;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoValidator;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingDtoValidatorTest {
    BookingDtoValidator validator = new BookingDtoValidator();
    private static final LocalDateTime DATE_TIME = LocalDateTime.now();

    private final BookingDto bookingDto = BookingDto.builder()
            .itemId(1L)
            .start(DATE_TIME)
            .end(DATE_TIME).build();

    @Test
    public void validateBookingTime_whenEndEqualsStart_thenValidationException() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateBookingTime(bookingDto));

        assertEquals("Окончание аренды не может совпадать с ее началом", exception.getMessage());
    }

    @Test
    public void validateBookingTime_whenEndIsBeforeStart_thenValidationException() {
        bookingDto.setEnd(DATE_TIME.minusHours(1L));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateBookingTime(bookingDto));

        assertEquals("Окончание аренды не может быть раньше ее начала!", exception.getMessage());
    }


}