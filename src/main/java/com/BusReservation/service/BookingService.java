package com.BusReservation.service;

import com.BusReservation.constants.*;
import com.BusReservation.dto.*;
import com.BusReservation.mapper.BookingMapper;
import com.BusReservation.model.Booking;
import com.BusReservation.model.Bus;
import com.BusReservation.model.AppUser;
import com.BusReservation.model.MasterLocation;
import com.BusReservation.repository.BookingRepo;
import com.BusReservation.utils.*;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepo bookingRepo;
    private final MasterLocationService masterLocationService;


    public ServiceResponse<ApiPageResponse<List<ManagementBookingDataDto>>> getAllBookingList(PaginationRequest request, String keyword) {
        Pageable pageable = PaginationRequest.getPageable(request);
        Integer pageNum = pageable.getPageNumber() + 1;

        if(keyword == null || keyword.isBlank()) {
            Page<Booking> pageData = bookingRepo.findAll(pageable);
            List<ManagementBookingDataDto> bookingList = pageData.stream().map(BookingMapper::bookingToManagementBookingDataDto).toList();

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Booking data found in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "The available Booking data list in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }

        if(keyword.startsWith("id_")) {
            Long bookingId = Long.parseLong(keyword.substring(3).trim());
            if(bookingId <= 0) return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid Booking ID. ID cannot be 0 or less than 0");

            Page<Booking> pageData = bookingRepo.findById(bookingId, pageable);
            List<ManagementBookingDataDto> bookingList = pageData.stream().map(BookingMapper::bookingToManagementBookingDataDto).toList();

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Booking data found for the given Booking ID " + bookingId + " in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "The Booking data found for the given Booking ID " + bookingId + " in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if(keyword.startsWith("mobile_")) {
            String mobileNum = keyword.substring(7).trim();
            if(mobileNum.matches(RegExPatterns.MOBILE_REGEX)) {
                Page<Booking> pageData = bookingRepo.findByAppUser_Mobile(mobileNum, pageable);
                List<ManagementBookingDataDto> bookingList = pageData.stream().map(BookingMapper::bookingToManagementBookingDataDto).toList();

                if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Booking data found for the given Mobile Number in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
                return new ServiceResponse<>(ResponseStatus.SUCCESS, "The Booking data found for the given Mobile Number in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            }
            return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid mobile number. Mobile number must starts with either 6 or 7 or 8 or 9 and followed by 9 digits");
        }
        else if (keyword.startsWith("cost_")) {
            String costStr = keyword.trim().substring(5);

            BigDecimal cost;
            if(costStr.matches(RegExPatterns.COST_WITH_DECIMAL_REGEX)) {
                cost = BigDecimal.valueOf(Double.parseDouble(costStr));
            }
            else if (costStr.matches(RegExPatterns.COST_WITHOUT_DECIMAL_REGEX)) {
                cost = BigDecimal.valueOf(Long.parseLong(costStr));
            }
            else return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid input. Please enter a valid amount like 123, 123.00, or 123.99.");

            Page<Booking> pageData = bookingRepo.findByBookingCost(cost, pageable);
            List<ManagementBookingDataDto> bookingList = BookingMapper.bookingToManagementBookingDataDtoList(pageData.getContent());

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Booking data found for the given Booking Cost in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "The Booking data found for the given Booking Cost in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if(keyword.startsWith("bus_")) {
            String busName = keyword.substring(4).trim();
            if(!busName.matches(RegExPatterns.BUS_NAME_REGEX)) return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid Bus name input. Bus name must start with a capital letter and can include letters, numbers, spaces, and symbols like @, (), /, &, .");

            Page<Booking> pageData = bookingRepo.findByBus_BusName(busName, pageable);
            List<ManagementBookingDataDto> bookingList = pageData.stream().map(BookingMapper::bookingToManagementBookingDataDto).toList();

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Booking data found for the given Bus Name in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "Booking data found for the given Bus Name in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if(keyword.startsWith("user_")) {
            String appUserName = keyword.substring(5).trim();
            if(!appUserName.matches(RegExPatterns.NAME_REGEX)) return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid App-User Name. Only alphabets and spaces are allowed (e.g., 'John Doe'). No numbers or special characters.");

            Page<Booking> pageData = bookingRepo.findByAppUser_Name(appUserName, pageable);
            List<ManagementBookingDataDto> bookingList = pageData.stream().map(BookingMapper::bookingToManagementBookingDataDto).toList();

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Booking data found for the given App-user Name in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "Booking data found for the given App-user Name in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if(keyword.startsWith("location_")) {
            String location = keyword.substring(9).trim();
            if(!location.matches(RegExPatterns.LOCATION_REGEX)) return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid City Name. Only letters, spaces, and hyphens are allowed. No numbers or special characters.");

            Optional<MasterLocation> masterLocationOptional = masterLocationService.fetchEntityByCity(location);
            if(masterLocationOptional.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "Given location '" + location + "' is not found in MasterLocation table.");

            Page<Booking> pageData = bookingRepo.findByLocations(masterLocationOptional.get(), pageable);
            List<ManagementBookingDataDto> bookingList = BookingMapper.bookingToManagementBookingDataDtoList(pageData.getContent());

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Booking data found for the given Location in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "Booking data found for the given Location in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if(keyword.matches(RegExPatterns.EMAIL_REGEX)) {
            Page<Booking> pageData = bookingRepo.findByAppUser_Email(keyword, pageable);
            List<ManagementBookingDataDto> bookingList = pageData.stream().map(BookingMapper::bookingToManagementBookingDataDto).toList();

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Booking data found for the given Email in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "The Booking data found for the given Email in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if(keyword.matches(RegExPatterns.BUS_TICKET_REGEX)) {
            Page<Booking> pageData = bookingRepo.findByBusTicket(keyword, pageable);
            List<ManagementBookingDataDto> bookingList = pageData.stream().map(BookingMapper::bookingToManagementBookingDataDto).toList();

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Booking data found for the given Bus Ticket in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "The Booking data found for the given Bus Ticket in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if(keyword.matches(RegExPatterns.TRANSACTION_REGEX)) {
            Page<Booking> pageData = bookingRepo.findByTransactionId(keyword, pageable);
            List<ManagementBookingDataDto> bookingList = pageData.stream().map(BookingMapper::bookingToManagementBookingDataDto).toList();

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Booking data found for the given Transaction ID in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "The Booking data found for the given Transaction ID in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if(keyword.matches(RegExPatterns.BUS_NUMBER_REGEX)) {
            Page<Booking> pageData = bookingRepo.findByBus_BusNumber(keyword, pageable);
            List<ManagementBookingDataDto> bookingList = pageData.stream().map(BookingMapper::bookingToManagementBookingDataDto).toList();

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Booking data found for the given Bus Number in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "The Booking data found for the given Bus Number in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if(keyword.matches(RegExPatterns.DATE_REGEX)) {
            DateTimeFormatter format = (keyword.contains("/")) ? DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT) : DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

            ServiceResponse<LocalDate> parsedDate = DateParserUtils.parseDate(keyword, format);
            if(parsedDate.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(parsedDate.getStatus(), parsedDate.getMessage());

            String dateStr = parsedDate.getData().toString();
            ServiceResponse<Page<Booking>> response = this.getBookingByDate(dateStr, pageable);

            Page<Booking> pageData = response.getData();
            List<ManagementBookingDataDto> bookingList = BookingMapper.bookingToManagementBookingDataDtoList(pageData.getContent());

            if(response.getStatus() == ResponseStatus.NOT_FOUND) return new ServiceResponse<>(response.getStatus(), response.getMessage(), new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(response.getStatus(), response.getMessage(), new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        return this.getByAnyKeyword(keyword, pageable);
    }


    @Transactional
    public ServiceResponse<BookingPreviewDto> createNewBooking(AppUser appUser, Bus bookedBus, @Valid BookingDto bookingDto, LocalDate date) {
        if(bookingDto.getSeatsBooked() >= 1) {
            try {
                if(bookedBus.getAvailableSeats() >= bookingDto.getSeatsBooked()) {
                    Long remaining = bookedBus.getAvailableSeats() - bookingDto.getSeatsBooked();

                    if(remaining < 0) return new ServiceResponse<>(ResponseStatus.CONFLICT, "Seats just got booked by others. Please try again new booking.");

                    bookedBus.setAvailableSeats(remaining);
                    Booking booking = BookingMapper.newBooking(appUser,bookedBus, bookingDto, date);

                    Booking newBooking = bookingRepo.save(booking);
                    log.info("NEW BOOKING: Booking with Booking ID: {} for User ID: {}, Name: {} was created successfully.", newBooking.getId(), newBooking.getAppUser().getId(), newBooking.getAppUser().getName());
                    return new ServiceResponse<>(ResponseStatus.SUCCESS, "Booking created successfully in BookMyRide. Please click 'Continue' to review your trip.", BookingMapper.getBookingPreview(newBooking));
                }
                log.info("NEW BOOKING ATTEMPT FAILED: Requested seats ({}) exceed available seats ({}) on Bus number {} for User {}-{}.", bookingDto.getSeatsBooked(), bookedBus.getAvailableSeats(), bookedBus.getBusNumber(), appUser.getId(), appUser.getName());
                return new ServiceResponse<>(ResponseStatus.INSUFFICIENT, "Requested number of seats exceeds availability. Only " + bookedBus.getAvailableSeats() + " seat(s) left. Please try other buses");
            }
            catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
                log.info("OPTIMISTIC LOCK -> NEW BOOKING: Booking tried by user {}-{} could not completed due to their chosen seats from Bus number {} were booked by others.", bookingDto.getName(), bookingDto.getMobile(), bookingDto.getBusNumber());
                throw e;
            }
            catch (Exception e) {
                log.warn("INTERNAL ERROR -> NEW BOOKING failed: {}", e.getMessage());
                throw e;
            }
        }
        log.info("NEW BOOKING ATTEMPT FAILED: User {}-{} requested {} seat(s), which is below the minimum allowed (1).", appUser.getId(), appUser.getName(), bookingDto.getSeatsBooked());
        return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "To make a successful booking, Booked seats should be atleast 1 or more");
    }


    @Transactional
    public ServiceResponse<BookingSummaryDto> continueBooking(Long bookingId) {
        Optional<Booking> booking = bookingRepo.findById(bookingId);
        if(booking.isPresent()) {
            LocalDateTime expiresAt = booking.get().getBookingExpiresAt();
            Booking continueBooking = booking.get();
            if(continueBooking.getBookingStatus() == BookingStatus.PENDING && continueBooking.getPaymentStatus() == PaymentStatus.UNPAID) {
                if(expiresAt.isAfter(LocalDateTime.now())) {
                    continueBooking.setBookingStatus(BookingStatus.PROCESSING);
                    continueBooking.setPaymentStatus(PaymentStatus.PENDING);
                    continueBooking.setBusTicket(null);
                    continueBooking.setTransactionId(null);
                    Booking currentBooking = bookingRepo.save(continueBooking);
                    BookingSummaryDto bookingSummary = BookingMapper.bookingToBookingSummary(currentBooking);

                    log.info("CONTINUE BOOKING: Booking with Booking ID: {} for User ID: {}, Name: {} was been continued successfully.", continueBooking.getId(), continueBooking.getAppUser().getId(), continueBooking.getAppUser().getName());
                    return new ServiceResponse<>(ResponseStatus.SUCCESS, "Here is your updated booking summary. Please proceed to payment to confirm your reservation.", bookingSummary);
                }

                try {
                    bookingRepo.save(BookingMapper.bookingExpiredBeforeContinue(continueBooking));
                    log.info("CONTINUE BOOKING: Booking with Booking ID: {} has expired at {}", continueBooking.getId(), continueBooking.getBookingExpiresAt());
                    return new ServiceResponse<>(ResponseStatus.TIMEOUT, "Oops! Your booking has expired, and couldn't continue the process. Please make a new booking to continue.");
                }
                catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
                    log.warn("OPTIMISTIC LOCK -> CONTINUE BOOKING: Booking with Booking ID {} has been expired at {}, that was detected and had modified by autoExpiredDetection().", continueBooking.getId(), continueBooking.getBookingExpiresAt());
                    throw e;
                }
                catch (Exception e) {
                    log.warn("INTERNAL ERROR -> CONTINUE BOOKING failed: {}", e.getMessage());
                    throw e;
                }
            }
            else if(continueBooking.getBookingStatus() == BookingStatus.PROCESSING) return new ServiceResponse<>(ResponseStatus.FORBIDDEN, "This booking cannot be continued again. It has been processed.");
            else if(continueBooking.getBookingStatus() == BookingStatus.CANCELLED) return new ServiceResponse<>(ResponseStatus.FORBIDDEN, "This booking cannot be continued. It has been canceled.");
            else if(continueBooking.getBookingStatus() == BookingStatus.EXPIRED) return new ServiceResponse<>(ResponseStatus.FORBIDDEN, "This booking cannot be continued. It has already been expired.");
            return new ServiceResponse<>(ResponseStatus.FORBIDDEN, "This booking cannot be continued. It has already been confirmed.");
        }
        return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "Booking data not found for given Booking ID " + bookingId);
    }


    @Transactional
    public ServiceResponse<BookingFinalInfoDto> confirmBooking(Long bookingId, PaymentMethod paymentMethod) {
        Optional<Booking> booking = bookingRepo.findById(bookingId);
        if(booking.isPresent()) {
            LocalDateTime expiresAt = booking.get().getBookingExpiresAt();
            Booking confirmBooking = booking.get();
            if(confirmBooking.getBookingStatus() == BookingStatus.PROCESSING && confirmBooking.getPaymentStatus() == PaymentStatus.PENDING) {
                if(expiresAt.isAfter(LocalDateTime.now())) {
                    confirmBooking.setPaymentMethod(paymentMethod);
                    confirmBooking.setPaymentStatus(PaymentStatus.PAID);
                    confirmBooking.setBookingStatus(BookingStatus.CONFIRMED);
                    confirmBooking.setBusTicket(UniqueGenerationUtils.generateTravelTicket(12, confirmBooking.getId()));
                    confirmBooking.setTransactionId(UniqueGenerationUtils.generateTransactionId(20, confirmBooking.getId()));
                    confirmBooking.setBookingExpiresAt(null);

                    Booking savedBooking = bookingRepo.save(confirmBooking);

                    log.info("CONFIRM BOOKING: Booking ID {} has been confirmed successfully for the User {}-{}", confirmBooking.getId(), confirmBooking.getAppUser().getId(), confirmBooking.getAppUser().getName());
                    return new ServiceResponse<>(ResponseStatus.SUCCESS, "Payment successful! Your booking has been confirmed. Thank you for choosing BookMyRide.", BookingMapper.finalBookingSummary(savedBooking));
                }

                try {
                    bookingRepo.save(BookingMapper.bookingExpired(confirmBooking));
                    log.info("CONFIRM BOOKING: Booking with Booking ID: {} has expired at {}", confirmBooking.getId(), confirmBooking.getBookingExpiresAt());
                    return new ServiceResponse<>(ResponseStatus.TIMEOUT, "Oops! Your booking has expired, and the payment couldn’t be processed. Please make a new booking to continue.");
                }
                catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
                    log.warn("OPTIMISTIC LOCK -> CONFIRM BOOKING: Booking with Booking ID {} has been expired at {}, that was detected and had modified by autoExpiredDetection().", confirmBooking.getId(), confirmBooking.getBookingExpiresAt());
                    throw e;
                }
                catch (Exception e) {
                    log.warn("INTERNAL ERROR -> CONFIRM BOOKING failed: {}", e.getMessage());
                    throw e;
                }
            }
            else if(confirmBooking.getBookingStatus() == BookingStatus.PENDING) return new ServiceResponse<>(ResponseStatus.FORBIDDEN, "This booking cannot be confirmed. It is no longer eligible for confirmation.");
            else if(confirmBooking.getBookingStatus() == BookingStatus.CANCELLED) return new ServiceResponse<>(ResponseStatus.FORBIDDEN, "This booking cannot be confirmed. It has been cancelled..");
            else if(confirmBooking.getBookingStatus() == BookingStatus.EXPIRED) return new ServiceResponse<>(ResponseStatus.FORBIDDEN, "This booking cannot be confirmed. It has been expired and no longer eligible for confirmation.");
            return new ServiceResponse<>(ResponseStatus.FORBIDDEN, "This booking cannot be confirmed. It has already been confirmed.");
        }
        return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No booking data found for given Booking ID " + bookingId);
    }


    @Transactional
    public ServiceResponse<String> cancelBooking(Long bookingId) {
        Optional<Booking> existedBooking = bookingRepo.findById(bookingId);
        if(existedBooking.isPresent()) {
            Booking booking = existedBooking.get();

            if(booking.getBookingStatus() == BookingStatus.PENDING || booking.getBookingStatus() == BookingStatus.PROCESSING) {
                try {
                    booking.setBookingStatus(BookingStatus.CANCELLED);
                    booking.setPaymentStatus(PaymentStatus.UNPAID);
                    booking.setPaymentMethod(PaymentMethod.NONE);
                    booking.setBusTicket(null);
                    booking.setTransactionId(null);
                    booking.setCanceledAt(LocalDateTime.now());

                    Bus bus = booking.getBus();
                    bus.setAvailableSeats(bus.getAvailableSeats() + booking.getSeatsBooked());
                    bookingRepo.save(booking);

                    log.info("CANCEL BOOKING: Booking ID {} has been cancelled successfully for the User {}-{}", booking.getId(), booking.getAppUser().getId(), booking.getAppUser().getName());
                    String str = "Booking with ID " + booking.getId() + " has been cancelled successfully. You're aAlways welcome to book your next trip with BooMyRide!";
                    return new ServiceResponse<>(ResponseStatus.SUCCESS, str);
                }
                catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
                    log.warn("OPTIMISTIC LOCK -> CANCEL BOOKING: Booking with Booking ID {} has been expired at {}, that was detected and had modified by autoExpiredDetection().", booking.getId(), booking.getBookingExpiresAt());
                    throw e;
                }
                catch (Exception e) {
                    log.warn("INTERNAL ERROR -> CANCEL BOOKING failed: {}", e.getMessage());
                    throw e;
                }
            }
            return new ServiceResponse<>(ResponseStatus.FORBIDDEN,  "You can only cancel bookings that haven’t been confirmed yet. Booking ID " + bookingId + " is no longer eligible for cancellation.");
        }
        return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No booking data found for given Booking ID " + bookingId);
    }


   // Handle expired booking when status is still PENDING or PROCESSING
    @Transactional
    @Scheduled(fixedRate = 1000 * 60)
    public void autoExpiredDetection() {
        List<Booking> bookings = bookingRepo.findByBookingStatus(BookingStatus.PENDING, BookingStatus.PROCESSING);

        if(bookings.isEmpty()) return;

        for(Booking booking : bookings) {
            try {
                if((booking.getBookingStatus() == BookingStatus.PENDING && booking.getBookingExpiresAt().isBefore(LocalDateTime.now()))) {
                    bookingRepo.save(BookingMapper.bookingExpiredBeforeContinue(booking));
                    log.info("AUTO-EXPIRED DETECTION: Booking ID {} with booking status PENDING & expired at {} was been auto-detected by autoExpiredDetection() and modified as EXPIRED.", booking.getId(), booking.getBookingExpiresAt());
                }
                else if ((booking.getBookingStatus() == BookingStatus.PROCESSING && booking.getBookingExpiresAt().isBefore(LocalDateTime.now()))) {
                    bookingRepo.save(BookingMapper.bookingExpired(booking));
                    log.info("AUTO-EXPIRED DETECTION: Booking ID {} with status PROCESSING & expired at {} was been auto-detected by autoExpiredDetection() and modified as EXPIRED.", booking.getId(), booking.getBookingExpiresAt());
                }
            }
            catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
                log.warn("OPTIMISTIC LOCK -> AUTO-EXPIRED DETECTION: Booking ID {} may processed by either cancellation or confirmBooking method.", booking.getId());
            }
            catch (Exception e) {
                log.warn("INTERNAL ERROR -> AUTO-EXPIRED DETECTION failed: {}", e.getMessage());
            }
        }
    }


    public ServiceResponse<Page<Booking>> getBookingByDate(String date, Pageable pageable) {
        Integer pageNum = pageable.getPageNumber() + 1;
        Page<Booking> pageData = bookingRepo.findBookingByAnyDate(date, pageable);

        if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Booking data found for the given Date " + date + " in this page " + pageNum, pageData);
        return new ServiceResponse<>(ResponseStatus.SUCCESS, "The Booking data found for the given Date " + date + " in this page " + pageNum, pageData);
    }


    public ServiceResponse<ApiPageResponse<List<ManagementBookingDataDto>>> getByAnyKeyword(String keyword, Pageable pageable) {
        Integer pageNum = pageable.getPageNumber() + 1;

        if(keyword.matches(RegExPatterns.BOOKING_STATUS_REGEX)) {
            ServiceResponse<BookingStatus> bookingStatusEnum = ParsingEnumUtils.getParsedEnumType(BookingStatus.class, keyword, "Booking Status");
            if(bookingStatusEnum.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(bookingStatusEnum.getStatus(), bookingStatusEnum.getMessage());

            Page<Booking> pageData = bookingRepo.findByBookingStatus(bookingStatusEnum.getData(), pageable);
            List<ManagementBookingDataDto> bookingList = BookingMapper.bookingToManagementBookingDataDtoList(pageData.getContent());

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Booking data found for the given Booking Status in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "Booking data found for the given Booking Status in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if(keyword.matches(RegExPatterns.PAYMENT_STATUS_REGEX)) {
            ServiceResponse<PaymentStatus> paymentStatusEnum = ParsingEnumUtils.getParsedEnumType(PaymentStatus.class, keyword, "Payment Status");
            if(paymentStatusEnum.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(paymentStatusEnum.getStatus(), paymentStatusEnum.getMessage());

            Page<Booking> pageData =  bookingRepo.findByPaymentStatus(paymentStatusEnum.getData(), pageable);
            List<ManagementBookingDataDto> bookingList = BookingMapper.bookingToManagementBookingDataDtoList(pageData.getContent());

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Booking data found for the given Payment Status in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "Booking data found for the given Payment Status in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if(keyword.matches(RegExPatterns.PAYMENT_METHOD_INTERNAL_REGEX)) {
            ServiceResponse<PaymentMethod> paymentMethodEnum = ParsingEnumUtils.getParsedEnumType(PaymentMethod.class, keyword, "Payment Method");
            if(paymentMethodEnum.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(paymentMethodEnum.getStatus(), paymentMethodEnum.getMessage());

            Page<Booking> pageData = bookingRepo.findByPaymentMethod(paymentMethodEnum.getData(), pageable);
            List<ManagementBookingDataDto> bookingList = BookingMapper.bookingToManagementBookingDataDtoList(pageData.getContent());

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Booking data found for the given Payment Method in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "Booking data found for the given Payment Method in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if(keyword.matches(RegExPatterns.BUS_TYPE_REGEX)) {
            ServiceResponse<BusType> busTypeEnum = ParsingEnumUtils.getParsedEnumType(BusType.class, keyword, "Bus Type");
            if(busTypeEnum.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(busTypeEnum.getStatus(), busTypeEnum.getMessage());

            Page<Booking> pageData = bookingRepo.findByBus_BusType(busTypeEnum.getData(), pageable);
            List<ManagementBookingDataDto> bookingList = BookingMapper.bookingToManagementBookingDataDtoList(pageData.getContent());

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Booking data found for the given Bus Type in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "Booking data found for the given Bus Type in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if(keyword.matches(RegExPatterns.ROLE_REGEX)) {
            ServiceResponse<Role> roleEnum = ParsingEnumUtils.getParsedEnumType(Role.class, keyword, "Role");
            if(roleEnum.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(roleEnum.getStatus(), roleEnum.getMessage());

            Page<Booking> pageData = bookingRepo.findByAppUser_Role(roleEnum.getData(), pageable);
            List<ManagementBookingDataDto> bookingList = BookingMapper.bookingToManagementBookingDataDtoList(pageData.getContent());

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Booking data found for the given AppUser's Role in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "Booking data found for the given AppUser's Role in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if(keyword.matches(RegExPatterns.GENDER_REGEX)) {
            ServiceResponse<Gender> genderEnum = ParsingEnumUtils.getParsedEnumType(Gender.class, keyword, "Gender");
            if(genderEnum.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(genderEnum.getStatus(), genderEnum.getMessage());

            Page<Booking> pageData = bookingRepo.findByAppUser_Gender(genderEnum.getData(), pageable);
            List<ManagementBookingDataDto> bookingList = pageData.stream().map(BookingMapper::bookingToManagementBookingDataDto).toList();

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Booking data found for the given AppUser's Gender in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "Booking data found for the given AppUser's Gender in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if(keyword.matches(RegExPatterns.BASIC_BUS_TYPE_REGEX)) {
            if(keyword.matches(RegExPatterns.AC_TYPE_REGEX)) {
                ServiceResponse<AcType> acTypeEnum = ParsingEnumUtils.getParsedEnumType(AcType.class, keyword, "AC Type");
                if(acTypeEnum.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(acTypeEnum.getStatus(), acTypeEnum.getMessage());

                Page<Booking> pageData = bookingRepo.findByBus_AcType(acTypeEnum.getData(), pageable);
                List<ManagementBookingDataDto> bookingList = pageData.stream().map(BookingMapper::bookingToManagementBookingDataDto).toList();

                if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Booking data found for the given Bus Type in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
                return new ServiceResponse<>(ResponseStatus.SUCCESS, "Booking data found for the given Bus Type in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            }
            else if(keyword.matches(RegExPatterns.SEAT_TYPE_REGEX)) {
                Page<Booking> pageData = bookingRepo.findByBus_SeatType(NormalizeStringUtils.getNormalize(keyword), pageable);
                List<ManagementBookingDataDto> bookingList = pageData.stream().map(BookingMapper::bookingToManagementBookingDataDto).toList();

                if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Booking data found for the given Bus Type in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
                return new ServiceResponse<>(ResponseStatus.SUCCESS, "Booking data found for the given Bus Type in this page " + pageNum, new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            }
            return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid filter keyword for Bus Type. Please use valid options such as AC, Non AC, Sleeper, or Seater.");
        }
        return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Booking data found for the given Input in this page " + pageNum, new ApiPageResponse<>(List.of(), 0, 0L, pageable.getPageNumber(), pageable.getPageSize(), true, true));
    }


    @Transactional
    public ServiceResponse<?> editUserBookingData(Long bookingId, @Valid EditBookingDto bookingDto, AppUserService appUserService, LocalDate date) {
        Optional<Booking> existingBooking = bookingRepo.findById(bookingId);

        if(existingBooking.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Booking data found for the given Booking ID " + bookingId);
        Booking booking = existingBooking.get();

        Long originalSeatCount = booking.getSeatsBooked();
        Long newSeatCount = bookingDto.getSeatsBooked();
        Long modifiedCount = newSeatCount - originalSeatCount;

        if(newSeatCount >= 1) {
            if(booking.getBookingStatus() == BookingStatus.PENDING && booking.getPaymentStatus() == PaymentStatus.UNPAID) {
                if(booking.getBus().getAvailableSeats() >= modifiedCount) {
                    try {
                        ServiceResponse<AppUser> updateResponse = appUserService.updateExistedAppUserData(booking.getAppUser(), bookingDto);
                        if(updateResponse.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(updateResponse.getStatus(), updateResponse.getMessage());
                        else if(updateResponse.getStatus() == ResponseStatus.CONFLICT) return new ServiceResponse<>(updateResponse.getStatus(), updateResponse.getMessage());
                        AppUser appUser = updateResponse.getData();

                        Booking editedBooking = BookingMapper.editedBooking(booking, appUser, date, newSeatCount, modifiedCount);
                        Booking updatedBooking = bookingRepo.save(editedBooking);

                        log.info("EDIT BOOKING -> Booking with Booking ID: {} has been edited successfully by User ID: {}, Name: {}.", updatedBooking.getId(), appUser.getId(), appUser.getName());
                        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Booking ID " + bookingId + " has been updated successfully.", BookingMapper.getBookingPreview(updatedBooking));
                    }
                    catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
                        log.warn("OPTIMISTIC LOCK -> EDIT BOOKING: Booking with Booking ID {} has been expired at {}, that was detected and had modified by autoExpiredDetection().", booking.getId(), booking.getBookingExpiresAt());
                    }
                    catch (Exception e) {
                        log.warn("INTERNAL ERROR -> EDIT BOOKING failed: {}", e.getMessage());
                    }
                }
                log.info("EDIT BOOKING ATTEMPT FAILED: Requested seats ({}) exceed available seats ({}) on Bus number {} for User {}-{}.", bookingDto.getSeatsBooked(), booking.getBus().getAvailableSeats(), booking.getBus().getBusNumber(), booking.getAppUser().getId(), booking.getAppUser().getName());
                return new ServiceResponse<>(ResponseStatus.INSUFFICIENT, "Requested number of seats exceeds availability. Only " + booking.getBus().getAvailableSeats() + " seat(s) left. Please choose a different bus or reduce the seat count.");
            }
            else if(booking.getBookingStatus() == BookingStatus.PROCESSING) return new ServiceResponse<>(ResponseStatus.FORBIDDEN, "This booking cannot be edit. It has already been processed..");
            else if(booking.getBookingStatus() == BookingStatus.CANCELLED) return new ServiceResponse<>(ResponseStatus.FORBIDDEN, "This booking cannot be edit. It has been cancelled..");
            else if(booking.getBookingStatus() == BookingStatus.EXPIRED) return new ServiceResponse<>(ResponseStatus.FORBIDDEN, "This booking cannot be edit. It has been expired and no longer eligible for editing.");
            return new ServiceResponse<>(ResponseStatus.FORBIDDEN, "This booking cannot be edit. It has already been confirmed..");
        }
        log.info("EDIT BOOKING ATTEMPT FAILED: User {}-{} requested {} seat(s), which is below the minimum allowed (1).", booking.getAppUser().getId(), booking.getAppUser().getName(), bookingDto.getSeatsBooked());
        return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "To proceed with the booking, you must select at least one seat.");
    }


    public Page<Booking> fetchUserBooking(Long id, Pageable pageable ) {
        return bookingRepo.getAllBookingData(id, pageable);
    }
}
