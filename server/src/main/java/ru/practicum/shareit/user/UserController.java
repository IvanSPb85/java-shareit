package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

import static ru.practicum.shareit.constant.Constant.REQUEST_DELETE_LOG;
import static ru.practicum.shareit.constant.Constant.REQUEST_GET_LOG;
import static ru.practicum.shareit.constant.Constant.REQUEST_PATCH_LOG;
import static ru.practicum.shareit.constant.Constant.REQUEST_POST_LOG;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody  UserDto userDto, HttpServletRequest request) {
        log.info(REQUEST_POST_LOG, request.getRequestURI());
        return new ResponseEntity<>(userService.create(userDto), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable long id, @RequestBody UserDto userDto,
                                          HttpServletRequest request) {
        log.info(REQUEST_PATCH_LOG, request.getRequestURI());
        return new ResponseEntity<>(userService.updateUser(id, userDto), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<UserDto>> findAll(HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findUser(@PathVariable long id, HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(userService.findUser(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id, HttpServletRequest request) {
        log.info(REQUEST_DELETE_LOG, request.getRequestURI());
        userService.deleteUser(id);
    }
}
