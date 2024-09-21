package uz.pdp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import uz.pdp.dao.AdminDAO;
import uz.pdp.dao.AuthUserDao;
import uz.pdp.dao.CityDao;
import uz.pdp.domain.City;
import uz.pdp.domain.SubscribedCity;
import uz.pdp.domain.Weather;
import uz.pdp.dto.CityDTO;
import uz.pdp.dto.WeatherDTO;

import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/admin")
//@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AuthUserDao authUserDao;
    private final CityDao cityDao;
    private final AdminDAO adminDAO;

    public AdminController(AuthUserDao authUserDao, CityDao cityDao, AdminDAO adminDAO) {
        this.authUserDao = authUserDao;
        this.cityDao = cityDao;
        this.adminDAO = adminDAO;
    }

    @GetMapping
    public String adminsPage() {
        return "admin";
    }

    @GetMapping("/users/list")
    public ModelAndView userList() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("usersList");
        modelAndView.addObject("users", authUserDao.getAllUsers());
        return modelAndView;
    }

    @GetMapping("/cities/list")
    public ModelAndView cities() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("cities");
        modelAndView.addObject("cities", cityDao.getAllCity());
        return modelAndView;
    }

    @GetMapping("/add/city")
    public String addCityPage(Model model) {
        return "adminaddcity";
    }

    @PostMapping("/add/city")
    public String addCity(@ModelAttribute CityDTO cityDTO, Authentication authentication) {
        City city = City.builder().country(cityDTO.country())
                .name(cityDTO.name()).build();
        cityDao.save(city, authentication);
        return "redirect:/admin/cities/list";
    }

    @GetMapping("/delete/city/{id}")
    public String deleteCityPage(@PathVariable("id") Integer id, Model model) {
        return "deleteCity";
    }

    @PostMapping("/delete/city/{id}")
    public String deleteCity(@PathVariable("id") Integer id) {
        cityDao.deleteCity(id);
        return "redirect:/admin/cities/list";
    }

    @GetMapping("/update/city/{id}")
    public String updatePage(@PathVariable("id") Integer id, Model model) {
        Optional<City> cityOptional = cityDao.findById(id);
        cityOptional.ifPresent(city -> {
            model.addAttribute("city", city);
        });
        return "updatecity";
    }

    @PostMapping("/update/city/{id}")
    public String updateCity(@PathVariable("id") Integer id, @ModelAttribute CityDTO cityDTO) {
        cityDao.update(id, cityDTO);
        return "redirect:/admin/cities/list";
    }

    @GetMapping("/weather/city/{id}")
    public String getWeatherByCity(@PathVariable("id") Integer id, Model model) {
        List<Weather> weather = cityDao.getWeather(id);
        model.addAttribute("weathers", weather);
        model.addAttribute("city_id", id);
        return "weather";
    }

    @GetMapping("/delete/weather/{id}")
    public String deleteWeatherPage(@PathVariable("id") Integer id, Model model) {
        List<Weather> weather = cityDao.getWeather(id);
        model.addAttribute("weather", weather);
        return "deleteWeather";
    }

    @PostMapping("/delete/weather/{id}")
    public String deleteWeather(@PathVariable("id") int id) {
        cityDao.deleteWeather(id);
        return "redirect:/admin/cities/list";
    }


    @GetMapping("/add/weather/{id}")
    public String addWeatherPage(@PathVariable("id") int id) {
        return "addweather";
    }

    @PostMapping("/add/weather/{city_id}")
    public String addWeather(@PathVariable("city_id") int city_id, @ModelAttribute WeatherDTO weatherDTO) {
        Integer i = cityDao.addWeather(city_id, weatherDTO);
        return "redirect:/admin/weather/city/" + i;
    }


    @GetMapping("/update/weather/{id}")
    public String updateWeatherPage(@PathVariable("id") Integer id, Model model) {
        Weather weather = cityDao.getWeatherById(id);
        model.addAttribute("weather", weather);
        System.out.println("weather: " + weather);
        return "updateWeather";
    }

    @PostMapping("/update/weather/{id}")
    public String updateWeather(@PathVariable("id") Integer id, @ModelAttribute WeatherDTO weatherDTO) {
        cityDao.updateWeather(id, weatherDTO);
        return "redirect:/admin/cities/list";
    }


    @GetMapping("/users/details/{id}")
    public String userDetail(@PathVariable("id") Long id, Model model) {
        List<SubscribedCity> cities = cityDao.getSubscribedCities(id);
        authUserDao.findById(id).ifPresent((authUser) -> {
            model.addAttribute("user", authUser);
        });
        model.addAttribute("cities", cities);
        return "userdetail";
    }

    @PostMapping("/deactivate/user")
//    @PreAuthorize("hasRole('ADMIN')")
    public String deactivateUser(@RequestParam("id") Long id) {
        adminDAO.deactivate(id);
        return "redirect:/admin/users/list";
    }

    @PostMapping("/activate/user")
//    @PreAuthorize("hasRole('ADMIN')")
    public String activateUser(@RequestParam("id") Long id) {
        adminDAO.activate(id);
        return "redirect:/admin/users/list";
    }
}