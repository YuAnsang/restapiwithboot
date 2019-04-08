package com.github.asyu.restapiwithboot.events;

import lombok.*;

import javax.persistence.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.asyu.restapiwithboot.accounts.Account;
import com.github.asyu.restapiwithboot.accounts.AccountSerializer;
import java.time.LocalDateTime;

@Getter @Setter
@Builder @NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
public class Event {

    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 이게 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT;
    @ManyToOne
    @JsonSerialize(using = AccountSerializer.class)
    private Account manager;
    
    public void update() {
        if (this.basePrice == 0 && this.getMaxPrice() == 0) {
            this.free = true;
        } else {
            this.free = false;
        }
        
        if (this.location == null || this.location.isBlank()) {
            this.offline = false;
        } else {
            this.offline = true;
        }
    }
}
