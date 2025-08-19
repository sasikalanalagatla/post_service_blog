package com.mb.postService.controller;

import com.mb.postService.dto.PostDto;
import com.mb.postService.service.impl.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostControllerTest {

    @Mock
    private PostServiceImpl postService;

    @InjectMocks
    private PostController postController;

    public PostControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPost() {
        PostDto inputDto = new PostDto();
        inputDto.setTitle("Test Post");

        PostDto savedDto = new PostDto();
        savedDto.setId(1L);
        savedDto.setTitle("Test Post");

        when(postService.createPost(inputDto)).thenReturn(savedDto);

        ResponseEntity<PostDto> response = postController.createPost(inputDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Test Post", response.getBody().getTitle());

        verify(postService, times(1)).createPost(inputDto);
    }

    @Test
    void getPostById() {
        Long postId = 1L;
        PostDto post = new PostDto();
        post.setId(postId);
        post.setTitle("Test Post");

        when(postService.getPostById(postId)).thenReturn(post);

        ResponseEntity<PostDto> response = postController.getPostById(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(postService, times(1)).getPostById(postId);
    }

    @Test
    void getAllPosts() {
        PostDto post1 = new PostDto();
        post1.setId(1L);
        post1.setTitle("Post 1");

        PostDto post2 = new PostDto();
        post2.setId(2L);
        post2.setTitle("Post 2");

        List<PostDto> mockPosts = Arrays.asList(post1, post2);

        when(postService.getAllPosts()).thenReturn(mockPosts);

        ResponseEntity<List<PostDto>> response = postController.getAllPosts();

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertNotNull(response.getBody());

        verify(postService, times(1)).getAllPosts();
    }

    @Test
    void getPostsByAuthor() {
        String author = "demo";
        PostDto post1 = new PostDto();
        post1.setId(1L);
        post1.setTitle("Post 1");
        post1.setAuthor(author);

        PostDto post2 = new PostDto();
        post2.setId(2L);
        post2.setTitle("Post 2");
        post2.setAuthor(author);

        List<PostDto> mockPosts = Arrays.asList(post1, post2);

        when(postService.getPostsByAuthor(author)).thenReturn(mockPosts);

        ResponseEntity<List<PostDto>> response = postController.getPostsByAuthor(author);

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertNotNull(response.getBody());

        verify(postService,times(1)).getPostsByAuthor(author);

    }

    @Test
    void getPublishedPosts() {
        PostDto post1 = new PostDto();
        post1.setId(1L);
        post1.setTitle("Post 1");
        post1.setPublished(true);

        PostDto post2 = new PostDto();
        post2.setId(2L);
        post2.setTitle("Post 2");
        post2.setPublished(true);

        List<PostDto> mockPosts = Arrays.asList(post1, post2);

        when(postService.getPublishedPosts()).thenReturn(mockPosts);

        ResponseEntity<List<PostDto>> response = postController.getPublishedPosts();

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(2,response.getBody().size());
        assertNotNull(response.getBody());

        verify(postService, times(1)).getPublishedPosts();

    }

    @Test
    void getPostsByTag() {
        String tagName = "java";
        List<String> tags = Arrays.asList(tagName);
        PostDto post1 = new PostDto();
        post1.setId(1L);
        post1.setTitle("Post 1");
        post1.setTags(tags);

        PostDto post2 = new PostDto();
        post2.setId(2L);
        post2.setTitle("Post 2");
        post2.setTags(tags);

        List<PostDto> mockPosts = Arrays.asList(post1, post2);

        when(postService.getPostsByTag(tagName)).thenReturn(mockPosts);

        ResponseEntity<List<PostDto>> response = postController.getPostsByTag(tagName);

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertNotNull(response.getBody());

        verify(postService, times(1)).getPostsByTag(tagName);

    }

    @Test
    void searchPosts() {
        String keyword = "java";
        PostDto post1 = new PostDto();
        post1.setId(1L);
        post1.setTitle("java keywords");
        PostDto post2 = new PostDto();
        post2.setId(2L);
        post2.setTitle("java");

        List<PostDto> mockPosts = Arrays.asList(post1, post2);

        when(postService.searchPosts(keyword)).thenReturn(mockPosts);

        ResponseEntity<List<PostDto>> response = postController.searchPosts(keyword);

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertNotNull(response.getBody());

        verify(postService, times(1)).searchPosts(keyword);

    }

    @Test
    void getAllTags() {
        List<String> tags = Arrays.asList("java","python","css","html");

        when(postService.getAllTags()).thenReturn(tags);

        ResponseEntity<List<String>> response = postController.getAllTags();

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertNotNull(response.getBody());

        verify(postService, times(1)).getAllTags();
    }

    @Test
    void updatePost() {
        Long postID = 1L;

        PostDto updatedPost = new PostDto();
        updatedPost.setId(postID);
        updatedPost.setTitle("Java Intro");
        updatedPost.setPublished(false);

        when(postService.updatePost(postID,updatedPost)).thenReturn(updatedPost);

        ResponseEntity<PostDto> response = postController.updatePost(postID,updatedPost);

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals("Java Intro",response.getBody().getTitle());
        assertEquals(false,response.getBody().isPublished());
        assertNotNull(response.getBody());

        verify(postService, times(1)).updatePost(postID,updatedPost);
    }

    @Test
    void deletePost() {
        Long postId = 1L;
        PostDto post = new PostDto();
        post.setId(postId);
        post.setTitle("Java Intro");
        post.setPublished(true);

        postService.deletePost(postId);

        ResponseEntity<Void> response = postController.deletePost(postId);

        assertEquals(HttpStatus.NO_CONTENT,response.getStatusCode());

        verify(postService, times(2)).deletePost(postId);
    }

    @Test
    void deletePostsByAuthor() {
        String author = "author";
        PostDto post = new PostDto();
        post.setId(1L);
        post.setTitle("Java Intro");
        post.setPublished(true);
        post.setAuthor(author);

        postService.deletePostsByAuthor(author);

        ResponseEntity<Void> response = postController.deletePostsByAuthor(author);

        assertEquals(HttpStatus.NO_CONTENT,response.getStatusCode());

        verify(postService, times(2)).deletePostsByAuthor(author);

    }

    @Test
    void publishPost() {
        Long postId = 1L;
        PostDto post = new PostDto();
        post.setId(postId);
        post.setPublished(true);
        post.setTitle("Java Intro");
        post.setAuthor("author");

        when(postService.publishPost(postId)).thenReturn(post);
        ResponseEntity<PostDto> response = postController.publishPost(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(postService, times(1)).publishPost(postId);
    }

    @Test
    void unpublishPost() {
        Long postId = 1L;
        PostDto post = new PostDto();
        post.setId(postId);
        post.setPublished(false);
        post.setTitle("Java Intro");
        post.setAuthor("author");

        when(postService.unpublishPost(postId)).thenReturn(post);
        ResponseEntity<PostDto> response = postController.unpublishPost(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(postService, times(1)).unpublishPost(postId);
    }
}