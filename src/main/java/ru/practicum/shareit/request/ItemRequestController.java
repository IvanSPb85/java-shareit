package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.Collection;

import static ru.practicum.shareit.constant.Constant.*;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> create(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                                 @RequestBody @Valid ItemRequestDto itemRequestDto,
                                                 HttpServletRequest request) {
        log.info(REQUEST_POST_LOG, request.getRequestURI());
        return new ResponseEntity<>(itemRequestService.create(userId, itemRequestDto), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<ItemRequestDto>> getOwnRequests(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId, HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(itemRequestService.getOwnRequests(userId), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<Collection<ItemRequestDto>> getAllRequests(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size, HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(itemRequestService.getAllRequests(userId, from, size), HttpStatus.OK);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequestById(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                                         @PathVariable long requestId, HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(itemRequestService.getRequestById(userId, requestId), HttpStatus.OK);
    }
}
