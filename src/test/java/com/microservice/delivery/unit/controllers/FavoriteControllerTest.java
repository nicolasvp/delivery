package com.microservice.delivery.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.delivery.controllers.FavoriteController;
import com.microservices.commons.enums.CrudMessagesEnum;
import com.microservices.commons.models.entity.delivery.Favorite;
import com.microservice.delivery.models.services.IFavoriteService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FavoriteControllerTest {

    // Simulate HTTP requests
    private MockMvc mockMvc;

    // Transform objects to json and json to objects
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private IFavoriteService favoriteService;

    @Mock
    private IUtilService utilService;

    @InjectMocks
    private FavoriteController favoriteController;

    private List<Favorite> dummyFavorites;

    private List<String> invalidParamsMessages = new ArrayList<>();
    private List<String> emptyFavoriteMessages = new ArrayList<>();

    private Favorite favorite1;
    private Favorite favorite2;
    private Favorite favorite3;

    private void createDummyFavorites(){
        favorite1 = new Favorite(1L, 1L, new Date());
        favorite2 = new Favorite(1L, 2L, new Date());
        favorite3 = new Favorite(1L, 3L, new Date());

        dummyFavorites = Arrays.asList(favorite1, favorite2, favorite3);
    }

    private void setInvalidFavoriteParamsMessages() {
        invalidParamsMessages.add("The user field must have between 1 and 20 characters");
    }

    private void setEmptyFavoriteMessages() {
        emptyFavoriteMessages.add("The user field can't be empty");
        emptyFavoriteMessages.add("The phraseId field can't be empty");
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(favoriteController)
                .build();

        createDummyFavorites();
        setInvalidFavoriteParamsMessages();
        setEmptyFavoriteMessages();
    }

    @Test
    public void index() throws Exception {
        when(favoriteService.findAll()).thenReturn(dummyFavorites);

        mockMvc.perform(get("/api/favorities")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0].phraseId", is(1)));

        verify(favoriteService, times(1)).findAll();
        verifyNoMoreInteractions(favoriteService);
    }

    /* BEGIN SHOW favoriteController method tests */

    @Test
    public void show_withProperId() throws Exception {
        when(favoriteService.findById("1")).thenReturn(favorite1);

        mockMvc.perform(get("/api/favorities/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.phraseId", is(1)));

        verify(favoriteService, times(1)).findById("1");
        verifyNoMoreInteractions(favoriteService);
    }

    @Test
    public void show_whenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/favorities/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void show_whenRecordDoesnotExist() throws Exception {
        when(favoriteService.findById("1")).thenReturn(null);
        mockMvc.perform(get("/api/favorities/{id}", anyLong()))
                .andExpect(status().isNotFound());

        verify(favoriteService, times(1)).findById("1");
        verifyNoMoreInteractions(favoriteService);
    }

    @Test
    public void show_whenDBFailsThenThrowsException() throws Exception {
        when(favoriteService.findById("1")).thenThrow(new DataAccessException("..."){});

        mockMvc.perform(get("/api/favorities/{id}", 1))
                .andExpect(status().isInternalServerError());

        verify(favoriteService, times(1)).findById("1");
        verifyNoMoreInteractions(favoriteService);
    }

    /* END SHOW favoriteController method tests */

    /* BEGIN CREATE favoriteController method tests */

    @Test
    public void create_withProperFavorite() throws Exception {
        when(favoriteService.save(any(Favorite.class))).thenReturn(favorite1);
        
        mockMvc.perform(post("/api/favorities")
                .content(objectMapper.writeValueAsString(favorite1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.favorite").exists())
                .andExpect(jsonPath("$.favorite.phraseId", is(1)))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.CREATED.getMessage())));

        verify(favoriteService, times(1)).save(any(Favorite.class));
        verifyNoMoreInteractions(favoriteService);
    }

    @Test
    public void create_whenFavoriteIsEmpty() throws Exception {
        when(utilService.listErrors(any())).thenReturn(emptyFavoriteMessages);

        mockMvc.perform(post("/api/favorities")
                .content(objectMapper.writeValueAsString(new Favorite()))
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
        when(favoriteService.save(any(Favorite.class))).thenThrow(new DataAccessException("..."){});

        mockMvc.perform(post("/api/favorities")
                .content(objectMapper.writeValueAsString(favorite1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(favoriteService, times(1)).save(any(Favorite.class));
        verifyNoMoreInteractions(favoriteService);
    }

    /* END CREATE favoriteController method tests */

    /* BEGIN UPDATE favoriteController method tests */

    @Test
    public void update_withProperFavoriteAndId() throws Exception {
        when(favoriteService.findById("1")).thenReturn(favorite1);
        when(favoriteService.save(any(Favorite.class))).thenReturn(favorite1);
        
        mockMvc.perform(put("/api/favorities/{id}", 1)
                .content(objectMapper.writeValueAsString(favorite1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.favorite").exists())
                .andExpect(jsonPath("$.favorite.phraseId", is(1)))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.UPDATED.getMessage())));

        verify(favoriteService, times(1)).findById("1");
        verify(favoriteService, times(1)).save(any(Favorite.class));
        verifyNoMoreInteractions(favoriteService);
    }

    @Test
    public void update_whenFavoriteIsProper_andInvalidId() throws Exception {
        mockMvc.perform(put("/api/favorities/{id}", "randomString")
                .content(objectMapper.writeValueAsString(favorite1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_whenFavoriteIsEmpty_AndProperId() throws Exception {
        when(utilService.listErrors(any())).thenReturn(emptyFavoriteMessages);

        mockMvc.perform(put("/api/favorities/{id}", 1)
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
    public void update_whenFavoriteIsNotFound() throws Exception {
        when(favoriteService.findById("1")).thenReturn(null);

        mockMvc.perform(put("/api/favorities/{id}", anyLong())
                .content(objectMapper.writeValueAsString(favorite1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isNotFound());

        verify(favoriteService, times(1)).findById("1");
        verifyNoMoreInteractions(favoriteService);
    }

    @Test
    public void update_whenDBFailsThenThrowsException() throws Exception {
        when(favoriteService.save(any(Favorite.class))).thenThrow(new DataAccessException("..."){});
        when(favoriteService.findById("1")).thenReturn(favorite1);

        mockMvc.perform(put("/api/favorities/{id}", 1)
                .content(objectMapper.writeValueAsString(favorite1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isInternalServerError());

        verify(favoriteService, times(1)).save(any(Favorite.class));
        verify(favoriteService, times(1)).findById("1");
        verifyNoMoreInteractions(favoriteService);
    }

    /* END UPDATE favoriteController method tests */

    /* BEGIN DELETE favoriteController method tests */

    @Test
    public void delete_withProperId() throws Exception {
        doNothing().when(favoriteService).delete("1");
        
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/favorities/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.DELETED.getMessage())));

        verify(favoriteService, times(1)).delete("1");
        verifyNoMoreInteractions(favoriteService);
    }

    @Test
    public void delete_withInvalidId() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/favorities/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void delete_whenUserIsNotFoundThenThrowException() throws Exception {
        doThrow(new DataAccessException("..."){}).when(favoriteService).delete("1");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/favorities/{id}", anyLong())
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isInternalServerError());

        verify(favoriteService, times(1)).delete("1");
        verifyNoMoreInteractions(favoriteService);
    }

    /* END DELETE favoriteController method tests */
}
