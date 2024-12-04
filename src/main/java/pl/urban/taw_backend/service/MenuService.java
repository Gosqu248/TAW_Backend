package pl.urban.taw_backend.service;

import org.springframework.stereotype.Service;
import pl.urban.taw_backend.model.Menu;
import pl.urban.taw_backend.repository.MenuRepository;

import java.util.List;

@Service
public class MenuService {

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    public Menu getMenuById(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu with this id not found"));
    }

    public Menu addMenu(Menu menu) {
        return menuRepository.save(menu);
    }

    public Menu updateMenu(Long menuId, Menu updatedMenu) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu with this id not found"));

        menu.setName(updatedMenu.getName());
        menu.setPrice(updatedMenu.getPrice());
        menu.setCategory(updatedMenu.getCategory());
        menu.setIngredients(updatedMenu.getIngredients());
        menu.setImage(updatedMenu.getImage());
        return menuRepository.save(menu);
    }

    public void deleteMenu(Long menuId) {
        menuRepository.deleteById(menuId);
    }
}
