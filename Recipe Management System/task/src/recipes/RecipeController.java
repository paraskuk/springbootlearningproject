import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recipe")
public class RecipeController {

    @Autowired
    private RecipeRepository recipeRepository;

    @PostMapping("/new")
    public ResponseEntity<?> createRecipe(@RequestBody RecipeDTO recipeDTO) {
        if (recipeDTO.getName().isEmpty() || recipeDTO.getCategory().isEmpty() || recipeDTO.getDescription().isEmpty() ||
                recipeDTO.getIngredients().length == 0 || recipeDTO.getDirections().length == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Recipe recipe = new Recipe();
        recipe.setName(recipeDTO.getName());
        recipe.setCategory(recipeDTO.getCategory());
        recipe.setDescription(recipeDTO.getDescription());
        recipe.setIngredients(recipeDTO.getIngredients());
        recipe.setDirections(recipeDTO.getDirections());
        recipe.setDate(LocalDateTime.now());

        recipe = recipeRepository.save(recipe);
        return new ResponseEntity<>(Map.of("id", recipe.getId()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipe(@PathVariable Long id) {
        Optional<Recipe> recipe = recipeRepository.findById(id);
        return recipe.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable Long id) {
        if (recipeRepository.existsById(id)) {
            recipeRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRecipe(@PathVariable Long id, @RequestBody RecipeDTO recipeDTO) {
        if (recipeDTO.getName().isEmpty() || recipeDTO.getCategory().isEmpty() || recipeDTO.getDescription().isEmpty() ||
                recipeDTO.getIngredients().length == 0 || recipeDTO.getDirections().length == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Recipe> existingRecipeOpt = recipeRepository.findById(id);
        if (existingRecipeOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Recipe existingRecipe = existingRecipeOpt.get();
        existingRecipe.setName(recipeDTO.getName());
        existingRecipe.setCategory(recipeDTO.getCategory());
        existingRecipe.setDescription(recipeDTO.getDescription());
        existingRecipe.setIngredients(recipeDTO.getIngredients());
        existingRecipe.setDirections(recipeDTO.getDirections());
        existingRecipe.setDate(LocalDateTime.now());

        recipeRepository.save(existingRecipe);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchRecipes(@RequestParam(required = false) String category,
                                           @RequestParam(required = false) String name) {
        if ((category == null && name == null) || (category != null && name != null)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Recipe> recipes;
        if (category != null) {
            recipes = recipeRepository.findByCategoryIgnoreCaseOrderByDateDesc(category);
        } else {
            recipes = recipeRepository.findByNameContainingIgnoreCaseOrderByDateDesc(name);
        }

        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }
}
