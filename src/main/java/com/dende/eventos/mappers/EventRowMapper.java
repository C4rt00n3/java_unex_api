package com.dende.eventos.mappers;

import com.dende.eventos.entities.Event;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import repositories.util.RowMapper;

public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(String[] row) {
        Event event = new Event();
        event.setId(Long.valueOf(row[0]));
        event.setOrganizerId(Long.valueOf(row[1]));
        event.setParentEventId(row[2] == null ? null : Long.valueOf(row[2]));
        event.setTitle(row[3]);
        event.setDescription(row[4]);
        event.setWebPage(row[5]);
        event.setEventType(row[6]);
        event.setModality(row[7]);
        event.setLocation(row[8]);
        event.setStartDate(LocalDateTime.parse(row[9].replace(" ", "T")));
        event.setEndDate(LocalDateTime.parse(row[10].replace(" ", "T")));
        event.setMaximumCapacity(Integer.valueOf(row[11]));
        event.setTicketPrice(new BigDecimal(row[12]));
        event.setRefundTicket("1".equals(row[13]) || "true".equalsIgnoreCase(row[13]));
        event.setRefundFee(new BigDecimal(row[14]));
        event.setActive("1".equals(row[15]) || "true".equalsIgnoreCase(row[15]));
        event.setCreatedAt(LocalDateTime.parse(row[16].replace(" ", "T")));
        return event;
    }
}
