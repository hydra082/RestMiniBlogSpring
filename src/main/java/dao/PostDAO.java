package dao;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostDAO {
    private Long id;
    private String title;
    private String content;
    private Long userId;

    @JsonProperty("id")
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @JsonProperty("title")
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    @JsonProperty("content")
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    @JsonProperty("userId")
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
