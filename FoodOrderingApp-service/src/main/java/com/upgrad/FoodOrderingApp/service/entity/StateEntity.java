package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "state")
@NamedQueries({
        @NamedQuery(name = "getAllStates", query = "select s from StateEntity s"),
        @NamedQuery(name = "getStateById", query = "select s from StateEntity s where s.uuid=:uuid")
})
public class StateEntity {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "state_name")
    private String stateName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

}
