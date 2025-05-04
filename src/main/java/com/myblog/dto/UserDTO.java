package com.myblog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class UserDTO {
    private Long id;
    private String name;
    private List<PostDTO> posts = new ArrayList<>();
    private List<CommentDTO> comments = new ArrayList<>();

    @JsonProperty("id")
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @JsonProperty("name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @JsonProperty("posts")
    public List<PostDTO> getPosts() { return posts; }
    public void setPosts(List<PostDTO> posts) { this.posts = posts; }

    @JsonProperty("comments")
    public List<CommentDTO> getComments() { return comments; }
    public void setComments(List<CommentDTO> comments) { this.comments = comments; }
}