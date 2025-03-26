package dao;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentDAO {
    private Long id;
    private String text;
    private Long userId;
    private Long postId;

    @JsonProperty("id")
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @JsonProperty("text")
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    @JsonProperty("userId")
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    @JsonProperty("postId")
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
}
