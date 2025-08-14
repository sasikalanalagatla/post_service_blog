package com.mb.postService.service.impl;

import com.mb.postService.dto.PostDto;
import com.mb.postService.exception.PostNotFoundException;
import com.mb.postService.model.Post;
import com.mb.postService.model.Tag;
import com.mb.postService.repository.PostRepository;
import com.mb.postService.repository.TagRepository;
import com.mb.postService.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
public class PostServiceImpl implements PostService {
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private TagRepository tagRepository;
    
    @Override
    public PostDto createPost(PostDto postDto) {
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setExcerpt(postDto.getExcerpt());
        post.setContent(postDto.getContent());
        post.setAuthor(postDto.getAuthor());
        post.setPublished(postDto.isPublished());
        
        LocalDateTime now = LocalDateTime.now();
        post.setCreatedAt(now);
        post.setUpdatedAt(now);
        
        if (postDto.isPublished()) {
            post.setPublishedAt(now);
        }
        
        if (postDto.getTags() != null && !postDto.getTags().isEmpty()) {
            List<Tag> tags = new ArrayList<>();
            for (String tagName : postDto.getTags()) {
                if (tagName != null && !tagName.trim().isEmpty()) {
                    Tag tag = getOrCreateTag(tagName.trim());
                    tags.add(tag);
                }
            }
            post.setTags(tags);
        }
        
        Post savedPost = postRepository.save(post);
        return convertToDto(savedPost);
    }
    
    @Override
    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));
        return convertToDto(post);
    }
    
    @Override
    public List<PostDto> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        List<PostDto> postDtos = new ArrayList<>();
        for (Post post : posts) {
            postDtos.add(convertToDto(post));
        }
        return postDtos;
    }
    
    @Override
    public List<PostDto> getPostsByAuthor(String author) {
        List<Post> posts = postRepository.findByAuthorOrderByCreatedAtDesc(author);
        List<PostDto> postDtos = new ArrayList<>();
        for (Post post : posts) {
            postDtos.add(convertToDto(post));
        }
        return postDtos;
    }
    
    @Override
    public List<PostDto> getPublishedPosts() {
        List<Post> posts = postRepository.findByIsPublishedTrueOrderByCreatedAtDesc();
        List<PostDto> postDtos = new ArrayList<>();
        for (Post post : posts) {
            postDtos.add(convertToDto(post));
        }
        return postDtos;
    }
    
    @Override
    public List<PostDto> getPostsByTag(String tag) {
        List<Post> posts = postRepository.findByTagOrderByCreatedAtDesc(tag);
        List<PostDto> postDtos = new ArrayList<>();
        for (Post post : posts) {
            postDtos.add(convertToDto(post));
        }
        return postDtos;
    }
    
    @Override
    public List<PostDto> searchPosts(String keyword) {
        List<Post> posts = postRepository.searchByKeyword(keyword);
        List<PostDto> postDtos = new ArrayList<>();
        for (Post post : posts) {
            postDtos.add(convertToDto(post));
        }
        return postDtos;
    }
    
    @Override
    public List<String> getAllTags() {
        return postRepository.findAllTags();
    }
    
    @Override
    public PostDto updatePost(Long id, PostDto postDto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));
        
        post.setTitle(postDto.getTitle());
        post.setExcerpt(postDto.getExcerpt());
        post.setContent(postDto.getContent());
        post.setAuthor(postDto.getAuthor());
        post.setPublished(postDto.isPublished());
        
        if (postDto.getTags() != null) {
            List<Tag> tags = new ArrayList<>();
            for (String tagName : postDto.getTags()) {
                if (tagName != null && !tagName.trim().isEmpty()) {
                    Tag tag = getOrCreateTag(tagName.trim());
                    tags.add(tag);
                }
            }
            post.setTags(tags);
        }
        
        Post updatedPost = postRepository.save(post);
        return convertToDto(updatedPost);
    }
    
    @Override
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new PostNotFoundException("Post not found with id: " + id);
        }
        postRepository.deleteById(id);
    }
    
    @Override
    public void deletePostsByAuthor(String author) {
        List<Post> posts = postRepository.findByAuthorOrderByCreatedAtDesc(author);
        postRepository.deleteAll(posts);
    }
    
    @Override
    public PostDto publishPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));
        post.setPublished(true);
        Post publishedPost = postRepository.save(post);
        return convertToDto(publishedPost);
    }
    
    @Override
    public PostDto unpublishPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));
        post.setPublished(false);
        Post unpublishedPost = postRepository.save(post);
        return convertToDto(unpublishedPost);
    }
    
    private PostDto convertToDto(Post post) {
        List<String> tagNames = new ArrayList<>();
        if (post.getTags() != null) {
            for (Tag tag : post.getTags()) {
                tagNames.add(tag.getName());
            }
        }
        
        return new PostDto(
                post.getId(),
                post.getTitle(),
                post.getExcerpt(),
                post.getContent(),
                post.getAuthor(),
                post.getPublishedAt(),
                post.isPublished(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                tagNames
        );
    }

    private Tag getOrCreateTag(String tagName) {
        Tag existingTag = tagRepository.findByName(tagName);
        if (existingTag != null) {
            return existingTag;
        }
        
        Tag newTag = new Tag();
        newTag.setName(tagName);
        newTag.setCreatedAt(LocalDateTime.now());
        newTag.setUpdatedAt(LocalDateTime.now());
        return tagRepository.save(newTag);
    }
}
