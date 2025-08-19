package com.mb.postService.service.impl;

import com.mb.postService.dto.PostDto;
import com.mb.postService.exception.PostNotFoundException;
import com.mb.postService.model.Post;
import com.mb.postService.model.Tag;
import com.mb.postService.repository.PostRepository;
import com.mb.postService.repository.TagRepository;
import com.mb.postService.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
@Slf4j
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Override
    public PostDto createPost(PostDto postDto) {
        log.info("Creating post with title: {}", postDto.getTitle());

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
        log.debug("Created post with id: {}", savedPost.getId());

        return convertToDto(savedPost);
    }

    @Override
    public PostDto getPostById(Long id) {
        log.info("Fetching post with id: {}", id);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Post not found with id: {}", id);
                    return new PostNotFoundException("Post not found with id: " + id);
                });
        return convertToDto(post);
    }

    @Override
    public List<PostDto> getAllPosts() {
        log.info("Fetching all posts");
        List<Post> posts = postRepository.findAll();
        log.debug("Fetched {} posts", posts.size());
        List<PostDto> postDtos = new ArrayList<>();
        for (Post post : posts) {
            postDtos.add(convertToDto(post));
        }
        return postDtos;
    }

    @Override
    public List<PostDto> getPostsByAuthor(String author) {
        log.info("Fetching posts by author: {}", author);
        List<Post> posts = postRepository.findByAuthorOrderByCreatedAtDesc(author);
        log.debug("Fetched {} posts by author {}", posts.size(), author);
        List<PostDto> postDtos = new ArrayList<>();
        for (Post post : posts) {
            postDtos.add(convertToDto(post));
        }
        return postDtos;
    }

    @Override
    public List<PostDto> getPublishedPosts() {
        log.info("Fetching published posts");
        List<Post> posts = postRepository.findByIsPublishedTrueOrderByCreatedAtDesc();
        log.debug("Fetched {} published posts", posts.size());
        List<PostDto> postDtos = new ArrayList<>();
        for (Post post : posts) {
            postDtos.add(convertToDto(post));
        }
        return postDtos;
    }

    @Override
    public List<PostDto> getPostsByTag(String tag) {
        log.info("Fetching posts with tag: {}", tag);
        List<Post> posts = postRepository.findByTagOrderByCreatedAtDesc(tag);
        log.debug("Fetched {} posts with tag {}", posts.size(), tag);
        List<PostDto> postDtos = new ArrayList<>();
        for (Post post : posts) {
            postDtos.add(convertToDto(post));
        }
        return postDtos;
    }

    @Override
    public List<PostDto> searchPosts(String keyword) {
        log.info("Searching posts with keyword: {}", keyword);
        List<Post> posts = postRepository.searchByKeyword(keyword);
        log.debug("Found {} posts with keyword {}", posts.size(), keyword);
        List<PostDto> postDtos = new ArrayList<>();
        for (Post post : posts) {
            postDtos.add(convertToDto(post));
        }
        return postDtos;
    }

    @Override
    public List<String> getAllTags() {
        log.info("Fetching all tags");
        return postRepository.findAllTags();
    }

    @Override
    public PostDto updatePost(Long id, PostDto postDto) {
        log.info("Updating post with id: {}", id);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot update, post not found with id: {}", id);
                    return new PostNotFoundException("Post not found with id: " + id);
                });

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
        log.debug("Updated post with id: {}", updatedPost.getId());

        return convertToDto(updatedPost);
    }

    @Override
    public void deletePost(Long id) {
        log.info("Deleting post with id: {}", id);

        if (!postRepository.existsById(id)) {
            log.warn("Cannot delete, post not found with id: {}", id);
            throw new PostNotFoundException("Post not found with id: " + id);
        }

        postRepository.deleteById(id);
        log.debug("Deleted post with id: {}", id);
    }

    @Override
    public void deletePostsByAuthor(String author) {
        log.warn("Deleting all posts by author: {}", author);
        List<Post> posts = postRepository.findByAuthorOrderByCreatedAtDesc(author);
        postRepository.deleteAll(posts);
        log.debug("Deleted {} posts by author {}", posts.size(), author);
    }

    @Override
    public PostDto publishPost(Long id) {
        log.info("Publishing post with id: {}", id);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot publish, post not found with id: {}", id);
                    return new PostNotFoundException("Post not found with id: " + id);
                });
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        Post publishedPost = postRepository.save(post);
        log.debug("Published post with id: {}", publishedPost.getId());
        return convertToDto(publishedPost);
    }

    @Override
    public PostDto unpublishPost(Long id) {
        log.info("Unpublishing post with id: {}", id);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot unpublish, post not found with id: {}", id);
                    return new PostNotFoundException("Post not found with id: " + id);
                });
        post.setPublished(false);
        post.setPublishedAt(null);
        Post unpublishedPost = postRepository.save(post);
        log.debug("Unpublished post with id: {}", unpublishedPost.getId());
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
        log.debug("Fetching or creating tag: {}", tagName);
        Tag existingTag = tagRepository.findByName(tagName);
        if (existingTag != null) {
            return existingTag;
        }

        Tag newTag = new Tag();
        newTag.setName(tagName);
        newTag.setCreatedAt(LocalDateTime.now());
        newTag.setUpdatedAt(LocalDateTime.now());
        Tag savedTag = tagRepository.save(newTag);
        log.debug("Created new tag with id: {}", savedTag.getId());
        return savedTag;
    }
}