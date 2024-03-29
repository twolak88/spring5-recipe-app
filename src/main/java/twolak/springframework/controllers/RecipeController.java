package twolak.springframework.controllers;

import javax.validation.Valid;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;
import twolak.springframework.commands.RecipeCommand;
import twolak.springframework.services.RecipeService;

/**
 * @author twolak
 *
 */
@Slf4j
@Controller
@RequestMapping("/recipe")
public class RecipeController {

	private static final String VIEW_RECIPE_SHOW = "recipe/show";
	private static final String VIEW_RECIPE_RECIPEFORM = "recipe/recipeform";
	private RecipeService recipeService;

	public RecipeController(RecipeService recipeService) {
		this.recipeService = recipeService;
	}

	@GetMapping("/{recipeId}/show")
	public String getRecipe(@PathVariable Long recipeId, Model model) {
		model.addAttribute("recipe", this.recipeService.findById(recipeId));
		return VIEW_RECIPE_SHOW;
	}
	
	@GetMapping("/new")
	public String newRecipe(Model model) {
		model.addAttribute("recipe", new RecipeCommand());
		return VIEW_RECIPE_RECIPEFORM;
	}
	
	@GetMapping("/{recipeId}/update")
	public String updateRecipe(@PathVariable Long recipeId, Model model) {
		model.addAttribute("recipe", this.recipeService.findById(recipeId));
		return VIEW_RECIPE_RECIPEFORM;
	}
	
	@PostMapping
	public String saveOrUpdate(@Valid @ModelAttribute("recipe") RecipeCommand recipeCommand, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach(ObjectError -> {
				log.error(ObjectError.toString());
			});
			return VIEW_RECIPE_RECIPEFORM;
		}
		RecipeCommand savedRecipeCommand = this.recipeService.save(recipeCommand);
		return "redirect:/recipe/" + savedRecipeCommand.getId() + "/show";
	}
	
	@GetMapping("/{recipeId}/delete")
	public String deleteRecipe(@PathVariable Long recipeId) {
		log.debug("Deleting id: " + recipeId);
		this.recipeService.deleteById(recipeId);
		return "redirect:/";
	}
}
