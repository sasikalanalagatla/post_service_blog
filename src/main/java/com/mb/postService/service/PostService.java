package com.mb.postService.service;

import com.mb.postService.dto.PostDto;

import java.util.List;

public interface PostService {
    
    PostDto createPost(PostDto postDto);
    
    PostDto getPostById(Long id);
    
    List<PostDto> getAllPosts();
    
    List<PostDto> getPostsByAuthor(String author);
    
    List<PostDto> getPublishedPosts();
    
    List<PostDto> getPostsByTag(String tag);
    
    List<PostDto> searchPosts(String keyword);
    
    List<String> getAllTags();
    
    PostDto updatePost(Long id, PostDto postDto);
    
    void deletePost(Long id);
    
    void deletePostsByAuthor(String author);
    
    PostDto publishPost(Long id);
    
    PostDto unpublishPost(Long id);
}
