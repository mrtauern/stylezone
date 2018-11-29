package com.stylezone.demo.controllers;

import com.stylezone.demo.models.Booking;
import com.stylezone.demo.models.ReCaptchaResponse;
import com.stylezone.demo.models.BookingGroup;
import com.stylezone.demo.models.Staff;
import com.stylezone.demo.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.logging.Logger;

@Controller
public class BookingController {

    public BookingController() {
    }

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    BookingService bookingService;

    private final String REDIRECT = "redirect:/";
    private final String SAVEBOOKING = "saveBooking";
    private final String BOOKING = "booking";
    private final String TIMESELECT = "timeSelect";
    private final String BILLEDEGALLERI = "billedeGalleri";
    private final String INDEX = "index";
    private final String OMOS = "omOs";
    private final String REDIGEREPESONALE = "redigerePersonale";
    private final String STAFF = "staff";
    private final String DELETESTAFF = "deleteStaff";
    Logger log = Logger.getLogger(BookingController.class.getName());

    @GetMapping("/")
    public String index(Model model) {
        log.info("Index called...");

        model.addAttribute("pageTitle", "Forside");
        model.addAttribute("isIndex", true);

        return INDEX;
    }

    @GetMapping("/booking")
    public String booking(Model model) {
        log.info("booking called...");

        List<BookingGroup> bookingGroups = bookingService.getBookingGroups("12-12-2018", "10:00", "18:30");
        String[] dates = bookingService.getDatesOfWeek();
        model.addAttribute("monday", dates[0]);
        model.addAttribute("tuesday", dates[1]);
        model.addAttribute("wednesday", dates[2]);
        model.addAttribute("thursday", dates[3]);
        model.addAttribute("friday", dates[4]);
        model.addAttribute("saturday", dates[5]);
        model.addAttribute("sunday", dates[6]);
        model.addAttribute("weekNumber",bookingService.getWeekToday());
        model.addAttribute("mondayBookings", bookingGroups);
        model.addAttribute("pageTitle", "Book tid");

        log.info(bookingService.getDateToday());

        return BOOKING;
    }

    @GetMapping("/timeSelect/{date}/{start}/{end}")
    public String timeSelect(@PathVariable String date, @PathVariable String start, @PathVariable String end, Model model) {
        log.info("timeSelect called...");

        List<Booking> bookings = bookingService.getSelectedBookings(date, start, end);
        model.addAttribute("bookings", bookings);
        model.addAttribute("date", date);
        model.addAttribute("pageTitle", "Tider for " + date + ", mellem kl." + start + " - " + end);

        return TIMESELECT;
    }

    @GetMapping("/billedeGalleri")
    public String billedGalleri() {
        log.info("billedeGalleri called...");

        return BILLEDEGALLERI;

    }


    @GetMapping("/saveBooking/{bookingTime}/{bookingDate}")
    public String saveBooking(@PathVariable("bookingTime") String bookingTime, @PathVariable("bookingDate") String bookingDate, Model model){

        log.info("saveBooking getmapping called...");

        model.addAttribute("booking", new Booking());

        model.addAttribute("time", bookingTime);
        model.addAttribute("date", bookingDate);


        return SAVEBOOKING;
    }

    @PostMapping("/saveBooking")
    public String saveBooking(@ModelAttribute Booking booking,
                              @RequestParam("g-recaptcha-response") String captchaResponse){

        log.info("saveBooking postmapping called...");

        String url = "https://www.google.com/recaptcha/api/siteverify";
        String params = "?secret=6LeWE30UAAAAAMUpo7seu91Da6DXig-DQxN8YKEQ&response="+captchaResponse;

        ReCaptchaResponse reCaptchaResponse = restTemplate.exchange(url+params, HttpMethod.POST, null, ReCaptchaResponse.class).getBody();

        if(reCaptchaResponse.isSuccess()) {

            bookingService.saveBooking(booking);
            bookingService.sendEmail(booking);
            return REDIRECT+BOOKING;
        }


        return SAVEBOOKING;

    }

    @GetMapping("/omOs")
    public String omOs(Model model) {

        return OMOS;

    }

    @GetMapping("/redigerePersonale/{staffId}")
    public String redigerPersonale(@PathVariable("staffId")int staffId, Model model){
        log.info("redigerePersonale GetMapping called...");

        Staff staff = bookingService.getStaffMember(staffId);

        model.addAttribute("staff",staff );

        return REDIGEREPESONALE;

    }

    @PutMapping("/redigerePersonale")
    public String redigerePersonale(@ModelAttribute Staff staff, Model model){

        bookingService.updateStaff(staff);
        log.info("redigerePersonale called..." + staff.getStaffId());
        model.addAttribute("", bookingService.getStaff());

        return REDIRECT + STAFF;
    }

    @GetMapping("/staff")
    public String staff(Model model){
        log.info("staff called...");

        List<Staff> staffs = bookingService.getStaff();
        model.addAttribute("staffs", staffs);

        return STAFF;

    }
    @PostMapping("/staff")
    public String staff(@ModelAttribute Staff staff, Model model){
        log.info("Staff called...");

        model.addAttribute("staffs", bookingService.getStaff());

        return STAFF;
    }

    @GetMapping("/deleteStaff/{id}")
    public String deleteStaff(@PathVariable("staffId") int staffId, Model model){
        log.info("deleteStaff with called with id :" + staffId );

        model.addAttribute("staffs",bookingService.getStaffMember(staffId));
        String staffName = bookingService.getStaffMember(staffId).getStaffName();
        model.addAttribute("pageTitle", "Delete staff ("+ staffName + ")");

        return DELETESTAFF;

    }
    @PutMapping("/deleteStaff")
    public String deleteStaff(@ModelAttribute Staff staff, Model model){
        log.info("delete confirmed deleting staffmember with Id" + staff.getStaffId());
        int id = staff.getStaffId();

        bookingService.deleteStaffMember(id);

        model.addAttribute("staffs", bookingService.getStaff() );
        model.addAttribute("pageTitle", "Delete staffMember" );

        return REDIRECT;

    }
}


