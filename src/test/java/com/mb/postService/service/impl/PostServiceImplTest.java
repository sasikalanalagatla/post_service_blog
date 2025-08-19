package com.mb.postService.service.impl;

import com.mb.postService.dto.PostDto;
import com.mb.postService.exception.PostNotFoundException;
import com.mb.postService.model.Post;
import com.mb.postService.model.Tag;
import com.mb.postService.repository.PostRepository;
import com.mb.postService.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private TagRepository tagRepository;

    @Spy
    @InjectMocks
    private PostServiceImpl postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPost_ShouldSavePostAndReturnDto() {
        Tag javaTag = new Tag(1L, "Java", LocalDateTime.now(), LocalDateTime.now());
        Tag springTag = new Tag(2L, "Spring", LocalDateTime.now(), LocalDateTime.now());

        PostDto postDto = new PostDto();
        postDto.setTitle("Test Title");
        postDto.setExcerpt("Test Excerpt");
        postDto.setContent("Test Content");
        postDto.setAuthor("John Doe");
        postDto.setPublished(true);
        postDto.setTags(Arrays.asList("Java", "Spring"));

        when(tagRepository.findByName("Java")).thenReturn(javaTag);
        when(tagRepository.findByName("Spring")).thenReturn(springTag);

        Post savedPost = new Post();
        savedPost.setId(100L);
        savedPost.setTitle(postDto.getTitle());
        savedPost.setExcerpt(postDto.getExcerpt());
        savedPost.setContent(postDto.getContent());
        savedPost.setAuthor(postDto.getAuthor());
        savedPost.setPublished(true);
        savedPost.setTags(Arrays.asList(javaTag, springTag));
        savedPost.setCreatedAt(LocalDateTime.now());
        savedPost.setUpdatedAt(LocalDateTime.now());
        savedPost.setPublishedAt(LocalDateTime.now());

        when(postRepository.save(any(Post.class))).thenReturn(savedPost);

        PostDto result = postService.createPost(postDto);

        assertNotNull(result);
        assertTrue(result.isPublished());
        assertEquals(2, result.getTags().size());
        verify(tagRepository, times(1)).findByName("Java");
    }

    @Test
    void getPostById_WhenPostExists_ShouldReturnDto() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setTitle("Test Title");
        post.setAuthor("John Doe");

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        PostDto result = postService.getPostById(postId);

        assertNotNull(result);
        assertEquals(postId, result.getId());
        assertEquals("Test Title", result.getTitle());
        assertEquals("John Doe", result.getAuthor());

        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void getPostById_WhenPostDoesNotExist_ShouldThrowException() {
        Long postId = 99L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        PostNotFoundException exception = assertThrows(
                PostNotFoundException.class,
                () -> postService.getPostById(postId)
        );

        assertEquals("Post not found with id: 99", exception.getMessage());
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void getAllPosts_WhenPostsExist_ShouldReturnPostDtos() {
        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("First Post");
        post1.setAuthor("Alice");

        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Second Post");
        post2.setAuthor("Bob");

        when(postRepository.findAll()).thenReturn(Arrays.asList(post1, post2));

        List<PostDto> result = postService.getAllPosts();

        assertNotNull(result);
        assertEquals(2, result.size());

        PostDto dto1 = result.get(0);
        assertEquals(1L, dto1.getId());
        assertEquals("First Post", dto1.getTitle());
        assertEquals("Alice", dto1.getAuthor());

        PostDto dto2 = result.get(1);
        assertEquals(2L, dto2.getId());
        assertEquals("Second Post", dto2.getTitle());
        assertEquals("Bob", dto2.getAuthor());

        verify(postRepository, times(1)).findAll();
    }

    @Test
    void getAllPosts_WhenNoPosts_ShouldReturnEmptyList() {
        when(postRepository.findAll()).thenReturn(Collections.emptyList());

        List<PostDto> result = postService.getAllPosts();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(postRepository, times(1)).findAll();
    }

    @Test
    void getPostsByAuthor_WhenPostsExist_ShouldReturnDtos() {
        String author = "Alice";

        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("First Post");
        post1.setAuthor(author);
        post1.setCreatedAt(LocalDateTime.now().minusDays(1));

        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Second Post");
        post2.setAuthor(author);
        post2.setCreatedAt(LocalDateTime.now());

        when(postRepository.findByAuthorOrderByCreatedAtDesc(author))
                .thenReturn(Arrays.asList(post2, post1));

        List<PostDto> result = postService.getPostsByAuthor(author);

        assertNotNull(result);
        assertEquals(2, result.size());

        PostDto dto1 = result.get(0);
        assertEquals(2L, dto1.getId());
        assertEquals("Second Post", dto1.getTitle());
        assertEquals("Alice", dto1.getAuthor());

        PostDto dto2 = result.get(1);
        assertEquals(1L, dto2.getId());
        assertEquals("First Post", dto2.getTitle());
        assertEquals("Alice", dto2.getAuthor());

        verify(postRepository, times(1)).findByAuthorOrderByCreatedAtDesc(author);
    }

    @Test
    void getPostsByAuthor_WhenNoPosts_ShouldReturnEmptyList() {
        String author = "Bob";
        when(postRepository.findByAuthorOrderByCreatedAtDesc(author)).thenReturn(Collections.emptyList());

        List<PostDto> result = postService.getPostsByAuthor(author);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(postRepository, times(1)).findByAuthorOrderByCreatedAtDesc(author);
    }
    @Test
    void getPublishedPosts_WhenPostsExist_ShouldReturnDtos() {
        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("First Post");
        post1.setAuthor("Alice");
        post1.setPublished(true);
        post1.setCreatedAt(LocalDateTime.now().minusDays(1));

        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Second Post");
        post2.setAuthor("Bob");
        post2.setPublished(true);
        post2.setCreatedAt(LocalDateTime.now());

        when(postRepository.findByIsPublishedTrueOrderByCreatedAtDesc())
                .thenReturn(Arrays.asList(post2, post1));

        List<PostDto> result = postService.getPublishedPosts();

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(postRepository, times(1)).findByIsPublishedTrueOrderByCreatedAtDesc();
    }

    @Test
    void getPublishedPosts_WhenNoPosts_ShouldReturnEmptyList() {
        when(postRepository.findByIsPublishedTrueOrderByCreatedAtDesc()).thenReturn(Collections.emptyList());

        List<PostDto> result = postService.getPublishedPosts();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(postRepository, times(1)).findByIsPublishedTrueOrderByCreatedAtDesc();
    }

    @Test
    void getPostsByTag_WhenPostsExist_ShouldReturnDtos() {
        String tag = "Java";

        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("Spring Boot Basics");
        post1.setAuthor("Alice");
        post1.setCreatedAt(LocalDateTime.now().minusDays(1));

        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Advanced Java Streams");
        post2.setAuthor("Bob");
        post2.setCreatedAt(LocalDateTime.now());

        when(postRepository.findByTagOrderByCreatedAtDesc(tag))
                .thenReturn(Arrays.asList(post2, post1));

        List<PostDto> result = postService.getPostsByTag(tag);

        assertNotNull(result);
        assertEquals(2, result.size());

        PostDto dto1 = result.get(0);
        assertEquals(2L, dto1.getId());

        PostDto dto2 = result.get(1);
        assertEquals(1L, dto2.getId());

        verify(postRepository, times(1)).findByTagOrderByCreatedAtDesc(tag);
    }

    @Test
    void getPostsByTag_WhenNoPosts_ShouldReturnEmptyList() {
        String tag = "Python";
        when(postRepository.findByTagOrderByCreatedAtDesc(tag)).thenReturn(Collections.emptyList());

        List<PostDto> result = postService.getPostsByTag(tag);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(postRepository, times(1)).findByTagOrderByCreatedAtDesc(tag);
    }

    @Test
    void searchPosts_WhenMatchesExist_ShouldReturnDtos() {
        String keyword = "Java";

        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("Java Basics");
        post1.setAuthor("Alice");
        post1.setCreatedAt(LocalDateTime.now().minusDays(2));

        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Spring with Java");
        post2.setAuthor("Bob");
        post2.setCreatedAt(LocalDateTime.now().minusDays(1));

        when(postRepository.searchByKeyword(keyword))
                .thenReturn(Arrays.asList(post1, post2));

        List<PostDto> result = postService.searchPosts(keyword);

        assertNotNull(result);
        assertEquals(2, result.size());

        PostDto dto1 = result.get(0);
        assertEquals(1L, dto1.getId());

        PostDto dto2 = result.get(1);
        assertEquals(2L, dto2.getId());

        verify(postRepository, times(1)).searchByKeyword(keyword);
    }

    @Test
    void searchPosts_WhenNoMatches_ShouldReturnEmptyList() {
        String keyword = "Python";
        when(postRepository.searchByKeyword(keyword)).thenReturn(Collections.emptyList());

        List<PostDto> result = postService.searchPosts(keyword);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(postRepository, times(1)).searchByKeyword(keyword);
    }

    @Test
    void getAllTags_WhenTagsExist_ShouldReturnTagList() {
        List<String> mockTags = Arrays.asList("Java", "Spring", "Hibernate");
        when(postRepository.findAllTags()).thenReturn(mockTags);

        List<String> result = postService.getAllTags();

        assertNotNull(result);
        assertEquals(3, result.size());

        verify(postRepository, times(1)).findAllTags();
    }

    @Test
    void getAllTags_WhenNoTagsExist_ShouldReturnEmptyList() {
        when(postRepository.findAllTags()).thenReturn(Collections.emptyList());

        List<String> result = postService.getAllTags();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(postRepository, times(1)).findAllTags();
    }

    @Test
    void updatePost_WhenPostExists_ShouldUpdateAndReturnDto() {
        Post existingPost = new Post();
        existingPost.setId(1L);
        existingPost.setTitle("Old title");

        PostDto dto = new PostDto();
        dto.setTitle("New title");
        dto.setTags(List.of("Spring", "Java"));

        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));
        when(tagRepository.findByName("Spring")).thenReturn(null);
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostDto result = postService.updatePost(1L, dto);

        assertEquals("New title", result.getTitle());
        assertTrue(result.getTags().contains("Spring"));
    }

    @Test
    void updatePost_WhenPostNotFound_ShouldThrowException() {
        Long postId = 99L;
        PostDto postDto = new PostDto();
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.updatePost(postId, postDto));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void updatePost_WhenTagsAreNull_ShouldNotFail() {
        Long postId = 1L;
        Post existingPost = new Post();
        existingPost.setId(postId);

        PostDto postDto = new PostDto();
        postDto.setTags(null);

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        PostDto result = postService.updatePost(postId, postDto);

        assertNotNull(result);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void deletePost_ShouldDelete_WhenPostExists() {
        Long postId = 1L;
        when(postRepository.existsById(postId)).thenReturn(true);

        postService.deletePost(postId);

        verify(postRepository, times(1)).deleteById(postId);
    }

    @Test
    void deletePost_ShouldThrowException_WhenPostDoesNotExist() {
        Long postId = 1L;
        when(postRepository.existsById(postId)).thenReturn(false);

        PostNotFoundException exception = assertThrows(PostNotFoundException.class,
                () -> postService.deletePost(postId));

        assertEquals("Post not found with id: " + postId, exception.getMessage());
        verify(postRepository, never()).deleteById(postId);
    }

    @Test
    void deletePostsByAuthor_ShouldDeleteAllPostsOfAuthor() {
        String author = "JohnDoe";
        List<Post> posts = new ArrayList<>();
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setTitle("Test Title");
        post.setAuthor("John Doe");
        Long postId1 = 2L;
        Post post1 = new Post();
        post1.setId(postId1);
        post1.setTitle("Test Title");
        post1.setAuthor("John Doe");
        posts.add(post1);
        posts.add(post);

        when(postRepository.findByAuthorOrderByCreatedAtDesc(author)).thenReturn(posts);

        postService.deletePostsByAuthor(author);

        verify(postRepository, times(1)).findByAuthorOrderByCreatedAtDesc(author);
        verify(postRepository, times(1)).deleteAll(posts);
    }

    @Test
    void publishPost_ShouldSetPublishedTrue_WhenPostExists() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setTitle("Test Title");
        post.setAuthor("John Doe");
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        PostDto result = postService.publishPost(postId);

        assertTrue(result.isPublished());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void publishPost_ShouldThrowException_WhenPostNotFound() {
        Long postId = 99L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.publishPost(postId));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void unpublishPost_ShouldSetPublishedFalse_WhenPostExists() {
        Long postId = 2L;
        Post post = new Post();
        post.setId(postId);
        post.setTitle("Test Title");
        post.setAuthor("John Doe");
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        PostDto result = postService.unpublishPost(postId);

        assertFalse(result.isPublished());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void unpublishPost_ShouldThrowException_WhenPostNotFound() {
        Long postId = 100L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.unpublishPost(postId));
        verify(postRepository, never()).save(any(Post.class));
    }
}