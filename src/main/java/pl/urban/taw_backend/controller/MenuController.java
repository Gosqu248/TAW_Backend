package pl.urban.taw_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<Menu> getMenuById(Long id) {
        return ResponseEntity.ok(menuService.getMenuById(id));
    }

    @GetMapping("/add")
    public ResponseEntity<Menu> addMenu(Menu menu) {
        return ResponseEntity.ok(menuService.addMenu(menu));
    }

    @GetMapping("/update")
    public ResponseEntity<Menu> updateMenu(Long menuId, Menu updatedMenu) {
        return ResponseEntity.ok(menuService.updateMenu(menuId, updatedMenu));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteMenu(Long menuId) {
        menuService.deleteMenu(menuId);
        return ResponseEntity.ok().build();
    }
}
