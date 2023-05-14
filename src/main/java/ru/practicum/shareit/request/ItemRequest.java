package ru.practicum.shareit.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class ItemRequest {
    private int id;
    private int requestor;
    private String description;
    private LocalDate created;
}
