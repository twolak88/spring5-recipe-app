/**
 * 
 */
package twolak.springframework.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import twolak.springframework.commands.RecipeCommand;
import twolak.springframework.services.ImageService;
import twolak.springframework.services.RecipeService;

/**
 * @author twolak
 *
 */
@ExtendWith(MockitoExtension.class)
class ImageControllerTest {
	
	@Mock
	private ImageService imageService;
	
	@Mock
	private RecipeService recipeService;
	
	@InjectMocks
	private ImageController imageController;
	
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.imageController)
							.setControllerAdvice(new ControllerExceptionHandler()).build();
	}
	
	@Test
	void getImageForm() throws Exception {
		RecipeCommand recipeCommand = new RecipeCommand();
		recipeCommand.setId(1L);
		
		when(this.recipeService.findById(anyLong())).thenReturn(recipeCommand);
		
		this.mockMvc.perform(get("/recipe/1/image"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("recipe"));
		
		verify(this.recipeService, times(1)).findById(anyLong());
		verifyNoMoreInteractions(this.recipeService);
	}
	
	@Test
	@Disabled
	void testHandleImagePost() throws Exception {
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "testing.txt", "text/plain", "Spring Framework TW".getBytes());
		
		//doNothing().when(this.imageService).saveImageFile(anyLong(), any());
		
		this.mockMvc.perform(multipart("/recipe/1/image").file(mockMultipartFile))
					.andExpect(status().is3xxRedirection())
					.andExpect(header().string("Location", "/recipe/1/show"));
		
		verify(this.imageService, times(1)).saveImageFile(anyLong(), any());
		verifyNoMoreInteractions(this.imageService);
	}
	
	@Test
	void testRenderImageFromDB() throws Exception {
		RecipeCommand recipeCommand = new RecipeCommand();
		recipeCommand.setId(1L);
		
		String s = "fake image text";
		Byte[] bytesBoxed = new Byte[s.getBytes().length];
		
		int i = 0;
		for (byte b : s.getBytes()) {
			bytesBoxed[i++] = b;
		}
		
		recipeCommand.setImage(bytesBoxed);
		
		when(this.recipeService.findById(anyLong())).thenReturn(recipeCommand);
		
		MockHttpServletResponse mockHttpServletResponse = this.mockMvc.perform(get("/recipe/1/recipeimage"))
				.andExpect(status().isOk()).andReturn().getResponse();
		byte[] responseBytes = mockHttpServletResponse.getContentAsByteArray();
		assertEquals(s.getBytes().length, responseBytes.length);
	}
	
	@Test
	void testGetImageNumberFormatException() throws Exception {
		this.mockMvc.perform(get("/recipe/abc/recipeimage"))
			.andExpect(status().isBadRequest())
			.andExpect(view().name("recipe/error/404error"));
	}
}
