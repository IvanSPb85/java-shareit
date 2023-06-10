package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.InComingCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static ru.practicum.shareit.constant.Constant.REQUEST_GET_LOG;
import static ru.practicum.shareit.constant.Constant.REQUEST_HEADER_USER_ID;
import static ru.practicum.shareit.constant.Constant.REQUEST_PATCH_LOG;
import static ru.practicum.shareit.constant.Constant.REQUEST_POST_LOG;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @RequestBody @Valid ItemDto itemDto, HttpServletRequest request) {
        log.info(REQUEST_POST_LOG, request.getRequestURI());
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @PathVariable long itemId,
            @RequestBody ItemDto itemDto, HttpServletRequest request) {
        log.info(REQUEST_PATCH_LOG, request.getRequestURI());
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @PathVariable long itemId, HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return itemClient.findItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findItemsByOwner(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return itemClient.findItemsByOwner(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemForRent(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @RequestParam(name = "text") String itemName,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return itemClient.findItemForRent(userId, itemName, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @PathVariable long itemId,
            @RequestBody @Valid InComingCommentDto inComingCommentDto,
            HttpServletRequest request) {
        log.info(REQUEST_POST_LOG, request.getRequestURI());
        return itemClient.createComment(userId, itemId, inComingCommentDto);
    }
}
