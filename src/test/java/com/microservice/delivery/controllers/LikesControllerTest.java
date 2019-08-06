package com.microservice.delivery.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.commons.enums.CrudMessagesEnum;
import com.microservices.commons.models.entity.delivery.Like;
import com.microservice.delivery.models.services.ILikesService;
import com.microservices.commons.models.entity.users.User;
import com.microservices.commons.models.services.IUtilService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class LikesControllerTest {

    // Simulate HTTP requests
    private MockMvc mockMvc;

    // Transform objects to json and json to objects
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ILikesService likesService;

    @Mock
    private IUtilService utilService;

    @InjectMocks
    private LikesController likesController;
	
    private List<Like> dummyLikes;

    private Like like1;
    private Like like2;
    private Like like3;

    private List<String> invalidParamsMessages = new ArrayList<>();
    private List<String> emptyLikeMessages = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(likesController)
                .build();

        createDummyLikes();
        setInvalidLikeParamsMessages();
        setEmptyLikeMessages();
    }

    private void createDummyLikes(){
        like1 = new Like(1L, 1L, new Date());
        like2 = new Like(1L, 2L, new Date());
        like3 = new Like(1L, 3L, new Date());

        dummyLikes = Arrays.asList(like1, like2, like3);
    }

    private void setInvalidLikeParamsMessages() {
        invalidParamsMessages.add("The user field must have 1 and 20 characters");
    }

    private void setEmptyLikeMessages() {
        emptyLikeMessages.add("The user field can't be empty");
        emptyLikeMessages.add("The phraseId field can't be empty");
    }

    @Test
    public void index() throws Exception {
        when(likesService.findAll()).thenReturn(dummyLikes);

        mockMvc.perform(get("/api/likes")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0].phraseId", is(1)));

        verify(likesService, times(1)).findAll();
        verifyNoMoreInteractions(likesService);
    }

    /* BEGIN SHOW likesController method tests */

    @Test
    public void show_withProperId() throws Exception {
        when(likesService.findById(1L)).thenReturn(like1);

        mockMvc.perform(get("/api/likes/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.phraseId", is(1)));

        verify(likesService, times(1)).findById(1L);
        verifyNoMoreInteractions(likesService);
    }

    @Test
    public void show_whenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/likes/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void show_whenRecordDoesnotExist() throws Exception {
        when(likesService.findById(anyLong())).thenReturn(null);
        mockMvc.perform(get("/api/likes/{id}", anyLong()))
                .andExpect(status().isNotFound());

        verify(likesService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(likesService);
    }

    @Test
    public void show_whenDBFailsThenThrowsException() throws Exception {
        when(likesService.findById(1L)).thenThrow(new DataAccessException("..."){});

        mockMvc.perform(get("/api/likes/{id}", 1))
                .andExpect(status().isInternalServerError());

        verify(likesService, times(1)).findById(1L);
        verifyNoMoreInteractions(likesService);
    }

    /* END SHOW likesController method tests */

    /* BEGIN CREATE likesController method tests */

    @Test
    public void create_withProperLike() throws Exception {
        when(likesService.save(any(Like.class))).thenReturn(like1);

        mockMvc.perform(post("/api/likes")
                .content(objectMapper.writeValueAsString(like1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.like").exists())
                .andExpect(jsonPath("$.like.phraseId", is(1)))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.CREATED.getMessage())));

        verify(likesService, times(1)).save(any(Like.class));
        verifyNoMoreInteractions(likesService);
    }

    @Test
    public void create_whenLikeIsEmpty() throws Exception {
        when(utilService.listErrors(any())).thenReturn(emptyLikeMessages);

        mockMvc.perform(post("/api/likes")
                .content(objectMapper.writeValueAsString(new Like()))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors", hasItem("The phraseId field can't be empty")))
                .andExpect(jsonPath("$.errors", hasItem("The user field can't be empty")));
    }

    @Test
    public void create_whenDBFailsThenThrowsException() throws Exception {
        when(likesService.save(any(Like.class))).thenThrow(new DataAccessException("..."){});

        mockMvc.perform(post("/api/likes")
                .content(objectMapper.writeValueAsString(like1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(likesService, times(1)).save(any(Like.class));
        verifyNoMoreInteractions(likesService);
    }

    /* END CREATE likesController method tests */

    /* BEGIN UPDATE likesController method tests */

    @Test
    public void update_withProperLikeAndId() throws Exception {
        when(likesService.findById(anyLong())).thenReturn(like1);
        when(likesService.save(any(Like.class))).thenReturn(like1);

        mockMvc.perform(put("/api/likes/{id}", 1)
                .content(objectMapper.writeValueAsString(like1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.like").exists())
                .andExpect(jsonPath("$.like.phraseId", is(1)))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.UPDATED.getMessage())));

        verify(likesService, times(1)).findById(anyLong());
        verify(likesService, times(1)).save(any(Like.class));
        verifyNoMoreInteractions(likesService);
    }

    @Test
    public void update_whenLikeIsProper_andInvalidId() throws Exception {
        mockMvc.perform(put("/api/likes/{id}", "randomString")
                .content(objectMapper.writeValueAsString(like1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_whenLikeIsEmpty_AndProperId() throws Exception {
        when(utilService.listErrors(any())).thenReturn(emptyLikeMessages);

        mockMvc.perform(put("/api/likes/{id}", 1)
                .content(objectMapper.writeValueAsString(new User()))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors", hasItem("The phraseId field can't be empty")))
                .andExpect(jsonPath("$.errors", hasItem("The user field can't be empty")));
    }

    @Test
    public void update_whenLikeIsNotFound() throws Exception {
        when(likesService.findById(anyLong())).thenReturn(null);

        mockMvc.perform(put("/api/likes/{id}", anyLong())
                .content(objectMapper.writeValueAsString(like1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isNotFound());

        verify(likesService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(likesService);
    }

    @Test
    public void update_whenDBFailsThenThrowsException() throws Exception {
        when(likesService.save(any(Like.class))).thenThrow(new DataAccessException("..."){});
        when(likesService.findById(anyLong())).thenReturn(like1);

        mockMvc.perform(put("/api/likes/{id}", 1)
                .content(objectMapper.writeValueAsString(like1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isInternalServerError());

        verify(likesService, times(1)).save(any(Like.class));
        verify(likesService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(likesService);
    }

    /* END UPDATE likesController method tests */

    /* BEGIN DELETE likesController method tests */

    @Test
    public void delete_withProperId() throws Exception {
        doNothing().when(likesService).delete(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/likes/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.DELETED.getMessage())));

        verify(likesService, times(1)).delete(anyLong());
        verifyNoMoreInteractions(likesService);
    }

    @Test
    public void delete_withInvalidId() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/likes/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void delete_whenUserIsNotFoundThenThrowException() throws Exception {
        doThrow(new DataAccessException("..."){}).when(likesService).delete(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/likes/{id}", anyLong())
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isInternalServerError());

        verify(likesService, times(1)).delete(anyLong());
        verifyNoMoreInteractions(likesService);
    }

    /* END DELETE likesController method tests */
}
