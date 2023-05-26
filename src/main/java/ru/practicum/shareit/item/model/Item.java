package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;


@Entity
@Table(name = "items")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;
    @NotBlank(message = "Поле логина не может быть пустым.")
    @EqualsAndHashCode.Exclude
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Поле логина не может быть пустым.")
    @EqualsAndHashCode.Exclude
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "is_available", nullable = false)
    private Boolean available;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnore
    private User owner;

    @Column(name = "request_id")
    private Long requestId;  //

    @OneToMany(mappedBy = "item")
    @JsonIgnore
    private List<Booking> bookings;

    @OneToMany(mappedBy = "item")

    private List<Comment> comments;
}
