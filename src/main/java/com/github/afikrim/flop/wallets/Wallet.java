package com.github.afikrim.flop.wallets;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.afikrim.flop.systemwallets.SystemWallet;
import com.github.afikrim.flop.userwallets.UserWallet;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.hateoas.RepresentationModel;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wallets")
@AllArgsConstructor
@NoArgsConstructor
public class Wallet extends RepresentationModel<Wallet> implements Serializable {

    private static final long serialVersionUID = 1901170291515847534L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "enabled", columnDefinition = "boolean default false", nullable = false)
    private Boolean enabled = false;

    @Transient
    private Long balance;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
    private List<UserWallet> users;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
    private List<SystemWallet> systems;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @JsonIgnore
    public List<UserWallet> getUsers() {
        return users;
    }

    public void setUsers(List<UserWallet> users) {
        this.users = users;
    }

    @JsonIgnore
    public List<SystemWallet> getSystems() {
        return systems;
    }

    public void setSystems(List<SystemWallet> systems) {
        this.systems = systems;
    }

    @JsonProperty("is_available")
    public Boolean getIsAvailable() {
        Long maxBalance = 0L;
        for (SystemWallet system: systems) {
            if (system.getBalance() > maxBalance) {
                maxBalance = system.getBalance();
            }
        }

        return maxBalance > 50000;
    }

    public Long getBalance() {
        Long totalBalance = 0L;
        for (SystemWallet system: systems) {
            totalBalance += system.getBalance();
        }

        return totalBalance;
    }

}
