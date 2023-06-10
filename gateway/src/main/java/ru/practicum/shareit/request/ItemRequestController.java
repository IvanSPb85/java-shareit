package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestIncomingDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static ru.practicum.shareit.constant.Constant.REQUEST_GET_LOG;
import static ru.practicum.shareit.constant.Constant.REQUEST_HEADER_USER_ID;
import static ru.practicum.shareit.constant.Constant.REQUEST_POST_LOG;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @RequestBody @Valid ItemRequestIncomingDto itemRequestIncomingDto,
            HttpServletRequest request) {
        log.info(REQUEST_POST_LOG, request.getRequestURI());
        return itemRequestClient.create(userId, itemRequestIncomingDto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnRequests(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId, HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return itemRequestClient.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "20") Integer size, HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @PathVariable long requestId, HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
