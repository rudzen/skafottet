package com.undyingideas.thor.skafottet.firebase.DTO;

/**
 * Created by theis on 05-01-2016.
 */
public class LobbyKeyDTO {

    String key;

    public LobbyKeyDTO() {
    }

    public LobbyKeyDTO(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }
}
