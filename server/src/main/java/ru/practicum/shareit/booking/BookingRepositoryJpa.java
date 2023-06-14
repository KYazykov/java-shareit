package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepositoryJpa extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerOrderByStartTimeDesc(User user, Pageable pageable);


    @Query("select b from Booking b " +
            "where b.booker = ?1 and b.startTime < ?2 and b.endTime > ?3 " +
            "order by b.startTime DESC")
    List<Booking> findAllBookingsForBookerWithStartAndEndTime(
            User user, LocalDateTime dateTime1, LocalDateTime dateTime2, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner = ?1 and b.startTime < ?2 and b.endTime > ?3 " +
            "order by b.startTime DESC")
    List<Booking> findAllBookingsItemByForOwnerWithStartAndEndTime(
            User user, LocalDateTime dateTime1, LocalDateTime dateTime2, Pageable pageable);

    List<Booking> findAllByBookerAndEndTimeIsBeforeOrderByStartTimeDesc(
            User user, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findAllByBookerAndStartTimeIsAfterOrderByStartTimeDesc(
            User user, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findAllByBookerAndBookingStatusEqualsOrderByStartTimeDesc(
            User booker, BookingStatus bookingStatus, Pageable pageable);

    List<Booking> findAllByItem_OwnerOrderByStartTimeDesc(User userId, Pageable pageable);


    List<Booking> findAllByItem_OwnerAndStartTimeBeforeAndEndTimeAfterOrderByStartTimeDesc(
            User user, LocalDateTime dateTime1, LocalDateTime dateTime2);

    @Query("select b from Booking b where b.item.owner = ?1 and b.endTime < ?2 order by b.startTime DESC")
    List<Booking> findAllByItem_OwnerAndEndTimeIsBeforeOrderByStartTimeDesc(
            User user, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findAllByItem_OwnerAndStartTimeIsAfterOrderByStartTimeDesc(
            User user, LocalDateTime localDateTime, Pageable pageable);


    List<Booking> findAllByItem_OwnerAndBookingStatusEqualsOrderByStartTimeDesc(
            User user, BookingStatus bookingStatus, Pageable pageable);

    List<Booking> findAllByItemOrderByStartTimeDesc(Item item);

    Booking save(Booking booking);

    Optional<Booking> findById(Long bookingId);
}
