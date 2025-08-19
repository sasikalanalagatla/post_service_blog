package com.mb.postService.controller;

import com.mb.postService.dto.PostDto;
import com.mb.postService.service.impl.PostServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@Slf4j
public class PostController {

    @Autowired
    private PostServiceImpl postService;

    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto) {
        log.info("Received request to create post: {}", postDto.getTitle());
        PostDto createdPost = postService.createPost(postDto);
        log.debug("Created post with id: {}", createdPost.getId());
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
        log.info("Fetching post with id: {}", id);
        PostDto post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts() {
        log.info("Fetching all posts");
        List<PostDto> posts = postService.getAllPosts();
        log.debug("Fetched {} posts", posts.size());
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/author/name/{author}")
    public ResponseEntity<List<PostDto>> getPostsByAuthor(@PathVariable String author) {
        log.info("Fetching posts by author: {}", author);
        List<PostDto> posts = postService.getPostsByAuthor(author);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/published")
    public ResponseEntity<List<PostDto>> getPublishedPosts() {
        log.info("Fetching published posts");
        List<PostDto> posts = postService.getPublishedPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<PostDto>> getPostsByTag(@PathVariable String tag) {
        log.info("Fetching posts with tag: {}", tag);
        List<PostDto> posts = postService.getPostsByTag(tag);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostDto>> searchPosts(@RequestParam String keyword) {
        log.info("Searching posts with keyword: {}", keyword);
        List<PostDto> posts = postService.searchPosts(keyword);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/tags")
    public ResponseEntity<List<String>> getAllTags() {
        log.info("Fetching all tags");
        List<String> tags = postService.getAllTags();
        return ResponseEntity.ok(tags);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(@PathVariable Long id, @RequestBody PostDto postDto) {
        log.info("Updating post with id: {}", id);
        PostDto updatedPost = postService.updatePost(id, postDto);
        log.debug("Updated post with id: {}", updatedPost.getId());
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        log.warn("Deleting post with id: {}", id);
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/author/{author}")
    public ResponseEntity<Void> deletePostsByAuthor(@PathVariable String author) {
        log.warn("Deleting all posts by author: {}", author);
        postService.deletePostsByAuthor(author);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<PostDto> publishPost(@PathVariable Long id) {
        log.info("Publishing post with id: {}", id);
        PostDto publishedPost = postService.publishPost(id);
        return ResponseEntity.ok(publishedPost);
    }

    @PutMapping("/{id}/unpublish")
    public ResponseEntity<PostDto> unpublishPost(@PathVariable Long id) {
        log.info("Unpublishing post with id: {}", id);
        PostDto unpublishedPost = postService.unpublishPost(id);
        return ResponseEntity.ok(unpublishedPost);
    }
}