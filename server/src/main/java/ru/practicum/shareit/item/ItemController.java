package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.InComingCommentDto;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OutComingCommentDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

import static ru.practicum.shareit.constant.Constant.REQUEST_GET_LOG;
import static ru.practicum.shareit.constant.Constant.REQUEST_HEADER_USER_ID;
import static ru.practicum.shareit.constant.Constant.REQUEST_PATCH_LOG;
import static ru.practicum.shareit.constant.Constant.REQUEST_POST_LOG;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> create(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @RequestBody ItemDto itemDto, HttpServletRequest request) {
        log.info(REQUEST_POST_LOG, request.getRequestURI());
        return new ResponseEntity<>(itemService.create(userId, itemDto), HttpStatus.OK);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @PathVariable long itemId,
            @RequestBody ItemDto itemDto, HttpServletRequest request) {
        log.info(REQUEST_PATCH_LOG, request.getRequestURI());
        return new ResponseEntity<>(itemService.update(userId, itemId, itemDto), HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemBookingsDto> findItemById(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @PathVariable long itemId, HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(itemService.findItemById(userId, itemId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<ItemBookingsDto>> findItemsByOwner(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(itemService.findItemsByOwner(userId, from, size), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDto>> findItemForRent(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @RequestParam(name = "text") String itemName,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(itemService.findItemForRent(userId, itemName, from, size), HttpStatus.OK);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<OutComingCommentDto> createComment(
            @RequestHeader(REQUEST_HEADER_USER_ID) long userId,
            @PathVariable long itemId,
            @RequestBody InComingCommentDto inComingCommentDto,
            HttpServletRequest request) {
        log.info(REQUEST_POST_LOG, request.getRequestURI());
        return new ResponseEntity<OutComingCommentDto>(itemService.createComment(
                userId, itemId, inComingCommentDto), HttpStatus.OK);
    }
}
