package com.undyingideas.thor.skafottet.firebase.DTO;

/**
 * Created on 05-01-2016, 12:13.
 * Project : skafottet
 * @author theis
 */
class LobbyKeyDTO {

    private String key;

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
