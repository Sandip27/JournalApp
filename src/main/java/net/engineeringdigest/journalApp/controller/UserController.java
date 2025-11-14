package net.engineeringdigest.journalApp.controller;

import net.engineeringdigest.journalApp.api.response.WeatherResponse;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.scheduler.MailScheduler;
import net.engineeringdigest.journalApp.service.UserService;
import net.engineeringdigest.journalApp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private WeatherService weatherService;

    @Autowired
    private MailScheduler mailScheduler;

    @GetMapping
    public ResponseEntity<User> getUserByUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findUserByUsername(username);

        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User userInDB = userService.findUserByUsername(username);
        userInDB.setUsername(user.getUsername());
        userInDB.setPassword(user.getPassword());
        userInDB.setEmail(user.getEmail());
        userService.saveNewUser(userInDB);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        userService.deleteUserByUsername(username);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/weather/{city}")
    public ResponseEntity<?> greeting(@PathVariable String city) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        String greeting = "";
        WeatherResponse weatherResponse = weatherService.getWeather(city);

        if (weatherResponse != null) {
            greeting = ". Today's weather feels like " + weatherResponse.getCurrent().getFeelslike() + "°C" + " in " + city;
        }

        return new ResponseEntity<>("Hi, " + username + greeting, HttpStatus.OK);
    }

}
