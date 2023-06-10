package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.user.dto.UserDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static ru.practicum.shareit.constant.Constant.REQUEST_DELETE_LOG;
import static ru.practicum.shareit.constant.Constant.REQUEST_GET_LOG;
import static ru.practicum.shareit.constant.Constant.REQUEST_PATCH_LOG;
import static ru.practicum.shareit.constant.Constant.REQUEST_POST_LOG;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid UserDto userDto, HttpServletRequest request) {
        log.info(REQUEST_POST_LOG, request.getRequestURI());
        return userClient.create(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable long id, @RequestBody UserDto userDto,
                                         HttpServletRequest request) {
        log.info(REQUEST_PATCH_LOG, request.getRequestURI());
        return userClient.update(id, userDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return userClient.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findUser(@PathVariable long id, HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return userClient.findUser(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable long id, HttpServletRequest request) {
        log.info(REQUEST_DELETE_LOG, request.getRequestURI());
        return userClient.deleteUser(id);
    }
}
