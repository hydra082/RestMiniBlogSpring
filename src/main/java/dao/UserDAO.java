package dao;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDAO {
    private Long id;
    private String name;

    @JsonProperty("id")
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @JsonProperty("name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
