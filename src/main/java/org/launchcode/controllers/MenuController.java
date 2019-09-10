package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("menu")
public class MenuController {

    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private MenuDao menuDao;

    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "My Menus");

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddCheeseForm(Model model) {
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddCheeseForm(@ModelAttribute @Valid Menu newMenu,
                                       Errors errors, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(newMenu);
        return "redirect:view/" + newMenu.getId();
    }

    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(@PathVariable("menuId") int menu, Model model) {
        Menu newMenu = menuDao.findOne(menu);

        model.addAttribute("menu", newMenu);
        model.addAttribute("title", newMenu.getName());

        return "menu/view";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(@PathVariable("menuId") int menu, Model model) {

        Menu newMenu = menuDao.findOne(menu);
        Iterable<Cheese> allCheese = cheeseDao.findAll();

        model.addAttribute("form", new AddMenuItemForm(newMenu, allCheese));
        model.addAttribute("title", "Add item to menu:" + newMenu.getName());

        return "menu/add-item";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.POST)
    public String addItem(@ModelAttribute  @Valid AddMenuItemForm menuItemForm,
                                       Errors errors, @RequestParam int menuId, @RequestParam int cheeseId, Model model) {

        Menu addToMenu = menuDao.findOne(menuId);

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Cheese");
            return "menu/add-item" + addToMenu.getId();
        }

        Cheese cheeseToAdd = cheeseDao.findOne(cheeseId);

        addToMenu.addItem(cheeseToAdd);
        menuDao.save(addToMenu);

        return "redirect:/menu/view/" + addToMenu.getId();
    }
}
