package pl.urban.taw_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.urban.taw_backend.model.Menu;
import pl.urban.taw_backend.service.MenuService;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Menu>> getAllMenus() {
        return ResponseEntity.ok(menuService.getAllMenus());
    }

    @GetMapping("/id")
    public ResponseEntity<Menu> getMenuById(@RequestParam Long id) {
        return ResponseEntity.ok(menuService.getMenuById(id));
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Menu> addMenu(@RequestBody Menu menu) {
        return ResponseEntity.ok(menuService.addMenu(menu));
    }

    @GetMapping("/update")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Menu> updateMenu(@RequestParam Long menuId, @RequestBody Menu updatedMenu) {
        return ResponseEntity.ok(menuService.updateMenu(menuId, updatedMenu));
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteMenu(@RequestParam Long menuId) {
        menuService.deleteMenu(menuId);
        return ResponseEntity.ok().build();
    }
}
