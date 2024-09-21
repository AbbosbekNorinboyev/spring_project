package uz.pdp.controller;

import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import uz.pdp.SessionUser;
import uz.pdp.dao.AuthUserDao;
import uz.pdp.dao.CityDao;
import uz.pdp.domain.AuthUser;
import uz.pdp.domain.SubscribedCity;
import uz.pdp.domain.Weather;
import uz.pdp.dto.UserRegisterDTO;

import java.io.IOException;
import java.util.List;

@Controller
public class UserController {
    private final AuthUserDao authUserDao;
    private final PasswordEncoder passwordEncoder;
    private final CityDao cityDao;
    private final SessionUser sessionUser;

    public UserController(AuthUserDao authUserDao, PasswordEncoder passwordEncoder, CityDao cityDao, SessionUser sessionUser) {
        this.authUserDao = authUserDao;
        this.passwordEncoder = passwordEncoder;
        this.cityDao = cityDao;
        this.sessionUser = sessionUser;
    }

    @GetMapping("/auth/login")
    public String login() {
        return "login";
    }

    @GetMapping("/city/list")
    public ModelAndView cityList() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user_cities");
        modelAndView.addObject("cities", cityDao.getAllCity());
        return modelAndView;
    }

    @GetMapping("/")
    public String getHome() {
        return "home";
    }


    @GetMapping("/city/subscribedCities")
    public ModelAndView subscribedCities() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("subscribedCities");
        Long id = sessionUser.getId();
        List<SubscribedCity> subscribedCities = cityDao.getSubscribedCities(id);
        modelAndView.addObject("cities", subscribedCities);
        System.out.println("subscribedCities: " + subscribedCities);
        return modelAndView;
    }

    @GetMapping("/auth/register")
    public String registerPage(Model model) {
        model.addAttribute("dto", new UserRegisterDTO());
        return "register";
    }

    @GetMapping("/auth/logout")
    public String logoutPage() {
        return "logout";
    }

    @PostMapping("/auth/register")
    public String register(@Valid @ModelAttribute("dto") UserRegisterDTO dto, BindingResult errors) throws IOException {
        if (errors.hasErrors()) return "register";
        AuthUser authUser = AuthUser.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role("USER")
                .build();
        authUserDao.save(authUser);
        return "redirect:/auth/login";
    }

    @GetMapping("/weather/city/{id}")
    public String getWeatherByCity(@PathVariable int id, Model model) {
        List<Weather> weather = cityDao.getWeather(id);
        model.addAttribute("weathers", weather);
        System.out.println("weather: " + weather);
        model.addAttribute("city_id", id);
        return "weather";
    }


    @PostMapping("/weather/subscribe/{city_name}/{city_id}")
    public String subscribeCity(@PathVariable("city_name") String city_name, @PathVariable("city_id") Integer city_id) {
        AuthUser user = sessionUser.getUser();
        cityDao.subscribeCity(user.getId(), city_name, city_id);
        return "redirect:/city/subscribedCities";
    }

// /weather/city/unsubscribe/{id}(id=${city.getCity_id()})

    @PostMapping("/weather/city/unsubscribe/{city_id}")
    public String subscribeCity(@PathVariable Integer city_id) {
        AuthUser user = sessionUser.getUser();
        cityDao.unsubscribeCity(user.getId(), city_id);
        return "redirect:/city/subscribedCities";
    }

//    @GetMapping("/user/image/{username:.+}")
//    public ResponseEntity<Resource> downloadPage(@PathVariable String username) {
//        int i = username.indexOf(".");
//        String substring = username.substring(0,i);
//        Uploads uploads = uploadsDao.findImgByUserName(substring);
//        FileSystemResource fileSystemResource = new FileSystemResource(rootPath.resolve(username));
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(uploads.getMimeType()))
//                .contentLength(uploads.getSize())
//                .header("Content-Disposition", "attachment; filename = " + uploads.getOriginalName())
//                .body(fileSystemResource);
//    }
}