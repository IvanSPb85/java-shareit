package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.time.LocalDateTime;
import java.util.Collection;

import static ru.practicum.shareit.constant.Constant.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingItemDto> create(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @RequestBody @Valid BookingDto bookingDto,
            HttpServletRequest request) {
        log.info(REQUEST_POST_LOG, request.getRequestURI());
        return new ResponseEntity<>(bookingService.create(userId, bookingDto), HttpStatus.OK);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingItemDto> approve(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @PathVariable long bookingId,
            @RequestParam(name = "approved") Boolean approved,
            HttpServletRequest request) {
        log.info(REQUEST_PATCH_LOG, request.getRequestURI());
        return new ResponseEntity<>(bookingService.approve(userId, bookingId, approved), HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingItemDto> getBookingById(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @PathVariable long bookingId, HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(bookingService.findBookingById(userId, bookingId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<BookingItemDto>> getAllBookingsByUser(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userid,
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "20") Integer size, HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(bookingService.findAllBookingByUser(
                userid, state, from, size, LocalDateTime.now()), HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<Collection<BookingItemDto>> getAllBookingsByOwner(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "20") Integer size, HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(bookingService.findAllBookingsByOwner(userId, state.toUpperCase(), from, size),
                HttpStatus.OK);
    }
}
