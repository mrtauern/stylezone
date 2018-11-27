package com.stylezone.demo.repositories;

import com.stylezone.demo.models.Booking;
import com.stylezone.demo.models.BookingGroup;
import com.stylezone.demo.models.Holiday;
import com.stylezone.demo.models.Opening;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.logging.Logger;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Repository

public class BookingRepoImpl implements BookingRepo {
    Logger log = Logger.getLogger(BookingRepoImpl.class.getName());


    @Autowired
    JdbcTemplate template;

    @Override
    public Booking findBooking(int bookingId) {
        String sql = "SELECT * FROM booking WHERE BookingId = ?";
        RowMapper<Booking> rowMapper = new BeanPropertyRowMapper<>(Booking.class);

        Booking booking = template.queryForObject(sql, rowMapper, bookingId);


        return booking;
    }


    @Override
    public List<Booking> getBookings() {
        String sql = "SELECT * FROM Booking";
        return this.template.query(sql, new ResultSetExtractor<List<Booking>>() {

            @Override
            public List<Booking> extractData(ResultSet rs) throws SQLException, DataAccessException {
                int bookingId, bookingPhone, staffId;
                String bookingTime, bookingDate, bookingName, bookingComment;
                ArrayList<Booking> bookings = new ArrayList<>();

                while (rs.next()) {
                    bookingId = rs.getInt("bookingId");
                    bookingPhone = rs.getInt("bookingPhone");
                    staffId = rs.getInt("staffId");
                    bookingTime = rs.getString("bookingTime");
                    bookingDate = rs.getString("bookingDate");
                    bookingName = rs.getString("bookingName");
                    bookingComment = rs.getString("bookingComment");

                    bookings.add(new Booking(bookingId, bookingTime, bookingDate, bookingName, bookingPhone, bookingComment, staffId));
                }
                return bookings;
            }
        });
    }

    @Override
    public List<Booking> getSelectedBookings(String date, String timeStart, String timeEnd) {
        log.info("BookingRepo.getSelectedBookings("+date+", "+timeStart+", "+timeEnd+")");

        String sql = "SELECT * FROM Booking\n" +
                "WHERE bookingDate = STR_TO_DATE(?, '%d-%m-%Y')\n" +
                "AND bookingTime >= ?\n" +
                "AND bookingTime < ?\n" +
                "ORDER BY bookingTime ASC";
        return this.template.query(sql, new ResultSetExtractor<List<Booking>>() {

            @Override
            public List<Booking> extractData(ResultSet rs) throws SQLException, DataAccessException {
                int bookingId, bookingPhone, staffId;
                String bookingTime, bookingDate, bookingName, bookingComment;
                ArrayList<Booking> bookings = new ArrayList<>();

                while (rs.next()) {
                    bookingId = rs.getInt("bookingId");
                    bookingPhone = rs.getInt("bookingPhone");
                    staffId = rs.getInt("fk_staffId");
                    bookingTime = rs.getString("bookingTime");
                    bookingDate = rs.getString("bookingDate");
                    bookingName = rs.getString("bookingName");
                    bookingComment = rs.getString("bookingComment");

                    bookings.add(new Booking(bookingId, bookingTime, bookingDate, bookingName, bookingPhone, bookingComment, staffId));
                }
                return bookings;
            }
        }, date, timeStart, timeEnd);
    }

    @Override
    public List<BookingGroup> getBookingGroups(String date, String timeStart, String timeEnd) {
        log.info("BookingRepo.getBookingGroups("+date+", "+timeStart+", "+timeEnd+")");

        String sql = "SELECT HOUR(bookingTime) AS startTime, COUNT(bookingId) AS booked FROM Booking\n" +
                "WHERE bookingDate = STR_TO_DATE(?, '%d-%m-%Y')\n" +
                "AND bookingTime > ?\n" +
                "AND bookingTime < ?\n" +
                "GROUP BY HOUR(bookingTime)";

        return this.template.query(sql, new ResultSetExtractor<List<BookingGroup>>() {

            @Override
            public List<BookingGroup> extractData(ResultSet rs) throws SQLException, DataAccessException {
                int bookingGroupId, boookingGroupBooked, boookingGroupTotal;
                String bookingGroupStart,  bookingGroupEnd, bookingGroupDate;
                List<BookingGroup> bookingGroups = new ArrayList<>();
                //List<BookingGroup> bookingTemp = new ArrayList<>();

                while (rs.next()) {
                    bookingGroupStart = rs.getString("startTime");
                    bookingGroupStart = bookingGroupStart + ":00";
                    boookingGroupBooked = rs.getInt("booked");

                    bookingGroups.add(new BookingGroup(bookingGroupStart, boookingGroupBooked));
                }

                /*for (int i = Integer.parseInt(timeStart.substring(0,2)); i <= Integer.parseInt(timeEnd.substring(0,2)); i++){

                    boookingGroupTotal = 6;

                    bookingGroupDate = date;

                    bookingGroupStart = "" + i + ":00";
                    bookingGroupEnd = "" + (i+1) + ":00";


                    if(i == Integer.parseInt(timeStart.substring(0,2))){
                        int param = Integer.parseInt(timeStart.substring(3,5));
                        switch (param){
                            case 00:
                                boookingGroupTotal = 6;
                                break;
                            case 10:
                                boookingGroupTotal = 5;
                                bookingGroupStart = "" + i + ":10";
                                break;
                            case 20:
                                boookingGroupTotal = 4;
                                bookingGroupStart = "" + i + ":20";
                                break;
                            case 30:
                                boookingGroupTotal = 3;
                                bookingGroupStart = "" + i + ":30";
                                break;
                            case 40:
                                boookingGroupTotal = 2;
                                bookingGroupStart = "" + i + ":40";
                                break;
                            case 50:
                                boookingGroupTotal = 1;
                                bookingGroupStart = "" + i + ":50";
                                break;
                        }
                    }

                    if(i == Integer.parseInt(timeEnd.substring(0,2))){
                        int param = Integer.parseInt(timeEnd.substring(3,5));
                        switch (param){
                            case 00:
                                boookingGroupTotal = 0;
                                break;
                            case 10:
                                boookingGroupTotal = 1;
                                bookingGroupEnd = "" + i + ":10";
                                break;
                            case 20:
                                boookingGroupTotal = 2;
                                bookingGroupEnd = "" + i + ":20";
                                break;
                            case 30:
                                boookingGroupTotal = 3;
                                bookingGroupEnd = "" + i + ":30";
                                break;
                            case 40:
                                boookingGroupTotal = 4;
                                bookingGroupEnd = "" + i + ":40";
                                break;
                            case 50:
                                boookingGroupTotal = 5;
                                bookingGroupEnd = "" + i + ":50";
                                break;
                        }


                    }

                    boookingGroupBooked = 0;

                    for (BookingGroup temp: bookingTemp) {
                        if(i == Integer.parseInt(temp.getBookingGroupStart().substring(0,2))){
                            boookingGroupBooked = temp.getBoookingGroupBooked();
                        }
                    }

                    assert boookingGroupBooked <= boookingGroupTotal;

                    log.info("bookingGroupStart:" + bookingGroupStart + ", bookingGroupEnd;" + bookingGroupEnd + ", bookingGroupEnd;" + bookingGroupDate + ", boookingGroupBooked:" + boookingGroupBooked + ", boookingGroupTotal" + boookingGroupTotal);

                    bookingGroups.add(new BookingGroup(bookingGroupStart, bookingGroupEnd, bookingGroupDate, boookingGroupBooked, boookingGroupTotal));
                }*/
                return bookingGroups;
            }
        }, date, timeStart, timeEnd);
    }

    @Override
    public Booking createBooking(Booking booking) {


        return null;
    }

    @Override
    public Booking updateBooking(Booking booking) {
        return null;
    }

    @Override
    public void deleteBooking(int bookingId) {

    }

    @Override
    public Holiday findHoliday(int holidayId) {
        String sql = "SELECT * FROM holiday WHERE holidayId = ?";
        RowMapper<Holiday> rowMapper = new BeanPropertyRowMapper<>(Holiday.class);

        Holiday holiday = template.queryForObject(sql, rowMapper, holidayId);


        return holiday;
    }

    @Override
    public List<Holiday> getHolidays() {
        String sql = "SELECT * FROM Holiday";
        return this.template.query(sql, new ResultSetExtractor<List<Holiday>>() {

            @Override
            public List<Holiday> extractData(ResultSet rs) throws SQLException, DataAccessException {
                int holidayId;
                String holidayDate, holidayName;
                ArrayList<Holiday> holidays = new ArrayList<>();

                while (rs.next()) {
                    holidayId = rs.getInt("holidayId");
                    holidayDate = rs.getString("holidayDate");
                    holidayName = rs.getString("holidayName");


                    holidays.add(new Holiday(holidayId, holidayDate, holidayName));
                }
                return holidays;
            }
        });
    }

    @Override
    public Opening findOpening(int openingId) {
        String sql = "SELECT * FROM opening WHERE openingId = ?";
        RowMapper<Opening> rowMapper = new BeanPropertyRowMapper<>(Opening.class);

        Opening opening = template.queryForObject(sql, rowMapper, openingId);


        return opening;
    }

    @Override
    public List<Opening> getOpenings() {
        String sql = "SELECT * FROM opening";
        return this.template.query(sql, new ResultSetExtractor<List<Opening>>() {

            @Override
            public List<Opening> extractData(ResultSet rs) throws SQLException, DataAccessException {
                int openingId, openingTime, openingClose;
                String openingDay;
                ArrayList<Opening> openings = new ArrayList<>();

                while (rs.next()) {

                    openingId = rs.getInt("openingId");
                    openingDay = rs.getString("openingDay");
                    openingTime = rs.getInt("openingTime");
                    openingClose = rs.getInt("openingClose");

                    openings.add(new Opening(openingId, openingDay, openingTime, openingClose));
                }
                return openings;
            }
        });
    }
}


