package pl.urban.taw_backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.urban.taw_backend.dto.MenuDTO;
import pl.urban.taw_backend.exception.MenuServiceException;
import pl.urban.taw_backend.exception.ResourceNotFoundException;
import pl.urban.taw_backend.exception.ValidationException;
import pl.urban.taw_backend.model.Menu;
import pl.urban.taw_backend.service.MenuService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;

import java.util.List;


@RestController
@RequestMapping("/api/menu")
@Slf4j
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/getAll")
    public ResponseEntity<List<MenuDTO>> getAllMenus() {
        return ResponseEntity.ok(menuService.getAllMenus());
    }
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getMenuImage(@PathVariable Long id) {
        byte[] imageData = menuService.getMenuImage(id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageData);
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Menu> addMenu(@RequestPart("menu") Menu menu,
                                        @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(menuService.addMenu(menu, image));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Menu> updateMenu(@PathVariable Long id,
                                           @RequestPart("menu") Menu menu,
                                           @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(menuService.updateMenu(id, menu, image));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MenuServiceException.class)
    public ResponseEntity<String> handleServiceException(MenuServiceException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }


}
