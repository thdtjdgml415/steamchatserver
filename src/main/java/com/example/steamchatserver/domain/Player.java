package com.example.steamchatserver.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Player {

    @Id
    private String steamId;

    private String personaName;
    private String profileUrl;
    private String avatar;
    private String avatarMedium;
    private String avatarFull;
    private int personaState;
    private int communityVisibilityState;
    private int profileState;
    private String realName;
    private String primaryClanId;
    private long timeCreated;
    private String locCountryCode;
    private String locStateCode;
    private int locCityId;
}
