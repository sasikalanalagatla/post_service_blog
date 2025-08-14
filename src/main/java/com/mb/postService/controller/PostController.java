package com.mb.postService.controller;

import com.mb.postService.dto.PostDto;
import com.mb.postService.service.impl.PostServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {
    
    @Autowired
    private PostServiceImpl postService;
    
    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto) {
        PostDto createdPost = postService.createPost(postDto);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
        PostDto post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }
    
    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts() {
        List<PostDto> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/author/name/{author}")
    public ResponseEntity<List<PostDto>> getPostsByAuthor(@PathVariable String author) {
        List<PostDto> posts = postService.getPostsByAuthor(author);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/published")
    public ResponseEntity<List<PostDto>> getPublishedPosts() {
        List<PostDto> posts = postService.getPublishedPosts();
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<PostDto>> getPostsByTag(@PathVariable String tag) {
        List<PostDto> posts = postService.getPostsByTag(tag);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<PostDto>> searchPosts(@RequestParam String keyword) {
        List<PostDto> posts = postService.searchPosts(keyword);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/tags")
    public ResponseEntity<List<String>> getAllTags() {
        List<String> tags = postService.getAllTags();
        return ResponseEntity.ok(tags);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(@PathVariable Long id, @RequestBody PostDto postDto) {
        PostDto updatedPost = postService.updatePost(id, postDto);
        return ResponseEntity.ok(updatedPost);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/author/{author}")
    public ResponseEntity<Void> deletePostsByAuthor(@PathVariable String author) {
        postService.deletePostsByAuthor(author);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/publish")
    public ResponseEntity<PostDto> publishPost(@PathVariable Long id) {
        PostDto publishedPost = postService.publishPost(id);
        return ResponseEntity.ok(publishedPost);
    }
    
    @PutMapping("/{id}/unpublish")
    public ResponseEntity<PostDto> unpublishPost(@PathVariable Long id) {
        PostDto unpublishedPost = postService.unpublishPost(id);
        return ResponseEntity.ok(unpublishedPost);
    }
}
